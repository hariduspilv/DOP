package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.BrokenContent;
import ee.hm.dop.model.Material;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrokenContentAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_SET_BROKEN = "material/setBroken";
    private static final String MATERIAL_GET_BROKEN = "admin/brokenContent/getBroken";
    private static final String MATERIAL_GET_BROKEN_COUNT = "admin/brokenContent/getBroken/count";
    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
    private static final String MATERIAL_IS_BROKEN = "admin/brokenContent/isBroken";
    private static final long MATERIAL_ID = 5L;

    @Test
    public void setNotBroken_sets_material_unbroken() {
        login(USER_ADMIN);
        setMaterialBroken();
        assertTrue("Material is not broken", getMaterial(MATERIAL_ID).getBroken() > 0);
    }

    @Test
    public void setNotBroken_sets_material_unbroken_and_material_is_reviewed() {
        login(USER_ADMIN);
        setMaterialBroken();
        assertTrue("Material is not broken", getMaterial(MATERIAL_ID).getBroken() > 0);

        Material material = getMaterial(MATERIAL_ID);
        assertTrue(material.getUnReviewed() == 0);
    }

    @Test
    public void isBroken_returns_if_material_is_broken() {
        login(USER_ADMIN);
        setMaterialBroken();

        Response isBrokenResponseAdmin = doGet(MATERIAL_IS_BROKEN + "?materialId=" + MATERIAL_ID);
        assertEquals(Response.Status.OK.getStatusCode(), isBrokenResponseAdmin.getStatus());
        assertEquals(isBrokenResponseAdmin.readEntity(Boolean.class), true);
    }

    @Test
    public void getBroken_returns_broken_materials_to_admin() {
        login(USER_ADMIN);

        Material material = getMaterial(MATERIAL_ID);
        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Response getBrokenResponseAdmin = doGet(MATERIAL_GET_BROKEN, MediaType.APPLICATION_JSON_TYPE);
        List<BrokenContent> brokenMaterials = getBrokenResponseAdmin.readEntity(list());
        assertTrue(brokenMaterials.stream().map(BrokenContent::getMaterial).anyMatch(m -> m.getId().equals(MATERIAL_ID)));

        long brokenMaterialsCount = doGet(MATERIAL_GET_BROKEN_COUNT, Long.class);
        assertEquals("Broken materials count", brokenMaterials.size(), brokenMaterialsCount);
    }

    @Test
    public void regular_user_is_not_allowed_to_check_if_material_is_broken() throws Exception {
        login(USER_SECOND);
        Response isBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + MATERIAL_ID);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), isBrokenResponse.getStatus());
    }

    @Test
    public void regular_user_is_not_allowed_to_set_material_not_broken() throws Exception {
        login(USER_SECOND);
        Response response = doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(MATERIAL_ID));
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void regular_user_is_not_allowed_to_get_broken_materials() throws Exception {
        login(USER_SECOND);
        Response getBrokenResponse = doGet(MATERIAL_GET_BROKEN);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), getBrokenResponse.getStatus());
    }

    private void setMaterialBroken() {
        doPost(MATERIAL_SET_BROKEN, getMaterial(MATERIAL_ID));
    }

    private GenericType<List<BrokenContent>> list() {
        return new GenericType<List<BrokenContent>>() {
        };
    }
}