package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.CategoryDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage categories. Categories can be inserted, updated, fetched and deleted.
 *
 * @author Kilian Lichtenauer
 */
@Named
@RequestScoped
public class CategoryService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public CategoryService() {
    }

    /**
     * Handles the insertion of a new category.
     *
     * @param category Contains all information about the category to be inserted.
     */
    public void insertCategory(CategoryDTO category) {
        logger.fine("Inserting the category with id: " + category.getId());
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getCategoryDAO(transaction).insertCategory(category);
            transaction.commit();
        }
    }

    /**
     * Handles the update of a new category.
     *
     * @param category            Contains all information about the category to be updated.
     * @param categoryForShifting Category which is chosen for being the new parent category of {@code category}.
     */
    public void updateCategory(CategoryDTO category, CategoryDTO categoryForShifting) {
        logger.fine("Upgrading the category with id: " + category.getId());
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getCategoryDAO(transaction).updateCategory(category, categoryForShifting);
            transaction.commit();
        }
    }

    /**
     * Handles the deletion of a new category.
     *
     * @param categoryToBeDeleted Contains all information about the category to be deleted.
     * @param categoryForShifting Category which ads can be shifted to if the user doesn't want the ads included in the category to be deleted to be
     *                            deleted. Can be empty if no category was selected.
     */
    public void deleteCategory(CategoryDTO categoryToBeDeleted, CategoryDTO categoryForShifting) {
        logger.fine("Deletion of the category with id: " + categoryToBeDeleted.getId());
        Transaction transaction = TransactionFactory.produce();
        try {
            DAOFactory.getCategoryDAO(transaction).deleteCategory(categoryToBeDeleted, categoryForShifting);
            transaction.commit();
        } finally {
            transaction.abort();
        }
    }

    /**
     * Handles the fetch of root categories which are these who have no parent node in the database.
     *
     * @return A list containing all categories wrapped in {@code CategoryDTO}'s.
     */
    public List<CategoryDTO> fetchRootCategories() {
        logger.fine("Fetching root categories.");

        List<CategoryDTO> categories;
        try (Transaction transaction = TransactionFactory.produce()) {
            categories = DAOFactory.getCategoryDAO(transaction).fetchRootCategories();
            transaction.commit();
        }
        return categories;
    }

    /**
     * Fetches all subcategories of a given category.
     *
     * @param category The category of which subcategories have to be fetched.
     * @return List of fetched subcategories, list is empty if error occurs.
     */
    public List<CategoryDTO> fetchSubCategories(CategoryDTO category) {
        logger.log(Level.FINE, "Fetching sub-categories.");

        List<CategoryDTO> categories;
        try (Transaction transaction = TransactionFactory.produce()) {
            categories = DAOFactory.getCategoryDAO(transaction).fetchSubCategories(category);
            transaction.commit();
        }
        return categories;
    }

    /**
     * Transfers all categories from one category to another given category.
     *
     * @param transferFrom The category where the ads get transferred from.
     * @param transferTo   The category where the ads get transferred to.
     */
    public void transferAds(CategoryDTO transferFrom, CategoryDTO transferTo) {
        logger.fine("Transferring ads from category: " + transferFrom.getId() + " to category: " + transferTo.getId());
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getCategoryDAO(transaction).transferAds(transferFrom, transferTo);
            transaction.commit();
        }
    }

}
