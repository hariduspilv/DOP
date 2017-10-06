package ee.hm.dop.service.content;

import ee.hm.dop.dao.FirstReviewDao;
import ee.hm.dop.model.FirstReview;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

import java.math.BigInteger;
import java.util.List;

import static org.joda.time.DateTime.now;

public class FirstReviewService {

    @Inject
    private FirstReviewDao firstReviewDao;

    public List<FirstReview> getUnReviewed(User loggedInUser) {
        if (UserUtil.isAdmin(loggedInUser)) {
            return firstReviewDao.findAllUnreviewed();
        } else {
            return firstReviewDao.findAllUnreviewed(loggedInUser);
        }
    }

    public BigInteger getUnReviewedCount(User loggedInUser) {
        if (UserUtil.isAdmin(loggedInUser)) {
            return firstReviewDao.findCountOfUnreviewed();
        } else {
            return firstReviewDao.findCountOfUnreviewed(loggedInUser);
        }
    }

    public FirstReview save(LearningObject learningObject) {
        FirstReview firstReview = new FirstReview();
        firstReview.setLearningObject(learningObject);
        firstReview.setReviewed(false);
        firstReview.setCreatedAt(now());

        return firstReviewDao.createOrUpdate(firstReview);
    }

    public void setReviewed(LearningObject learningObject, User loggedInUser, ReviewStatus reviewStatus) {
        for (FirstReview firstReview : learningObject.getFirstReviews()) {
            if (!firstReview.isReviewed()) {
                firstReview.setReviewedAt(DateTime.now());
                firstReview.setReviewedBy(loggedInUser);
                firstReview.setReviewed(true);
                firstReview.setStatus(reviewStatus);
                firstReviewDao.createOrUpdate(firstReview);
            }
        }
    }
}
