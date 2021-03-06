package ee.hm.dop.dao;

import ee.hm.dop.model.PortfolioLog;
import ee.hm.dop.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.time.LocalDateTime.now;

@Repository
public class PortfolioLogDao extends AbstractDao<PortfolioLog> {

    public Class<PortfolioLog> entity() {
        return PortfolioLog.class;
    }

    public List<PortfolioLog> findAllPortfolioLogsByLoId(Long learningObjectId) {
        return getEntityManager()
                .createQuery("" +
                                "SELECT p FROM PortfolioLog p \n" +
                                "   WHERE p.learningObject = :id " +
                                "ORDER BY p.createdAt DESC"
                        , PortfolioLog.class)
                .setParameter("id", learningObjectId)
                .getResultList();
    }

    public List<PortfolioLog> findPortfolioLogsByLoIdAndUserId(Long learningObjectId, User creatorid) {
        return getEntityManager()
                .createQuery("" +
                                "SELECT p FROM PortfolioLog p \n" +
                                " WHERE p.learningObject = :learningObjectId" +
                                " AND p.creator = :creatorid " +
                                "ORDER BY p.createdAt DESC"
                        , PortfolioLog.class)
                .setParameter("learningObjectId", learningObjectId)
                .setParameter("creatorid", creatorid)
                .getResultList();
    }

    @Override
    public PortfolioLog createOrUpdate(PortfolioLog entity) {
        return super.createOrUpdate(entity);
    }
}
