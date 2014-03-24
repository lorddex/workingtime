package org.ldlabs.workingtime;

import java.util.Calendar;

import android.util.Log;

public class TimeEntry {
	
	private int ID;
	private String type;
	private Calendar date;
	
	public void setID(int id)
	{
		ID = id;
	}
	
	public void setType(String t){
		type = t;
	}
	
	public void setDate(Calendar t){
		date = t;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Calendar getDate()
	{
		return date;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public TimeEntry() 	{ }
	
	public TimeEntry(int ID, String type, Calendar date)
	{
		this.ID = ID;
		this.type = type;
		this.date = date;
	}
	
	public String toString()
	{
		//Log.d("toString", date.toString());
		return this.ID + ":" +  date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + ":" + date.getTimeInMillis();
	}
	

}
