package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.RatingDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Controls the PostgreSQL database access for a rating.
 *
 * @author michaelgruner
 */
public class RatingDAOPsql extends BaseDAOPsql implements RatingDAO {

    private final Logger logger = LoggerProducer.get(RatingDAOPsql.class);

    /**
     * Creates dao for getting ratings via given transaction.
     *
     * @param transaction The transaction for database access.
     */
    public RatingDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void insertRating(RatingDTO rating) {
        logger.fine("Inserting the rating.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "INSERT INTO schwarzes_brett.rating (rated_user, rating_user, valuation) "
                + "VALUES (?, ?, ?);"
        )) {
            ps.setInt(1, rating.getRated().getId());
            ps.setInt(2, rating.getRater().getId());
            ps.setInt(3, rating.getRating());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on ratings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Inserting the rating was successful.");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void updateRating(RatingDTO rating) {
        logger.fine("Updating the rating.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "UPDATE schwarzes_brett.rating SET valuation = ? "
                + "WHERE rated_user = ? AND rating_user = ?;"
        )) {
            ps.setInt(1, rating.getRating());
            ps.setInt(2, rating.getRated().getId());
            ps.setInt(3, rating.getRater().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on ratings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Updating the rating was successful.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRating(RatingDTO rating) {
        logger.fine("Deleting the rating.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "DELETE FROM schwarzes_brett.rating WHERE rated_user = ? AND rating_user = ? ;"
        )) {
            ps.setInt(1, rating.getRated().getId());
            ps.setInt(2, rating.getRater().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on ratings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Deleting the rating was successful.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchRating(RatingDTO rating) {
        logger.fine("Fetching rating.");
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(
                "SELECT valuation FROM schwarzes_brett.rating WHERE rated_user = ? AND rating_user = ? ;"
        )) {
            ps.setInt(1, rating.getRated().getId());
            ps.setInt(2, rating.getRater().getId());
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                rating.setRating(resultSet.getInt("valuation"));
            }
            resultSet.close();
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on ratings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Fetching the rating was successful.");
    }
}
