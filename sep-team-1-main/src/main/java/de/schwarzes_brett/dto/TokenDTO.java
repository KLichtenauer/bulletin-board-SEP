package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the data of a token.
 *
 * @author Valentin Damjantschitsch.
 */
public class TokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The token value.
     */
    private String token;

    /**
     * The username of the user to be verified.
     */
    private String username;


    /**
     * Default constructor.
     */
    public TokenDTO() {
    }

    /**
     * Getter for the token's value.
     *
     * @return The token's value.
     */
    public String getToken() {
        return token;
    }

    /**
     * Setter for the token's value.
     *
     * @param token The token's value to be set.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Getter for the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username.
     *
     * @param username The username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
