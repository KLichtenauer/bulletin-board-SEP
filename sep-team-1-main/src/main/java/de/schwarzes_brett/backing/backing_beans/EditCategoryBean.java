package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.CategoryService;
import de.schwarzes_brett.dto.CategoryDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Backing Bean for creating, deleting and editing a category by the admin.
 *
 * @author Kilian Lichtenauer
 */
@Named
@ViewScoped
public class EditCategoryBean implements NotificationDisplay, Serializable, CategoryNavigation {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int ID_OF_ROOT_CATEGORY = 0;

    /**
     * The corresponding service-class.
     */
    @Inject
    private CategoryService categoryService;
    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    @Inject
    private transient Dictionary dictionary;


    /**
     * The category to edit which got selected at the category administration.
     */
    @Inject
    @ManagedProperty("#{flash.category}")
    private CategoryDTO categoryToBeEdited;

    /**
     * The flag if category is getting edited or a new category is getting created.
     */
    @Inject
    @ManagedProperty("#{flash.edit}")
    private boolean categoryGettingEdited;

    /**
     * The category which got selected from the category-navigation-tree.
     */
    private CategoryDTO fromTreeSelectedCategory;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public EditCategoryBean() {
    }

    /**
     * Getter for the category which should be edited.
     *
     * @return An instance of {@code CategoryDTO}.
     */
    public CategoryDTO getCategoryToBeEdited() {
        return categoryToBeEdited;
    }

    /**
     * Setter for the new values for the category which gets edited.
     *
     * @param categoryToBeEdited An instance of {@code CategoryDTO} containing the new values.
     */
    public void setCategoryToBeEdited(CategoryDTO categoryToBeEdited) {
        this.categoryToBeEdited = categoryToBeEdited;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.info("EditCategoryBean gets initialized.");
        if (categoryToBeEdited == null) {
            categoryToBeEdited = new CategoryDTO();
        }
    }

    /**
     * Gets called when the saved button gets pressed.
     *
     * @return Returns the page which should get forwarded to. For the ability to further edit or create categories the user gets redirected to the
     * category administration page.
     */
    public String save() {
        logger.info("Changes to edited category get saved.");
        if (categoryGettingEdited) {
            categoryService.updateCategory(categoryToBeEdited, fromTreeSelectedCategory);
        } else {
            if (fromTreeSelectedCategory != null) {
                categoryToBeEdited.setParentID(fromTreeSelectedCategory.getId());
            } else {
                categoryToBeEdited.setParentID(ID_OF_ROOT_CATEGORY);
            }
            categoryService.insertCategory(categoryToBeEdited);
        }
        String message;
        if (isCategoryGettingEdited()) {
            message = dictionary.get("f_editCategory_successful_changed");
        } else {
            message = dictionary.get("f_editCategory_successful_created");
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
        return "/view/admin/categoryAdministration.xhtml";
    }

    /**
     * Gets called when the admin wants to delete the category.
     *
     * @return The corresponding facelet for deleting a category.
     */
    public String delete() {
        logger.info("Category gets deleted.");
        categoryService.deleteCategory(categoryToBeEdited, fromTreeSelectedCategory);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(dictionary.get("f_editCategory_successful_deleted")));
        return "categoryAdministration";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        notificationHelper.generateFacesMessage(NotificationContext.CATEGORY_NAME, null);
    }

    /**
     * Gets performed when a category is clicked and initializes a new category editing.
     *
     * @param category The category which got clicked on.
     * @return Returns the website to which the redirection should go. If "" is returned it will stay on the current site.
     */
    public String onCategoryClick(CategoryDTO category) {
        logger.fine("A category got selected as the parent of the category to create.");
        if (category.equals(categoryToBeEdited)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(dictionary.get("f_error_edit_category")));
        } else {
            fromTreeSelectedCategory = category;
        }
        return "";
    }

    /**
     * Getter for the category which got selected at the tree.
     *
     * @return The tree selected category.
     */
    public CategoryDTO getFromTreeSelectedCategory() {
        return fromTreeSelectedCategory;
    }

    /**
     * Setter for the tree selected category.
     *
     * @param fromTreeSelectedCategory The new category which got selected of the tree.
     */
    public void setFromTreeSelectedCategory(CategoryDTO fromTreeSelectedCategory) {
        this.fromTreeSelectedCategory = fromTreeSelectedCategory;
    }

    /**
     * Returns if category is getting edited or if create category was chosen.
     *
     * @return True if the category is getting edited.
     */
    public boolean isCategoryGettingEdited() {
        return categoryGettingEdited;
    }

    /**
     * Sets the boolean value for telling if a category editing is in process or if a new category gets created.
     *
     * @param categoryGettingEdited The boolean value if a category gets edited.
     */
    public void setCategoryGettingEdited(boolean categoryGettingEdited) {
        this.categoryGettingEdited = categoryGettingEdited;
    }

}
