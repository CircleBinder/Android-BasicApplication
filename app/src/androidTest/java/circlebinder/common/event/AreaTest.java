package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class AreaTest {

    @Test
    public void testParcelable() {
        Area expect = new AreaBuilder()
                .setName("Name!")
                .setSimpleName("SimpleName!")
                .build();

        try {
            Area got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getName(), got.getName());
            Assert.assertEquals(expect.getSimpleName(), got.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
