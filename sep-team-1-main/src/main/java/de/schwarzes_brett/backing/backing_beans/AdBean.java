package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.dto.RatingDTO;
import de.schwarzes_brett.dto.Role;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The bean responsible for ads. An ad has multiple attributes which get managed in this class.
 *
 * @author Jonas Elsper
 */
@Named
@ViewScoped
public class AdBean implements NotificationDisplay, Serializable {

    /**
     * Serial of the bean.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Dictionary for phrases.
     */
    @Inject
    private transient Dictionary dict;
    /**
     * AdDTO of the bean.
     */
    private AdDTO ad;

    /**
     * AdService of the bean.
     */
    @Inject
    private AdService adService;

    /**
     * Service to handle user specific actions.
     */
    @Inject
    private UserService userService;

    /**
     * MessageBean of the bean.
     */
    @Inject
    private MessageBean messageBean;
    /**
     * UserSession of the bean.
     */
    @Inject
    private UserSession userSession;
    /**
     * Dictionary of the bean.
     */
    @Inject
    private transient Dictionary dictionary;
    /**
     * ExternalContext used for flash scope.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public AdBean() {
    }

    /**
     * Getter for the ad which should be shown in detail.
     *
     * @return An instance of {@code AdDTO}.
     */
    public AdDTO getAd() {
        return ad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    private void init() {
        ad = new AdDTO();
    }

    /**
     * Loads any values the ad has included.
     */
    public void loadAd() {
        if (ad.getId() == null) {
            throw new UnauthorisedAccessException("Url param for ad id was not set");
        }
        adService.fetchAd(ad, userSession.getUser());
    }

    /**
     * Returns the release-period as a String.
     *
     * @return The release-period
     */
    public String getReleasePeriod() {
        if (ad.getEnd() == null) {
            return dictionary.get("f_ad_from") + " " + ad.getRelease().toString();
        } else {
            return dictionary.get("f_ad_from") + " " + ad.getRelease().toString() + " "
                   + dictionary.get("f_ad_until") + " " + ad.getEnd().toString();
        }
    }

    /**
     * Returns the location as a String.
     *
     * @return The location.
     */
    public String getLocation() {
        return ad.getLocation();
    }

    /**
     * Returns the average rating of a user as a String.
     *
     * @return The average Rating.
     */
    public String getAvgRating() {
        BigDecimal rating = ad.getCreator().getRating();
        if (rating == null) {
            return dict.get("f_ad_noRating");
        } else {
            return rating.setScale(1, RoundingMode.HALF_DOWN) + "★";
        }
    }

    /**
     * Returns the number of follower for a user as a String.
     *
     * @return The number of follower.
     */
    public String getNumberOfFollower() {
        return Integer.toString(ad.getFollower());
    }

    /**
     * Gets called when the owner of the ad gets rated.
     *
     * @return The corresponding facelet for rating a user.
     */
    public String rateUser() {
        RatingDTO rating = new RatingDTO();
        rating.setRated(ad.getCreator());
        rating.setRater(userSession.getUser());
        externalContext.getFlash().put("adId", ad.getId());
        externalContext.getFlash().put("rating", rating);
        return "/view/user/rate";
    }

    /**
     * Gets called when the owner of the ad gets messaged.
     *
     * @return The corresponding facelet for messaging a user.
     */
    public String messageUser() {
        MessageDTO answer = new MessageDTO();
        userService.fetchUserByUsername(ad.getCreator());
        answer.setReceiver(ad.getCreator());
        answer.setSender(userSession.getUser());
        answer.setAd(ad);
        answer.setSharedPublic(true);
        externalContext.getFlash().put("message", answer);
        return "/view/user/message?faces-redirect=true";
    }

    /**
     * Gets called when the owner of the ad gets followed.
     *
     * @return The corresponding facelet for following a user.
     */
    public String follow() {
        externalContext.getFlash().put("ad", ad);
        return "/view/user/follow?faces-redirect=true";
    }

    /**
     * Changes the visibility of the message.
     *
     * @param message The id of the message.
     * @author Valentin Damjnatschitsch.
     */
    public void changeMessageVisibility(MessageDTO message) {
        logger.log(Level.FINEST, "Updating the visibility of the message");
        messageBean.updateVisibilityMessage(message);
    }

    /**
     * Deletes the message with the given ID.
     *
     * @param messageId The id of the message.
     */
    public void deleteMessage(Integer messageId) {
        List<MessageDTO> messages = ad.getMessages();
        messages.removeIf(message -> message.getMessageId() == messageId.intValue());
        messageBean.deleteMessage(messageId);
        ad.setMessages(messages);
    }

    /**
     * Gets called when a user answers to a message with a certain ID.
     *
     * @param message The message.
     * @return answerMessage The facelet for sending a message.
     * @author Valentin Damjantschitsch.
     */
    public String answerMessage(MessageDTO message) {
        logger.log(Level.FINEST, "Send answer of a message");
        MessageDTO answer = new MessageDTO();
        answer.setSender(userSession.getUser());
        userService.fetchUserByUsername(message.getSender());
        answer.setReceiver(message.getSender());
        answer.setMessageToBeAnswered(message);
        answer.setAd(ad);
        answer.setSharedPublic(true);
        externalContext.getFlash().put("message", answer);
        return "/view/user/message?faces-redirect=true";
    }

    /**
     * Checks if a user has the required role to see a certain element.
     * Only admins or logged-in users can see this element.
     *
     * @return True if user is allowed to see a certain element else false.
     */
    public boolean loggedInR() {
        Role userRole = userSession.getUser().getRole();
        return userRole == Role.USER || userRole == Role.ADMIN;
    }

    /**
     * Checks if user is allowed to delete a certain message or change visibility of a certain message.
     *
     * @param messageId The id of affected message.
     * @return True íf the user is allowed else false.
     */
    public boolean ownOrAdminR(Integer messageId) {
        for (MessageDTO message : ad.getMessages()) {
            if (Objects.equals(message.getMessageId(), messageId)) {
                boolean isOwn = Objects.equals(message.getSender().getId(), userSession.getUser().getId());
                boolean isAdmin = userSession.getUser().getRole() == Role.ADMIN;
                boolean isCreator = Objects.equals(userSession.getUser().getId(), ad.getCreator().getId());
                return isCreator || isAdmin || isOwn;
            }
        }
        return false;
    }

    /**
     * Checks if a user is allowed to see the nickname of the sender of a certain message.
     *
     * @param messageId The message of the sender.
     * @return True if user is allowed to see the nickname.
     */
    public boolean isAnonymousOrOwn(Integer messageId) {
        for (MessageDTO message : ad.getMessages()) {
            if (message.getMessageId() == messageId.intValue()) {
                boolean isOwn = Objects.equals(message.getSender().getId(), userSession.getUser().getId());
                boolean isIAmAdmin = userSession.getUser().getRole() == Role.ADMIN;
                boolean isAnonymous = message.isAnonymous();
                return isOwn || isIAmAdmin || !isAnonymous;
            }
        }
        return false;
    }

    /**
     * Checks if a user is allowed to answer a certain message.
     *
     * @param messageId The message to answer.
     * @return True if user is allowed to answer.
     */
    public boolean isAllowedToAnswer(Integer messageId) {
        for (MessageDTO message : ad.getMessages()) {
            if (message.getMessageId() == messageId.intValue()) {
                boolean isCreator = Objects.equals(ad.getCreator().getId(), userSession.getUser().getId());
                boolean isOwn = Objects.equals(message.getSender().getId(), userSession.getUser().getId());
                boolean isAdmin = userSession.getUser().getRole() == Role.ADMIN;
                if (!isOwn && (isAdmin || isCreator)) {
                    return true;
                }
                if (!isOwn && message.isSharedPublic() && Objects.equals(message.getSender().getId(), ad.getCreator().getId())) {
                    return true;
                }
                if (!isOwn && !message.isSharedPublic() && Objects.equals(message.getReceiver().getId(), userSession.getUser().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a user is allowed to write a new message.
     *
     * @return True, if a user is allowed to write a new message.
     * @author Valentin Damjantschitsch.
     */
    public boolean isAllowedWriteNewMessage() {
        return !Objects.equals(ad.getCreator().getId(), userSession.getUser().getId());
    }

    /**
     * Checks if a user is logged in or allowed to write a new message.
     *
     * @return True, if a user is logged in and allowed to write a new message.
     */
    public boolean isMessageButtonVisible() {
        return loggedInR() && isAllowedWriteNewMessage();
    }

    /**
     * Getter for the name of the public contact data.
     *
     * @return The name of the public contact data as string.
     */
    public String getName() {
        return Arrays.stream(new String[]{ad.getPublicData().getFirstName(), ad.getPublicData().getLastName()})
                     .filter(Objects::nonNull)
                     .filter(s -> !s.equals(""))
                     .collect(Collectors.joining(" "));
    }

    /**
     * Getter for the address of the public contact data.
     *
     * @return The address of the public contact data as string.
     */
    public String getAddress() {
        return Arrays.stream(new String[]{ad.getPublicData().getCountry(), ad.getPublicData().getPostalCode(),
                             ad.getPublicData().getCity(), ad.getPublicData().getStreet(),
                             ad.getPublicData().getStreetNumber(), ad.getPublicData().getAddressAddition()})
                     .filter(Objects::nonNull)
                     .filter(s -> !s.equals(""))
                     .collect(Collectors.joining(", "));
    }

    /**
     * Returns the correct link for the avatar image of the creator.
     *
     * @param avatar The avatar object.
     * @return The generated link.
     */
    public String generateAvatarId(ImageDTO avatar) {
        if (avatar.getId() == null) {
            return "/image-default?id=avatar";
        } else {
            return "/image?id=" + avatar.getId();
        }
    }

    /**
     * Checks whether a user is allowed to rate the ad creator or not.
     *
     * @return True if the user is allowed to rate.
     */
    public boolean isAllowedToRate() {
        return loggedInR() && !ad.getCreator().getId().equals(userSession.getUser().getId());
    }

    /**
     * Checks whether a user is allowed to follow the ad creator or the ad.
     *
     * @return True if the user is allowed to follow.
     */
    public boolean isAllowedToFollow() {
        return isAllowedToRate();
    }
}
