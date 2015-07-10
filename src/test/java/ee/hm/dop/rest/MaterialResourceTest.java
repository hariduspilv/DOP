package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.IssueDate;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAllMaterials() {
        Response response = doGet("material/getAll");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(8, materials.size());

        // Verify if all fields are loaded
        Material material = materials.get(1);
        assertEquals(Long.valueOf(2), material.getId());
        assertEquals("Математика учебник для 8-го класса", material.getTitles().get(0).getText());
        assertEquals(new IssueDate((short) 27, (short) 1, -983), material.getIssueDate());
        List<Author> authors = material.getAuthors();
        assertEquals(2, authors.size());

        boolean newton = false, fibonacci = false;
        for (Author author : authors) {
            if (author.getId() == 1) {
                assertEquals("Isaac", author.getName());
                assertEquals("John Newton", author.getSurname());
                newton = true;
            } else if (author.getId() == 3) {
                assertEquals("Leonardo", author.getName());
                assertEquals("Fibonacci", author.getSurname());
                fibonacci = true;
            }
        }

        assertTrue(fibonacci && newton);
    }

    @Test
    public void getMaterialDescriptionAndLanguage() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        List<LanguageString> descriptions = material.getDescriptions();
        assertEquals(2, descriptions.size());
        for (LanguageString languageString : descriptions) {
            if (languageString.getId() == 1) {
                assertEquals("est", languageString.getLanguage().getCode());
                assertEquals("Test description in estonian. (Russian available)", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals("est", languageString.getLanguage());
                assertEquals("Test description in russian, which is the only language available.",
                        languageString.getText());

            }
        }
    }

    @Test
    public void getMaterialClassifications() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("Plants", material.getClassifications().get(0).getChildren().get(0).getName());

    }

    @Test
    public void getMaterialEducationalContext() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("PRESCHOOL", material.getEducationalContexts().get(0).getName());
    }

    @Test
    public void getMaterialLicenseType() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void getMaterialPublisher() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("Koolibri", material.getPublishers().get(0).getName());
    }

    @Test
    public void getMaterialAddedDate() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void GetNewestMaterials() {
        Response response = doGet("material/getNewestMaterials?numberOfMaterials=8");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(8, materials.size());

        Material material = materials.get(0);
        assertEquals(Long.valueOf(8), material.getId());
        assertEquals("Aabits 123", material.getTitles().get(0).getText());

        List<Author> authors = material.getAuthors();
        assertEquals(1, authors.size());
        assertEquals("Karl Simon Ben", material.getAuthors().get(0).getName());
        assertEquals("Tom Oliver Marx", material.getAuthors().get(0).getSurname());

    }
}
