package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class TimestampTest {

    @Test
    public void testParcelable() {
        Timestamp expect = new TimestampBuilder()
                .setDisplayName("2000-11-18")
                .setTimestamp(653115)
                .build();

        try {
            Timestamp got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getDisplayName(), got.getDisplayName());
            Assert.assertEquals(expect.getTimestamp(), got.getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
