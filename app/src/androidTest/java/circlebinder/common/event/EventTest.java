package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class EventTest {

    @Test
    public void testParcelable() {
        Event expect = new EventBuilder()
                .setName("イベント名")
                .setDay(new EventDayBuilder().setName("今日").build())
                .setLocation(new LocationBuilder().setDisplayName("場所").build())
                .build();

        try {
            Event got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getName(), got.getName());
            Assert.assertEquals(expect.getDay(), got.getDay());
            Assert.assertEquals(expect.getLocation().getDisplayName(), got.getLocation().getDisplayName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
