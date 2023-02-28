package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.MessageDTO;

/**
 * Controls the database access for a message. The access happens through prepared statements and filling DTOs which get iterated through the layers.
 */
public interface MessageDAO {

    /**
     * Inserts message in database.
     *
     * @param message Contains all information about the message to be inserted.
     */
    void insertMessage(MessageDTO message);

    /**
     * Deletes message in database.
     *
     * @param messageID Contains all information about the message to be deleted.
     */
    void deleteMessage(Integer messageID);

    /**
     * Updates the visibility of a given message in the database.
     *
     * @param messageID The message of which the visibility has to be updated.
     */
    void updateVisibility(MessageDTO messageID);

}
