package ee.hm.dop.rest.metadata;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.LandingPageObject;
import ee.hm.dop.model.enums.LanguageC;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TranslationResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_TRANSLATIONS = "translation?lang=";

    @Test
    public void russian_translations_are_supported() {
        Map<String, String> translations = getTranslations(LanguageC.RUS);

        assertTrue(translations.size() >= 4);
        assertEquals("FOO сообщение", translations.get("FOO"));
        assertEquals("Эстонский язык", translations.get("Estonian"));
        assertEquals("русский язык", translations.get("Russian"));
        assertEquals("Mатематика", translations.get("TOPIC_MATHEMATICS"));
    }

    @Test
    public void estonian_translations_are_supported() {
        Map<String, String> translations = getTranslations(LanguageC.EST);

        assertTrue(translations.size() >= 4);
        assertEquals("FOO sõnum", translations.get("FOO"));
        assertEquals("Eesti keeles", translations.get("Estonian"));
        assertEquals("Vene keel", translations.get("Russian"));
        assertEquals("Matemaatika", translations.get("TOPIC_MATHEMATICS"));
    }

    @Test
    public void english_translations_are_supported() {
        Map<String, String> translations = getTranslations(LanguageC.ENG);

        assertTrue(translations.size() >= 4);
        assertEquals("FOO message", translations.get("FOO"));
        assertEquals("Estonian", translations.get("Estonian"));
        assertEquals("Russian", translations.get("Russian"));
        assertEquals("Mathematics", translations.get("TOPIC_MATHEMATICS"));
    }

    @Test
    public void language_must_be_specified() {
        Response response = doGet("translation");
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void translations_for_landing_page() {
        LandingPageObject landingPageObject = doGet("translation/landingPage/admin", LandingPageObject.class);
        assertEquals(3, landingPageObject.getNotices().size());
        assertEquals(3, landingPageObject.getDescriptions().size());
    }

    @Test
    public void unknown_language_is_unsupported() {
        Response response = doGet(GET_TRANSLATIONS + "unSupported");
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private Map<String, String> getTranslations(String language) {
        return doGet(GET_TRANSLATIONS + language, map());
    }

    private GenericType<Map<String, String>> map() {
        return new GenericType<Map<String, String>>() {
        };
    }
}
