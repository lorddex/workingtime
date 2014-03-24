package org.ldlabs.workingtime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

public class MyBadgeDatabase extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "MyBadgeDatabase.db";
	
	// TIMES TABLE
	private static final String TIMES_TABLE_NAME = "times";
	private static final String TIMES_KEY_ID = "id";
	private static final String TIMES_KEY_TYPE = "type";
	private static final String TIMES_KEY_DATE = "date";
	private static final String TIMES_KEY_IOTIME = "iotime";
    private static final String TIMES_TABLE_CREATE =
                "CREATE TABLE " + TIMES_TABLE_NAME + " (" +
                TIMES_KEY_ID + " INTEGER PRIMARY KEY, " +
                TIMES_KEY_TYPE + " TEXT, " +
                TIMES_KEY_DATE + " TEXT, " +
                TIMES_KEY_IOTIME + " TEXT);";
    
    // BALANCE TABLE
    private static final String BALANCES_TABLE_NAME = "balances";
	private static final String BALANCES_KEY_ID = "id";
	private static final String BALANCES_DATE_YEAR = "year";
	private static final String BALANCES_DATE_MONTH = "month";
	private static final String BALANCES_DATE_DAY = "day";
	private static final String BALANCES_BALANCE = "balance";
    private static final String BALANCES_TABLE_CREATE =
                "CREATE TABLE " + BALANCES_TABLE_NAME + " (" +
                BALANCES_KEY_ID + " INTEGER PRIMARY KEY, " +
                BALANCES_DATE_YEAR + " TEXT, " +
                BALANCES_DATE_MONTH + " TEXT, " +
                BALANCES_DATE_DAY + " TEXT, " +
                BALANCES_BALANCE + " INTEGER(10));";

	public MyBadgeDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TIMES_TABLE_CREATE);
		db.execSQL(BALANCES_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		//Log.w("onUpgrade", "Upgrading database from version " + arg1 + " to "
        //         + arg2 + ".");
		// Drop older books table if existed
		dropTimesTable();
		dropBalanceTable();
	 
	    // create fresh books table
	    this.onCreate(db);
	    
	}
	
	// TIMES TABLE
	
	public void truncTimesTable()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TIMES_TABLE_NAME, null, null);
    }
	
	public void dropTimesTable()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TIMES_TABLE_NAME);
	}
	
	public List<BalanceEntry> getTempBals()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		List<BalanceEntry> lst = new ArrayList<BalanceEntry>();
		
		Cursor cursor = db.query(TIMES_TABLE_NAME, new String[] { TIMES_KEY_ID, TIMES_KEY_IOTIME }, TIMES_KEY_TYPE+"=\"bal\"", null, null, null, TIMES_KEY_IOTIME, null);
		 
		Log.d("", cursor.toString() + " " + cursor.getCount());
		if (cursor.moveToFirst())
		{
			do 
			{
				 Calendar c = Calendar.getInstance();
				 BalanceEntry b = new BalanceEntry(cursor.getInt(0), c, cursor.getInt(1));
				 Log.d("", b.toString());
				 lst.add(b);
			} while(cursor.moveToNext());
		}
		
		return lst;
	}
	
	public List<TimeEntry> getIns() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<TimeEntry> lst = new ArrayList<TimeEntry>();
		
	    Cursor cursor = db.query(TIMES_TABLE_NAME, new String[] { TIMES_KEY_ID,
	    		TIMES_KEY_TYPE, TIMES_KEY_DATE, TIMES_KEY_IOTIME }, TIMES_KEY_TYPE+"=\"in\"", null, null, null, TIMES_KEY_IOTIME, null);
	    
	    if (cursor.moveToFirst())
	    {
	    	do 
	    	{
	    		Calendar c = Calendar.getInstance();
	    		//Log.d("getInOfTheDay", cursor.getString(2));
	    		StringTokenizer st = new StringTokenizer(cursor.getString(2), "/");
	    		int year = Integer.parseInt(st.nextToken());
	    		int month = Integer.parseInt(st.nextToken())-1;
	    		int d = Integer.parseInt(st.nextToken());
	    		//Log.d("getInOfTheDay", ""+ year + "/"+month+"/"+d);
	    		c.clear();
	    		c.set(year, month, d);
	    		c.setTimeInMillis(Long.parseLong(cursor.getString(3)));
	    		TimeEntry t = new TimeEntry(cursor.getInt(0), cursor.getString(1), c);
	    		lst.add(t);
	    	} while(cursor.moveToNext());
	    }
	    
		return lst;
	}
	
	public List<TimeEntry> getInOfTheDay(Calendar day) {
		SQLiteDatabase db = this.getReadableDatabase();
		List<TimeEntry> lst = new ArrayList<TimeEntry>();
		
	    Cursor cursor = db.query(TIMES_TABLE_NAME, new String[] { TIMES_KEY_ID,
	    		TIMES_KEY_TYPE, TIMES_KEY_DATE, TIMES_KEY_IOTIME }, TIMES_KEY_DATE + "=?" + " AND " + TIMES_KEY_TYPE + "=\"in\"",
	            new String[] { String.valueOf(day.get(Calendar.YEAR) + "/" + day.get(Calendar.MONTH) + "/" + day.get(Calendar.DAY_OF_MONTH)) }, null, null, TIMES_KEY_IOTIME, null);
	    
	    if (cursor.moveToFirst())
	    {
	    	do 
	    	{
	    		Calendar c = Calendar.getInstance();
	    		//Log.d("getInOfTheDay", cursor.getString(2));
	    		StringTokenizer st = new StringTokenizer(cursor.getString(2), "/");
	    		int year = Integer.parseInt(st.nextToken());
	    		int month = Integer.parseInt(st.nextToken())-1;
	    		int d = Integer.parseInt(st.nextToken());
	    		//Log.d("getInOfTheDay", ""+ year + "/"+month+"/"+d);
	    		c.clear();
	    		c.set(year, month, d);
	    		c.setTimeInMillis(Long.parseLong(cursor.getString(3)));
	    		TimeEntry t = new TimeEntry(cursor.getInt(0), cursor.getString(1), c);
	    		lst.add(t);
	    	} while(cursor.moveToNext());
	    }
	    
		return lst;
	}
	
	public void delFromTemp(int ID)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TIMES_TABLE_NAME, TIMES_KEY_ID + "=?", new String[] {""+ID});
		db.close();
	}
	
	// Adding new In time
    public void addIn(Calendar in) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(TIMES_KEY_TYPE, "in");
        values.put(TIMES_KEY_DATE, in.get(Calendar.YEAR) + "/" + in.get(Calendar.MONTH) + "/" + in.get(Calendar.DAY_OF_MONTH));
        values.put(TIMES_KEY_IOTIME, in.getTime().getTime());

        db.insert(TIMES_TABLE_NAME, null, values);
        db.close();
    }
    
    public void addTempBal(int balance)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(TIMES_KEY_TYPE, "bal");
        values.put(TIMES_KEY_IOTIME, ""+balance);

        db.insert(TIMES_TABLE_NAME, null, values);
        db.close();
    }
    
    // BALANCE TABLE
    
    public void truncBalanceTable()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(BALANCES_TABLE_NAME, null, null);
    }
    
    public void dropBalanceTable()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + BALANCES_TABLE_NAME);
	}
    
    public void addBalance(Calendar cal, int balance)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(BALANCES_DATE_YEAR, ""+cal.get(Calendar.YEAR));
        values.put(BALANCES_DATE_MONTH, ""+cal.get(Calendar.MONTH));
        values.put(BALANCES_DATE_DAY, ""+cal.get(Calendar.DAY_OF_MONTH));
        values.put(BALANCES_BALANCE, balance);

        db.insert(BALANCES_TABLE_NAME, null, values);
        db.close(); 
    }
    
    public BalanceEntry getBalsOf(String year, String month, String day) {
		SQLiteDatabase db = this.getReadableDatabase();
				
		String where="";
		Vector<String> args	= new Vector();
		
		if (year!=null)
		{
			//if (where.equals(""))
			//	where = " WHERE ";
			where += BALANCES_DATE_YEAR + "=? ";
			args.add(year);
		} 
		if (month != null)
		{
			//if (where.equals(""))
			//	where = " WHERE ";
			if (year != null)
				where += "AND ";
			where += BALANCES_DATE_MONTH + "=? ";
			args.add(month);
		} 
		if (day != null)
		{
			//if (where.equals(""))
			//	where = " WHERE ";
			if (year != null || month != null)
				where += "AND ";
			where += BALANCES_DATE_DAY + "=? ";
			args.add(day);
		}
		
		//Log.i("", args.toArray()[0] +""+ args.toArray()[1] +""+ args.toArray()[2] );
		//Log.d("", ((String[]) args.toArray()).toString());
		//Cursor cursor = db.rawQuery("SELECT * as sum_bal FROM " + BALANCES_TABLE_NAME + where, (String[]) args.toArray());
		//Cursor cursor = db.rawQuery("SELECT SUM("+BALANCES_BALANCE+ ") as sum_bal FROM " + BALANCES_TABLE_NAME + where, (String[]) args.toArray());
		String[] strArr = new String[args.size()];
	    Cursor cursor = db.query(BALANCES_TABLE_NAME, new String[] { "SUM("+BALANCES_BALANCE+")" }, where, 
	    		args.toArray(strArr), null, null, null, null);
	    
	    if (cursor.moveToFirst())
	    {
	    //	do 
	    //	{
	    		Calendar c = Calendar.getInstance();
	    		c.clear(); 
	    		c.set(Calendar.YEAR, Integer.parseInt(year));
	    		if (month != null)
	    			c.set(Calendar.MONTH, Integer.parseInt(month));
	    		if (day != null)
	    			c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
	    		BalanceEntry b = new BalanceEntry(cursor.getInt(0));
	    		b.setDate(c);
	    		//Log.d("", b.toString());
	    		return b;
	    		//Log.i("", cursor.getString(0));
	    //	} while(cursor.moveToNext());
	    }
	    
	    return null;
	    
	}    
    
}
