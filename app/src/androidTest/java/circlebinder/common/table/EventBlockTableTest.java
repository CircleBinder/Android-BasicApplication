package circlebinder.common.table;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import circlebinder.common.event.Block;
import circlebinder.common.event.EventBlockType;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class EventBlockTableTest {

    @Test
    public void testInsert() {
        String sampleBlockName = "block_name";
        EventBlockType sampleBlockType = EventBlockType.一般的なスタイル;
        EventBlockTableForInsert forInsert = new EventBlockTableForInsert.Builder()
                .setName(sampleBlockName)
                .setType(sampleBlockType)
                .build();
        SQLiteDatabase database = SQLite.getDatabase(Robolectric.application);
        EventBlockTable.insert(database, forInsert);
        Block got = EventBlockTable.getAll(database).get(0);
        Assert.assertEquals(sampleBlockName, got.getName());
        Assert.assertEquals(sampleBlockType, got.getType());
    }
    
    @After
    public void sweep() {
        SQLite.destroy(Robolectric.application);
    }
    
}
