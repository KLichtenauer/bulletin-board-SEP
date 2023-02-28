package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DuplicateEmailAddressException;
import de.schwarzes_brett.data_access.exception.DuplicateUsernameException;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.ResetPasswordDTO;
import de.schwarzes_brett.dto.TokenDTO;
import de.schwarzes_brett.dto.UserDTO;

import java.util.List;

/**
 * Controls the database access for a user. The access happens through prepared statements and filling DTOs which get iterated through the layers.
 */
public interface UserDAO extends CacheableDAO {


    /**
     * Inserts a new user in the database.
     *
     * @param user  The user to be inserted.
     * @param token The token for the verification.
     * @throws DuplicateUsernameException     When the username already exists.
     * @throws DuplicateEmailAddressException When the email already exists.
     */
    void insertUser(UserDTO user, TokenDTO token) throws DuplicateUsernameException, DuplicateEmailAddressException;

    /**
     * Updates changes to a user profile in the database.
     *
     * @param user The {@code UserDTO} containing the information for the update.
     */
    void updateUser(UserDTO user);

    /**
     * Updates changes concerning a token.
     *
     * @param token The {@code TokenDTO} to update.
     */
    void updateToken(TokenDTO token);

    /**
     * Deletes a given user in the database.
     *
     * @param user The user to be deleted.
     */
    void deleteUser(UserDTO user);

    /**
     * Gets information about a user from the database for a given username.
     *
     * @param user A {@code UserDTO} instance which is getting filled with the fetched user.
     * @return {@code true} iff the fetch was successful.
     */
    boolean fetchUserByUsername(UserDTO user);

    /**
     * Gets information about a user from the database for a given id.
     *
     * @param user A {@code UserDTO} instance which is getting filled with the fetched user.
     * @return {@code true} if the fetch was successful.
     */
    boolean fetchUserById(UserDTO user);

    /**
     * Gets email of the given user from database.
     *
     * @param email The email to be validated.
     * @return If email is valid.
     */
    boolean isEmailUnique(String email);


    /**
     * Fetches multiple users from database.
     *
     * @param pagination Tells which users get fetched depending on pagination parameters.
     * @return A list of users included in the pagination.
     */
    List<UserDTO> fetchUsers(PaginationDTO pagination);

    /**
     * Processes the given token and verifies the corresponding user.
     *
     * @param token The token to be processed.
     * @return {@code true} if the token was successfully processed.
     */
    boolean processToken(TokenDTO token);

    /**
     * Checks if a username exists.
     *
     * @param username username to be checked.
     * @return {@code true} if the username exists.
     */
    boolean doesUserExist(String username);

    /**
     * Inserts new contact information and returns the generated id.
     *
     * @param user User containing the contact Information.
     * @return Generated id of the contact information.
     */
    Long insertContactData(UserDTO user);

    /**
     * Updates the contact information of user or an ad.
     *
     * @param user user with the information to be stored.
     */
    void updateContactInfo(UserDTO user);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @return The number of pages.
     */
    int fetchLastPageNumber(PaginationDTO pagination);

    /**
     * Cleans all unverified users that have been unverified for over {@code maxAge}.
     *
     * @param maxAge The maximum age that a unverified user can have, in seconds.
     */
    void cleanUnverifiedUsers(int maxAge);

    /**
     * Sets a token for a specific user, so he can reset his password. If the email was valid the username is set in {@code dto}.
     *
     * @param dto   Contains the email of the user.
     * @param token Contains the token which is stored in the db.
     * @return {@code true} if the token was stored for the user corresponding to the email.
     */
    boolean resetPassword(ResetPasswordDTO dto, TokenDTO token);

    /**
     * Changes the password value and hash of a user to the given values.
     *
     * @param user The user who wants to change his password.
     * @return {@code true} if the password was changed successfully.
     */
    boolean changePassword(UserDTO user);

}
