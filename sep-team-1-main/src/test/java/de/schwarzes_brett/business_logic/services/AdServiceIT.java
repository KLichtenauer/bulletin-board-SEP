package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.Currency;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.PriceDTO;
import de.schwarzes_brett.dto.SearchDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ApplicationScoped.class, ViewScoped.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
class AdServiceIT extends ITBase {

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Inject
    private AdService adService;

    @Inject
    private MessageService messageService;

    @Inject
    private FollowService follow;

    /**
     * @author michalgruner
     */
    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    void fetchAds() {
        PaginationDTO pagination = new PaginationDTO();
        pagination.setLastPageNumber(1);
        pagination.setItemsPerPage(10);
        pagination.setSortAscending(false);
        SearchDTO search = new SearchDTO();
        search.setSearchTerm("hammer");
        pagination.setSearch(search);
        pagination.setSortBy("title");
        List<AdDTO> ads = adService.fetchAds(pagination);

        boolean adWithId3Exists = false;
        boolean adWithId4Exists = false;
        for (AdDTO ad : ads) {
            if (ad.getId() == 300) {
                adWithId3Exists = true;
            }
            if (ad.getId() == 400) {
                adWithId4Exists = true;
            }
        }

        boolean finalAdWithId4Exists = adWithId4Exists;
        boolean finalAdWithId3Exists = adWithId3Exists;

        assertAll(() -> assertTrue(finalAdWithId3Exists),
                  () -> assertTrue(finalAdWithId4Exists),
                  () -> assertEquals(2, ads.size()));
    }

    /**
     * @author Jonas Elsper
     */
    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    void fetchCommentedAds() {
        AdDTO ad1 = initAd1();
        AdDTO ad2 = initAd2();
        MessageDTO message1 = initMessage1();
        message1.setAd(ad1);
        MessageDTO message2 = initMessage2();
        message2.setAd(ad2);
        PaginationDTO pagination = initPagination();
        UserDTO user = new UserDTO();
        user.setId(1);
        adService.insertAd(ad1, -1);
        adService.insertAd(ad2, -1);
        String path1 = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                       + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/ad.xhtml"
                       + "?id=" + message1.getAd().getId().toString();
        String path2 = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                       + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/ad.xhtml"
                       + "?id=" + message2.getAd().getId().toString();
        messageService.insertMessage(message1, path1);
        messageService.insertMessage(message2, path2);
        List<AdDTO> ads = adService.fetchCommentedAds(pagination, user);
        assertEquals("Große Hüpfburg", ads.get(0).getTitle());
        assertEquals("Kleine Hüpfburg", ads.get(1).getTitle());
        cleanup();
    }

    /**
     * @author Jonas Elsper
     */
    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    void fetchFollowedAds() {
        AdDTO ad1 = initAd1();
        ad1.getCreator().setId(1);
        AdDTO ad2 = initAd2();
        ad2.getCreator().setId(1);
        UserDTO follower = new UserDTO();
        follower.setId(300);
        UserDTO followed = new UserDTO();
        followed.setId(1);
        PaginationDTO pagination = initPagination();
        adService.insertAd(ad1, -1);
        adService.insertAd(ad2, -1);
        follow.insertFollowUser(follower, followed);
        List<AdDTO> ads = adService.fetchFollowedAds(pagination, follower);
        assertEquals("Große Hüpfburg", ads.get(0).getTitle());
        assertEquals("Kleine Hüpfburg", ads.get(1).getTitle());
    }


    /**
     * @author Kilian Lichtenauer
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void removeFollowedUser() {
        AdDTO ad1 = initAd1();
        ad1.getCreator().setId(1);
        AdDTO ad2 = initAd2();
        ad2.getCreator().setId(1);
        UserDTO follower = new UserDTO();
        follower.setId(300);
        UserDTO followed = new UserDTO();
        followed.setId(1);
        PaginationDTO pagination = initPagination();
        adService.insertAd(ad1, -1);
        adService.insertAd(ad2, -1);
        if (adService.fetchFollowedAds(pagination, follower).isEmpty()) {
            follow.insertFollowUser(follower, followed);
        }
        follow.removeFollowUser(follower, followed);
        List<AdDTO> ads = adService.fetchFollowedAds(pagination, follower);
        assertTrue(ads.isEmpty());
        adService.deleteAd(ad1);
        adService.deleteAd(ad2);
    }

    /**
     * @author Jonas Elsper
     */
    @AfterAll
    @SuppressWarnings("checkstyle:MagicNumber")
    void cleanup() {
        AdDTO ad1 = new AdDTO();
        ad1.setId(1);
        AdDTO ad2 = new AdDTO();
        ad2.setId(2);
        adService.deleteAd(ad1);
        adService.deleteAd(ad2);
        messageService.deleteMessage(1);
        messageService.deleteMessage(2);
    }

