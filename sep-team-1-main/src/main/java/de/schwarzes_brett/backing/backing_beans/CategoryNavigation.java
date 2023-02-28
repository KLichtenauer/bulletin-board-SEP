package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.CategoryDTO;

/**
 * Classes with this implementation use the category navigation and thus need to implement on category click for correct behavior control if a
 * category was clicked.
 *
 * @author Kilian Lichtenauer
 */
public interface CategoryNavigation {

    /**
     * Gets called when a user clicks on a category at the category navigation tree. Can have multiple different usages depending on the bean in which
     * it is used.
     *
     * @param category The unique name of the category.
     * @return The facelet for editing categories.
     */
    String onCategoryClick(CategoryDTO category);
}

