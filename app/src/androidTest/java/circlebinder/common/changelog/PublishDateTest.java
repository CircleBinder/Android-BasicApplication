package circlebinder.common.changelog;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class PublishDateTest {

    @Test
    public void testParselable() {
        PublishDate expect = new PublishDate.Builder()
                .setFormattedDate("2013-04-05")
                .setTimestamp(100)
                .build();

        try {
            PublishDate got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getFormattedDate(), got.getFormattedDate());
            Assert.assertEquals(expect.getTimestamp(), got.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
