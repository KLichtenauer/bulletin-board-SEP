package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.cache.UserDaoCache;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.exception.DuplicateEmailAddressException;
import de.schwarzes_brett.data_access.exception.DuplicateUsernameException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.ResetPasswordDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.TokenDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the PostgreSQL database access for a user.
 */
public class UserDAOPsql extends BaseDAOPsql implements UserDAO {

    private static final UserDaoCache CACHE = UserDaoCache.getInstance();
    private final Logger logger = LoggerProducer.get(UserDAOPsql.class);
    private final List<UserDTO> usersToBeCached;
    private final List<UserDTO> usersToBeInvalidated;

    /**
     * Creates dao for getting user via given transaction.
     *
     * @param transaction The transaction for database access.
     */
    public UserDAOPsql(TransactionPsql transaction) {
        super(transaction);
        usersToBeCached = new ArrayList<>();
        usersToBeInvalidated = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public void commitChangesToCache() {
        for (UserDTO user : usersToBeCached) {
            CACHE.storeUser(user);
        }
        usersToBeCached.clear();
        for (UserDTO user : usersToBeInvalidated) {
            CACHE.invalidate(user);
        }
        usersToBeInvalidated.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruner
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public void insertUser(UserDTO user, TokenDTO token) throws DuplicateUsernameException, DuplicateEmailAddressException {
        if (doesUserExist(user.getCredentials().getUsername())) {
            logger.warning("Username " + user.getCredentials().getUsername() + "does already exist.");
            throw new DuplicateUsernameException("Username " + user.getCredentials().getUsername() + " does already exist.");
        }
        if (!isEmailUnique(user.getEmail())) {
            logger.warning("Email " + user.getEmail() + " does already exist.");
            throw new DuplicateEmailAddressException("Email " + user.getEmail() + " does already exist.");
        }
        Connection connection = getTransaction().getConnection();
        String query = "INSERT INTO schwarzes_brett.user (nickname, avatar_image_oid, user_role, password_hash, password_salt, language,"
                       + " verification_status, verification_secret, verification_secret_creation_time, contact_data) "
                       + "VALUES (?, ?, ?::schwarzes_brett.USER_ROLE, ?, ?, ?, ?::schwarzes_brett.VERIFICATION_STATUS, ?, NOW(), ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getCredentials().getUsername());
            statement.setNull(2, Types.INTEGER);
            statement.setString(3, user.getRole().toString());
            statement.setString(4, user.getCredentials().getPassword().getPasswordHash());
            statement.setString(5, user.getCredentials().getPassword().getPwdSalt());
            statement.setString(6, user.getLanguage().toString());
            statement.setString(7, user.getVerificationStatus().toString());
            statement.setString(8, token.getToken());
            statement.setLong(9, user.getContactInfoId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to insert user.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Tim-Florian Feulner
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public void updateUser(UserDTO user) {
        Connection connection = getTransaction().getConnection();

        try {
            try (PreparedStatement statement = connection.prepareStatement(String.format(
                    "UPDATE schwarzes_brett.user SET nickname = ?, avatar_image_oid = ?, user_role = '%s', password_hash = ?, password_salt = ?, "
                    + "language = ?, verification_status = '%s' WHERE id = ?;", user.getRole() == null ? null : user.getRole().toString(),
                    user.getVerificationStatus() == null ? null : user.getVerificationStatus().toString()))) {

                DAOPsqlUtil.insertNullableString(1, user.getCredentials().getUsername(), statement);
                DAOPsqlUtil.insertNullableLong(2, user.getAvatar().getId(), statement);
                DAOPsqlUtil.insertNullableString(3, user.getCredentials().getPassword().getPasswordHash(), statement);
                DAOPsqlUtil.insertNullableString(4, user.getCredentials().getPassword().getPwdSalt(), statement);
                DAOPsqlUtil.insertNullableString(5, user.getLanguage().toLanguageTag(), statement);
                DAOPsqlUtil.insertNullableInteger(6, user.getId(), statement);

                if (statement.executeUpdate() != 1) {
                    throw new DataStorageAccessException("User with id " + user.getId() + " does not exist.");
                }
            }

            Integer contactDataId = null;
            try (PreparedStatement statement = connection.prepareStatement("SELECT contact_data FROM schwarzes_brett.user WHERE id = ?;")) {
                DAOPsqlUtil.insertNullableInteger(1, user.getId(), statement);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        contactDataId = DAOPsqlUtil.extractNullableInteger("contact_data", result);
                    }
                }
            }

            if (contactDataId == null) {
                logger.warning("User with id " + user.getId() + " does not have contact data.");
                throw new DataStorageAccessException("User with id " + user.getId() + " does not have contact data.");
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE schwarzes_brett.contact_data SET first_name = ?, last_name = ?, e_mail = ?, phone_number = ?, country = ?, "
                    + "city = ?, postcode = ?, street = ?, house_number = ?, address_suffix = ? WHERE id = ?;")) {

                DAOPsqlUtil.insertNullableString(1, user.getFirstName(), statement);
                DAOPsqlUtil.insertNullableString(2, user.getLastName(), statement);
                DAOPsqlUtil.insertNullableString(3, user.getEmail(), statement);
                DAOPsqlUtil.insertNullableString(4, user.getPhone(), statement);
                DAOPsqlUtil.insertNullableString(5, user.getCountry(), statement);
                DAOPsqlUtil.insertNullableString(6, user.getCity(), statement);
                DAOPsqlUtil.insertNullableString(7, user.getPostalCode(), statement);
                DAOPsqlUtil.insertNullableString(8, user.getStreet(), statement);
                DAOPsqlUtil.insertNullableString(9, user.getStreetNumber(), statement);
                DAOPsqlUtil.insertNullableString(10, user.getAddressAddition(), statement);
                DAOPsqlUtil.insertNullableInteger(11, contactDataId, statement);

                if (statement.executeUpdate() != 1) {
                    throw new DataStorageAccessException("Contact data for user with id " + user.getId() + " does not exist.");
                }
                usersToBeInvalidated.add(user);
            }

        } catch (SQLException | DataStorageAccessException e) {
            String message = "Failed to update user with username " + user.getCredentials().getUsername() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Tim-Florian Feulner
     */
    @Override
    public void updateToken(TokenDTO token) {
        Connection connection = getTransaction().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE schwarzes_brett.user SET verification_secret = ?, verification_secret_creation_time = LOCALTIMESTAMP WHERE nickname = ?;")) {
            statement.setString(1, token.getToken());
            statement.setString(2, token.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to update token for user " + token.getUsername() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Tim-Florian Feulner
     */
    @Override
    public void deleteUser(UserDTO user) {
        Connection connection = getTransaction().getConnection();

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM schwarzes_brett.user WHERE id = ?;")) {
            statement.setInt(1, user.getId());

            if (statement.executeUpdate() != 1) {
                String message = "Could not find user with username " + user.getCredentials().getUsername() + " in the database.";
                logger.severe(message);
                throw new DataStorageAccessException(message);
            }
            usersToBeInvalidated.add(user);
        } catch (SQLException e) {
            String message = "Failed to delete user with username " + user.getCredentials().getUsername() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Tim-Florian Feulner
     */
    @Override
    public boolean fetchUserByUsername(UserDTO user) {
        Connection connection = getTransaction().getConnection();

        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT u.id AS u_id, * FROM schwarzes_brett.user u LEFT OUTER JOIN schwarzes_brett.contact_data c ON u.contact_data = c.id "
                    + "WHERE u.nickname = ?;")) {
                DAOPsqlUtil.insertNullableString(1, user.getCredentials().getUsername(), statement);
                try (ResultSet result = statement.executeQuery()) {
                    if (insertUserData(user, result)) {
                        usersToBeCached.add(user);
                        return true;
                    }
                    return false;
                }
            }

        } catch (SQLException e) {
            String message = "Failed to fetch user with username " + user.getCredentials().getUsername() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruner
     */
    @Override
    public boolean fetchUserById(UserDTO user) {
        if (CACHE.fillUser(user)) {
            return true;
        }
        Connection connection = getTransaction().getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT u.id AS u_id, * FROM schwarzes_brett.user u LEFT OUTER JOIN schwarzes_brett.contact_data c ON u.contact_data = c.id "
                    + "WHERE u.id = ?;")) {
                DAOPsqlUtil.insertNullableInteger(1, user.getId(), statement);
                try (ResultSet result = statement.executeQuery()) {
                    if (insertUserData(user, result)) {
                        usersToBeCached.add(user);
                        return true;
                    }
                    return false;
                }
            }

        } catch (SQLException e) {
            String message = "Failed to fetch user with id " + user.getId() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * Fetches a user by his contact info id.
     *
     * @param user User to be fetched per id.
     * @author Daniel Lipp
     */
    private void updateCacheWithContactInfo(UserDTO user) {
        Connection connection = getTransaction().getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT c.id AS u_id, * FROM schwarzes_brett.user u LEFT OUTER JOIN schwarzes_brett.contact_data c ON u.contact_data = c.id "
                    + "WHERE c.id = ?;")) {
                DAOPsqlUtil.insertNullableLong(1, user.getContactInfoId(), statement);
                try (ResultSet result = statement.executeQuery()) {
                    if (insertUserData(user, result)) {
                        // contact info of a user was updated otherwise the contact info of an ad was updated.
                        usersToBeInvalidated.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            String message = "Failed to update cache for user with contact_info_id " + user.getContactInfoId() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    private boolean insertUserData(UserDTO user, ResultSet result) throws SQLException {
        if (result.next()) {
            // User successfully fetched, now fill user with fetched data.
            user.setId(result.getInt("u_id"));

            ImageDTO avatarImage = new ImageDTO();
            avatarImage.setId(result.getLong("avatar_image_oid"));
            if (result.wasNull()) {
                avatarImage.setId(null);
            }
            user.setAvatar(avatarImage);

            CredentialsDTO credentials = new CredentialsDTO();
            credentials.setUsername(result.getString("nickname"));
            PasswordDTO password = new PasswordDTO();
            password.setPasswordHash(result.getString("password_hash"));
            password.setPwdSalt(result.getString("password_salt"));
            credentials.setPassword(password);
            user.setCredentials(credentials);

            user.setVerificationStatus(UserDTO.VerificationStatus.valueOf(result.getString("verification_status")));
            user.setLanguage(Locale.forLanguageTag(result.getString("language")));
            user.setRole(Role.valueOf(result.getString("user_role")));
            user.setFirstName(DAOPsqlUtil.extractNullableString("first_name", result));
            user.setLastName(DAOPsqlUtil.extractNullableString("last_name", result));
            user.setAddressAddition(DAOPsqlUtil.extractNullableString("address_suffix", result));
            user.setStreet(DAOPsqlUtil.extractNullableString("street", result));
            user.setStreetNumber(DAOPsqlUtil.extractNullableString("house_number", result));
            user.setPostalCode(DAOPsqlUtil.extractNullableString("postcode", result));
            user.setCity(DAOPsqlUtil.extractNullableString("city", result));
            user.setCountry(DAOPsqlUtil.extractNullableString("country", result));
            user.setEmail(DAOPsqlUtil.extractNullableString("e_mail", result));
            user.setPhone(DAOPsqlUtil.extractNullableString("phone_number", result));
            user.setContactInfoId(result.getLong("contact_data"));

            return true;
        } else {
            // User with requested username or id does not exist.
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruner
     */
    @Override
    public boolean isEmailUnique(String email) {
        Connection connection = getTransaction().getConnection();
        boolean unique = true;

        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT u.id AS u_id, * FROM schwarzes_brett.user u LEFT JOIN schwarzes_brett.contact_data c ON u.contact_data = c.id "
                    + "WHERE LOWER(c.e_mail) " + "= LOWER(?);")) {
                statement.setString(1, email);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        unique = false;
                    }
                }
            }
        } catch (SQLException e) {
            String message = "Failed to validate email " + email + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
        return unique;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kilian Lichtenauer
     */
    @Override
    public List<UserDTO> fetchUsers(PaginationDTO pagination) {

        logger.fine("Fetching users");
        PreparedStatement fetchUserStatement;
        String searchTerm = pagination.getSearch().getSearchTerm();
        List<UserDTO> users = new ArrayList<>();
        try {
            fetchUserStatement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.user u LEFT OUTER JOIN schwarzes_brett.contact_data contact ON u.contact_data = contact.id"
                    + " WHERE (lower(nickname)) LIKE ('%' || ? || '%') OR ? IS NULL "
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage() + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1))
                    + ";");
            fetchUserStatement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
            fetchUserStatement.setString(2, pagination.getSearch().getSearchTerm());

            ResultSet set = fetchUserStatement.executeQuery();
            UserDTO userDTO;
            CredentialsDTO credentialsDTO;


            while (set.next()) {
                userDTO = new UserDTO();
                credentialsDTO = new CredentialsDTO();
                userDTO.setId(DAOPsqlUtil.extractNullableInteger("id", set));
                userDTO.setFirstName(DAOPsqlUtil.extractNullableString("first_name", set));
                userDTO.setLastName(DAOPsqlUtil.extractNullableString("last_name", set));
                userDTO.setEmail(DAOPsqlUtil.extractNullableString("e_mail", set));
                userDTO.setPhone(DAOPsqlUtil.extractNullableString("phone_number", set));
                userDTO.setCountry(DAOPsqlUtil.extractNullableString("country", set));
                userDTO.setCity(DAOPsqlUtil.extractNullableString("city", set));
                userDTO.setPostalCode(DAOPsqlUtil.extractNullableString("postcode", set));
                userDTO.setStreet(DAOPsqlUtil.extractNullableString("street", set));
                userDTO.setStreetNumber(DAOPsqlUtil.extractNullableString("house_number", set));
                userDTO.setAddressAddition(DAOPsqlUtil.extractNullableString("address_suffix", set));
                userDTO.setRole(Role.valueOf(DAOPsqlUtil.extractNullableString("user_role", set)));

                credentialsDTO.setUsername(DAOPsqlUtil.extractNullableString("nickname", set));

                fetchUserImage(userDTO);

                userDTO.setCredentials(credentialsDTO);
                users.add(userDTO);
                //cache.setValue(userDTO.getId(), userDTO);
            }
            set.close();
            fetchUserStatement.close();
        } catch (SQLException e) {
            logger.severe("Error occurred while accessing the database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return users;
    }

    /**
     * Fetches the image of a user profile from the database.
     *
     * @param user The user the fetched image belongs to.
     * @throws SQLException Gets thrown if data access not possible.
     * @author Kilian Lichtenauer
     */
    private void fetchUserImage(UserDTO user) throws SQLException {
        logger.fine("Fetching images.");
        ImageDTO profileImage = new ImageDTO();
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement("SELECT * FROM schwarzes_brett.user WHERE id=?;")) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setAvatar(profileImage);
                    profileImage.setId(DAOPsqlUtil.extractNullableLong("avatar_image_oid", rs));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public boolean processToken(TokenDTO token) {
        Connection connection = getTransaction().getConnection();
        String query = "UPDATE schwarzes_brett.user "
                       + "SET verification_status = 'VERIFIED'::schwarzes_brett.VERIFICATION_STATUS, "
                       + "verification_secret = NULL, "
                       + "verification_secret_creation_time = NULL "
                       + "WHERE verification_secret = ? AND nickname = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token.getToken());
            statement.setString(2, token.getUsername());
            logger.fine("Statement for verifying generated.");
            int val = statement.executeUpdate();
            logger.finest("Statement for verifying executed.");
            return val > 0;
        } catch (SQLException e) {
            String message = "Failed to process token";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public boolean doesUserExist(String username) {
        Connection connection = getTransaction().getConnection();
        String query = "SELECT id FROM schwarzes_brett.user WHERE nickname = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet set = statement.executeQuery();
            boolean ret = set.next();
            set.close();
            return ret;
        } catch (SQLException e) {
            String message = "Failed to check if username " + username + " exists.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * Inserts the parameter into an insert/update contact info statement.
     *
     * @param statement Statement which parameters are set.
     * @param user      User containing the contact info.
     * @throws SQLException Should not be thrown.
     * @author Daniel Lipp
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private void insertContactParams(PreparedStatement statement, UserDTO user) throws SQLException {
        DAOPsqlUtil.insertNullableString(1, user.getFirstName(), statement);
        DAOPsqlUtil.insertNullableString(2, user.getLastName(), statement);
        statement.setString(3, user.getEmail());
        DAOPsqlUtil.insertNullableString(4, user.getPhone(), statement);
        statement.setString(5, user.getCountry());
        statement.setString(6, user.getCity());
        statement.setString(7, user.getPostalCode());
        DAOPsqlUtil.insertNullableString(8, user.getStreet(), statement);
        DAOPsqlUtil.insertNullableString(9, user.getStreetNumber(), statement);
        DAOPsqlUtil.insertNullableString(10, user.getAddressAddition(), statement);
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public Long insertContactData(UserDTO user) {
        Connection connection = getTransaction().getConnection();
        String query
                = "INSERT INTO schwarzes_brett.contact_data (first_name, last_name, e_mail, phone_number, country, city, postcode, street, "
                  + "house_number, address_suffix) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertContactParams(statement, user);
            if (statement.executeUpdate() == 0) {
                throw new SQLException();
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    String message = "Failed to insert contact info. Could not extract generated Id.";
                    logger.severe(message);
                    throw new DataStorageAccessException(message);
                }
            }
        } catch (SQLException e) {
            String message = "Failed to insert contact info.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public void updateContactInfo(UserDTO user) {
        Connection connection = getTransaction().getConnection();
        String query
                =
                "UPDATE schwarzes_brett.contact_data SET first_name = ?, last_name = ?, e_mail = ?, phone_number = ?, country = ?, city = ?, "
                + "postcode = ?, street = ?, house_number = ? , address_suffix = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            insertContactParams(statement, user);
            statement.setLong(11, user.getContactInfoId());
            statement.executeUpdate();
            updateCacheWithContactInfo(user);
        } catch (SQLException e) {
            String message = "Failed to insert contact info.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Kilian Lichtenauer
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public int fetchLastPageNumber(PaginationDTO pagination) {
        logger.fine("Fetching users");
        int items = 1;
        PreparedStatement fetchLastPageNumberStatement;
        String searchTerm = pagination.getSearch().getSearchTerm();
        try {
            fetchLastPageNumberStatement = getTransaction().getConnection().prepareStatement(
                    "SELECT count(*) as total FROM schwarzes_brett.user LEFT OUTER JOIN schwarzes_brett.contact_data contact ON contact_data"
                    + " = contact.id WHERE (lower(nickname)) LIKE ('%' || ? || '%') OR ? IS NULL");
            fetchLastPageNumberStatement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
            fetchLastPageNumberStatement.setString(2, pagination.getSearch().getSearchTerm());

            ResultSet set = fetchLastPageNumberStatement.executeQuery();


            if (set.next()) {
                items = set.getInt("total");
            }
            set.close();
            fetchLastPageNumberStatement.close();

        } catch (SQLException e) {
            logger.severe("Error occurred while accessing the database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }

        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Tim-Florian Feulner
     */
    @Override
    public void cleanUnverifiedUsers(int maxAge) {
        final int millisecondsPerSecond = 1000;

        Connection connection = getTransaction().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM schwarzes_brett.user WHERE verification_status = 'REGISTERED_NOT_VERIFIED' "
                + "AND LOCALTIMESTAMP - verification_secret_creation_time >= (? - TO_TIMESTAMP(0));")) {
            statement.setTimestamp(1, new Timestamp(((long) maxAge) * millisecondsPerSecond));
            int count = statement.executeUpdate();
            logger.fine("Cleaned " + count + " unverified users.");
        } catch (SQLException e) {
            logger.severe("Failed to clean unverified users.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            // Do now throw DataStorageAccessException to not interrupt potential future unverified user cleanup.
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public boolean resetPassword(ResetPasswordDTO dto, TokenDTO token) {
        String username = fetchUserWithEmail(dto.getEmail());
        if (username == null) {
            return false;
        }
        Connection connection = getTransaction().getConnection();
        String query = "UPDATE schwarzes_brett.user "
                       + "SET verification_secret = ?, verification_secret_creation_time = NOW() "
                       + "WHERE nickname = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, token.getToken());
            statement.setString(2, username);
            logger.fine("Statement for password reset generated.");
            int val = statement.executeUpdate();
            logger.finest("Statement for password reset executed.");
            dto.setUsername(username);
            return val > 0;
        } catch (SQLException e) {
            String message = "Failed to insert token for the user for the reset of his password.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * Fetches the username to the given email.
     *
     * @param email Email that identifies the user
     * @return Username of the user or {@code null} if no user has this email.
     */
    private String fetchUserWithEmail(String email) {
        Connection connection = getTransaction().getConnection();
        String query = "SELECT nickname FROM schwarzes_brett.user u "
                       + "LEFT JOIN schwarzes_brett.contact_data c "
                       + "ON u.contact_data = c.id "
                       + "WHERE c.e_mail = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            logger.fine("Statement for fetching user with email generated.");
            ResultSet set = statement.executeQuery();
            logger.finest("Statement for fetching user with email executed.");
            if (set.next()) {
                return set.getString(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            String message = "Failed to fetch user with email " + email + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public boolean changePassword(UserDTO user) {
        Connection connection = getTransaction().getConnection();
        String query = "UPDATE schwarzes_brett.user "
                       + "SET password_hash = ?, password_salt = ? "
                       + "WHERE nickname = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getCredentials().getPassword().getPasswordHash());
            statement.setString(2, user.getCredentials().getPassword().getPwdSalt());
            statement.setString(3, user.getCredentials().getUsername());
            logger.fine("Statement for password change generated.");
            int val = statement.executeUpdate();
            logger.finest("Statement for password change executed.");
            if (val > 0) {
                if (user.getId() == null) {
                    fetchUserByUsername(user);
                }
                usersToBeInvalidated.add(user);
                return true;
            }
            return false;
        } catch (SQLException e) {
            String message = "Failed to change the passwort of the user " + user.getCredentials().getUsername() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

}
