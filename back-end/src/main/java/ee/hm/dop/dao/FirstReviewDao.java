package ee.hm.dop.dao;

import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.User;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

public class FirstReviewDao extends AbstractDao<FirstReview> {

    @Inject
    private TaxonDao taxonDao;

    public List<FirstReview> findAllUnreviewedOld() {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setMaxResults(300)
                .getResultList();
    }

    public List<FirstReview> findAllUnreviewedOld(User user) {
        return getEntityManager()
                .createNativeQuery("SELECT f.*\n" +
                        "FROM FirstReview f\n" +
                        "  JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "  JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "  AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )" +
                        "  AND lt.taxon IN (:taxonIds)\n" +
                        "ORDER BY f.createdAt ASC, f.id ASC", entity())
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(300)
                .getResultList();
    }

    public List<AdminLearningObject> findAllUnreviewed() {
        return getEntityManager()
                .createQuery("SELECT DISTINCT lo\n" +
                        "FROM AdminLearningObject lo\n" +
                        "JOIN FETCH lo.firstReviews r " +
                        "WHERE r.reviewed = 0 " +
                        "   AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "   AND NOT exists(SELECT 1\n" +
                        "                     FROM ImproperContent ic\n" +
                        "                     WHERE ic.learningObject = lo\n" +
                        "                           AND ic.reviewed = 0)\n" +
                        "   AND NOT exists(SELECT 1\n" +
                        "                     FROM BrokenContent ic\n" +
                        "                     WHERE ic.material = lo\n" +
                        "                           AND ic.deleted = 0)" +
                        "ORDER BY r.createdAt ASC, r.id ASC", AdminLearningObject.class)
                .setMaxResults(300)
                .getResultList();
    }


    public List<AdminLearningObject> findAllUnreviewed(User user) {
        return getEntityManager()
                .createQuery("SELECT DISTINCT lo \n" +
                        "FROM AdminLearningObject lo \n" +
                        "JOIN FETCH lo.firstReviews r " +
                        "JOIN lo.taxons lt " +
                        "WHERE r.reviewed = 0 " +
                        "   AND (lo.visibility = 'PUBLIC' OR lo.visibility = 'NOT_LISTED')\n" +
                        "   AND NOT exists(SELECT 1\n" +
                        "                     FROM ImproperContent ic\n" +
                        "                     WHERE ic.learningObject = lo\n" +
                        "                           AND ic.reviewed = 0)\n" +
                        "   AND NOT exists(SELECT 1\n" +
                        "                     FROM BrokenContent ic\n" +
                        "                     WHERE ic.material = lo\n" +
                        "                           AND ic.deleted = 0)" +
                        "   AND lt.id in (:taxonIds)" +
                        "ORDER BY r.createdAt ASC, r.id ASC", AdminLearningObject.class)
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .setMaxResults(300)
                .getResultList();
    }

    public long findCountOfUnreviewed() {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(1) AS c\n" +
                        "FROM FirstReview f\n" +
                        "   JOIN LearningObject o ON f.learningObject = o.id\n" +
                        "WHERE f.reviewed = 0\n" +
                        "   AND (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = f.learningObject " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = f.learningObject" +
                        "                   AND bc.deleted = 0 )")
                .getSingleResult()).longValue();
    }

    public long findCountOfUnreviewed(User user) {
        return ((BigInteger) getEntityManager()
                .createNativeQuery("SELECT count(DISTINCT o.id) AS c\n" +
                        "FROM LearningObject o\n" +
                        "   JOIN LearningObject_Taxon lt ON lt.learningObject = o.id\n" +
                        "WHERE (o.visibility = 'PUBLIC' OR o.visibility = 'NOT_LISTED')\n" +
                        "  AND exists(SELECT 1 FROM FirstReview ic " +
                        "                   WHERE ic.learningObject = o.id " +
                        "                   AND ic.reviewed = 0)" +
                        "  AND NOT exists(SELECT 1 FROM ImproperContent ic " +
                        "                   WHERE ic.learningObject = o.id " +
                        "                   AND ic.reviewed = 0)\n" +
                        "  AND NOT exists(SELECT 1 FROM BrokenContent bc " +
                        "                   WHERE bc.material = o.id" +
                        "                   AND bc.deleted = 0 ) " +
                        "  AND lt.taxon IN (:taxonIds)")
                .setParameter("taxonIds", taxonDao.getUserTaxonsWithChildren(user))
                .getSingleResult()).longValue();
    }
}
