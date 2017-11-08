package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;

import ee.hm.dop.model.ChapterObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.Visibility;

public class ChapterObjectDao extends AbstractDao<ChapterObject> {

    public ChapterObject update(ChapterObject chapterObject) {
        chapterObject.setLastInteraction(now());
        chapterObject.setUpdated(now());
        chapterObject.setAdded(now());
        chapterObject.setVisibility(Visibility.PUBLIC);
        return createOrUpdate(chapterObject);
    }
}
