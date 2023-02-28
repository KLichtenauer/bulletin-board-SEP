package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.CategoryDoesNotExistException;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.exception.DataStorageUnavailableException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls the PostgreSQL database access for a category.
 *
 * @author Kilian Lichtenauer
 */
public class CategoryDAOPsql extends BaseDAOPsql implements CategoryDAO {

    private final Logger logger = LoggerProducer.get(CategoryDAOPsql.class);

    /**
     * Creates dao for getting categories via given transaction.
     *
     * @param transaction The transaction for database access.
     */
    public CategoryDAOPsql(TransactionPsql transaction) {
        super(transaction);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("checkstyle:magicnumber")
    @Override
    public void insertCategory(CategoryDTO category) {
        logger.fine("Inserting new Category");
        PreparedStatement insertCategoryStatement;
        try {
            insertCategoryStatement = getTransaction().getConnection()
                                                      .prepareStatement("INSERT INTO schwarzes_brett.category(name, description, parent_id,"
                                                                        + " child_counter) VALUES( ?, ?, ?, 0)");
            insertCategoryStatement.setString(1, category.getName());
            insertCategoryStatement.setString(2, category.getDescription());
            insertCategoryStatement.setInt(3, category.getParentID());
        } catch (SQLException e) {
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            insertCategoryStatement.executeUpdate();
            insertCategoryStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("checkstyle:magicnumber")
    @Override
    public void updateCategory(CategoryDTO category, CategoryDTO categoryForShifting) {
        logger.fine("Updating category with id: " + category.getId());
        checkForExistenceOfCategory(category);
        PreparedStatement updateCategoryStatement;
        try {
            updateCategoryStatement = getTransaction().getConnection()
                                                      .prepareStatement("UPDATE schwarzes_brett.category SET name = ?, description = ? WHERE "
                                                                        + "id = ?");
            updateCategoryStatement.setString(1, category.getName());
            updateCategoryStatement.setString(2, category.getDescription());
            updateCategoryStatement.setInt(3, category.getId());

            if (categoryForShifting != null) {
                assignNewParent(category, categoryForShifting);
            }

        } catch (SQLException e) {
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            updateCategoryStatement.executeUpdate();
            updateCategoryStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
    }

    private void assignNewParent(CategoryDTO category, CategoryDTO categoryForShifting) {
        logger.finest("Moving category to the new parent.");
        PreparedStatement moveCategoryStatement;
        try {
            moveCategoryStatement = getTransaction().getConnection()
                                                    .prepareStatement("UPDATE schwarzes_brett.category SET parent_id = ? "
                                                                      + "WHERE id = ?");
            moveCategoryStatement.setInt(1, categoryForShifting.getId());
            moveCategoryStatement.setInt(2, category.getId());
        } catch (SQLException e) {
            logger.severe("getConnection not possible.");
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            moveCategoryStatement.executeUpdate();
            moveCategoryStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCategory(CategoryDTO categoryToBeDeleted, CategoryDTO categoryToBeShiftedTo) {
        logger.log(Level.FINE, "Category with id = " + categoryToBeDeleted.getId() + " gets deleted.");
        checkForExistenceOfCategory(categoryToBeDeleted);
        if (categoryToBeShiftedTo != null) {
            logger.finest("Moving ads to chosen category: " + categoryToBeShiftedTo.getName());
            checkForExistenceOfCategory(categoryToBeShiftedTo);
            moveAdsFromCategory(categoryToBeDeleted, categoryToBeShiftedTo);
            moveCategories(categoryToBeDeleted, categoryToBeShiftedTo);
        }

        PreparedStatement deleteCategoryStatement;
        try {
            deleteCategoryStatement = getTransaction().getConnection()
                                                      .prepareStatement("DELETE FROM schwarzes_brett.category WHERE id = ?");
            deleteCategoryStatement.setInt(1, categoryToBeDeleted.getId());
        } catch (SQLException e) {

            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            deleteCategoryStatement.executeUpdate();
            deleteCategoryStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
    }

    private void checkForExistenceOfCategory(CategoryDTO categoryToExist) {
        logger.finest("Checking for existing the existence of the category with which an operation should get performed.");
        try {
            try (PreparedStatement getCategoryStatement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.category WHERE id = ? ORDER BY name;")) {
                getCategoryStatement.setInt(1, categoryToExist.getId());
                if (evaluateResultSet(getCategoryStatement).size() == 0) {
                    logger.severe("Category was not found.");
                    throw new CategoryDoesNotExistException("Category was not found.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection to database got aborted.");
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
    }

    private void moveCategories(CategoryDTO categoryToBeDeleted, CategoryDTO categoryToBeShiftedTo) {
        logger.finest("Moving categories to the parent of the category to be deleted.");
        PreparedStatement moveChildrenStatement;
        try {
            moveChildrenStatement = getTransaction().getConnection()
                                                    .prepareStatement("UPDATE schwarzes_brett.category SET parent_id = ? "
                                                                      + "WHERE parent_id = ?");
            moveChildrenStatement.setInt(1, categoryToBeShiftedTo.getId());
            moveChildrenStatement.setInt(2, categoryToBeDeleted.getId());
        } catch (SQLException e) {
            logger.severe("getConnection not possible.");
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            moveChildrenStatement.executeUpdate();
            moveChildrenStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
    }

    private void moveAdsFromCategory(CategoryDTO categoryToBeDeleted, CategoryDTO categoryToBeShiftedTo) {
        logger.finest("Moving ads from category with id: " + categoryToBeDeleted.getId() + " to category: " + categoryToBeShiftedTo.getId());
        PreparedStatement moveAdsStatement;
        try {
            moveAdsStatement = getTransaction().getConnection()
                                               .prepareStatement("UPDATE schwarzes_brett.ad SET category = ? WHERE category = ?");
            moveAdsStatement.setInt(1, categoryToBeShiftedTo.getId());
            moveAdsStatement.setInt(2, categoryToBeDeleted.getId());
        } catch (SQLException e) {
            logger.severe("getConnection not possible.");
            throw new DataStorageUnavailableException(e.getMessage(), e);
        }
        try {
            moveAdsStatement.executeUpdate();
            moveAdsStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> fetchRootCategories() {
        logger.fine("Root-categories get fetched.");
        try {
            try (PreparedStatement getRootStatement = getTransaction().getConnection()
                                                                      .prepareStatement("SELECT * FROM schwarzes_brett.category WHERE id = 0")) {
                if (evaluateResultSet(getRootStatement).size() == 0) {
                    logger.severe("Category was not found.");
                    throw new CategoryDoesNotExistException("Category was not found.");
                }
            }
            try (PreparedStatement getRootsChildrenStatement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.category WHERE parent_id = 0 ORDER BY name")) {
                return evaluateResultSet(getRootsChildrenStatement);
            }
        } catch (SQLException e) {
            logger.severe("Access to database not possible.");
            throw new DataStorageAccessException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDTO> fetchSubCategories(CategoryDTO category) {
        logger.log(Level.FINE, "Sub-categories get fetched.");
        try {
            try (PreparedStatement getCategoryStatement = getTransaction().getConnection()
                                                                          .prepareStatement("SELECT * FROM schwarzes_brett.category WHERE id = ?")) {
                getCategoryStatement.setInt(1, category.getId());
                if (evaluateResultSet(getCategoryStatement).size() == 0) {
                    logger.severe("Category was not found.");
                    throw new CategoryDoesNotExistException("Category was not found.");
                }
            }
            try (PreparedStatement getCategoriesStatement = getTransaction().getConnection().prepareStatement(
                    "SELECT * FROM schwarzes_brett.category WHERE parent_id = ? ORDER BY name")) {
                getCategoriesStatement.setInt(1, category.getId());
                return evaluateResultSet(getCategoriesStatement);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Access to database not possible.");
            throw new DataStorageAccessException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void transferAds(CategoryDTO transferFrom, CategoryDTO transferTo) {
        logger.fine("Transferring ads.");
        checkForExistenceOfCategory(transferFrom);
        checkForExistenceOfCategory(transferTo);
        PreparedStatement transferAdsStatement;
        try {
            transferAdsStatement = getTransaction().getConnection().prepareStatement("UPDATE schwarzes_brett.ad SET category = ?"
                                                                                     + " WHERE category = ?");
            transferAdsStatement.setInt(1, transferTo.getId());
            transferAdsStatement.setInt(2, transferTo.getId());
        } catch (SQLException e) {
            logger.severe("getConnection not possible.");
            throw new DataStorageUnavailableException(e.getMessage());
        }

        try {
            transferAdsStatement.executeUpdate();
            transferAdsStatement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage());
        }
    }

    private List<CategoryDTO> evaluateResultSet(PreparedStatement statement) {
        logger.log(Level.FINE, "Result gets evaluated.");
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        try (ResultSet resultSetCategories = statement.executeQuery()) {
            while (resultSetCategories.next()) {
                categoryDTOList.add(new CategoryDTO(resultSetCategories.getInt("id"),
                                                    resultSetCategories.getString("name"),
                                                    resultSetCategories.getString("description"),
                                                    resultSetCategories.getInt("parent_id"),
                                                    resultSetCategories.getInt("child_counter")));
            }
            resultSetCategories.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL-Exception got triggered, while searching for Categories in database.");
            throw new DataStorageAccessException(e.getMessage());
        }
        return categoryDTOList;
    }

}
