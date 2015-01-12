package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class GenreTest {

    @Test
    public void testParcel() {
        Genre expect = new GenreBuilder()
                .setId(653115)
                .setName("ジャンル！")
                .build();

        try {
            Genre got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getId(), got.getId());
            Assert.assertEquals(expect.getName(), got.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
