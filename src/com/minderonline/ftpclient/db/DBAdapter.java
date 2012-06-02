package com.minderonline.ftpclient.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.minderonline.ftpclient.db.ConnectionDBAdapter;

public class DBAdapter{

    public static final String DATABASE_NAME = "FtpClient";
    public static final int DATABASE_VERSION = 1;
    
    // Creates the first table in the Database
    public static final String CREATE_TABLE_CONNECTIONS = "create table tbl_connections ("
    + ConnectionDBAdapter.CONNECTION_ID	+" integer primary key ,"
    + ConnectionDBAdapter.CONNECTION_NAME	+" not null ,"
    + ConnectionDBAdapter.FTP_MODE_ID		+" integer not null ,"
    + ConnectionDBAdapter.HOST			+" not null ,"
    + ConnectionDBAdapter.PORT			+" integer not null ,"
    + ConnectionDBAdapter.USERNAME		+" ,"
    + ConnectionDBAdapter.PASSWORD		+" );";
    

    private final Context context; 
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    
    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        
    }

    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
        	//Add Tables
        	
        	db.execSQL(CREATE_TABLE_CONNECTIONS);
        	
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {               
            // Adding any table mods to this guy here
        }
    } 

   /**
     * open the db
     * @return this
     * @throws SQLException
     * return type: DBAdapter
     */
    public DBAdapter open() throws SQLException 
    {
    	this.DBHelper = new DatabaseHelper(this.context);
        this.setDb(this.DBHelper.getWritableDatabase());
        return this;
    }

    /**
     * close the db 
     * return type: void
     */
    public void close() 
    {
        this.DBHelper.close();
    }

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

}
