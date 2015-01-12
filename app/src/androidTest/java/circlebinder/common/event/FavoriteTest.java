package circlebinder.common.event;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class FavoriteTest {
    
    @Test
    public void testParcelable() {
        Favorite expect = new FavoriteBuilder()
                .setChecklistColor(ChecklistColor.LIGHT_BLUE)
                .setCircle(new CircleBuilder().setName("サークル名").build())
                .build();

        try {
            Favorite got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getChecklist(), got.getChecklist());
            Assert.assertEquals(expect.getCircle().getName(), got.getCircle().getName());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
