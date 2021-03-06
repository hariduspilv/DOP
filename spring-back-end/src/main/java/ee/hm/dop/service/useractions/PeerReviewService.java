package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.PeerReviewDao;
import ee.hm.dop.model.PeerReview;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class PeerReviewService {

    @Inject
    private PeerReviewDao peerReviewDao;

    public PeerReview getPeerReviewByURL(String url) {
        return peerReviewDao.getPeerReviewByURL(url);
    }

    public List<PeerReview> getPeerReviewByURL(List<String> url) {
        return peerReviewDao.getPeerReviewByURL(url);
    }

    public PeerReview createPeerReview(String url) {
        PeerReview peerReview = new PeerReview();
        peerReview.setUrl(url);
        return peerReviewDao.createOrUpdate(peerReview);
    }

}
