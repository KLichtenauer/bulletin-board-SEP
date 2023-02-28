package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.business_logic.dictionary.CompleteDictionary;
import de.schwarzes_brett.business_logic.exception.MailUnavailableException;
import de.schwarzes_brett.business_logic.mail.MailService;
import de.schwarzes_brett.business_logic.notification.Notification;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.notification.NotificationLevel;
import de.schwarzes_brett.business_logic.notification.NotificationProvider;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.exception.DuplicateEmailAddressException;
import de.schwarzes_brett.data_access.exception.DuplicateUsernameException;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.Limits;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.ResetPasswordDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.TokenDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Provides a service to the backing layer to manage user.  Users can be inserted, updated, fetched and deleted. Also needed for verifying data in
 * login and register process.
 */
@Named
@RequestScoped
public class UserService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final int END_OF_SMALL_LETTERS = 26;
    private static final int END_OF_BIG_LETTERS = 52;
    private static final int TOKEN_LENGTH = 50;
    private static final int BASE = 62;
    /**
     * Regex pattern to check if an email is valid.
     */
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+", Pattern.CASE_INSENSITIVE);
    /**
     * Regex pattern to check if a phone number is valid.
     */
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\s*\\+?[0-9 ]+", Pattern.CASE_INSENSITIVE);
    /**
     * Generator for random numbers for the token.
     */
    private final SecureRandom tokenGenerator;
    /**
     * The notification storage.
     */
    @Inject
    private NotificationProvider notificationProvider;
    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;
    /**
     * The service used for password hashing.
     */
    @Inject
    private PasswordHashService passwordHashService;

    /**
     * Default constructor.
     */
    public UserService() {
        tokenGenerator = new SecureRandom();
    }

    /**
     * Performs a login.
     *
     * @param credentials The credentials used for the login.
     * @param newLocale   The new locale of the user.
     * @return The {@code UserDTO} object if the login was successful, {@code null} otherwise.
     * @author Tim-Florian Feulner
     */
    public UserDTO login(CredentialsDTO credentials, Locale newLocale) {
        try (Transaction transaction = TransactionFactory.produce()) {
            UserDTO user = new UserDTO();
            user.getCredentials().setUsername(credentials.getUsername());
            boolean userExists = DAOFactory.getUserDAO(transaction).fetchUserByUsername(user);

            if (credentials.getPassword().getPasswordHash() == null || !userExists) {
                // Login unsuccessful: unknown user.
                transaction.abort();
                notificationProvider.insert(new Notification(NotificationLevel.ERROR, "f_login_failed", NotificationContext.NONE));
                logger.info("Login failed with unknown username " + credentials.getUsername() + ".");
                return null;
            } else if (!user.getCredentials().getPassword().getPasswordHash().equals(credentials.getPassword().getPasswordHash())) {
                // Login unsuccessful: wrong password.
                transaction.abort();
                notificationProvider.insert(new Notification(NotificationLevel.ERROR, "f_login_failed", NotificationContext.NONE));
                logger.info("Login failed with wrong password for user with username " + credentials.getUsername() + ".");
                return null;
            } else if (user.getVerificationStatus() != UserDTO.VerificationStatus.VERIFIED) {
                // Login unsuccessful: user not verified.
                transaction.abort();
                notificationProvider.insert(new Notification(NotificationLevel.ERROR, "f_login_unverified", NotificationContext.NONE));
                logger.info("Login failed with unverified user with username " + credentials.getUsername() + ".");
                return null;
            } else {
                // Login successful.

                // Save new local if needed.
                if (!user.getLanguage().equals(newLocale)) {
                    user.setLanguage(newLocale);
                    DAOFactory.getUserDAO(transaction).updateUser(user);
                }

                transaction.commit();
                return user;
            }
        }
    }

    /**
     * Handles the insertion of a new user.
     *
     * @param user The user to be inserted.
     * @param path The path of the verification mail.
     * @return True if the user was successfully inserted.
     * @author michaelgruner
     */
    public boolean insertUser(UserDTO user, String path) {
        logger.fine("Trying to insert user with the username " + user.getCredentials().getUsername());
        try (Transaction transaction = TransactionFactory.produce()) {
            user.setRole(Role.USER);
            user.setVerificationStatus(UserDTO.VerificationStatus.REGISTERED_NOT_VERIFIED);
            TokenDTO token = generateToken();
            logger.finest("Token for verification: " + token.getToken());
            user.setContactInfoId(DAOFactory.getUserDAO(transaction).insertContactData(user));
            DAOFactory.getUserDAO(transaction).insertUser(user, token);
            logger.finest("Insertion of the user " + user.getCredentials().getUsername() + " was successfully.");
            String message = generateMessageForValidation(user, token, path);
            MailService.sendMail(user.getEmail(), CompleteDictionary.get("mail_subject_registration", user.getLanguage()), message);
            logger.finest("Verification mail was sent.");
            transaction.commit();
            notificationProvider.insert(new Notification(NotificationLevel.INFORMATION, "m_registrationSuccess", NotificationContext.NONE));
        } catch (DuplicateEmailAddressException e) {
            notificationProvider.insert(new Notification(NotificationLevel.ERROR, "m_emailNotUniqueMessage", NotificationContext.EMAIL));
            return false;
        } catch (DuplicateUsernameException e) {
            notificationProvider.insert(new Notification(NotificationLevel.ERROR, "m_userNameNotUniqueMessage", NotificationContext.USERNAME));
            return false;
        } catch (MailUnavailableException e) {
            logger.severe(
                    "Unable to send the verification mail to the user " + user.getCredentials().getUsername() + " and the email " + user.getEmail());
            notificationProvider.insert(new Notification(NotificationLevel.ERROR, "m_registrationError", NotificationContext.NONE));
            return false;
        }
        return true;
    }


    /**
     * @author michaelgruner
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private String generateMessageForValidation(UserDTO user, TokenDTO token, String path) {
        return CompleteDictionary.get("mail_opening", user.getLanguage()) + " " + user.getCredentials().getUsername() + ","
               + System.getProperty("line.separator")
               + CompleteDictionary.get("mail_message", user.getLanguage()) + System.getProperty("line.separator") + System.getProperty(
                "line.separator") + path + "?username=" + user.getCredentials().getUsername() + "&token=" + token.getToken();
    }

    /**
     * Updates the stored data of a user.
     *
     * @param user         The {@code UserDTO} containing the information for the update.
     * @param loggedInUser The {@code UserDTO} of the currently logged-in user.
     * @param oldUser      The previous user data.
     * @param url          The URL of this server for mail link generation.
     * @return The reaction to the user update.
     * @author Tim-Florian Feulner
     */
    public UpdateUserResult updateUser(UserDTO user, UserDTO loggedInUser, UserDTO oldUser, String url) {
        try (Transaction transaction = TransactionFactory.produce()) {

            // Check that changed username is unique.
            if (!user.getCredentials().getUsername().equals(oldUser.getCredentials().getUsername()) && doesUsernameExist(
                    user.getCredentials().getUsername())) {
                notificationProvider.insert(new Notification(NotificationLevel.ERROR, "v_username_not_exists_error", NotificationContext.USERNAME));
                transaction.abort();
                return UpdateUserResult.ERROR;
            }

            // Check that changed email is unique.
            if (!user.getEmail().equals(oldUser.getEmail()) && !isEmailUnique(user.getEmail())) {
                notificationProvider.insert(new Notification(NotificationLevel.ERROR, "m_emailNotUniqueMessage", NotificationContext.EMAIL));
                transaction.abort();
                return UpdateUserResult.ERROR;
            }

            UserDTO dataBaseUser = new UserDTO();
            dataBaseUser.getCredentials().setUsername(oldUser.getCredentials().getUsername());
            DAOFactory.getUserDAO(transaction).fetchUserByUsername(dataBaseUser);

            UpdateUserResult result;
            notificationProvider.insert(new Notification(NotificationLevel.SUCCESS, "f_profile_save_success_message", NotificationContext.NONE));

            if (user.getEmail().equals(dataBaseUser.getEmail()) || !user.getId().equals(loggedInUser.getId())) {
                result = UpdateUserResult.SUCCESS;
            } else {
                TokenDTO token = generateToken();

                // Send verification mail.
                try {
                    MailService.sendMail(user.getEmail(), CompleteDictionary.get("f_profile_validation_mail_subject", user.getLanguage()),
                                         generateMessageForUpdatedValidation(user, url, token));
                } catch (MailUnavailableException e) {
                    logger.severe("Verification email could not be sent to " + user.getEmail() + ".");
                    notificationProvider.insert(new Notification(NotificationLevel.SUCCESS, "f_profile_save_fail_message", NotificationContext.NONE));
                    transaction.abort();
                    return UpdateUserResult.ERROR;
                }

                notificationProvider.insert(
                        new Notification(NotificationLevel.SUCCESS, "f_profile_renew_verification_message", NotificationContext.NONE));
                result = UpdateUserResult.LOGOUT;
                user.setVerificationStatus(UserDTO.VerificationStatus.REGISTERED_NOT_VERIFIED);
                token.setUsername(user.getCredentials().getUsername());
                DAOFactory.getUserDAO(transaction).updateToken(token);
            }

            DAOFactory.getUserDAO(transaction).updateUser(user);

            transaction.commit();
            return result;
        }
    }

    /**
     * Generates the mail message for updating a user's mail address.
     *
     * @param user    The user to be updated.
     * @param urlBase The base url of the application server.
     * @param token   The verification token.
     * @return The generated message to be sent by mail.
     * @author Tim-Florian Feulner
     */
    private String generateMessageForUpdatedValidation(UserDTO user, String urlBase, TokenDTO token) {
        return String.format(CompleteDictionary.get("f_profile_validation_mail_body", user.getLanguage()), user.getCredentials().getUsername(),
                             user.getEmail()) + System.getProperty("line.separator") + System.getProperty("line.separator") + urlBase + "?username="
               + user.getCredentials().getUsername() + "&token=" + token.getToken();
    }

    /**
     * Inserts an avatar image for a user.
     *
     * @param user The user containing the avatar to be set.
     * @author Tim-Florian Feulner
     */
    public void insertAvatar(UserDTO user) {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).insertImage(user.getAvatar());
            DAOFactory.getUserDAO(transaction).updateUser(user);
            transaction.commit();
        }
    }

    /**
     * Deletes an avatar image of a user.
     *
     * @param user The user whose avatar image is to be deleted.
     * @author Tim-Florian Feulner
     */
    public void deleteAvatar(UserDTO user) {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).deleteImage(user.getAvatar());

            user.setAvatar(new ImageDTO());
            DAOFactory.getUserDAO(transaction).updateUser(user);
            transaction.commit();
        }
    }

    /**
     * Deletes the given user.
     *
     * @param user The user to be deleted.
     * @return {@code true} iff the deletion was successful.
     * @author Tim-Florian Feulner
     */
    public boolean deleteUser(UserDTO user) {
        if (user.getId() == UserDTO.DEFAULT_ADMIN_ID) {
            notificationProvider.insert(
                    new Notification(NotificationLevel.ERROR, "f_profile_deletion_fail_default_admin_message", NotificationContext.NONE));
            return false;
        } else {
            try (Transaction transaction = TransactionFactory.produce()) {
                DAOFactory.getUserDAO(transaction).deleteUser(user);
                transaction.commit();
            }
            notificationProvider.insert(new Notification(NotificationLevel.SUCCESS, "f_profile_deletion_success_message", NotificationContext.NONE));
            return true;
        }
    }

    /**
     * Fetches user data for a given username.
     *
     * @param user A {@code UserDTO} instance which is getting filled with the fetched user.
     * @return {@code true} iff the user exists.
     * @author Tim-Florian Feulner
     */
    public boolean fetchUserByUsername(UserDTO user) {
        try (Transaction transaction = TransactionFactory.produce()) {
            boolean success = DAOFactory.getUserDAO(transaction).fetchUserByUsername(user);
            transaction.commit();
            return success;
        }
    }

    /**
     * Fetches the information about a user from a given id.
     *
     * @param user A {@code UserDTO} instance which is getting filled with the fetched user.
     * @return {@code true} if the user exists.
     * @author michaelgruner
     */
    public boolean fetchUserById(UserDTO user) {
        try (Transaction transaction = TransactionFactory.produce()) {
            boolean success = DAOFactory.getUserDAO(transaction).fetchUserById(user);
            transaction.commit();
            return success;
        }
    }

    /**
     * Checks if a password is valid.
     *
     * @param password The password to be validated.
     * @return If password is valid.
     * @author Tim-Florian Feulner
     */
    public boolean isPasswordValid(String password) {
        return isPasswordSecureEnough(password);
    }

    /**
     * Checks if email is valid.
     *
     * @param email The email to be validated.
     * @return If email is valid.
     * @author Daniel Lipp
     */
    public boolean isEmailValid(String email) {
        return email != null && email.length() <= Limits.SHORT_TEXT_MAX_LENGTH && EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Checks if email is unique.
     *
     * @param email The email to be validated.
     * @return If email is unique.
     * @author michaelgruner
     */
    public boolean isEmailUnique(String email) {
        boolean unique;
        try (Transaction t = TransactionFactory.produce()) {
            unique = DAOFactory.getUserDAO(t).isEmailUnique(email);
            t.commit();
        }
        return unique;
    }

    /**
     * Checks if phone number is valid.
     *
     * @param number The phone number to be validated.
     * @return If phone number is valid.
     * @author Daniel lipp
     */
    public boolean isPhoneNumberValid(String number) {
        return number != null && PHONE_REGEX.matcher(number).matches();
    }

    /**
     * Generates token for registration process.
     *
     * @return An instance of {@code TokenDTO} containing the token.
     * @author michaelgruner
     */
    private TokenDTO generateToken() {
        TokenDTO token = new TokenDTO();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; ++i) {
            builder.append(readAsChar(tokenGenerator.nextInt(0, BASE)));
        }
        token.setToken(builder.toString());
        return token;
    }

    /**
     * @author michaelgruner
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private char readAsChar(int val) {
        char base;
        if (val < END_OF_SMALL_LETTERS) {
            base = 'a';
        } else if (val < END_OF_BIG_LETTERS) {
            base = 'a';
            val -= END_OF_SMALL_LETTERS;
        } else {
            base = '0';
            val -= END_OF_BIG_LETTERS;
        }
        return (char) ((int) base + val);
    }

    /**
     * Fetches multiple users shown on a given pagination-list.
     *
     * @param pagination The pagination of which the users should get fetched.
     * @return A list of users included in the pagination.
     * @author Kilian Lichtenauer
     */
    public List<UserDTO> fetchUsers(PaginationDTO pagination) {
        if (pagination.getSortBy() == null) {
            String message = "SortBy of Pagination is null";
            logger.severe(message);
            throw new IllegalStateException(message);
        }
        logger.fine("Fetching Users.");
        List<UserDTO> users;
        try (Transaction transaction = TransactionFactory.produce()) {
            users = DAOFactory.getUserDAO(transaction).fetchUsers(pagination);
            pagination.setLastPageNumber(DAOFactory.getUserDAO(transaction).fetchLastPageNumber(pagination));
            transaction.commit();
        }
        return users;
    }

    /**
     * Processes the given token and verifies the corresponding user.
     *
     * @param token The token to be processed.
     * @return {@code true} if the token was successfully processed.
     * @author Daniel Lipp
     */
    public boolean processToken(TokenDTO token) {
        try (Transaction t = TransactionFactory.produce()) {
            boolean ret = DAOFactory.getUserDAO(t).processToken(token);
            t.commit();
            return ret;
        }
    }

    /**
     * Checks if passwords are equal.
     *
     * @param password1 First password to be checked.
     * @param password2 Second password to be checked.
     * @return True if passwords are the equal.
     * @author michaelgruner
     */
    public boolean arePasswordsEqual(PasswordDTO password1, String password2) {
        if (password1 == null || password1.getPasswordHash() == null || password2 == null) {
            logger.finest("password1 and/or password 2 is null");
            return false;
        }
        String hashedPassword2 = passwordHashService.hashPassword(password2, password1.getPwdSalt());
        return password1.getPasswordHash().equals(hashedPassword2);
    }

    /**
     * Checks if password is secure enough.
     *
     * @param password The password to be checked.
     * @return True if password is secure enough.
     * @author Tim-Florian Feulner
     */
    private boolean isPasswordSecureEnough(String password) {
        return password != null && password.length() >= Limits.MIN_PASSWORD_LENGTH;
    }

    /**
     * Checks if a username exists.
     *
     * @param username username to be checked.
     * @return {@code true} if the username exists.
     * @author Daniel Lipp
     */
    public boolean doesUsernameExist(String username) {
        if (username == null) {
            return false;
        }
        try (Transaction t = TransactionFactory.produce()) {
            boolean ret = DAOFactory.getUserDAO(t).doesUserExist(username);
            t.commit();
            return ret;
        }
    }

    /**
     * Checks if the given price is valid.
     *
     * @param price Price to be validated.
     * @return {@code} true if the price is not negative and not null.
     * @author Daniel Lipp
     */
    public boolean isPriceValid(BigDecimal price) {
        return price != null && price.abs().equals(price);
    }

    /**
     * Sends an email to the given mail with a resetPassword link.
     *
     * @param rsPwd DTO containing the rsPwd
     * @param url   Url to the site where the user can change his password.
     * @return {@code true} if the process was successful.
     * @author Daniel Lipp
     */
    public boolean sendMailWithResetPasswordLink(ResetPasswordDTO rsPwd, String url) {
        TokenDTO token = generateToken();
        try (Transaction t = TransactionFactory.produce()) {
            boolean ret = DAOFactory.getUserDAO(t).resetPassword(rsPwd, token);
            generateMessageForResetPassword(rsPwd, url, token);
            MailService.sendMail(rsPwd.getEmail(), CompleteDictionary.get("f_resetPwd_mail_subject", rsPwd.getLanguage()),
                                 generateMessageForResetPassword(rsPwd, url, token));
            t.commit();
            Notification not;
            if (ret) {
                not = new Notification(NotificationLevel.SUCCESS, "f_resetPwd_emailSentMsg", NotificationContext.NONE);
            } else {
                not = new Notification(NotificationLevel.ERROR, "f_resetPwd_emailNotSentMsg", NotificationContext.NONE);
            }
            notificationProvider.insert(not);
            return ret;
        } catch (MailUnavailableException e) {
            logger.severe("Set password email could not be sent to " + rsPwd.getEmail() + ".");
            notificationProvider.insert(new Notification(NotificationLevel.ERROR, "f_resetPwd_emailNotPossible", NotificationContext.NONE));
            return false;
        }
    }


    private String generateMessageForResetPassword(ResetPasswordDTO rsPwd, String urlBase, TokenDTO token) {
        return String.format(CompleteDictionary.get("f_resetPwd_mail_body", rsPwd.getLanguage()), rsPwd.getUsername())
               + System.getProperty("line.separator") + System.getProperty("line.separator")
               + urlBase + "?username=" + rsPwd.getUsername()
               + "&token=" + token.getToken();
    }

    /**
     * Changes the password value and hash of a user to the given values.
     *
     * @param user The user who wants to change his password.
     * @return {@code true} if the password was changed successfully.
     * @author Daniel Lipp
     */
    public boolean changePwd(UserDTO user) {
        try (Transaction t = TransactionFactory.produce()) {
            boolean ret = DAOFactory.getUserDAO(t).changePassword(user);
            Notification not;
            if (ret) {
                not = new Notification(NotificationLevel.SUCCESS, "f_setPwd_pwdChangedMsg", NotificationContext.NONE);
            } else {
                not = new Notification(NotificationLevel.ERROR, "f_setPwd_errorChangePwd", NotificationContext.NONE);
            }
            notificationProvider.insert(not);
            t.commit();
            return ret;
        }
    }

    /**
     * The result information of a user update.
     *
     * @author Tim-Florian Feulner
     */
    public enum UpdateUserResult {
        /**
         * The update succeeded.
         */
        SUCCESS,

        /**
         * The update succeeded, a logout has to be performed.
         */
        LOGOUT,

        /**
         * The update failed.
         */
        ERROR
    }
}
