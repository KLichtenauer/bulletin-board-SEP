package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.CategoryDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * Backing Bean for the categorisation page of the administrator. Categories can be edited, deleted or created.
 *
 * @author Kilian Lichtenauer
 */
@Named
@RequestScoped
public class CategoryAdministrationBean implements NotificationDisplay {

    @Inject
    private ExternalContext externalContext;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public CategoryAdministrationBean() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
    }

    /**
     * Gets called when an admin wants to create a category.
     *
     * @return The facelet for creating a category.
     */
    public String createCategory() {
        logger.fine("User chose to create new category.");
        externalContext.getFlash().put("category", new CategoryDTO());
        externalContext.getFlash().put("edit", false);
        return "/view/admin/editCategory?faces-redirect=true";
    }

    /**
     * Gets called when a user wants to edit a category.
     *
     * @param category The unique name of the category.
     * @return The facelet for editing categories.
     */
    public String onCategoryClick(CategoryDTO category) {
        logger.fine("Category gets edited.");
        externalContext.getFlash().put("category", category);
        externalContext.getFlash().put("edit", true);
        return "/view/admin/editCategory?faces-redirect=true";
    }
}
