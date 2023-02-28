package de.schwarzes_brett.business_logic.mail;

import de.schwarzes_brett.business_logic.exception.MailUnavailableException;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author michaelgruner
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MailServiceIT extends ITBase {

    @RegisterExtension
    private static final ITPerClassExtension IT_EXTENSION = new ITPerClassExtension();

    @Test
    void testMailSending() throws MessagingException, IOException, MailUnavailableException {

        final String from = "no-reply@schwarzes-brett.de";
        final String to = "to@localhost";
        final String subject = "subject";
        final String content = "message content";

        MailService.sendMail(to, subject, content);

        MimeMessage received = IT_EXTENSION.getMail().getReceivedMessages()[0];

        assertAll(() -> assertEquals(from, received.getFrom()[0].toString()),
                  () -> assertEquals(to, received.getAllRecipients()[0].toString()),
                  () -> assertEquals(subject, received.getSubject()),
                  () -> assertEquals(content, received.getContent().toString().trim()));
    }

}
