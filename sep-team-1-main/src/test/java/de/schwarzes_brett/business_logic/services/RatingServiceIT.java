package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.RatingDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author michaelgruner
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RatingServiceIT extends ITBase {

    private static final UserDTO RATED = new UserDTO();
    private static final UserDTO RATER_1 = new UserDTO();
    private static final UserDTO RATER_2 = new UserDTO();
    private static final RatingDTO RATING_1 = new RatingDTO();
    private static final RatingDTO RATING_2 = new RatingDTO();
    private static BigDecimal ratingAfterInsertion;
    private static BigDecimal ratingAfterDeletion;

    @Inject
    private RatingService ratingService;

    /**
     * Init of the DTOs.
     */
    @BeforeAll
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void init() {
        RATED.setId(32);
        RATED.getCredentials().setUsername("Kilian2");
        RATER_1.setId(200);
        RATER_2.setId(300);
        RATING_1.setRating(4);
        RATING_1.setRated(RATED);
        RATING_1.setRater(RATER_1);
        RATING_2.setRating(2);
        RATING_2.setRated(RATED);
        RATING_2.setRater(RATER_2);
        ratingAfterInsertion = new BigDecimal("3.0");
        ratingAfterDeletion = new BigDecimal("2.0");
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        try (TransactionPsql transaction = (TransactionPsql) TransactionFactory.produce()) {
            Connection connection = transaction.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM schwarzes_brett.rating WHERE rated_user = 32");
                statement.executeUpdate();
                statement.close();
                transaction.commit();
            } finally {
                transaction.abort();
            }
        }
    }

    /**
     * Tests the insertion of a new rating.
     *
     * @throws SQLException Should not be thrown.
     */
    @Test
    @Order(1)
    public void testInsertRating() throws SQLException {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getRatingDAO(transaction).insertRating(RATING_1);
            DAOFactory.getRatingDAO(transaction).insertRating(RATING_2);
            fetchAverageRating((TransactionPsql) transaction, RATED);
            transaction.commit();
        }
        assertEquals(ratingAfterInsertion, RATED.getRating());
    }

    /**
     * Tests the deletion of a rating.
     *
     * @throws SQLException Should not be thrown.
     */
    @Test
    @Order(2)
    public void testDeleteRating() throws SQLException {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getRatingDAO(transaction).deleteRating(RATING_1);
            fetchAverageRating((TransactionPsql) transaction, RATED);
            transaction.commit();
        }
        assertEquals(ratingAfterDeletion, RATED.getRating());
    }

    private void fetchAverageRating(TransactionPsql transaction, UserDTO rated) throws SQLException {
        try (PreparedStatement ps = transaction.getConnection().prepareStatement("SELECT avg(valuation) "
                                                                                 + "FROM schwarzes_brett.rating "
                                                                                 + "Where rated_user=?;")) {
            ps.setInt(1, rated.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rated.setRating(rs.getBigDecimal("avg").setScale(1, RoundingMode.HALF_EVEN));
                }
            }
        }
    }
}
