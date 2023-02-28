package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the credentials of a user.
 *
 * @author Valentin Damjantschitsch.
 */
public class CredentialsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The username of the credentials.
     */
    private String username;

    /**
     * The password of the credentials.
     */
    private PasswordDTO password = new PasswordDTO();


    /**
     * Default constructor.
     */
    public CredentialsDTO() {
    }

    /**
     * Copy constructor.
     *
     * @param toCopy The {@code CredentialsDTO} to copy.
     */
    public CredentialsDTO(CredentialsDTO toCopy) {
        this.username = toCopy.username;
        this.password = new PasswordDTO(toCopy.password);
    }

    /**
     * Getter for the username of the credentials.
     *
     * @return The username of the credentials.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username of the credentials.
     *
     * @param username The username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for the password of the credentials.
     *
     * @return The password of the credentials.
     */
    public PasswordDTO getPassword() {
        return password;
    }

    /**
     * Setter for the password of the credentials.
     *
     * @param password The password to be set.
     */
    public void setPassword(PasswordDTO password) {
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CredentialsDTO that = (CredentialsDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

}
