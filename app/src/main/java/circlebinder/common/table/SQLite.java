package circlebinder.common.table;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.ichigotake.sqlitehelper.DatabaseHelper;

public class SQLite {
    
    private static DatabaseHelper helper;
    
    public static synchronized DatabaseHelper getHelper(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (helper == null) {
            helper = new DatabaseHelper(applicationContext, new SQLiteConfiguration());
        }
        return helper;
    }

    public static synchronized SQLiteDatabase getDatabase(Context context) {
        return getHelper(context).getWritableDatabase();
    }

    public static void destroy(Context context) {
        getHelper(context).close();
    }

    private SQLite() {}
}
