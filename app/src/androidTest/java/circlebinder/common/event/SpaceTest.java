package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class SpaceTest {

    @Test
    public void testParcelable() {
        Space expect = new SpaceBuilder()
                .setName("桃")
                .setSimpleName("P")
                .setNo(33)
                .setNoSub("c")
                .setBlockName("ブロック名")
                .build();

        try {
            Space got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getName(), got.getName());
            Assert.assertEquals(expect.getNo(), got.getNo());
            Assert.assertEquals(expect.getNoSub(), got.getNoSub());
            Assert.assertEquals(expect.getBlockName(), got.getBlockName());
            Assert.assertEquals(expect.getSimpleName(), got.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
