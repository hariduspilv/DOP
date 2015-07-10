package ee.hm.dop.dao;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;

public class MaterialDAOTest extends DatabaseTestBase {

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void findAll() {
        List<Material> materials = materialDAO.findAll();
        assertEquals(8, materials.size());

        // Verify if all required fields are loaded
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            assertEquals(Long.valueOf(i + 1), material.getId());
            assertFalse(isBlank(material.getSource()));

            List<Author> authors = material.getAuthors();
            assertNotNull(authors);
            for (Author author : authors) {
                assertNotNull(author.getId());
                assertFalse(isBlank(author.getName()));
                assertFalse(isBlank(author.getSurname()));
            }
            List<LanguageString> descriptions = material.getDescriptions();
            assertNotNull(descriptions);

            for (LanguageString languageDescription : descriptions) {
                assertNotNull(languageDescription.getId());
                assertNotNull(languageDescription.getLanguage());
                assertFalse(isBlank(languageDescription.getText()));
            }

            List<LanguageString> titles = material.getTitles();
            assertNotNull(titles);

            for (LanguageString title : titles) {
                assertNotNull(title.getId());
                assertNotNull(title.getLanguage());
                assertFalse(isBlank(title.getText()));
            }
        }
    }

    @Test
    public void find() {
        long a = 1;
        Material material = materialDAO.findById(a);

        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language language = material.getDescriptions().get(0).getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals("et", language.getCodes().get(0));
    }

    @Test
    public void testAuthorAndDesc() {
        Material material = materialDAO.findById(1);
        assertEquals(1, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
    }

    @Test
    public void testAuthors() {
        Material material = materialDAO.findById(2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void testMaterialLanguage() {
        Material material1 = materialDAO.findById(2);
        assertEquals("rus", material1.getLanguage().getCode());

        Material material2 = materialDAO.findById(1);
        assertEquals("est", material2.getLanguage().getCode());
    }

    @Test
    public void testMaterialResourceType() {
        Material material1 = materialDAO.findById(1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDAO.findById(1);
        assertEquals("EXPERIMENT1", material2.getResourceTypes().get(1).getName());
    }

    @Test
    public void testMaterialClassification() {
        Material material1 = materialDAO.findById(1);
        assertEquals("Biology", material1.getClassifications().get(0).getName());
        assertEquals("Plants", material1.getClassifications().get(0).getChildren().get(0).getName());

        Material material2 = materialDAO.findById(5);

        assertEquals("Algebra", material2.getClassifications().get(0).getParent().getName());
    }

    @Test
    public void testMaterialEducationalContext() {
        Material material1 = materialDAO.findById(1);
        assertEquals("PRESCHOOL", material1.getEducationalContexts().get(0).getName());
        assertEquals("COMPULSORYEDUCATION", material1.getEducationalContexts().get(1).getName());
    }

    @Test
    public void testMaterialLicense() {
        Material material = materialDAO.findById(1);
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void testMaterialPublisher() {
        Material material = materialDAO.findById(1);
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals("http://www.pegasus.ee", material.getPublishers().get(1).getWebsite());
    }

    @Test
    public void findAllById() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 5);
        idList.add((long) 7);
        idList.add((long) 3);

        List<Long> idListCopy = new ArrayList<>(idList);

        List<Material> result = materialDAO.findAllById(idList);

        assertNotNull(result);
        assertEquals(3, result.size());

        for (Material material : result) {
            idListCopy.remove(material.getId());
        }

        assertTrue(idListCopy.isEmpty());
    }

    @Test
    public void findAllByIdNoResult() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 1155);

        List<Material> result = materialDAO.findAllById(idList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<Material> result = materialDAO.findAllById(new ArrayList<>());

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
