package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.CategoryService;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Kilian Lichtenauer
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ApplicationScoped.class, ViewScoped.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
class EditCategoryIT extends ITBase {


    private static final int ID_OF_CATEGORY_DELETE = 4;
    private static final int ID_OF_PARENT = 1;
    private static final int ID_OF_CATEGORY_SHIFT = 1;

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Inject
    private CategoryService categoryService;


    @Test
    void delete() throws SQLException {
        CategoryDTO categoryToDelete = new CategoryDTO();
        categoryToDelete.setId(ID_OF_CATEGORY_DELETE);
        categoryToDelete.setParentID(ID_OF_PARENT);

        CategoryDTO categoryForShiftingAds = new CategoryDTO();
        categoryForShiftingAds.setId(ID_OF_CATEGORY_SHIFT);

        categoryService.deleteCategory(categoryToDelete, categoryForShiftingAds);

        TransactionPsql transaction = (TransactionPsql) TransactionFactory.produce();

        int fetchedCategoryOfShiftedAd = 0;
        int fetchedParentCategory = 0;

        try (PreparedStatement fetchAd = transaction.getConnection().prepareStatement("select category from schwarzes_brett.ad where id = 600")) {
            ResultSet rs = fetchAd.executeQuery();
            if (rs.next()) {
                fetchedCategoryOfShiftedAd = rs.getInt("category");
            }
            rs.close();
        }
        try (PreparedStatement fetchCategories = transaction.getConnection().prepareStatement(
                "select parent_id from schwarzes_brett.category where id = 6 OR id = 7")) {
            ResultSet rs = fetchCategories.executeQuery();
            if (rs.next()) {
                fetchedParentCategory = rs.getInt("parent_id");
            }
            rs.close();
        }

        assertEquals(ID_OF_CATEGORY_SHIFT, fetchedCategoryOfShiftedAd);
        assertEquals(ID_OF_CATEGORY_SHIFT, fetchedParentCategory);
    }
}
