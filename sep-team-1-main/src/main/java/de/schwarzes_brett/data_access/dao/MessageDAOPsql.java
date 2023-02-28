package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Controls the PostgreSQL database access for a message.
 */
public class MessageDAOPsql extends BaseDAOPsql implements MessageDAO {

    private final Logger logger = LoggerProducer.get(MessageDAOPsql.class);

    /**
     * Creates dao for getting messages via given transaction.
     *
     * @param transaction The transaction for database access.
     */
    public MessageDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void insertMessage(MessageDTO message) {
        logger.fine("Inserting message into database.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "INSERT INTO schwarzes_brett.message (time_of_publication, content, is_public, is_anonymous, author, addressee, ad_id) "
                + "VALUES (NOW(), ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, message.getContent());
            ps.setBoolean(2, message.isSharedPublic());
            ps.setBoolean(3, message.isAnonymous());
            ps.setInt(4, message.getSender().getId());
            ps.setInt(5, message.getReceiver().getId());
            ps.setInt(6, message.getAd().getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting message failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Inserting message into database was successful.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @Override
    public void deleteMessage(Integer messageID) {
        logger.fine("Deleting the message.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "DELETE FROM schwarzes_brett.message WHERE id = ?;"
        )) {
            ps.setInt(1, messageID);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Deleting the message was successful.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:SimplifyBooleanExpression"})
    @Override
    public void updateVisibility(MessageDTO message) {
        logger.fine("Update visibility of the message.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "UPDATE schwarzes_brett.message SET is_public = ? WHERE id = ?;"
        )) {
            ps.setBoolean(1, !message.isSharedPublic());
            ps.setInt(2, message.getMessageId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Updating application settings was successful.");
    }

}
