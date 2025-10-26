package me.etylix.configgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
public class SocDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "soc_config-v1.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SOC_TIERS = "soc_tiers";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SOC_MODEL = "soc_model";
    public static final String COLUMN_TIER = "tier";

    // Câu lệnh tạo bảng
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_SOC_TIERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SOC_MODEL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_TIER + " TEXT NOT NULL" +
                    ");";

    public SocDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i("SocDatabaseHelper", "Database table created.");
        populateInitialData(db);
    }

    // Sample data
    private void populateInitialData(SQLiteDatabase db) {
        String[][] initialData = {
                {"SM8750", "high"},
                {"SM8650", "high"},
                {"MT6989", "high"},
                {"SM7675", "medplus"},
                {"MT6896", "medplus"},
                {"MT6789", "med"},
                {"SM7325", "med"},
                {"SM6450", "med"},
                {"SM4350", "low"},
                {"MT6768", "low"}
        };

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String[] entry : initialData) {
                values.put(COLUMN_SOC_MODEL, entry[0]);
                values.put(COLUMN_TIER, entry[1]);
                db.insert(TABLE_SOC_TIERS, null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
            Log.i("SocDatabaseHelper", "Initial data populated.");
        } catch (Exception e) {
            Log.e("SocDatabaseHelper", "Error populating initial data", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SocDatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOC_TIERS);
        onCreate(db);
    }

    public String getTierForSoc(String socModel) {
        if (socModel == null || socModel.isEmpty()) {
            return "low";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String tier = "low";

        try {
            String selection = COLUMN_SOC_MODEL + " = ?";
            String[] selectionArgs = { socModel.toUpperCase() };

            cursor = db.query(
                    TABLE_SOC_TIERS,
                    new String[]{COLUMN_TIER},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int tierIndex = cursor.getColumnIndex(COLUMN_TIER);
                if(tierIndex != -1) {
                    tier = cursor.getString(tierIndex);
                }
            }
        } catch (Exception e) {
            Log.e("SocDatabaseHelper", "Error querying tier for SOC: " + socModel, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tier;
    }
}
