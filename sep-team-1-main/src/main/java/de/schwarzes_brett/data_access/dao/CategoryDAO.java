package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.CategoryDTO;

import java.util.List;

/**
 * Controls the database access for a category. The access happens through prepared statements and filling DTOs which get iterated through the layers.
 */
public interface CategoryDAO {

    /**
     * Inserts new category in database.
     *
     * @param category Contains all information about the category to be inserted.
     */
    void insertCategory(CategoryDTO category);

    /**
     * Updates category in database.
     *
     * @param category            Contains all information about the category to be updated.
     * @param categoryForShifting Category which is chosen for being the new parent category of {@code category}.
     */
    void updateCategory(CategoryDTO category, CategoryDTO categoryForShifting);

    /**
     * Deletes category in database.
     *
     * @param categoryToBeDeleted   Contains all information about the category to be deleted.
     * @param categoryToBeShiftedTo The category to which ads should be shifted to. Can be null if ads should get deleted.
     */
    void deleteCategory(CategoryDTO categoryToBeDeleted, CategoryDTO categoryToBeShiftedTo);

    /**
     * Fetches all categories from database.
     *
     * @return A list containing all categories wrapped in {@code CategoryDTO}'s.
     */
    List<CategoryDTO> fetchRootCategories();

    /**
     * Fetches all subcategories of a given category from database.
     *
     * @param category The category of which subcategories have to be fetched.
     * @return List of fetched subcategories.
     */
    List<CategoryDTO> fetchSubCategories(CategoryDTO category);

    /**
     * Transfers all categories from one category to another given category from database.
     *
     * @param transferFrom The category where the ads get transferred from.
     * @param transferTo   The category where the ads get transferred to.
     */
    void transferAds(CategoryDTO transferFrom, CategoryDTO transferTo);

}
