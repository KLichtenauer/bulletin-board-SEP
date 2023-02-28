package de.schwarzes_brett.business_logic.mail;

import de.schwarzes_brett.business_logic.exception.MailUnavailableException;
import de.schwarzes_brett.data_access.config.Config;
import de.schwarzes_brett.logging.LoggerProducer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for sending a mail to a specific e-Mail address.
 *
 * @author michaelgruner
 */
@Named
@RequestScoped
public final class MailService {

    private static final Logger LOGGER = LoggerProducer.get(MailService.class);

    private MailService() {}

    /**
     * Sends a mail including to a given receiver.
     *
     * @param to      The receiver of the mail.
     * @param subject The mail's subject.
     * @param msg     The message of the mail.
     * @throws MailUnavailableException When the mail could not be sent.
     */
    public static void sendMail(String to, String subject, String msg) throws MailUnavailableException {
        Config config = Config.getInstance();
        Session session = getSession(config);
        LOGGER.fine("Server configuration loaded.");
        try {
            //Create the message
            LOGGER.fine("Start creating message for the mail to " + to);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.get("MAIL_MESSAGE_FROM")));
            message.setRecipients(Message.RecipientType.TO,
                                  InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(msg);
            Transport.send(message);
            LOGGER.fine("Mail sent to " + to);
        } catch (MessagingException e) {
            LOGGER.severe("Error while sending the mail.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new MailUnavailableException("Mail could not be sent.");
        }
    }

    /**
     * Checks if the connection to the mail-server could be established.
     *
     * @return True if the Connection to the mail-server could be established.
     */
    public static boolean checkSMTPConfiguration() {
        LOGGER.info("Checking the connection to the smtp-server");
        boolean success;
        try {
            Transport transport = getSession(Config.getInstance()).getTransport("smtp");
            transport.connect();
            success = transport.isConnected();
            transport.close();
            LOGGER.info("Connection was successfully established.");
        } catch (MessagingException e) {
            LOGGER.severe("Error while trying to connect with the smtp-server");
            success = false;
        }
        return success;
    }

    private static Session getSession(Config config) {
        //configure SMTP server details
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", config.get("MAIL_AUTH"));
        properties.put("mail.smtp.starttls.enable", config.get("MAIL_USE_TLS"));
        properties.put("mail.smtp.host", config.get("MAIL_HOST"));
        properties.put("mail.smtp.port", config.get("MAIL_PORT"));

        return Session.getInstance(properties,
                                   new jakarta.mail.Authenticator() {
                                       protected PasswordAuthentication getPasswordAuthentication() {
                                           return new PasswordAuthentication(config.get("MAIL_USERNAME"), config.get("MAIL_PASSWORD"));
                                       }
                                   });
    }

}
