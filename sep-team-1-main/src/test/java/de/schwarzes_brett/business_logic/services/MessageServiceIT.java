package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.Currency;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.dto.PriceDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.Application;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.ExcludeBeanClasses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test class for testing sending a message. This includes the test T8.
 *
 * @author Valentin Damjantschitsch.
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
@ActivateScopes({RequestScoped.class, ViewScoped.class, SessionScoped.class, ApplicationScoped.class})
@ExcludeBeanClasses(Dictionary.class)
@ExplicitParamInjection
class MessageServiceIT extends ITBase {

    @Inject
    private AdService adService;

    @Inject
    private MessageService messageService;

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIViewRoot uiViewRoot;

    @Produces
    @Default
    @Mock(serializable = true)
    private Application application;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private HttpServletRequest httpServletRequest;

    @Produces
    @Default
    @Mock(serializable = true)
    private Dictionary dictionary;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIComponent usernameComponent;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIInput usernameInput;

    @Produces
    @Default
    @Mock(serializable = true)
    private Map<String, Object> componentAttributeMap;

    private AdDTO ad;
    private CategoryDTO category;
    private UserDTO user1;
    private UserDTO user2;
    private ImageDTO thumbnail;
    private PriceDTO price;
    private MessageDTO question;
    private MessageDTO answer;

    UserDTO initUser1() {
        user1 = new UserDTO();
        user1.setId(1);
        CredentialsDTO credentials = new CredentialsDTO();
        credentials.setUsername("admin");
        user1.setCredentials(credentials);
        return user1;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    UserDTO initUser2() {
        user2 = new UserDTO();
        user2.setId(300);
        CredentialsDTO credentials = new CredentialsDTO();
        credentials.setUsername("Lisa4");
        user2.setCredentials(credentials);
        return user2;
    }

    UserDTO contact() {
        UserDTO contact = new UserDTO();
        contact.setFirstName("Jonas");
        contact.setLastName("Tester");
        contact.setEmail("start@gmx.de");
        contact.setPhone("0815 123456");
        contact.setCountry("Germany");
        contact.setCity("Passau");
        contact.setPostalCode("94127");
        contact.setStreet("Am Tester 14");
        contact.setAddressAddition("b");
        return contact;
    }

    AdDTO initAd() {
        ad = new AdDTO();
        ad.setId(1);
        ad.setTitle("Große Hüpfburg");
        ad.setDescription("Hüpfburg für alle.");
        category = initCategory();
        ad.setCategory(category);
        ad.setCreator(user2);
        ad.setRelease(ZonedDateTime.now());
        ad.setEnd(ZonedDateTime.now());
        ad.setPublicData(contact());
        return ad;
    }

    CategoryDTO initCategory() {
        category = new CategoryDTO();
        category.setId(2);
        return category;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    MessageDTO initQuestion() {
        question = new MessageDTO();
        question.setMessageId(500);
        question.setSender(user1);
        question.setReceiver(user2);
        question.setAd(ad);
        MessageDTO toAnswer = new MessageDTO();
        toAnswer.setContent("hihihi");
        question.setMessageToBeAnswered(toAnswer);
        question.setContent("Ich will die Hüpfburg");
        question.setSharedPublic(true);
        question.setAnonymous(false);
        return question;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    MessageDTO initAnswer() {
        answer = new MessageDTO();
        answer.setMessageId(501);
        answer.setSender(user2);
        answer.setReceiver(user1);
        answer.setAd(ad);
        MessageDTO toAnswer = new MessageDTO();
        toAnswer.setContent("hihihi");
        answer.setMessageToBeAnswered(toAnswer);
        answer.setContent("Ok, du kannst sie haben.");
        answer.setSharedPublic(true);
        answer.setAnonymous(false);
        return answer;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    PriceDTO initPrice() {
        price = new PriceDTO();
        price.setValue(BigDecimal.valueOf(420));
        price.setCurrency(Currency.EUR);
        return price;
    }

    ImageDTO initThumbnail() {
        thumbnail = new ImageDTO();
        return thumbnail;
    }

    void init() {
        user1 = initUser1();
        user2 = initUser2();
        ad = initAd();
        thumbnail = initThumbnail();
        ad.setThumbnail(thumbnail);
        ad.setImages(new ArrayList<>());
        price = initPrice();
        ad.setPrice(price);
        question = initQuestion();
        answer = initAnswer();
    }

    @AfterAll
    void reset() {
        adService.deleteAd(ad);
    }

    @Order(1)
    @Test
    void insertMessage() {
        init();
        adService.insertAd(ad, -1);
        String path1 = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                       + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/ad.xhtml"
                       + "?id=" + question.getAd().getId().toString();
        messageService.insertMessage(question, path1);
        String path2 = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                       + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/ad.xhtml"
                       + "?id=" + answer.getAd().getId().toString();
        messageService.insertMessage(answer, path2);
        UserDTO user = new UserDTO();
        user.setId(1);
        user.setRole(Role.ADMIN);
        adService.fetchAd(ad, user);
        assertEquals(ad.getMessages().get(0).getMessageId(), 1);
        assertEquals(ad.getMessages().get(1).getMessageId(), 2);
    }

    @Order(2)
    @Test
    void deleteMessage() {
        messageService.deleteMessage(1);
        messageService.deleteMessage(2);
        UserDTO user = new UserDTO();
        user.setId(1);
        user.setRole(Role.ADMIN);
        adService.fetchAd(ad, user);
        assertEquals(ad.getMessages().size(), 0);
    }
}
