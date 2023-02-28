package de.schwarzes_brett.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Contains the data of a message.
 *
 * @author Valentin Damjantschitsch.
 */
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The messageId of the message.
     */
    private Integer messageId;

    /**
     * The sender of the message.
     */
    private UserDTO sender;

    /**
     * The receiver of the message.
     */
    private UserDTO receiver;

    /**
     * The ad of the message.
     */
    private AdDTO ad;

    /**
     * The content of the message.
     */
    private String content;

    /**
     * The isPublic of the message.
     */
    private boolean isSharedPublic;

    /**
     * The anonymous of the message.
     */
    private boolean anonymous;

    /**
     * The message to be answered. Can be null.
     */
    private MessageDTO messageToBeAnswered;


    /**
     * Default constructor.
     */
    public MessageDTO() {
    }

    /**
     * Getter for the messageId of the message.
     *
     * @return The messageId of the message.
     */
    public Integer getMessageId() {
        return messageId;
    }

    /**
     * Setter for the messageId of the message.
     *
     * @param messageId The messageId to be set.
     */
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    /**
     * Getter for the sender of the message.
     *
     * @return The sender of the message.
     */
    public UserDTO getSender() {
        return sender;
    }

    /**
     * Setter for the sender of the message.
     *
     * @param sender The sender to be set.
     */
    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    /**
     * Getter for the receiver of the message.
     *
     * @return The receiver of the message.
     */
    public UserDTO getReceiver() {
        return receiver;
    }

    /**
     * Setter for the receiver of the message.
     *
     * @param receiver The receiver to be set.
     */
    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }

    /**
     * Getter for the ad of the message.
     *
     * @return The ad of the message.
     */
    public AdDTO getAd() {
        return ad;
    }

    /**
     * Setter for the ad of the message.
     *
     * @param ad The ad to be set.
     */
    public void setAd(AdDTO ad) {
        this.ad = ad;
    }

    /**
     * Getter for the content of the message.
     *
     * @return The content of the message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of the message.
     *
     * @param content The content to be set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for the visibility of the message.
     *
     * @return The visibility of the message.
     */
    public boolean isSharedPublic() {
        return isSharedPublic;
    }

    /**
     * Setter for the visibility of the message.
     *
     * @param sharedPublic The visibility to be set.
     */
    public void setSharedPublic(boolean sharedPublic) {
        isSharedPublic = sharedPublic;
    }

    /**
     * Getter for anonymity of the message's sender.
     *
     * @return The anonymity of the message's sender.
     */
    public boolean isAnonymous() {
        return anonymous;
    }

    /**
     * Setter for the anonymity of the message's sender.
     *
     * @param anonymous The anonymity of the message's sender to be set.
     */
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    /**
     * The message to be answered.
     *
     * @return The message to be answered.
     */
    public MessageDTO getMessageToBeAnswered() {
        return messageToBeAnswered;
    }

    /**
     * Setter for the message to be answered.
     *
     * @param messageToBeAnswered The message to be answered.
     */
    public void setMessageToBeAnswered(MessageDTO messageToBeAnswered) {
        this.messageToBeAnswered = messageToBeAnswered;
    }
}
