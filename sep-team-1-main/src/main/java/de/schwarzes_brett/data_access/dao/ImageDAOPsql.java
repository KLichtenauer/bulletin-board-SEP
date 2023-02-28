package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.exception.ImageDoesNotExistException;
import de.schwarzes_brett.data_access.exception.LOAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.logging.LoggerProducer;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the PostgreSQL database access for images.
 *
 * @author Tim-Florian Feulner
 */
public class ImageDAOPsql extends BaseDAOPsql implements ImageDAO {

    private final Logger logger = LoggerProducer.get(ImageDAOPsql.class);

    /**
     * Creates the DAO via the given {@code TransactionPsql} instance.
     *
     * @param transaction The transaction for database access.
     */
    public ImageDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * Writes a large object from the database to an output stream.
     *
     * @param id     The id of the large object.
     * @param output The output stream to write to.
     */
    private void writeLOToStream(long id, OutputStream output) throws LOAccessException {
        PGConnection connection = (PGConnection) getTransaction().getConnection();
        try {
            LargeObjectManager objManager = connection.getLargeObjectAPI();
            try (LargeObject obj = objManager.open(id, LargeObjectManager.READ)) {
                obj.getInputStream().transferTo(output);
            }
        } catch (SQLException | IOException e) {
            if (e instanceof SQLException || !e.getMessage().contains("broken pipe")) {
                String message = "Failed to fetch large object " + id + ".";
                logger.log(Level.SEVERE, message, e);
                throw new LOAccessException(message, e);
            }
            // Image download was interrupted by user, ignore this exception.
            logger.finest("Image download was interrupted by user.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean imageExists(ImageDTO image) {
        logger.finest("Checking if image with id " + image.getId() + " exists.");
        Connection connection = getTransaction().getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT oid FROM schwarzes_brett.temp_img WHERE oid = ?;")) {
                statement.setLong(1, image.getId());
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            String message = "Failed to check if image exists.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchImage(ImageDTO image) throws ImageDoesNotExistException {
        logger.finest("Fetching image " + image.getId() + " from database.");
        try {
            writeLOToStream(image.getId(), image.getRetrieveStream());
        } catch (LOAccessException e) {
            String message = "Failed to fetch image " + image.getId() + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ImageDoesNotExistException(message, e);
        }
    }

    /**
     * Writes a large object from an input stream to the database.
     *
     * @param input The input stream to read from.
     * @return The id of the large object that was created.
     */
    private long writeStreamToLO(InputStream input) throws LOAccessException {
        PGConnection connection = (PGConnection) getTransaction().getConnection();

        try {
            LargeObjectManager objManager = connection.getLargeObjectAPI();

            long oid = objManager.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

            try (LargeObject obj = objManager.open(oid, LargeObjectManager.WRITE)) {
                // Copy the data from the file to the large object
                final int bufferSize = 2048;
                byte[] buffer = new byte[bufferSize];
                int byteCountInBuffer;
                while ((byteCountInBuffer = input.read(buffer, 0, buffer.length)) > 0) {
                    obj.write(buffer, 0, byteCountInBuffer);
                }
            }

            return oid;
        } catch (SQLException | IOException e) {
            String message = "Failed to write large object.";
            logger.log(Level.SEVERE, message, e);
            throw new LOAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertImage(ImageDTO image) {
        logger.finest("Inserting image into database.");

        long oid;
        try {
            // Insert large object.
            oid = writeStreamToLO(image.getStoreStream());
            image.setId(oid);

            // Insert image entry into table temp_img.
            try (PreparedStatement statement = getTransaction().getConnection().prepareStatement(
                    "INSERT INTO schwarzes_brett.temp_img VALUES (?, LOCALTIMESTAMP);")) {
                statement.setLong(1, oid);
                statement.executeUpdate();
            }
        } catch (LOAccessException | SQLException e) {
            String message = "Failed to insert image into database.";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DataStorageAccessException(message, e);
        }

        logger.finest("Successfully inserted image " + oid + " into database.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteImage(ImageDTO image) {
        logger.fine("Deleting image " + image.getId() + ".");
        Connection connection = getTransaction().getConnection();
        try {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM schwarzes_brett.temp_img WHERE oid = ?;")) {
                ps.setLong(1, image.getId());
                ps.execute();
            }
        } catch (SQLException e) {
            String message = "Failed to delete image " + image.getId() + ".";
            logger.log(Level.SEVERE, message, e);
            throw new DataStorageAccessException(message, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUnusedImages(int maxAge) {
        final int millisecondsPerSecond = 1000;

        Connection connection = getTransaction().getConnection();
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM schwarzes_brett.temp_img WHERE LOCALTIMESTAMP - creation > (? - TO_TIMESTAMP(0)) AND oid NOT IN ("
                    + "SELECT image_oid FROM schwarzes_brett.image UNION "
                    + "SELECT avatar_image_oid FROM schwarzes_brett.user WHERE avatar_image_oid IS NOT NULL UNION "
                    + "SELECT operator_img FROM schwarzes_brett.application_settings WHERE operator_img IS NOT NULL);")) {
                statement.setTimestamp(1, new Timestamp(((long) maxAge) * millisecondsPerSecond));
                int count = statement.executeUpdate();
                logger.fine("Cleaned " + count + " unused images.");
            }
        } catch (SQLException e) {
            logger.severe("Failed to clean unused images.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            // Do now throw DataStorageAccessException to not interrupt potential future image cleanup.
        }
    }

}
