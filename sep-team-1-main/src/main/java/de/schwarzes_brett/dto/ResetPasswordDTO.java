package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

/**
 * Contains the data of a password to be reset.
 *
 * @author Valentin Damjantschitsch.
 */
public class ResetPasswordDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The email to reset the password.
     */
    private String email;

    /**
     * Language of the user which should be used in emails.
     */
    private Locale language;

    /**
     * Username of the user who wants to reset his password.
     */
    private String username;


    /**
     * Default constructor.
     */
    public ResetPasswordDTO() {
    }

    /**
     * Getter for the email of the user who wants to reset the password.
     *
     * @return The email of the user who wants to reset the password.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the email of the user who wants to reset the password.
     *
     * @param email The email to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for the language of this reset password request.
     *
     * @return Language of this reset password request
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Sets the language of this request.
     *
     * @param language Language of this request.
     */
    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * Getter for the username of the user who wants to reset his password.
     *
     * @return Username of the user who wants to reset his password
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username of the user who wants to reset his password.
     *
     * @param username Username of the user who wants to reset his password
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
