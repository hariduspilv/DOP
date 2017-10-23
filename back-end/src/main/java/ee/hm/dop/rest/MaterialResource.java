package ee.hm.dop.rest;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.Like;
import ee.hm.dop.service.content.*;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.proxy.MaterialProxy;
import ee.hm.dop.service.reviewmanagement.BrokenContentService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.service.useractions.UserService;
import ee.hm.dop.utils.NumberUtils;

@Path("material")
public class MaterialResource extends BaseResource {

    @Inject
    private MaterialService materialService;
    @Inject
    private UserService userService;
    @Inject
    private MaterialProxy materialProxy;
    @Inject
    private UserLikeService userLikeService;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;
    @Inject
    private BrokenContentService brokenContentService;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private MaterialGetter materialGetter;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@QueryParam("materialId") long materialId) {
        return materialGetter.get(materialId, getLoggedInUser());
    }

    @GET
    @Path("getBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getMaterialsByUrl(@QueryParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getBySource(decode(materialSource), GetMaterialStrategy.ONLY_EXISTING);
    }

    @GET
    @Path("getOneBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Material getMaterialByUrl(@QueryParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getOneBySource(decode(materialSource), GetMaterialStrategy.INCLUDE_DELETED);
    }

    @POST
    @Path("increaseViewCount")
    public Response increaseViewCount(Material material) {
        Material originalMaterial = materialGetter.get(material.getId(), getLoggedInUser());
        if (originalMaterial == null) {
            throw notFound();
        }
        learningObjectService.incrementViewCount(originalMaterial);
        return Response.status(HttpURLConnection.HTTP_OK).build();
    }

    @POST
    @Path("like")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void likeMaterial(Material material) {
        userLikeService.addUserLike(material, getLoggedInUser(), Like.LIKE);
    }

    @POST
    @Path("dislike")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void dislikeMaterial(Material material) {
        userLikeService.addUserLike(material, getLoggedInUser(), Like.DISLIKE);
    }

    @POST
    @Path("getUserLike")
    public UserLike getUserLike(Material material) {
        return userLikeService.getUserLike(material, getLoggedInUser());
    }

    @POST
    @Path("removeUserLike")
    public void removeUserLike(Material material) {
        userLikeService.removeUserLike(material, getLoggedInUser());
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@QueryParam("username") String username, @QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorResult(creator, start, NumberUtils.zvl(maxResults, 12)) : null;
    }

    @GET
    @Path("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getByCreatorCount(@QueryParam("username") String username) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorSize(creator) : null;
    }

    private User getValidCreator(@QueryParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }

    @DELETE
    @Path("{materialID}")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public void delete(@PathParam("materialID") Long materialID) {
        Material material = materialGetter.get(materialID, getLoggedInUser());
        if (material == null) {
            throw notFound();
        }
        learningObjectAdministrationService.delete(material, getLoggedInUser());
    }

    @PUT
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material createOrUpdateMaterial(Material material) {
        if (material.getId() == null) {
            return materialService.createMaterial(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        }
        return materialService.update(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
    }

    @POST
    @Path("setBroken")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public BrokenContent setBrokenMaterial(Material material) {
        return brokenContentService.save(material, getLoggedInUser());
    }

    @GET
    @Path("hasSetBroken")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean hasSetBroken(@QueryParam("materialId") long materialId) {
        User user = getLoggedInUser();
        return user != null ? brokenContentService.hasSetBroken(materialId, getLoggedInUser()) : false;
    }
}
