package circlebinder.common.changelog;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class ChangeLogFeedTest {

    @Test
    public void testParcelable() {
        int versionCode = 2;
        String versionName = "0.2";
        ChangeLogFeedType type = ChangeLogFeedType.BUG;
        PublishDate publishDate = MockPublishDate.create();
        String title = "不具合を修正しました";
        ChangeLogFeed feed = new ChangeLogFeed(
                versionCode,
                versionName,
                type,
                publishDate,
                title
        );

        Assert.assertEquals(versionCode, feed.getVersionCode());
        Assert.assertEquals(versionName, feed.getVersionName());
        Assert.assertEquals(title, feed.getTitle());
        Assert.assertEquals(type, feed.getType());
        Assert.assertEquals(publishDate.getFormattedDate(), feed.getPublishDate().getFormattedDate());
        Assert.assertEquals(publishDate.getTimestamp(), feed.getPublishDate().getTimestamp());

        try {
            ChangeLogFeed restoredFeed = ParcelUtil.restore(feed);

            Assert.assertEquals(feed.getVersionCode(), restoredFeed.getVersionCode());
            Assert.assertEquals(feed.getVersionName(), restoredFeed.getVersionName());
            Assert.assertEquals(feed.getTitle(), restoredFeed.getTitle());
            Assert.assertEquals(feed.getType(), restoredFeed.getType());
            Assert.assertEquals(feed.getPublishDate().getFormattedDate(), restoredFeed.getPublishDate().getFormattedDate());
            Assert.assertEquals(feed.getPublishDate().getTimestamp(), restoredFeed.getPublishDate().getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