    /**
     * Returns Ad 2.
     *
     * @return The second ad.
     * @author Jonas Elsper
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private AdDTO initAd2() {
        AdDTO ad = new AdDTO();
        ad.setId(8);
        ad.setTitle("Kleine Hüpfburg");
        ad.setDescription("Kleine Hüpfburg für Kinder.");
        ad.setBasisOfNegotiation(false);
        ad.setRelease(ZonedDateTime.now());
        ad.setEnd(ZonedDateTime.now());
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
        ad.setPublicData(contact);
        CategoryDTO category = new CategoryDTO();
        category.setId(1);
        ad.setCategory(category);
        UserDTO creator = new UserDTO();
        creator.setId(300);
        ad.setCreator(creator);
        PriceDTO price = new PriceDTO();
        price.setValue(new BigDecimal(42));
        price.setCurrency(Currency.EUR);
        price.setHasPrice(true);
        ad.setPrice(price);
        ad.setImages(new ArrayList<>());
        return ad;
    }

    /**
     * Creates Ad 1.
     *
     * @return The first ad.
     * @author Jonas Elsper
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private AdDTO initAd1() {
        AdDTO ad = new AdDTO();
        ad.setId(7);
        ad.setTitle("Große Hüpfburg");
        ad.setDescription("Hüpfburg für alle.");
        ad.setBasisOfNegotiation(false);
        ad.setRelease(ZonedDateTime.now());
        ad.setEnd(ZonedDateTime.now());
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
        ad.setPublicData(contact);
        CategoryDTO category = new CategoryDTO();
        category.setId(2);
        ad.setCategory(category);
        UserDTO creator = new UserDTO();
        creator.setId(1);
        ad.setCreator(creator);
        PriceDTO price = new PriceDTO();
        price.setValue(new BigDecimal(420));
        price.setCurrency(Currency.EUR);
        price.setHasPrice(true);
        ad.setPrice(price);
        ad.setImages(new ArrayList<>());
        return ad;
    }

    /**
     * Creates Message1.
     *
     * @return The first message.
     * @author Jonas Elsper
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private MessageDTO initMessage1() {
        MessageDTO message = new MessageDTO();
        message.setMessageId(500);
        UserDTO sender = new UserDTO();
        sender.setId(1);
        MessageDTO toAnswer = new MessageDTO();
        toAnswer.setContent("hihi");
        message.setMessageToBeAnswered(toAnswer);
        CredentialsDTO credentials1 = new CredentialsDTO();
        credentials1.setUsername("admin");
        sender.setCredentials(credentials1);
        message.setSender(sender);
        UserDTO receiver = new UserDTO();
        CredentialsDTO credentials2 = new CredentialsDTO();
        credentials2.setUsername("Lisa4");
        receiver.setCredentials(credentials2);
        receiver.setId(300);
        message.setReceiver(receiver);
        message.setContent("Ich will die Hüpfburg");
        message.setSharedPublic(true);
        message.setAnonymous(false);
        return message;
    }

    /**
     * Creates Message2.
     *
     * @return The second message.
     * @author Jonas Elsper
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private MessageDTO initMessage2() {
        MessageDTO message = new MessageDTO();
        message.setMessageId(501);
        UserDTO sender = new UserDTO();
        sender.setId(1);
        MessageDTO toAnswer = new MessageDTO();
        toAnswer.setContent("hihi");
        message.setMessageToBeAnswered(toAnswer);
        CredentialsDTO credentials1 = new CredentialsDTO();
        credentials1.setUsername("admin");
        sender.setCredentials(credentials1);
        message.setSender(sender);
        UserDTO receiver = new UserDTO();
        receiver.setId(300);
        CredentialsDTO credentials2 = new CredentialsDTO();
        credentials2.setUsername("Lisa4");
        receiver.setCredentials(credentials2);
        message.setReceiver(receiver);
        message.setContent("Hüpfburg ist nicht mehr zu verkaufen.");
        message.setSharedPublic(true);
        message.setAnonymous(false);
        return message;
    }

    /**
     * Creates the pagination.
     *
     * @return The pagination used in this test.
     * @author Jonas Elsper
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private PaginationDTO initPagination() {
        PaginationDTO pagination = new PaginationDTO();
        pagination.setLastPageNumber(1);
        pagination.setItemsPerPage(10);
        pagination.setSortAscending(false);
        pagination.setShowExpiredAds(true);
        pagination.setSortBy("publishing_time");
        return pagination;
    }
}
