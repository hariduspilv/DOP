package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class FirstReviewAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_UNREVIEWED = "admin/firstReview/unReviewed";
    private static final String GET_UNREVIEWED_COUNT = "admin/firstReview/unReviewed/count";
    private static final String SET_REVIEWED = "admin/firstReview/setReviewed";
    private static final long PRIVATE_PORTFOLIO = PORTFOLIO_7;

    @Test
    public void after_first_review_is_reviewed_it_is_not_returned_by_getUnreviewed() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());
        BigDecimal count = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews.size(), count.longValueExact());

        AdminLearningObject learningObject = firstReviews.stream()
                .filter(l -> l.getId().equals(MATERIAL_1))
                .findAny()
                .orElseThrow(RuntimeException::new);
        Long learningObjectId = learningObject.getId();
        Response updateResponse = doPost(SET_REVIEWED, materialWithId(MATERIAL_1));
        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());

        List<AdminLearningObject> firstReviews2 = doGet(GET_UNREVIEWED, listTypeAdminLO());

        boolean noneMatchUpdatedOne = firstReviews2.stream().map(AdminLearningObject::getId)
                .noneMatch(l -> l.equals(learningObjectId));
        assertTrue(noneMatchUpdatedOne);

        BigDecimal count2 = doGet(GET_UNREVIEWED_COUNT, BigDecimal.class);
        assertEquals("UnReviewed size, UnReviewed count", firstReviews2.size(), count2.longValueExact());
    }

    @Test
    public void getUnreviewed_returns_not_an_empty_list() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Test
    public void private_portfolio_is_not_returned_as_part_of_the_unreviewed() {
        login(USER_ADMIN);

        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());

        boolean noneMatchUpdatedOne = firstReviews.stream().map(AdminLearningObject::getId)
                .noneMatch(l -> l.equals(PRIVATE_PORTFOLIO));
        assertTrue(noneMatchUpdatedOne);
        assertTrue("UnReviewed list", CollectionUtils.isNotEmpty(firstReviews));
    }

    @Test
    public void getUnreviewed_returns_different_unReviewed_materials_based_on_user_priviledge() {
        login(USER_MODERATOR);
        List<AdminLearningObject> firstReviewsModerator = doGet(GET_UNREVIEWED, listTypeAdminLO());
        assertTrue(CollectionUtils.isNotEmpty(firstReviewsModerator));
        logout();

        login(USER_ADMIN);
        List<AdminLearningObject> firstReviewsAdmin = doGet(GET_UNREVIEWED, listTypeAdminLO());
        assertTrue(CollectionUtils.isNotEmpty(firstReviewsAdmin));

        assertNotEquals("Admin UnReviewed list, Moderator UnReviewed list", firstReviewsAdmin, firstReviewsModerator);
    }

    @Test
    public void unreviewed_learningObject_is_unreviewed() throws Exception {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_2);
        assertEquals("Is reviewed", 1, material.getUnReviewed());
    }

    @Test
    public void unreviewed_learningObject_after_being_set_reviewed_is_reviewed() throws Exception {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_15);
        assertEquals("Is Reviewed",1, material.getUnReviewed());

        Response updateResponse = doPost(SET_REVIEWED, material);
        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());

        Material materialAfter = getMaterial(MATERIAL_15);
        assertEquals("Is Reviewed",0, materialAfter.getUnReviewed());
    }

    @Test
    public void unAuthorized_user_can_not_view() throws Exception {
        login(USER_MATI);
        List<AdminLearningObject> firstReviews = doGet(GET_UNREVIEWED, listTypeAdminLO());
        assertNull("UnReviewed list", firstReviews);
    }

    private GenericType<List<AdminLearningObject>> listTypeAdminLO() {
        return new GenericType<List<AdminLearningObject>>() {
        };
    }

}
