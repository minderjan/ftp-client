package com.minderonline.ftpclient.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionDBAdapter {

    public static final String CONNECTION_ID = "connection_id";
    public static final String CONNECTION_NAME = "connection_name";
    public static final String FTP_MODE_ID = "ftp_mode_id";
    public static final String HOST = "host";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PORT = "port";

    private static final String DATABASE_TABLE = "tbl_connections";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx
     *            the Context within which to work
     */
    public ConnectionDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the ADFilter database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public ConnectionDBAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * close return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }

    /** Insert a new record to the table **/
    public long insertConnection(String connection_name, int ftp_mode_id, String host, int port, String username, String password){
        ContentValues initialValues = new ContentValues();
        initialValues.put(CONNECTION_NAME, connection_name);
        initialValues.put(FTP_MODE_ID, ftp_mode_id);
        initialValues.put(HOST, host);
        initialValues.put(PORT, port);
        initialValues.put(USERNAME, username);
        initialValues.put(PASSWORD, password);
        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /** Delete one Record of the Table **/
    public boolean deleteConnection(long connection_id) {

        return this.mDb.delete(DATABASE_TABLE, CONNECTION_ID + "=" + connection_id, null) > 0;
    }

    /** Get all Records of the Table **/
    public Cursor getAllConnections() {

        return this.mDb.query(DATABASE_TABLE, new String[] {
                CONNECTION_ID, CONNECTION_NAME,  FTP_MODE_ID, HOST, PORT, USERNAME, PASSWORD }, null, null, null, null, null);
    }

    /** Get one Record back **/
    public Cursor getConnection(long connection_id) throws SQLException {

        Cursor mCursor =

        this.mDb.query(true, DATABASE_TABLE, new String[] { CONNECTION_ID, CONNECTION_NAME,
                FTP_MODE_ID, HOST, PORT, USERNAME, PASSWORD}, CONNECTION_ID + "=" + connection_id, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

   /** Update one Record **/
    public boolean updateConnection(long connection_id, String connection_name,int ftp_mode, int port,
            String host, String username, String password){
        ContentValues args = new ContentValues();
        args.put(CONNECTION_ID, connection_id);
        args.put(CONNECTION_NAME, connection_name);
        args.put(FTP_MODE_ID, ftp_mode);
        args.put(PORT, port);
        args.put(HOST, host);
        args.put(USERNAME, username);
        args.put(PASSWORD, password);

        return this.mDb.update(DATABASE_TABLE, args, CONNECTION_ID + "=" + connection_id, null) >0; 
    }
}
