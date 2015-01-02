package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class EventDayTest {

    @Test
    public void testParcelable() {
        EventDay expect = new EventDayBuilder()
                .setName("今日")
                .build();

        try {
            EventDay got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect, got);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
