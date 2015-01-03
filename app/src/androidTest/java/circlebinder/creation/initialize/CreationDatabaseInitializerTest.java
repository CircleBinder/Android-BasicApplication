package circlebinder.creation.initialize;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import junit.framework.Assert;

import net.ichigotake.common.util.Each;
import net.ichigotake.common.util.TupleCollections;
import net.ichigotake.common.util.Tuple;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import circlebinder.common.event.Block;
import circlebinder.common.event.Circle;
import circlebinder.common.search.CircleCursorConverter;
import circlebinder.common.table.EventBlockTable;
import circlebinder.common.table.EventBlockTableForInsert;
import circlebinder.common.table.EventCircleTableForInsert;
import circlebinder.common.table.SQLite;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class CreationDatabaseInitializerTest {

    @Test
    public void testInitialize() throws IOException {
        Context context = Robolectric.application;

        Assert.assertEquals(0, new EventBlockTable(context).getAll().size());
        Assert.assertEquals(0, new SQLite(context).findAll().getCount());

        final List<EventBlockTableForInsert> expectedBlock = new ArrayList<>();
        // before blocks initialized
        {
            InputStream blocksInputStream = createBlocksInputStream();
            new LTSVReader<>(blocksInputStream, new EventBlockParser(), new LTSVReadLineListener<EventBlockTableForInsert>() {
                @Override
                public void onLineRead(EventBlockTableForInsert item) {
                    expectedBlock.add(item);
                }
            }).read();
            Assert.assertTrue(expectedBlock.size() > 0);
        }

        // initialize
        {
            SQLiteDatabase database = SQLite.getWritableDatabase(context);
            try {
                database.beginTransaction();
                CreationDatabaseInitializer initializer = new CreationDatabaseInitializer(context);
                initializer.initializeBlocks(database, createBlocksInputStream());
                initializer.initializeCircles(database, createCirclesInputStream());
                database.setTransactionSuccessful();
            } catch (SQLiteException e) {
                e.getCause().printStackTrace();
                Assert.fail();
            } finally {
                database.endTransaction();
            }
        }

        // before circles initialized
        final List<EventCircleTableForInsert> expectedCircles = new ArrayList<>();
        final Cursor gotCircleCursor = new SQLite(context).findAll();
        final List<Circle> gotCircles = new ArrayList<>();
        {
            InputStream circlesInputStream = createCirclesInputStream();
            new LTSVReader<>(circlesInputStream, new EventCircleParser(context, SQLite.getWritableDatabase(context)), new LTSVReadLineListener<EventCircleTableForInsert>() {
                @Override
                public void onLineRead(EventCircleTableForInsert item) {
                    expectedCircles.add(item);
                }
            }).read();
            Assert.assertTrue(expectedCircles.size() > 0);
        }

        // after blocks initialized
        List<Block> gotBlocks = new EventBlockTable(context).getAll();
        Assert.assertTrue(gotBlocks.size() > 0);
        Assert.assertEquals(expectedBlock.size(), gotBlocks.size());
        TupleCollections.from(expectedBlock, gotBlocks)
                .each(new Each<Tuple<EventBlockTableForInsert, Block>>() {
                    @Override
                    public void each(Tuple<EventBlockTableForInsert, Block> item) {
                        boolean equaled = TextUtils.equals(item.item1.getName(), item.item2.getName())
                                && item.item1.getTypeId() == item.item2.getType().getTypeId();
                        if (!equaled) {
                            Assert.fail();
                        }
                    }
                });

        // after circles initialized
        while (gotCircleCursor.moveToNext()) {
            gotCircles.add(new CircleCursorConverter(context).create(gotCircleCursor));
        }
        Assert.assertTrue(expectedCircles.size() > 0);
        Assert.assertEquals(expectedCircles.size(), gotCircles.size());
        TupleCollections.from(expectedCircles, gotCircles)
                .each(new Each<Tuple<EventCircleTableForInsert, Circle>>() {
                    @Override
                    public void each(Tuple<EventCircleTableForInsert, Circle> item) {
                        boolean equaled = TextUtils.equals(item.item1.getCircleName(), item.item2.getName())
                                || TextUtils.equals(item.item1.getPenName(), item.item2.getPenName());
                        if (!equaled) {
                            Assert.fail();
                        }
                    }
                });
        Assert.assertTrue(true);
    }

    private InputStream createBlocksInputStream() throws FileNotFoundException {
        return new FileInputStream("./src/creation/res/raw/creation_spaces.txt");
    }

    private InputStream createCirclesInputStream() throws FileNotFoundException {
        return new FileInputStream("./src/creation/res/raw/creation_circles.txt");
    }

    @After
    public void sweep() {
        SQLite.destroy(Robolectric.application);
    }

}
