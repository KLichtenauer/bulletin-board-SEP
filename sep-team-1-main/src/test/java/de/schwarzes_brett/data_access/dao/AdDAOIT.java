package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.AdDoesNotExistException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for loading an ad with its id.
 * {@author Jonas Elper}
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
public class AdDAOIT extends ITBase {

    static final int AD_ID = 6;
    static final int USER_ID = 32;

    @Test
    void testInvalidAdId() {
        boolean success = false;
        TransactionPsql trans = new TransactionPsql();
        AdDAOPsql adDAOPsql = new AdDAOPsql(trans);
        AdDTO ad = new AdDTO();
        ad.setId(AD_ID);
        try {
            adDAOPsql.fetchAd(ad, USER_ID, true);
        } catch (AdDoesNotExistException e) {
            success = true;
        }
        assertTrue(success, "Could load ad with id that doesnt exist.");
    }
}
