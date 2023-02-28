package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the data for a password.
 *
 * @author Valentin Damjantschitsch.
 */
public class PasswordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The hash value.
     */
    private String pwdHash;

    /**
     * Salt of password either stored in the data_access layer or newly generated.
     */
    private String pwdSalt;

    /**
     * Default constructor.
     */
    public PasswordDTO() {
    }

    /**
     * Copy constructor.
     *
     * @param toCopy The {@code PasswordDTO} to copy.
     */
    public PasswordDTO(PasswordDTO toCopy) {
        this.pwdHash = toCopy.pwdHash;
        this.pwdSalt = toCopy.pwdSalt;
    }

    /**
     * Getter for the hash value of the password.
     *
     * @return The hash value of the password.
     */
    public String getPasswordHash() {
        return pwdHash;
    }

    /**
     * Setter for the hash value of the password.
     *
     * @param hash The hash value of the password.
     */
    public void setPasswordHash(String hash) {
        this.pwdHash = hash;
    }

    /**
     * Getter for the salt of the password.
     *
     * @return the salt of the password.
     */
    public String getPwdSalt() {
        return pwdSalt;
    }

    /**
     * Sets the Salt.
     *
     * @param pwdSalt Salt for this PasswordDTO.
     */
    public void setPwdSalt(String pwdSalt) {
        this.pwdSalt = pwdSalt;
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
        PasswordDTO that = (PasswordDTO) o;
        return Objects.equals(pwdHash, that.pwdHash) && Objects.equals(pwdSalt, that.pwdSalt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(pwdHash, pwdSalt);
    }

}
