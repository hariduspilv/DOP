package ee.hm.dop.dao;

import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;

public class LearningObjectDAO extends BaseDAO {

    @Inject
    protected EntityManager entityManager;

    public LearningObject findByIdNotDeleted(long objectId) {
        TypedQuery<LearningObject> findByCode = entityManager.createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.id = :id AND lo.deleted = false", LearningObject.class) //
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public LearningObject findById(long objectId) {
        TypedQuery<LearningObject> findByCode = entityManager.createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.id = :id", LearningObject.class) //
                .setParameter("id", objectId);

        return getSingleResult(findByCode);
    }

    public List<LearningObject> getDeletedLearningObjects() {
        TypedQuery<LearningObject> query = entityManager.createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.deleted = true", LearningObject.class);
        return query.getResultList();
    }

    /**
     * finds all LearningObjects contained in the idList. There is no guarantee
     * about in which order the LearningObjects will be in the result list.
     *
     * @param idList
     *            the list with LearningObject ids
     * @return a list of LearningObject specified by idList
     */
    public List<LearningObject> findAllById(List<Long> idList) {
        TypedQuery<LearningObject> findAllByIdList = entityManager.createQuery(
                "SELECT lo FROM LearningObject lo WHERE lo.deleted = false AND lo.id in :idList", LearningObject.class);
        return findAllByIdList.setParameter("idList", idList).getResultList();
    }

    public List<LearningObject> findNewestLearningObjects(int numberOfLearningObjects) {

        return entityManager
                .createQuery("FROM LearningObject lo WHERE lo.deleted = false ORDER BY added desc",
                        LearningObject.class).setMaxResults(numberOfLearningObjects).getResultList();
    }

    public List<LearningObject> findPopularLearningObjects(int numberOfLearningObjects) {
        return entityManager
                .createQuery("FROM LearningObject lo WHERE lo.deleted = false ORDER BY views DESC",
                        LearningObject.class).setMaxResults(numberOfLearningObjects).getResultList();
    }

    public LearningObject update(LearningObject learningObject) {
        if (learningObject.getId() != null) {
            learningObject.setUpdated(DateTime.now());
        } else {
            learningObject.setAdded(DateTime.now());
        }

        LearningObject merged = entityManager.merge(learningObject);
        entityManager.persist(merged);
        return merged;
    }

    public void delete(LearningObject learningObject) {
        setDeleted(learningObject, true);
    }

    public void restore(LearningObject learningObject) {
        setDeleted(learningObject, false);
    }

    private void setDeleted(LearningObject learningObject, boolean deleted) {
        if (learningObject.getId() == null) {
            throw new InvalidParameterException("LearningObject does not exist.");
        }

        learningObject.setDeleted(deleted);
        update(learningObject);
    }

    /**
     * For testing purposes.
     *
     * @param learningObject
     */
    protected void remove(LearningObject learningObject) {
        entityManager.remove(learningObject);
    }

    protected byte[] getBytes(LearningObject learningObject, TypedQuery<byte[]> findById) {
        findById.setParameter("id", learningObject.getId());
        return getSingleResult(findById);
    }

    /**
     * Find all LearningObjects with the specified creator. LearningObjects are
     * ordered by added date with newest first.
     *
     * @param creator
     *            User who created the LearningObjects
     * @return A list of LearningObject
     */
    public List<LearningObject> findByCreator(User creator) {
        String query = "SELECT lo FROM LearningObject lo WHERE lo.creator.id = :creatorId AND lo.deleted = false order by added desc";
        TypedQuery<LearningObject> findAllByCreator = entityManager.createQuery(query, LearningObject.class);
        return findAllByCreator.setParameter("creatorId", creator.getId()).getResultList();
    }

    protected <T> void removeNot(Class<T> clazz, List<LearningObject> learningObjects) {
        Iterator<LearningObject> iterator = learningObjects.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getClass() != clazz) {
                iterator.remove();
            }
        }
    }

    protected <T> T castTo(Class<T> clazz, LearningObject learningObject) {
        if (learningObject != null && learningObject.getClass() == clazz) {
            return clazz.cast(learningObject);
        }

        return null;
    }

    public synchronized void incrementViewCount(LearningObject learningObject) {
        entityManager
                .createQuery(
                        "update LearningObject lo set lo.views = lo.views + 1 where lo.id = :id AND lo.deleted = false")
                .setParameter("id", learningObject.getId()).executeUpdate();
        entityManager.flush();
    }
}
