package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.AdDoesNotExistException;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.Currency;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.PriceDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the PostgreSql database access for an ad.
 */
public class AdDAOPsql extends BaseDAOPsql implements AdDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId ZONE = ZoneId.of("UTC");
    private final Logger logger = LoggerProducer.get(AdDAOPsql.class);

    /**
     * Creates dao for getting ads via given transaction.
     *
     * @param transaction The transaction for database access.
     * @author Daniel Lipp
     */
    public AdDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * Inserts values into an insert/update Ad statement.
     *
     * @param ad        Ad to be inserted/updated.
     * @param statement Statement which parameters should be set.
     * @throws SQLException Is thrown when the data is invalid.
     * @author Daniel Lipp
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private void insertValues(AdDTO ad, PreparedStatement statement) throws SQLException {
        statement.setString(1, ad.getTitle());
        DAOPsqlUtil.insertNullableString(2, ad.getDescription(), statement);
        statement.setBigDecimal(3, ad.getPrice().getValue());
        statement.setString(4, ad.getPrice().getCurrency().toString());
        statement.setBoolean(5, ad.getPrice().isBasisOfNegotiation());
        statement.setBoolean(6, ad.getPrice().isHasPrice());
        setTimeString(statement, 7, ad.getRelease());
        setTimeString(statement, 8, ad.getEnd());
        statement.setInt(9, ad.getCategory().getId());
        statement.setInt(10, ad.getCreator().getId());
        statement.setLong(11, ad.getPublicData().getContactInfoId());
    }

    /**
     * Sets the timestamps into the given statement at the specified Index.
     *
     * @param statement Statement which value should be set.
     * @param index     Index at which the value is inserted.
     * @param date      Date to be set in the statement.
     * @throws SQLException Is thrown when the date is not valid.
     * @author Daniel Lipp
     */
    private void setTimeString(PreparedStatement statement, int index, ZonedDateTime date) throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(date.format(FORMATTER)));
        }
    }

    /**
     * @param connection Connection on which the update is executed.
     * @param ad         Ad to receive an image.
     * @param index      Index of the image.
     * @author Daniel Lipp
     */
    private void insertImage(Connection connection, AdDTO ad, int index) {
        manageImages(connection, ad, ad.getImages().get(index).getId(), true);
    }

    /**
     * @param connection Connection on which the update is executed.
     * @param ad         Ad with an image to be deleted.
     * @param imageId    Identifier of the image to be removed from the ad.
     * @author Daniel Lipp
     */
    private void deleteImage(Connection connection, AdDTO ad, Long imageId) {
        manageImages(connection, ad, imageId, false);
    }

    /**
     * @param connection Connection on which the update is executed.
     * @param ad         Ad with an image to be deleted.
     * @param imageId    Identifier of the image to be removed from the ad.
     * @param insert     {@code true} if the image should be inserted.
     * @author Daniel Lipp
     */
    private void manageImages(Connection connection, AdDTO ad, Long imageId, boolean insert) {
        String query;
        if (insert) {
            query = "INSERT INTO schwarzes_brett.image (image_oid, ad_id, is_thumbnail) VALUES (?, ?, FALSE);";
        } else {
            query = "DELETE FROM schwarzes_brett.image WHERE image_oid = ? AND ad_id = ?;";
        }
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, imageId);
            statement.setInt(2, ad.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to insert images of the ad.";
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
    public void insertAd(AdDTO ad) {
        Connection connection = getTransaction().getConnection();
        // creating the add;
        String query
                = "INSERT INTO schwarzes_brett.ad (title, description, value, currency, is_basis_of_negotiation, "
                  + "has_price, publishing_time, termination_time, category, creator, contact) "
                  + "VALUES (?, ?, ?, ?::schwarzes_brett.CURRENCY, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            insertValues(ad, statement);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ad.setId(generatedKeys.getInt(1));
                } else {
                    String message = "Failed to create a new ad. Could not get generated id.";
                    logger.severe(message);
                    throw new DataStorageAccessException(message);
                }
            }
        } catch (SQLException e) {
            String message = "Failed to create a new ad";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }

        for (int i = 0; i < ad.getImages().size(); ++i) {
            insertImage(connection, ad, i);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void updateAd(AdDTO ad) {
        Connection connection = getTransaction().getConnection();
        String query
                =
                "UPDATE schwarzes_brett.ad SET title = ?, description = ?, value = ?, currency = ?::schwarzes_brett.CURRENCY, "
                + "is_basis_of_negotiation = ?, "
                + "has_price = ?, publishing_time = ?, termination_time= ?, category = ?, creator = ?, contact = ? "
                + "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            insertValues(ad, statement);
            statement.setInt(12, ad.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to create a new ad";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * Removes all images of an ad.
     *
     * @param ad Ad where all images should be removed.
     * @author Daniel Lipp
     */
    private void deleteAllImages(AdDTO ad) {
        String query = "DELETE FROM schwarzes_brett.image WHERE ad_id = ?";

        try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(query)) {
            statement.setInt(1, ad.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to delete all images of the ad with id " + ad.getId() + ".";
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
    public void deleteAd(AdDTO ad) {
        deleteAllImages(ad);
        // deleting images
        String query = "DELETE FROM schwarzes_brett.ad WHERE id = ?";

        try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(query)) {
            statement.setInt(1, ad.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to delete ad with id " + ad.getId() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public void fetchAd(AdDTO ad, Integer userId, boolean isAdminOrCreator) {
        try {
            if (doesAdExist(ad.getId())) {
                logger.finest("Fetching ad info...");
                fetchAdInformation(ad);
                logger.finest("Fetching ad images...");
                fetchAdImages(ad);
                logger.finest("Fetching ad rating...");
                fetchRating(ad);
                logger.finest("Fetching ad follower...");
                fetchFollower(ad);
                if (userId != null) {
                    logger.finest("Fetching ad messages...");
                    fetchMessages(ad, userId, isAdminOrCreator);
                }
                logger.finest("Fetching was successful.");
            } else {
                throw new AdDoesNotExistException("Ad with id " + ad.getId() + " cannot be found.");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error occurred while fetching information for the ad with id=" + ad.getId() + ".", e);
            throw new DataStorageAccessException("Error occurred while fetching ad information.", e);
        } finally {
            getTransaction().abort();
        }
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private boolean doesAdExist(Integer id) throws SQLException {
        Connection conn = getTransaction().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM schwarzes_brett.ad a WHERE a.id=?;")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber"})
    private void fetchMessages(AdDTO ad, int userId, boolean isAdminOrCreator) throws SQLException {
        Connection conn = getTransaction().getConnection();
        List<MessageDTO> messages = new ArrayList<>();
        String sqlQuery;
        if (isAdminOrCreator) {
            sqlQuery = "SELECT m.*, u.nickname as nickname, u_r.nickname as r_nickname "
                       + "FROM schwarzes_brett.message m "
                       + "LEFT JOIN schwarzes_brett.\"user\" u on u.id = m.author "
                       + "LEFT JOIN schwarzes_brett.contact_data cd on cd.id = u.contact_data "
                       + "LEFT JOIN schwarzes_brett.ad a on a.id = m.ad_id "
                       + "LEFT JOIN schwarzes_brett.user u_r ON m.addressee = u_r.id "
                       + "WHERE m.ad_id=? ";
        } else {
            sqlQuery = "SELECT m.*, u.nickname as nickname, u_r.nickname as r_nickname "
                       + "FROM schwarzes_brett.message m "
                       + "LEFT JOIN schwarzes_brett.\"user\" u on u.id = m.author "
                       + "LEFT JOIN schwarzes_brett.contact_data cd on cd.id = u.contact_data "
                       + "LEFT JOIN schwarzes_brett.ad a on a.id = m.ad_id "
                       + "LEFT JOIN schwarzes_brett.user u_r ON m.addressee = u_r.id "
                       + "WHERE m.ad_id=? "
                       + "AND ((m.author=? OR m.addressee=?) "
                       + "OR (m.is_public=TRUE)"
                       + "OR (a.creator=?));";
        }
        try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
            ps.setInt(1, ad.getId());
            if (!isAdminOrCreator) {
                ps.setInt(2, userId);
                ps.setInt(3, userId);
                ps.setInt(4, userId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MessageDTO message = new MessageDTO();
                    message.setMessageId(rs.getInt("id"));
                    message.setAd(ad);
                    message.setContent(rs.getString("content"));
                    UserDTO sender = new UserDTO();
                    CredentialsDTO credentials = new CredentialsDTO();
                    credentials.setUsername(rs.getString("nickname"));
                    sender.setCredentials(credentials);
                    sender.setId(rs.getInt("author"));
                    message.setSender(sender);
                    UserDTO receiver = new UserDTO();
                    receiver.setId(rs.getInt("addressee"));
                    CredentialsDTO credentialsReceiver = new CredentialsDTO();
                    credentialsReceiver.setUsername(rs.getString("r_nickname"));
                    receiver.setCredentials(credentialsReceiver);
                    message.setReceiver(receiver);
                    message.setSharedPublic(rs.getBoolean("is_public"));
                    message.setAnonymous(rs.getBoolean("is_anonymous"));
                    messages.add(message);
                }
            }
        }
        ad.setMessages(messages);
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void fetchRating(AdDTO ad) throws SQLException {
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement("SELECT avg(valuation) "
                                                                                      + "FROM schwarzes_brett.rating "
                                                                                      + "Where rated_user=?;")) {
            ps.setInt(1, ad.getCreator().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ad.getCreator().setRating(rs.getBigDecimal("avg"));
                }
            }
        }
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void fetchFollower(AdDTO ad) throws SQLException {
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement("SELECT count(*) as follower "
                                                                                      + "FROM ("
                                                                                      + "  SELECT following_user as \"user\""
                                                                                      + "  FROM schwarzes_brett.follow"
                                                                                      + "  WHERE followed_user=?"
                                                                                      + "  UNION"
                                                                                      + "  SELECT \"user\""
                                                                                      + "  FROM schwarzes_brett.abonnement"
                                                                                      + "  WHERE \"user\"=?) sub;")) {
            ps.setInt(1, ad.getCreator().getId());
            ps.setInt(2, ad.getCreator().getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ad.setFollower(rs.getInt("follower"));
                }
            }
        }
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void fetchAdImages(AdDTO ad) throws SQLException {
        List<ImageDTO> images = new LinkedList<>();
        ImageDTO thumbnail = new ImageDTO();
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement("SELECT * FROM schwarzes_brett.image WHERE ad_id=?;")) {
            ps.setInt(1, ad.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImageDTO image = new ImageDTO();
                    if (rs.getBoolean("is_thumbnail")) {
                        thumbnail = image;
                    }
                    image.setId(rs.getLong("image_oid"));
                    images.add(image);
                }
            }
        }
        ad.setThumbnail(thumbnail);
        ad.setImages(images);
    }

    /**
     * Parses a date from the given String.
     *
     * @param date Date to be parsed.
     * @return ZonedDateTime parsed from the given String.
     * @author Daniel Lipp
     */
    private ZonedDateTime parseDateTime(String date) {
        return ZonedDateTime.of(LocalDateTime.parse(date, FORMATTER), ZONE);
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void fetchAdInformation(AdDTO ad) throws SQLException {
        Connection conn = getTransaction().getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT a.title as title, "
                                                          + " a.description as description, "
                                                          + " a.publishing_time as release, "
                                                          + " a.termination_time as \"end\", "
                                                          + " a.value as price, "
                                                          + " a.currency as currency, "
                                                          + " a.is_basis_of_negotiation as is_vb, "
                                                          + " a.has_price as has_price, "
                                                          + " a.category as category_id, "
                                                          + " c.name as category_name, "
                                                          + " cd.first_name as pd_firstname, "
                                                          + " cd.last_name as pd_lastname, "
                                                          + " cd.country as location_country, "
                                                          + " cd.postcode as location_postcode, "
                                                          + " cd.city as location_city, "
                                                          + " cd.street as location_street, "
                                                          + " cd.house_number as location_street_number, "
                                                          + " cd.address_suffix as location_address_addition, "
                                                          + " cd.phone_number as location_phone, "
                                                          + " cd.e_mail as location_email, "
                                                          + " u.avatar_image_oid as creator_avatar, "
                                                          + " u.id as creator_id, "
                                                          + " u.nickname as creator_username, "
                                                          + " a.contact as contact_id, "
                                                          + " cd_u.first_name as creator_firstname, "
                                                          + " cd_u.last_name as creator_lastname, "
                                                          + " cd_u.address_suffix as creator_address_addition, "
                                                          + " cd_u.street as creator_street, "
                                                          + " cd_u.house_number as creator_street_number, "
                                                          + " cd_u.postcode as creator_postcode, "
                                                          + " cd_u.city as creator_city, "
                                                          + " cd_u.country as creator_country, "
                                                          + " cd_u.e_mail as creator_email,"
                                                          + " cd_u.phone_number as creator_phone"
                                                          + "  FROM schwarzes_brett.ad a "
                                                          + "  LEFT JOIN schwarzes_brett.user u on u.id = a.creator "
                                                          + "  LEFT JOIN schwarzes_brett.contact_data cd on cd.id = a.contact "
                                                          + "  LEFT JOIN schwarzes_brett.category c on c.id = a.category "
                                                          + "  LEFT JOIN schwarzes_brett.contact_data cd_u on cd_u.id = u.contact_data"
                                                          + "  WHERE a.id=?;")) {
            ps.setInt(1, ad.getId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            ad.setTitle(rs.getString("title"));
            ad.setDescription(DAOPsqlUtil.extractNullableString("description", rs));
            ad.setRelease(parseDateTime(rs.getString("release")));
            String end = rs.getString("end");
            if (end != null) {
                ad.setEnd(parseDateTime(end));
            }
            ad.setBasisOfNegotiation(Boolean.parseBoolean(rs.getString("is_vb")));
            // set price
            PriceDTO price = new PriceDTO();
            price.setCurrency(Currency.valueOf(rs.getString("currency")));
            price.setHasPrice(rs.getBoolean("has_price"));
            price.setValue(rs.getBigDecimal("price"));
            price.setBasisOfNegotiation(rs.getBoolean("is_vb"));
            ad.setPrice(price);
            // set location
            String location = DAOPsqlUtil.extractNullableString("location_country", rs) + ", "
                              + DAOPsqlUtil.extractNullableString("location_postcode", rs) + ", "
                              + DAOPsqlUtil.extractNullableString("location_city", rs);
            ad.setLocation(location);
            // set creator
            UserDTO creator = new UserDTO();
            ImageDTO avatar = new ImageDTO();
            avatar.setId(DAOPsqlUtil.extractNullableLong("creator_avatar", rs));
            creator.setAvatar(avatar);
            creator.setId(rs.getInt("creator_id"));
            CredentialsDTO creatorCredentials = new CredentialsDTO();
            creatorCredentials.setUsername(rs.getString("creator_username"));
            creator.setCredentials(creatorCredentials);
            creator.setFirstName(rs.getString("creator_firstname"));
            creator.setLastName(rs.getString("creator_lastname"));
            creator.setAddressAddition(DAOPsqlUtil.extractNullableString("creator_address_addition", rs));
            creator.setStreet(DAOPsqlUtil.extractNullableString("creator_street", rs));
            creator.setStreetNumber(DAOPsqlUtil.extractNullableString("creator_street_number", rs));
            creator.setPostalCode(rs.getString("creator_postcode"));
            creator.setCity(rs.getString("creator_city"));
            creator.setCountry(rs.getString("creator_country"));
            creator.setEmail(rs.getString("creator_email"));
            creator.setPhone(DAOPsqlUtil.extractNullableString("creator_phone", rs));
            ad.setCreator(creator);
            // set publicData
            UserDTO publicData = new UserDTO();
            publicData.setContactInfoId(rs.getLong("contact_id"));
            publicData.setFirstName(DAOPsqlUtil.extractNullableString("pd_firstname", rs));
            publicData.setLastName(DAOPsqlUtil.extractNullableString("pd_lastname", rs));
            publicData.setPhone(DAOPsqlUtil.extractNullableString("location_phone", rs));
            publicData.setEmail(DAOPsqlUtil.extractNullableString("location_email", rs));
            publicData.setAddressAddition(DAOPsqlUtil.extractNullableString("location_address_addition", rs));
            publicData.setStreet(DAOPsqlUtil.extractNullableString("location_street", rs));
            publicData.setStreetNumber(DAOPsqlUtil.extractNullableString("location_street_number", rs));
            publicData.setPostalCode(rs.getString("location_postcode"));
            if (rs.wasNull()) {
                publicData.setPostalCode(null);
            }
            publicData.setCity(DAOPsqlUtil.extractNullableString("location_city", rs));
            publicData.setCountry(DAOPsqlUtil.extractNullableString("location_country", rs));
            ad.setPublicData(publicData);

            // category
            CategoryDTO category = new CategoryDTO();
            category.setId(rs.getInt("category_id"));
            category.setName(rs.getString("category_name"));
            ad.setCategory(category);
            rs.close();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public List<AdDTO> fetchAdsFromUser(PaginationDTO pagination, UserDTO user) {
        List<AdDTO> ads = new ArrayList<>();
        try {
            logger.finest("Started preparing statement");
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.ad ad "
                    + "LEFT JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL))"
                    + "AND ad.creator = ?"
                    + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                    + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage()
                    + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1)) + ";")) {
                setFetchOwnAdsQueryParams(pagination, user, statement);

                fetchAdsFromResultSet(ads, statement);
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return ads;
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void fetchAdsFromResultSet(List<AdDTO> ads, PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.executeQuery()) {
            AdDTO ad;
            PriceDTO price;
            while (rs.next()) {
                ad = new AdDTO();
                price = new PriceDTO();
                ad.setId(rs.getInt("id"));
                ad.setTitle(rs.getString("title"));
                ad.setDescription(rs.getString("description"));
                price.setHasPrice(rs.getBoolean("has_price"));
                if (price.isHasPrice()) {
                    price.setValue(BigDecimal.valueOf(rs.getInt("value")));
                    price.setCurrency(Currency.valueOf(rs.getString("currency")));
                }
                price.setBasisOfNegotiation(rs.getBoolean("is_basis_of_negotiation"));
                ad.setPrice(price);
                fetchAdImages(ad);
                ads.add(ad);
            }
            logger.finest("Finished creating List for fetchAds");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruner
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public List<AdDTO> fetchAds(PaginationDTO pagination) {
        List<AdDTO> ads = new ArrayList<>();
        try {
            logger.finest("Started preparing statement");
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.ad LEFT OUTER JOIN schwarzes_brett.contact_data contact ON contact = contact.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND ((LOWER(contact.city) LIKE ('%' || ? || '%') OR ? IS NULL) OR (contact.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND (category = ? OR ? is null) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "AND publishing_time < NOW() "
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage() + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1))
                    + ";")) {
                String searchTerm = pagination.getSearch().getSearchTerm();
                String locationSearch = pagination.getSearch().getLocationSearch();
                Integer category = pagination.getCategory().getId();

                statement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(2, pagination.getSearch().getSearchTerm());
                statement.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(4, pagination.getSearch().getSearchTerm());
                statement.setString(5, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(6, pagination.getSearch().getLocationSearch());
                statement.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(8, pagination.getSearch().getLocationSearch());
                if (category == null || category == 0) {
                    statement.setNull(9, Types.INTEGER);
                    statement.setNull(10, Types.INTEGER);
                } else {
                    statement.setInt(9, category);
                    statement.setInt(10, category);
                }

                fetchAdsFromResultSet(ads, statement);
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return ads;
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public List<AdDTO> fetchAdsWithUnreleased(PaginationDTO pagination) {
        List<AdDTO> ads = new ArrayList<>();
        try {
            logger.finest("Started preparing statement");
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.ad LEFT OUTER JOIN schwarzes_brett.contact_data contact ON contact = contact.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL))"
                    + "AND ((LOWER(contact.city) LIKE ('%' || ? || '%') OR ? IS NULL) OR (contact.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND (category = ? OR ? is null) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage() + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1))
                    + ";")) {
                String searchTerm = pagination.getSearch().getSearchTerm();
                String locationSearch = pagination.getSearch().getLocationSearch();
                Integer category = pagination.getCategory().getId();

                statement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(2, pagination.getSearch().getSearchTerm());
                statement.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(4, pagination.getSearch().getSearchTerm());
                statement.setString(5, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(6, pagination.getSearch().getLocationSearch());
                statement.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(8, pagination.getSearch().getLocationSearch());
                if (category == null || category == 0) {
                    statement.setNull(9, Types.INTEGER);
                    statement.setNull(10, Types.INTEGER);
                } else {
                    statement.setInt(9, category);
                    statement.setInt(10, category);
                }

                fetchAdsFromResultSet(ads, statement);
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return ads;
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public int fetchOwnAdsLastPageNumber(PaginationDTO pagination, UserDTO user) {
        int items = 1;
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "SELECT count(*) as total FROM schwarzes_brett.ad ad "
                + "LEFT JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL))"
                + "AND ad.creator = ?"
                + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL));")
        )) {
            setFetchOwnAdsQueryParams(pagination, user, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    items = rs.getInt("total");
                }
                logger.finest("Finished fetching items: " + items);
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public int fetchFollowedAdsLastPageNumber(PaginationDTO pagination, UserDTO user) {
        int items = 1;
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                ("SELECT count(*) as total FROM "
                 + "(SELECT ad.* FROM schwarzes_brett.ad ad "
                 + "LEFT OUTER JOIN schwarzes_brett.abonnement a ON ad.id = a.ad "
                 + "WHERE \"user\" = ? "
                 + "UNION "
                 + "SELECT ad.* FROM schwarzes_brett.ad ad "
                 + "LEFT JOIN schwarzes_brett.follow f ON f.followed_user = ad.creator WHERE following_user = ?) ad "
                 + "LEFT JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                 + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                 + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                 + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                 + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                 + "AND publishing_time < NOW();")
        )) {
            setFetchFollowedAdsQueryParams(pagination, user, ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    items = rs.getInt("total");
                }
                logger.finest("Finished fetching items: " + items);
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public int fetchCommentedAdsLastPageNumber(PaginationDTO pagination, UserDTO user) {
        int items = 1;
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                ("SELECT count(*) as total FROM (SELECT * FROM schwarzes_brett.ad ad WHERE EXISTS ("
                 + "SELECT 1 FROM schwarzes_brett.message m WHERE m.ad_id = ad.id AND (m.author = ? OR m.addressee = ?))) ad "
                 + "LEFT OUTER JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                 + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                 + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                 + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                 + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                 + "AND publishing_time < NOW() ")
        )) {
            setFetchCommentedAdsQueryParams(pagination, user, ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    items = rs.getInt("total");
                }
                logger.finest("Finished fetching items: " + items);
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public List<UserDTO> fetchAdFollower(AdDTO ad) {
        List<UserDTO> follower = new ArrayList<>();
        try {
            if (doesAdExist(ad.getId())) {
                String sqlCommand = "SELECT DISTINCT u.id as id, u.nickname as nickname "
                                    + "FROM "
                                    + "(SELECT following_user as id "
                                    + "FROM schwarzes_brett.follow "
                                    + "WHERE followed_user = (SELECT creator FROM schwarzes_brett.ad WHERE id = ?) "
                                    + "UNION "
                                    + "SELECT \"user\" as id "
                                    + "FROM schwarzes_brett.abonnement "
                                    + "WHERE ad = ?) follower "
                                    + "LEFT JOIN schwarzes_brett.\"user\" u ON follower.id = u.id "
                                    + "LEFT JOIN schwarzes_brett.contact_data c ON u.contact_data = c.id;";
                try (PreparedStatement ps = this.getTransaction().getConnection().prepareStatement(sqlCommand)) {
                    ps.setInt(1, ad.getId());
                    ps.setInt(2, ad.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            UserDTO user = new UserDTO();
                            user.setId(rs.getInt("id"));
                            CredentialsDTO credentials = new CredentialsDTO();
                            credentials.setUsername(rs.getString("nickname"));
                            user.setCredentials(credentials);
                            follower.add(user);
                        }
                    }
                }
                return follower;
            } else {
                throw new AdDoesNotExistException("Ad with id=" + ad.getId() + " does not exist.");
            }
        } catch (SQLException e) {
            throw new DataStorageAccessException("Database is not available.", e);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void setFetchCommentedAdsQueryParams(PaginationDTO pagination, UserDTO user, PreparedStatement ps) throws SQLException {
        String searchTerm = pagination.getSearch().getSearchTerm();
        String locationSearch = pagination.getSearch().getLocationSearch();

        ps.setInt(1, user.getId());
        ps.setInt(2, user.getId());
        ps.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(4, pagination.getSearch().getSearchTerm());
        ps.setString(5, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(6, pagination.getSearch().getSearchTerm());
        ps.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(8, pagination.getSearch().getLocationSearch());
        ps.setString(9, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(10, pagination.getSearch().getLocationSearch());
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber", "DuplicatedCode"})
    private void setFetchFollowedAdsQueryParams(PaginationDTO pagination, UserDTO user, PreparedStatement ps) throws SQLException {
        String searchTerm = pagination.getSearch().getSearchTerm();
        String locationSearch = pagination.getSearch().getLocationSearch();

        ps.setInt(1, user.getId());
        ps.setInt(2, user.getId());
        ps.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(4, pagination.getSearch().getSearchTerm());
        ps.setString(5, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(6, pagination.getSearch().getSearchTerm());
        ps.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(8, pagination.getSearch().getLocationSearch());
        ps.setString(9, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(10, pagination.getSearch().getLocationSearch());
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber"})
    private void setFetchOwnAdsQueryParams(PaginationDTO pagination, UserDTO user, PreparedStatement ps) throws SQLException {
        String searchTerm = pagination.getSearch().getSearchTerm();
        String locationSearch = pagination.getSearch().getLocationSearch();

        ps.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(2, pagination.getSearch().getSearchTerm());
        ps.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
        ps.setString(4, pagination.getSearch().getSearchTerm());
        ps.setInt(5, user.getId());
        ps.setString(6, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(7, pagination.getSearch().getLocationSearch());
        ps.setString(8, locationSearch == null ? null : locationSearch.toLowerCase());
        ps.setString(9, pagination.getSearch().getLocationSearch());
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruener
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public int fetchLastPageNumber(PaginationDTO pagination) {
        int items = 1;
        try {

            logger.finest("Started preparing statement");
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "SELECT COUNT(*) AS total FROM schwarzes_brett.ad LEFT OUTER JOIN schwarzes_brett.contact_data contact ON contact = contact.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND ((LOWER(contact.city) LIKE ('%' || ? || '%') OR ? IS NULL) OR (contact.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND (category = ? OR ? is null) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "AND publishing_time < NOW() "
                    + ";"
            )) {
                String searchTerm = pagination.getSearch().getSearchTerm();
                String locationSearch = pagination.getSearch().getLocationSearch();
                Integer category = pagination.getCategory().getId();

                statement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(2, pagination.getSearch().getSearchTerm());
                statement.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(4, pagination.getSearch().getSearchTerm());
                statement.setString(5, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(6, pagination.getSearch().getLocationSearch());
                statement.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(8, pagination.getSearch().getLocationSearch());
                if (category == null || category == 0) {
                    statement.setNull(9, Types.INTEGER);
                    statement.setNull(10, Types.INTEGER);
                } else {
                    statement.setInt(9, category);
                    statement.setInt(10, category);
                }

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        items = rs.getInt("total");
                    }
                    logger.finest("Finished fetching items: " + items);
                }
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author michaelgruener
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "DuplicatedCode"})
    @Override
    public int fetchLastPageNumberWithUnreleased(PaginationDTO pagination) {
        int items = 1;
        try {

            logger.finest("Started preparing statement");
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "SELECT COUNT(*) AS total FROM schwarzes_brett.ad LEFT OUTER JOIN schwarzes_brett.contact_data contact ON contact = contact.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND ((LOWER(contact.city) LIKE ('%' || ? || '%') OR ? IS NULL) OR (contact.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND (category = ? OR ? is null) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + ";"
            )) {
                String searchTerm = pagination.getSearch().getSearchTerm();
                String locationSearch = pagination.getSearch().getLocationSearch();
                Integer category = pagination.getCategory().getId();

                statement.setString(1, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(2, pagination.getSearch().getSearchTerm());
                statement.setString(3, searchTerm == null ? null : searchTerm.toLowerCase());
                statement.setString(4, pagination.getSearch().getSearchTerm());
                statement.setString(5, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(6, pagination.getSearch().getLocationSearch());
                statement.setString(7, locationSearch == null ? null : locationSearch.toLowerCase());
                statement.setString(8, pagination.getSearch().getLocationSearch());
                if (category == null || category == 0) {
                    statement.setNull(9, Types.INTEGER);
                    statement.setNull(10, Types.INTEGER);
                } else {
                    statement.setInt(9, category);
                    statement.setInt(10, category);
                }

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        items = rs.getInt("total");
                    }
                    logger.finest("Finished fetching items: " + items);
                }
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage());
        }
        return Math.max(1, Math.ceilDiv(items, pagination.getItemsPerPage()));
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public List<AdDTO> fetchFollowedAds(PaginationDTO pagination, UserDTO user) {
        List<AdDTO> ads = new ArrayList<>();
        try {
            logger.finest("Started preparing statement");
            try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM ("
                    + "SELECT ad.* FROM schwarzes_brett.ad ad "
                    + "LEFT OUTER JOIN schwarzes_brett.abonnement a ON ad.id = a.ad "
                    + "WHERE \"user\" = ? "
                    + "UNION "
                    + "SELECT ad.* FROM schwarzes_brett.ad ad "
                    + "LEFT JOIN schwarzes_brett.follow f ON f.followed_user = ad.creator WHERE following_user = ?) ad "
                    + "LEFT JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                    + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "AND publishing_time < NOW() "
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage()
                    + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1)) + ";")) {

                setFetchFollowedAdsQueryParams(pagination, user, ps);

                fetchAdsFromResultSet(ads, ps);
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage());
        }
        return ads;
    }

    /**
     * {@inheritDoc}
     *
     * @author Jonas Elsper
     */
    @Override
    public List<AdDTO> fetchCommentedAds(PaginationDTO pagination, UserDTO user) {
        List<AdDTO> ads = new ArrayList<>();
        try {
            logger.finest("Started preparing statement");
            try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM (SELECT * FROM schwarzes_brett.ad ad WHERE EXISTS ("
                    + "SELECT 1 FROM schwarzes_brett.message m WHERE m.ad_id = ad.id AND (m.author = ? OR m.addressee = ?))) ad "
                    + "LEFT OUTER JOIN schwarzes_brett.contact_data c ON ad.contact = c.id "
                    + "WHERE ((LOWER(title) LIKE ('%' || ? || '%') OR ? IS NULL) OR ((LOWER(ad.description)) LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND ((LOWER(c.city) LIKE ('%' || ? || '%') OR ? IS NULL) "
                    + "OR (c.postcode LIKE ('%' || ? || '%') OR ? IS NULL)) "
                    + "AND " + (pagination.isShowExpiredAds() ? "TRUE " : "((termination_time > NOW()) OR (termination_time IS NULL)) ")
                    + "AND publishing_time < NOW() "
                    + "ORDER BY " + pagination.getSortBy() + (pagination.isSortAscending() ? " ASC " : " DESC ")
                    + "LIMIT " + pagination.getItemsPerPage()
                    + " OFFSET " + (pagination.getItemsPerPage() * (pagination.getPageNumber() - 1)) + ";")) {

                setFetchCommentedAdsQueryParams(pagination, user, ps);

                fetchAdsFromResultSet(ads, ps);
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query for fetching ads");
            throw new DataStorageAccessException(e.getMessage());
        }
        return ads;
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public boolean isAdIdValid(AdDTO ad) {
        try {
            return doesAdExist(ad.getId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error occurred while validating ad id param.", e);
            throw new AdDoesNotExistException("Cannot find ad with given id.");
        }
    }


    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public void insertImage(AdDTO ad, ImageDTO image) {
        manageImages(getTransaction().getConnection(), ad, image.getId(), true);
    }


    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public void deleteImage(AdDTO ad, ImageDTO image) {
        deleteImage(getTransaction().getConnection(), ad, image.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    @SuppressWarnings({"checkstyle:MagicNumber"})
    public void updateThumbnail(AdDTO ad, int index) {
        Connection connection = getTransaction().getConnection();
        String query = "UPDATE schwarzes_brett.image SET is_thumbnail = false WHERE ad_id = ? AND is_thumbnail = true;"
                       + "UPDATE schwarzes_brett.image SET is_thumbnail = true WHERE ad_id = ? AND image_oid = ?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, ad.getId());
            statement.setLong(2, ad.getId());
            statement.setLong(3, ad.getImages().get(index).getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            String message = "Failed to change the thumbnail of the ad.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }
}
