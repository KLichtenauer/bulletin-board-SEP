package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.PaginationDTO;
import jakarta.inject.Inject;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Backing Bean for the pagination of a list. Is used in any case of listing objects. Functionality can be dependent of the user's role.
 *
 * @author michaelgruner
 */
public abstract class PaginationBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The current pagination information.
     */
    private PaginationDTO pagination;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public PaginationBean() {
    }

    /**
     * Getter for the specific pagination which should be shown.
     *
     * @return An instance of {@code PaginationDTO}.
     */
    public PaginationDTO getPagination() {
        return pagination;
    }

    /**
     * Setter for the pagination.
     *
     * @param pagination An instance of {@code PaginationDTO}.
     */
    public void setPagination(PaginationDTO pagination) {
        this.pagination = pagination;
    }

    /**
     * Gets called when the user wants to get on the next page.
     *
     * @return The corresponding facelet for the next pagination page.
     */
    public String goToNextPage() {
        pagination.setPageNumber(pagination.getPageNumber() + 1);
        return reload();
    }

    /**
     * Gets called when the user wants to get on the previous page.
     *
     * @return The corresponding facelet for the previous pagination page.
     */
    public String goToPrevPage() {
        pagination.setPageNumber(pagination.getPageNumber() - 1);
        return reload();
    }

    /**
     * Gets called when the user wants to get on the first page.
     *
     * @return The corresponding facelet for the first pagination page.
     */
    public String goToFirstPage() {
        pagination.setPageNumber(1);
        return reload();
    }

    /**
     * Gets called when the user wants to get on the last page.
     *
     * @return The corresponding facelet for the last pagination page.
     */
    public String goToLastPage() {
        pagination.setPageNumber(pagination.getLastPageNumber());
        return reload();
    }

    /**
     * Gets called when the user wants to get on a selected page.
     *
     * @return The corresponding facelet for a selected pagination page.
     */
    public String goToSelectedPage() {
        return reload();
    }

    /**
     * Gets called when the user wants to sort the list.
     *
     * @param sortBy The value to sort by.
     * @return The corresponding facelet for the sorted pagination list.
     */
    public String sort(String sortBy) {
        logger.finest("Sorting by " + sortBy);
        if (pagination.getSortBy().equals(sortBy)) {
            logger.finest("Toggle direction ascending " + pagination.isSortAscending());
            pagination.setSortAscending(!pagination.isSortAscending());
        }
        pagination.setSortBy(sortBy);
        return reload();
    }

    /**
     * Returns if previous button is enabled.
     *
     * @return If previous button is enabled.
     */
    public boolean prevButtonEnabled() {
        return pagination.getPageNumber() <= 1;
    }

    /**
     * Returns if next button is enabled.
     *
     * @return If next button is enabled.
     */
    public boolean nextButtonEnabled() {
        return pagination.getPageNumber() >= pagination.getLastPageNumber();
    }

    /**
     * Returns the title of the header with the sort direction if the sortableHeader is sorted by.
     *
     * @param title  The title of the header.
     * @param sortBy The sortBy attribute of the header.
     * @return The title for the header.
     */
    public String getTitleForHeader(String title, String sortBy) {
        if (pagination.getSortBy().equals(sortBy)) {
            return title + getSortDirection();
        } else {
            return title;
        }
    }

    /**
     * Returns a symbol for the sort direction dependent of the selection in the pagination.
     *
     * @return A symbol for the sort direction.
     */
    private String getSortDirection() {
        if (pagination.isSortAscending()) {
            return "↑";
        } else {
            return "↓";
        }
    }

    /**
     * Gets called when the admin wants to search the list.
     *
     * @return The corresponding facelet for searching a list.
     */
    public String search() {
        logger.finest("Searching for: " + getPagination().getSearch().getSearchTerm());
        return reload();
    }

    /**
     * Reloads the bean.
     *
     * @return The corresponding facelet of the bean.
     */
    public abstract String reload();

    /**
     * The initialisation of the backingBean.
     * Must set the property sortBy in the pagination. Otherwise, an IllegalStateException will be thrown.
     */
    public abstract void init();
}
