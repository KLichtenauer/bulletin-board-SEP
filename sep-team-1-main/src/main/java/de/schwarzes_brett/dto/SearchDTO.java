package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the data of a search.
 *
 * @author Valentin Damjantschitsch.
 */
public class SearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The searchTerm to be used for the search.
     */
    private String searchTerm;

    /**
     * The locationSearch to be used for the search.
     */
    private String locationSearch;


    /**
     * Default constructor.
     */
    public SearchDTO() {
    }

    /**
     * Getter for the term of a search.
     *
     * @return The term to be searched for.
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * Setter for the term of a search.
     *
     * @param searchTerm The term to be set.
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Getter for the location of a search.
     *
     * @return The location to be searched for.
     */
    public String getLocationSearch() {
        return locationSearch;
    }

    /**
     * Setter for the location of a search.
     *
     * @param locationSearch The location to be set.
     */
    public void setLocationSearch(String locationSearch) {
        this.locationSearch = locationSearch;
    }
}
