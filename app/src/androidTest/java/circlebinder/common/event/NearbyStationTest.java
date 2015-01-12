package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class NearbyStationTest {

    @Test
    public void testParcelable() {
        NearbyStation expect = new NearbyStationBuilder()
                .setDisplay("最寄り駅")
                .build();

        try {
            NearbyStation got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getDisplay(), got.getDisplay());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
