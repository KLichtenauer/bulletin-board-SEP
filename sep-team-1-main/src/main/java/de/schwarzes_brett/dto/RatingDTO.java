package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the data of a rating.
 *
 * @author Valentin Damjantschitsch.
 */
public class RatingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The rater.
     */
    private UserDTO rater;

    /**
     * The rated user.
     */
    private UserDTO rated;

    /**
     * The rating of the rating.
     */
    private Integer rating;


    /**
     * Default constructor.
     */
    public RatingDTO() {
    }

    /**
     * Getter for the username of a rater.
     *
     * @return The username of a rater.
     */
    public UserDTO getRater() {
        return rater;
    }

    /**
     * Setter for the username of the rater.
     *
     * @param rater The username to be set.
     */
    public void setRater(UserDTO rater) {
        this.rater = rater;
    }

    /**
     * Getter for the username of the rated user.
     *
     * @return The username of the rated user.
     */
    public UserDTO getRated() {
        return rated;
    }

    /**
     * Setter for the username of the rated user.
     *
     * @param rated The username to be set.
     */
    public void setRated(UserDTO rated) {
        this.rated = rated;
    }

    /**
     * Getter for the value of the rating.
     *
     * @return The value of the rating.
     */
    public Integer getRating() {
        return rating;
    }

    /**
     * Setter for the value of the rating.
     *
     * @param rating The value to be set.
     */
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
