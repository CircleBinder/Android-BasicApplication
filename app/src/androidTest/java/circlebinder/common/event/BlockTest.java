package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class BlockTest {

    @Test
    public void testParcelable() {
        Block expect = new BlockBuilder()
                .setId(653115)
                .setName("ブロック名")
                .setArea(new AreaBuilder().setName("area").build())
                .build();

        try {
            Block got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getId(), got.getId());
            Assert.assertEquals(expect.getName(), got.getName());
            Assert.assertEquals(expect.getArea().getName(), got.getArea().getName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
