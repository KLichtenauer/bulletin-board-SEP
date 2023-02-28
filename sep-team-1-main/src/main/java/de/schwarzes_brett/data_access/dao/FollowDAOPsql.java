package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.AdDoesNotExistException;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.exception.UserDoesNotExistException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PostgreSQL's implementation of the Interface FollowDAO. Subscriptions are managed in a PostgreSQL Database.
 *
 * @author Jonas Elsper
 */
public class FollowDAOPsql extends BaseDAOPsql implements FollowDAO {

    private final Logger logger = LoggerProducer.get(FollowDAOPsql.class);

    /**
     * Creates a new {@code FollowDAOPsql} instance.
     *
     * @param transaction The transaction for database access.
     */
    public FollowDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertFollowUser(UserDTO follower, UserDTO followed) {
        if (doesUserExist(follower) && doesUserExist(followed)) {
            String sqlQuery = "INSERT INTO schwarzes_brett.follow(following_user, followed_user) VALUES (?, ?);";
            try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
                ps.setInt(1, follower.getId());
                ps.setInt(2, followed.getId());
                int successID = ps.executeUpdate();
                if (successID == 0) {
                    logger.warning("Insert was not successfully.");
                    throw new DataStorageAccessException("Following the user failed.");
                }
            } catch (SQLException e) {
                logger.warning("Error occurred while inserting follower with id=" + follower.getId() + " and followed with id="
                               + followed.getId() + ".");
                throw new DataStorageAccessException("Error occurred while inserting follower.", e);
            }
        } else {
            logger.warning("Follower or followed user does not exist.");
            throw new UserDoesNotExistException("Follower or followed user does not exist.");
        }
    }

    private boolean doesUserExist(UserDTO user) {
        String sqlQuery = "SELECT 1 FROM schwarzes_brett.\"user\" WHERE id = ?;";
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while checking if user with id=" + user.getId() + " exists.");
            throw new DataStorageAccessException("Error occurred while checking if user exists.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertFollowAd(UserDTO follower, AdDTO ad) {
        if (doesUserExist(follower)) {
            if (doesAdExist(ad)) {
                String sqlQuery = "INSERT INTO schwarzes_brett.abonnement(\"user\", ad) VALUES (?, ?);";
                try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
                    ps.setInt(1, follower.getId());
                    ps.setInt(2, ad.getId());
                    int successID = ps.executeUpdate();
                    if (successID == 0) {
                        logger.warning("Insert was not successfully.");
                        throw new DataStorageAccessException("Following the ad failed.");
                    }
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Error occurred while inserting follower with id=" + follower.getId() + " and ad with id="
                                              + ad.getId() + ".", e);
                    throw new DataStorageAccessException("Error occurred while inserting follower.", e);
                }
            } else {
                logger.warning("Ad does not exist.");
                throw new AdDoesNotExistException("Ad does not exist.");
            }
        } else {
            logger.warning("Follower does not exist.");
            throw new UserDoesNotExistException("Follower does not exist.");
        }
    }

    private boolean doesAdExist(AdDTO ad) {
        String sqlQuery = "SELECT 1 FROM schwarzes_brett.ad WHERE id = ?;";
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
            ps.setInt(1, ad.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.warning("Error occurred while checking if ad with id=" + ad.getId() + " exists.");
            throw new DataStorageAccessException("Error occurred while checking if user exists.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFollowUser(UserDTO follower, UserDTO followed) {
        if (doesUserExist(follower) && doesUserExist(followed)) {
            String sqlQuery = "DELETE FROM schwarzes_brett.follow f WHERE f.following_user = ? AND  f.followed_user = ?;";
            try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
                ps.setInt(1, follower.getId());
                ps.setInt(2, followed.getId());
                int successID = ps.executeUpdate();
                if (successID == 0) {
                    logger.warning("Delete was not successfully.");
                    throw new DataStorageAccessException("Deleting follower failed.");
                }
            } catch (SQLException e) {
                logger.warning("Error occurred while deleting follower with id=" + follower.getId() + " and followed user with id="
                               + followed.getId() + ".");
                throw new DataStorageAccessException("Error occurred while deleting follower.", e);
            }
        } else {
            logger.warning("Follower or followed user does not exist.");
            throw new UserDoesNotExistException("Follower or followed user does not exist.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFollowAd(UserDTO follower, AdDTO ad) {
        if (doesUserExist(follower)) {
            if (doesAdExist(ad)) {
                String sqlQuery = "DELETE FROM schwarzes_brett.abonnement a WHERE a.\"user\" = ? AND a.ad = ?;";
                try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
                    ps.setInt(1, follower.getId());
                    ps.setInt(2, ad.getId());
                    int successID = ps.executeUpdate();
                    if (successID == 0) {
                        logger.warning("Delete was not successfully.");
                        throw new DataStorageAccessException("Deleting the ad failed.");
                    }
                } catch (SQLException e) {
                    logger.warning("Error occurred while deleting follower with id=" + follower.getId() + " and ad with id="
                                   + ad.getId() + ".");
                    throw new DataStorageAccessException("Error occurred while deleting follower.", e);
                }
            } else {
                logger.warning("Ad does not exist.");
                throw new AdDoesNotExistException("Ad does not exist.");
            }
        } else {
            logger.warning("Follower does not exist.");
            throw new UserDoesNotExistException("Follower does not exist.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAdFollowed(AdDTO ad, UserDTO user) {
        String sqlQuery = "SELECT 1 FROM schwarzes_brett.abonnement a WHERE a.ad=?;";
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
            ps.setInt(1, ad.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.warning("Error occured while checking if user is following a certain ad.");
            throw new DataStorageAccessException("Error occurred while fetching follow ad information.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserFollowed(UserDTO adCreator, UserDTO user) {
        String sqlQuery = "SELECT 1 FROM schwarzes_brett.follow f WHERE f.followed_user = ? AND f.following_user = ?;";
        try (PreparedStatement ps = getTransaction().getConnection().prepareStatement(sqlQuery)) {
            ps.setInt(1, adCreator.getId());
            ps.setInt(2, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.warning("Error occured while checking if user is following a certain adCreator.");
            throw new DataStorageAccessException("Error occurred while fetching follow ad information.", e);
        }
    }
}
