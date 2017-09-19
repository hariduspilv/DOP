package ee.hm.dop.service.content;

import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.model.*;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ImproperContentService {

    @Inject
    private ImproperContentDao improperContentDao;
    @Inject
    private LearningObjectService learningObjectService;

    public ImproperContent addImproper(ImproperContent improperContent, User creator) {
        LearningObject learningObject = findValid(improperContent, creator);

        ImproperContent improper = new ImproperContent();
        improper.setCreator(creator);
        improper.setAdded(DateTime.now());
        improper.setLearningObject(learningObject);
        improper.setReason(improperContent.getReason());

        return improperContentDao.createOrUpdate(improper);
    }

    private LearningObject findValid(ImproperContent improperContent, User creator) {
        if (improperContent == null || improperContent.getLearningObject() == null) {
            throw new RuntimeException("Invalid Improper object.");
        }
        LearningObject learningObject = learningObjectService.get(improperContent.getLearningObject().getId(), creator);
        if (learningObject == null) {
            throw new RuntimeException("LearningObject does not exists.");
        }
        return learningObject;
    }

    /**
     * @param improperContentId
     * @param user              who wants access to the list
     * @return the ImproperContent if user has rights to access
     */
    public ImproperContent get(long improperContentId, User user) {
        ImproperContent improperContent = improperContentDao.findByIdValid(improperContentId);
        if (improperContent != null && !learningObjectService.canAcess(user, improperContent.getLearningObject())) {
            return null;
        }
        return improperContent;
    }

    /**
     * @param user who wants access to the list
     * @return a list of improperContent that user has rights to access
     */
    public List<ImproperContent> getAll(User user) {
        List<ImproperContent> impropers = improperContentDao.findAllValid();
        removeIfHasNoAccess(user, impropers);
        return impropers;
    }

    public List<ImproperContent> getImproperMaterials(User user) {
        List<ImproperContent> impropers = getAll(user);
        return impropers.stream()
                .filter(imp -> imp.getLearningObject() instanceof Material)
                .collect(Collectors.toList());
    }

    public List<ImproperContent> getImproperPortfolios(User user) {
        return getAll(user).stream()
                .filter(imp -> imp.getLearningObject() instanceof Portfolio && !imp.getLearningObject().isDeleted())
                .collect(Collectors.toList());
    }

    public long getImproperMaterialSize(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        return improperContentDao.getImproperMaterialCount();
    }

    public long getImproperPortfolioSize(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        return improperContentDao.getImproperPortfolioCount();
    }

    /**
     * @param learningObject
     * @param creator        who created the ImproperContent
     * @param user           who wants access to the list
     * @return the ImproperContent which refers to learningObject, created by
     * creator and user has rights to access
     */
    public ImproperContent getByLearningObjectAndCreator(LearningObject learningObject, User creator, User user) {
        ImproperContent improperContent = improperContentDao.findByLearningObjectAndCreator(learningObject, creator);
        if (improperContent != null && !learningObjectService.canAcess(user, improperContent.getLearningObject())) {
            return null;
        }
        return improperContent;
    }

    /**
     * @param user who wants access to the list
     * @return a list of improperContent which refers to learningObject and user
     * has rights to access
     */
    public List<ImproperContent> getByLearningObject(LearningObject learningObject, User user) {
        List<ImproperContent> impropers = improperContentDao.findByLearningObject(learningObject);
        removeIfHasNoAccess(user, impropers);
        return impropers;
    }

    /**
     * @param impropers the list of ImproperContent to be deleted
     * @param user      who wants to delete the improper
     */
    public void deleteAll(List<ImproperContent> impropers, User user) {
        removeIfHasNoAccess(user, impropers);
        improperContentDao.deleteAll(impropers);
    }

    private void removeIfHasNoAccess(User user, List<ImproperContent> impropers) {
        impropers.removeIf(improper -> !learningObjectService.canAcess(user, improper.getLearningObject()));
    }
}
