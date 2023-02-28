package de.schwarzes_brett.dto;

/**
 * Contains the different types of user roles.
 *
 * @author Valentin Damjantschitsch.
 */
public enum Role {
    /**
     * Role of the user which is not registered.
     */
    ANONYMOUS,

    /**
     * Role of the user which is registered.
     */
    USER,

    /**
     * Role of an admin.
     */
    ADMIN

}
