package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Contains data of an ad.
 *
 * @author Valentin Damjantschitsch.
 */
public class AdDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The id of the ad.
     */
    private Integer id;

    /**
     * The title of the ad.
     */
    private String title;

    /**
     * The textual description of the ad.
     */
    private String description;

    /**
     * The category of the ad.
     */
    private CategoryDTO category;

    /**
     * The price of the ad.
     */
    private PriceDTO price;

    /**
     * The release time of the ad.
     */
    private ZonedDateTime release;

    /**
     * The end time of the ad.
     */
    private ZonedDateTime end;

    /**
     * The amount of follower of the ad.
     */
    private int follower;

    /**
     * The images of the ad.
     */
    private List<ImageDTO> images;

    /**
     * The thumbnail of the ad.
     */
    private ImageDTO thumbnail;

    /**
     * The creator of the ad.
     */
    private UserDTO creator;

    /**
     * The messages of the ad.
     */
    private List<MessageDTO> messages;

    /**
     * Country, City and Postcode of the ad.
     */
    private String location;

    /**
     * The public contact data of the ad creator.
     */
    private UserDTO publicData;

    /**
     * Represents if an ad is basis of negotiation or not.
     */
    private boolean isBasisOfNegotiation;

    /**
     * Default constructor.
     */
    public AdDTO() {
    }

    /**
     * Getter for the id of an ad.
     *
     * @return The id of an ad.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for the id of an ad.
     *
     * @param id The id to be set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for the title of an ad.
     *
     * @return The title of an ad.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title of an ad.
     *
     * @param title The title to be set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the description of an ad.
     *
     * @return The description of an ad.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description of an ad.
     *
     * @param description The description to be set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the category of an ad.
     *
     * @return The {@code CategoryDTO} of an ad.
     */
    public CategoryDTO getCategory() {
        return category;
    }

    /**
     * Setter for the category of an ad.
     *
     * @param category The category to be set.
     */
    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    /**
     * Getter for the price of an ad.
     *
     * @return The {@code PriceDTO} of an ad.
     */
    public PriceDTO getPrice() {
        return price;
    }

    /**
     * Setter for the price of an ad.
     *
     * @param price The price to be set.
     */
    public void setPrice(PriceDTO price) {
        this.price = price;
    }

    /**
     * Getter for the release date of an ad.
     *
     * @return The release date of an ad.
     */
    public ZonedDateTime getRelease() {
        return release;
    }

    /**
     * Setter for the release date of an ad.
     *
     * @param release The release date to be set.
     */
    public void setRelease(ZonedDateTime release) {
        this.release = release;
    }

    /**
     * Getter for the end date of an ad.
     *
     * @return The end date of an ad.
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**
     * Setter for the end date of an ad.
     *
     * @param end The end date to be set.
     */
    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    /**
     * Getter for the images of an ad.
     *
     * @return A list of {@code ImageDTO}.
     */
    public List<ImageDTO> getImages() {
        return images;
    }

    /**
     * Setter for the images of an ad.
     *
     * @param images The images to be set.
     */
    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    /**
     * Getter for the thumbnail of an ad.
     *
     * @return The thumbnail {@code ImageDTO} of an ad.
     */
    public ImageDTO getThumbnail() {
        return thumbnail;
    }

    /**
     * Setter for the thumbnail of an ad.
     *
     * @param thumbnail The thumbnail to be set.
     */
    public void setThumbnail(ImageDTO thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Getter for the creator of an ad.
     *
     * @return The {@code UserDTO} who created this ad.
     */
    public UserDTO getCreator() {
        return creator;
    }

    /**
     * Setter for the creator of an ad.
     *
     * @param creator The creator to be set.
     */
    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    /**
     * Getter for the messages of an ad.
     *
     * @return A list of {@code MessageDTO}.
     */
    public List<MessageDTO> getMessages() {
        return messages;
    }

    /**
     * Setter for the messages of an ad.
     *
     * @param messages The messages to be set.
     */
    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    /**
     * Getter for the public contact data of the ad creator.
     *
     * @return The public contact data of the ad creator.
     */
    public UserDTO getPublicData() {
        return publicData;
    }

    /**
     * Setter for the public contact data of the ad creator.
     *
     * @param publicData The new contact data of the ad creator.
     */
    public void setPublicData(UserDTO publicData) {
        this.publicData = publicData;
    }

    /**
     * Getter of country, city and postcode of the ad.
     *
     * @return City and postcode of the ad as String.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter of country, city and postcode of the ad.
     *
     * @param location The new location of the ad.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter if ad is basis of negotiation.
     *
     * @return True if ad is basis of negotiation else false.
     */
    public boolean isBasisOfNegotiation() {
        return isBasisOfNegotiation;
    }

    /**
     * Setter of basis of negotiation.
     *
     * @param basisOfNegotiation represents if an ad is basis of negotiation.
     */
    public void setBasisOfNegotiation(boolean basisOfNegotiation) {
        isBasisOfNegotiation = basisOfNegotiation;
    }

    /**
     * Getter for the follower of an ad.
     *
     * @return The amount of follower of an ad.
     */
    public int getFollower() {
        return follower;
    }

    /**
     * Setter for the follower of an ad.
     *
     * @param follower New amount of follower of an ad.
     */
    public void setFollower(int follower) {
        this.follower = follower;
    }
}
