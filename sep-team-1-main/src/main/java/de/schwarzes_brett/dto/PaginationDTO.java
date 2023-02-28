package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the data for pagination.
 *
 * @author Valentin Damjantschitsch.
 */
public class PaginationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The pageNumber of the pagination.
     */
    private int pageNumber = 1;

    /**
     * The lastPageNumber of the pagination.
     */
    private int lastPageNumber;

    /**
     * The itemsPerPage of the pagination.
     */
    private int itemsPerPage = Limits.ITEMS_PER_PAGE;

    /**
     * The sortBy of the pagination.
     */
    private String sortBy;

    /**
     * The sortAscending of the pagination.
     */
    private boolean sortAscending = true;

    /**
     * The showExpiredAds of the pagination.
     */
    private boolean showExpiredAds = false;

    /**
     * The search for the pagination.
     */
    private SearchDTO search = new SearchDTO();

    /**
     * The category of the pagination.
     */
    private CategoryDTO category = new CategoryDTO();

    /**
     * Default constructor.
     */
    public PaginationDTO() {
    }

    /**
     * Getter for the pageNumber of the pagination.
     *
     * @return The pageNumber of the pagination.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Setter for the pageNumber of the pagination.
     *
     * @param pageNumber The pageNumber to be set.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Getter for the last page number of the pagination.
     *
     * @return The last page number of the pagination.
     */
    public int getLastPageNumber() {
        return lastPageNumber;
    }

    /**
     * Setter for the lastPageNumber of the pagination.
     *
     * @param lastPageNumber The lastPageNumber to be set.
     */
    public void setLastPageNumber(int lastPageNumber) {
        this.lastPageNumber = lastPageNumber;
    }

    /**
     * Getter for the amount of items per page of the pagination.
     *
     * @return The amount of items per page of the pagination.
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * Setter for the items of a page of the pagination.
     *
     * @param itemsPerPage The items per page to be set.
     */
    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     * Getter for the name of column the items shall be sorted by.
     *
     * @return The name of column the items shall be sorted by.
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Setter for the column for sorting.
     *
     * @param sortBy The name of column for sorting.
     */
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * Get if sort is ascending.
     *
     * @return True of it's sorted ascending.
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets if sorting is ascending or not.
     *
     * @param sortAscending True for ascending order.
     */
    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    /**
     * Get if expired ads are shown or not.
     *
     * @return True if expired ads are shown.
     */
    public boolean isShowExpiredAds() {
        return showExpiredAds;
    }

    /**
     * Sets if expired ads are shown or not.
     *
     * @param showExpiredAds True to show expired ads.
     */
    public void setShowExpiredAds(boolean showExpiredAds) {
        this.showExpiredAds = showExpiredAds;
    }

    /**
     * Returns the search of the pagination.
     *
     * @return The search of the pagination.
     */
    public SearchDTO getSearch() {
        return search;
    }

    /**
     * Sets the search of the pagination.
     *
     * @param search The search for the pagination.
     */
    public void setSearch(SearchDTO search) {
        this.search = search;
    }

    /**
     * Returns the category of the pagination.
     *
     * @return The category of the pagination.
     */
    public CategoryDTO getCategory() {
        return category;
    }

    /**
     * Sets the category for the pagination.
     *
     * @param category The category for the pagination.
     */
    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}
