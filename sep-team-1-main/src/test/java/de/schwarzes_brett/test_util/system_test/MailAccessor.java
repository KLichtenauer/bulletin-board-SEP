package de.schwarzes_brett.test_util.system_test;

import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates the access to mails.
 * When running system tests, the {@link GreenMail} object is used.
 * When running load tests, the mails are retrieved from the external SMTP server via IMAP.
 *
 * @author Tim-Florian Feulner
 */
public final class MailAccessor {

    private static final int IMAP_PORT = 3028;
    private static final Logger LOGGER = Logger.getLogger(MailAccessor.class.getName());

    private static MailAccessor instance;

    private GreenMail greenMail = null;
    private Properties imapServerProperties = null;

    private MailAccessor() {}

    /**
     * Gets the instance of this singleton class.
     *
     * @return The class instance.
     */
    public static MailAccessor getInstance() {
        if (instance == null) {
            instance = new MailAccessor();
        }
        return instance;
    }

    private static MimeMessage messageToMimeMessage(Message message) {
        try {
            MimeMessage mimeMessage = new MimeMessage(message.getSession());
            mimeMessage.addFrom(message.getFrom());
            mimeMessage.setFlags(message.getFlags(), true);
            mimeMessage.setSubject(message.getSubject());
            mimeMessage.setSentDate(message.getSentDate());

            for (Message.RecipientType type : new Message.RecipientType[]{Message.RecipientType.TO, Message.RecipientType.CC,
                    Message.RecipientType.BCC}) {
                mimeMessage.addRecipients(type, message.getRecipients(type));
            }

            mimeMessage.setContent(message.getContent(), message.getContentType());

            return mimeMessage;
        } catch (MessagingException e) {
            LOGGER.severe("Could not read values of message.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.severe("Could not read content of message.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    /**
     * Sets the mode to using a {@link GreenMail} instance. Call this when performing system tests.
     *
     * @param greenMail The {@link GreenMail} instance to use for mail access.
     */
    public void useLocalGreenMail(GreenMail greenMail) {
        this.greenMail = greenMail;
    }

    /**
     * Sets the mode to using a remote SMTP server by retrieving mails via IMAP. Call this when performing load tests.
     */
    public void useRemoteIMAP() {
        readConfig();
        this.greenMail = null;
    }

    private void readConfig() {
        try (InputStream input = MailAccessor.class.getClassLoader().getResourceAsStream("load_tests/WEB-INF/config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not read config.");
            }
            Properties config = new Properties();
            try (Reader configReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                config.load(configReader);
                imapServerProperties = new Properties();
                imapServerProperties.put("mail.imap.host", config.get("MAIL_HOST"));
                imapServerProperties.put("mail.imap.port", IMAP_PORT);
                imapServerProperties.put("mail.imap.ssl.enable", "false");
                imapServerProperties.put("mail.imap.starttls.enable", "false");
            }

        } catch (IOException e) {
            String message = "Could not read config.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Retrieves the mails send to a specific address.
     *
     * @param receiverMailAddress The address of the receiver.
     * @return The resulting mail list.
     */
    public List<MimeMessage> getReceivedMessages(String receiverMailAddress) {
        if (greenMail != null) {
            // Using local greenmail instance.
            return Arrays.stream(greenMail.getReceivedMessages()).toList();
        } else {
            return downloadMailsFromServer(receiverMailAddress);
        }
    }

    private List<MimeMessage> downloadMailsFromServer(String receiverMailAddress) {
        Session session = Session.getDefaultInstance(imapServerProperties);

        try (Store store = session.getStore("imap")) {
            store.connect(receiverMailAddress, receiverMailAddress);
            try (Folder folderInbox = store.getFolder("INBOX")) {
                folderInbox.open(Folder.READ_ONLY);
                return Arrays.stream(folderInbox.getMessages()).map(MailAccessor::messageToMimeMessage).filter(Objects::nonNull)
                             .filter(mimeMessage -> {
                                 try {
                                     return Arrays.stream(mimeMessage.getAllRecipients())
                                                  .anyMatch(address -> address.toString().equals(receiverMailAddress));
                                 } catch (MessagingException e) {
                                     String message = "Could not read recipients of message.";
                                     LOGGER.severe(message);
                                     LOGGER.log(Level.SEVERE, e.getMessage(), e);
                                     return false;
                                 }
                             }).toList();
            }
        } catch (NoSuchProviderException e) {
            String message = "No provider for protocol imap.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (MessagingException e) {
            String message = "Could not connect to the message store via imap.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

}
