package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the application settings of the system.
 *
 * @author Valentin Damjantschitsch.
 */
public class ApplicationSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The name of the application.
     */
    private String name;

    /**
     * The logo stored in the settings.
     */
    private ImageDTO logo;

    /**
     * The description stored in the settings.
     */
    private String description;

    /**
     * The contact stored in the settings.
     */
    private String contact;

    /**
     * The privacyPolicy stored in the settings.
     */
    private String privacyPolicy;

    /**
     * The imprint stored in the settings.
     */
    private String imprint;

    /**
     * The styleSheet stored in the settings.
     */
    private String styleSheet;


    /**
     * Default constructor.
     */
    public ApplicationSettingsDTO() {
    }

    /**
     * Getter for the name of the application.
     *
     * @return The name of the application.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the application.
     *
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the logo of the application.
     *
     * @return The logo of the application.
     */
    public ImageDTO getLogo() {
        return logo;
    }

    /**
     * Setter for the logo of the application.
     *
     * @param logo The logo to be set.
     */
    public void setLogo(ImageDTO logo) {
        this.logo = logo;
    }

    /**
     * Getter for the description of the application.
     *
     * @return The description of the application.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description of the application.
     *
     * @param description The description to be set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the contact information of the application's owner.
     *
     * @return The contact information of the application's owner.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Setter for the contact of the application.
     *
     * @param contact The contact to be set.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Getter for the privacy policy of the application.
     *
     * @return The privacy policy of the application.
     */
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    /**
     * Setter for the privacyPolicy of the application.
     *
     * @param privacyPolicy The privacyPolicy to be set.
     */
    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    /**
     * Getter for the imprint of the application.
     *
     * @return The imprint of the application.
     */
    public String getImprint() {
        return imprint;
    }

    /**
     * Setter for the imprint of the application.
     *
     * @param imprint The imprint to be set.
     */
    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    /**
     * Getter for a stylesheet.
     *
     * @return A stylesheet.
     */
    public String getStyleSheet() {
        return styleSheet;
    }

    /**
     * Setter for the styleSheet of the application.
     *
     * @param styleSheet The styleSheet to be set.
     */
    public void setStyleSheet(String styleSheet) {
        this.styleSheet = styleSheet;
    }

    /**
     * Copys all values from the other object to this object.
     *
     * @param other The Object from which all values are copied.
     * @author Daniel Lipp
     */
    public void copyFrom(ApplicationSettingsDTO other) {
        name = other.name;
        logo = new ImageDTO(other.logo);
        description = other.description;
        contact = other.contact;
        imprint = other.imprint;
        privacyPolicy = other.privacyPolicy;
        styleSheet = other.styleSheet;
    }
}
