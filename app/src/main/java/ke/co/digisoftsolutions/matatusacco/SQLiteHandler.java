package ke.co.digisoftsolutions.matatusacco;
//imports
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sacco";

    // Login table name
    private static final String TABLE_USER = "0_members";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PIN = "pin";
    private static final String KEY_CONFIRMPIN = "confirmpin";
    private static final String KEY_IDNO = "idno";
    private static final String KEY_CREATED_AT = "created_at";
    private Integer pin;
    private Integer confirmpin;
    private Integer idno;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = new StringBuilder().append(" CREATE TABLE ").append(TABLE_USER).append(" (").append(KEY_ID).append(" INTEGER PRIMARY KEY,").append(KEY_PIN).append(" TEXT,").append(KEY_CONFIRMPIN).append(" TEXT ,").append(KEY_IDNO).append(" TEXT,").append(KEY_CREATED_AT).append(" TEXT").append(")").toString();
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(new StringBuilder().append(" DROP TABLE IF EXISTS ").append(TABLE_USER).toString());

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String pin, String confirmpin, String idno, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PIN, pin); // pin
        values.put(KEY_CONFIRMPIN, confirmpin); // confirm pin
        values.put(KEY_IDNO, idno); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = new StringBuilder().append("SELECT  * FROM ").append(TABLE_USER).toString();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("pin", cursor.getString(1));
            user.put("confirmpin", cursor.getString(2));
            user.put("idno", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


}
