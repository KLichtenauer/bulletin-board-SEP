package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/**
 * Contains the data of a user.
 *
 * @author Valentin Damjantschitsch.
 */
public class UserDTO implements Serializable {

    /**
     * The id of the default admin user.
     */
    public static final int DEFAULT_ADMIN_ID = 1;
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The id of the user.
     */
    private Integer id;

    /**
     * The credentials of the user.
     */
    private CredentialsDTO credentials = new CredentialsDTO();

    /**
     * The firstName of the user.
     */
    private String firstName;

    /**
     * The lastName of the user.
     */
    private String lastName;

    /**
     * The addressAddition of the user.
     */
    private String addressAddition;

    /**
     * The street of the user.
     */
    private String street;

    /**
     * The streetNumber of the user.
     */
    private String streetNumber;

    /**
     * The postalCode of the user.
     */
    private String postalCode;

    /**
     * The city of the user.
     */
    private String city;

    /**
     * The country of the user.
     */
    private String country;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The phone of the user.
     */
    private String phone;

    /**
     * The verification status of the user.
     */
    private VerificationStatus verificationStatus;

    /**
     * The avatar of the user.
     */
    private ImageDTO avatar = new ImageDTO();

    /**
     * The role of the user.
     */
    private Role role;

    /**
     * The language of the user.
     */
    private Locale language;

    /**
     * Rating of the user.
     */
    private BigDecimal rating;

    /**
     * Identifier of the contact Information.
     */
    private Long contactInfoId;

    /**
     * Default constructor.
     */
    public UserDTO() {
    }

    /**
     * Copy constructor.
     *
     * @param toCopy The {@code UserDTO} to copy.
     */
    public UserDTO(UserDTO toCopy) {
        copyFrom(toCopy);
    }

    /**
     * Getter for the id of the user.
     *
     * @return The id of the user.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for the id of the user.
     *
     * @param id The id to be set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter of the rating of a user.
     *
     * @return rating of a user represented as int.
     */
    public BigDecimal getRating() {
        return rating;
    }

    /**
     * Setter of the rating of a user.
     *
     * @param rating The new rating of the user.
     */
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    /**
     * Getter for the credentials of the user.
     *
     * @return The credentials of the user.
     */
    public CredentialsDTO getCredentials() {
        return credentials;
    }

    /**
     * Setter for the credentials of the user.
     *
     * @param credentials The credentials to be set.
     */
    public void setCredentials(CredentialsDTO credentials) {
        this.credentials = credentials;
    }

    /**
     * Getter for the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for the first name of the user.
     *
     * @param firstName The first name to be set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for the last name of the user.
     *
     * @param lastName The last name to be set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for the address addition of the user.
     *
     * @return The address addition of the user.
     */
    public String getAddressAddition() {
        return addressAddition;
    }

    /**
     * Setter for the address addition of the user.
     *
     * @param addressAddition The address addition to be set.
     */
    public void setAddressAddition(String addressAddition) {
        this.addressAddition = addressAddition;
    }

    /**
     * Getter for the street of the user.
     *
     * @return The street of the user.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Setter for the street of the user.
     *
     * @param street The street to be set.
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Getter for the street number of the user.
     *
     * @return The street number of the user.
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * Setter for the streetNumber of the user.
     *
     * @param streetNumber The streetNumber to be set.
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Getter for the postal code of the user.
     *
     * @return The postal code of the user.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Setter for the postal code of the user.
     *
     * @param postalCode The postal code to be set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Getter for the city of the user.
     *
     * @return The city of the user.
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for the city of the user.
     *
     * @param city The city to be set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter for the country of the user.
     *
     * @return The country of the user.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for the country of the user.
     *
     * @param country The country to be set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Getter for the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for the email of the user.
     *
     * @param email The email to be set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for the phone number of the user.
     *
     * @return The phone number of the user.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Setter for the phone number of the user.
     *
     * @param phone The phone number to be set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Getter for the verification status of the user.
     *
     * @return The verification status of the user.
     */
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    /**
     * Sets the verification status of the user.
     *
     * @param verificationStatus The verification status to be set.
     */
    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    /**
     * Getter for the avatar of the user.
     *
     * @return The avatar of the user.
     */
    public ImageDTO getAvatar() {
        return avatar;
    }

    /**
     * Setter for the avatar of the user.
     *
     * @param avatar The avatar to be set.
     */
    public void setAvatar(ImageDTO avatar) {
        this.avatar = avatar;
    }

    /**
     * Getter for the role of the user.
     *
     * @return The role of the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Setter for the role of the user.
     *
     * @param role The role to be set.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Getter for the language of the user.
     *
     * @return The language of the user.
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Setter for the language of the user.
     *
     * @param language The language of the user.
     */
    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * Getter for the contact info id of the user.
     *
     * @return Identification number of the contact info.
     */
    public Long getContactInfoId() {
        return contactInfoId;
    }

    /**
     * Setter for the contact info id of the user.
     *
     * @param contactInfoId The contact info id of the user.
     */
    public void setContactInfoId(Long contactInfoId) {
        this.contactInfoId = contactInfoId;
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
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(rating, userDTO.rating)
               && Objects.equals(id, userDTO.id)
               && Objects.equals(credentials, userDTO.credentials)
               && Objects.equals(firstName, userDTO.firstName)
               && Objects.equals(lastName, userDTO.lastName)
               && Objects.equals(addressAddition,
                                 userDTO.addressAddition)
               && Objects.equals(street, userDTO.street)
               && Objects.equals(streetNumber, userDTO.streetNumber)
               && Objects.equals(postalCode, userDTO.postalCode)
               && Objects.equals(city, userDTO.city)
               && Objects.equals(country, userDTO.country)
               && Objects.equals(email, userDTO.email)
               && Objects.equals(phone, userDTO.phone)
               && verificationStatus == userDTO.verificationStatus
               && Objects.equals(avatar, userDTO.avatar)
               && role == userDTO.role && Objects.equals(language,
                                                         userDTO.language)
               && Objects.equals(contactInfoId, userDTO.contactInfoId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, credentials, firstName, lastName, addressAddition,
                            street, streetNumber, postalCode, city, country, email,
                            phone, verificationStatus, avatar, role, language,
                            rating, contactInfoId);
    }

    /**
     * Copies all fields form the other user to this object.
     *
     * @param other User to be copied from
     * @author Daniel Lipp
     */
    public void copyFrom(UserDTO other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.addressAddition = other.addressAddition;
        this.street = other.street;
        this.streetNumber = other.streetNumber;
        this.postalCode = other.postalCode;
        this.city = other.city;
        this.country = other.country;
        this.email = other.email;
        this.phone = other.phone;
        this.verificationStatus = other.verificationStatus;
        this.role = other.role;
        this.language = other.language;
        this.rating = other.rating;
        this.contactInfoId = other.contactInfoId;

        this.credentials = new CredentialsDTO(other.credentials);
        this.avatar = new ImageDTO(other.avatar);

    }


    /**
     * An enum to specify the verification status of a user.
     */
    public enum VerificationStatus {

        /**
         * The user is verified.
         */
        VERIFIED,

        /**
         * The user is registered but not verified.
         */
        REGISTERED_NOT_VERIFIED,

        /**
         * The user is not registered.
         */
        NOT_REGISTERED
    }

}
