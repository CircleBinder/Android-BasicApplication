package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class LocationTest {

    @Test
    public void testParcelable() {
        Location expect = new LocationBuilder()
                .setDisplayName("大田区")
                .setLink("scheme://i-am/location/scheme!")
                .build();

        try {
            Location got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getDisplayName(), got.getDisplayName());
            Assert.assertEquals(expect.getLink(), got.getLink());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
