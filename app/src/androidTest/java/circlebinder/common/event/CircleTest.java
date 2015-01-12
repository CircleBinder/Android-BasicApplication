package circlebinder.common.event;

import android.net.Uri;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.test.ParcelUtil;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public final class CircleTest {

    @Test
    public void testParcel() {
        Circle expect = new CircleBuilder()
                .setId(653115)
                .setName("銀河ドリーム")
                .setPenName("銀河宇宙人")
                .setChecklistColor(ChecklistColor.GREEN)
                .setGenre(new GenreBuilder().setName("銀河ジャンル").build())
                .setSpace(new SpaceBuilder().setName("銀河スペース").build())
                .addLink(new CircleLinkBuilder().setUri(Uri.parse("http://my.homepage.com")).build())
                .build();

        try {
            Circle got = ParcelUtil.restore(expect);
            Assert.assertEquals(expect.getId(), got.getId());
            Assert.assertEquals(expect.getName(), got.getName());
            Assert.assertEquals(expect.getPenName(), got.getPenName());
            Assert.assertEquals(expect.getChecklistColor(), got.getChecklistColor());
            Assert.assertEquals(expect.getGenre().getName(), got.getGenre().getName());
            Assert.assertEquals(expect.getSpace().getName(), got.getSpace().getName());
            Assert.assertEquals(expect.getLinks(), got.getLinks());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
