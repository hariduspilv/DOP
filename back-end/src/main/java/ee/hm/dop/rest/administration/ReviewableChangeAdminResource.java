package ee.hm.dop.rest.administration;

import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin/changed/")
public class ReviewableChangeAdminResource extends BaseResource {

    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private LearningObjectService learningObjectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ReviewableChange> getChanged() {
        return reviewableChangeAdminService.getUnReviewed(getLoggedInUser());
    }

    @GET
    @Path("count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getCount() {
        return reviewableChangeAdminService.getUnReviewedCount(getLoggedInUser());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ReviewableChange> getChange(@PathParam("id") long id) {
        return reviewableChangeService.getAllByLearningObject(id);
    }

    @GET
    @Path("{id}/acceptAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response acceptAllChanges(@PathParam("id") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject, ReviewStatus.ACCEPTED, ReviewType.CHANGE);
        return ok();
    }

    @GET
    @Path("{id}/revertAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathParam("id") long id) {
        return reviewableChangeService.revertAllChanges(id, getLoggedInUser());
    }

}
