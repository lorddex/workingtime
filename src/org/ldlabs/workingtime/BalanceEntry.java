package org.ldlabs.workingtime;

import java.util.Calendar;

public class BalanceEntry {

	private int ID ;
	private Calendar date;
	private int balance;
	
	public BalanceEntry() {}
	
	public BalanceEntry(int bal)
	{
		balance = bal;
	}
	
	public BalanceEntry(int id, Calendar date, int bal) 
	{
		ID = id;
		this.date = date;
		balance = bal;
	}
	
	public BalanceEntry(int id, String year, String month, String day, int bal) 
	{
		ID = id;
		balance = bal;
		date = Calendar.getInstance();
		date.clear();
		date.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
	}
	
	public int getID() 
	{
		return ID;
	}
	
	public void setID(int iD) 
	{
		ID = iD;
	}
	
	public Calendar getDate() 
	{
		return date;
	}
	
	public void setDate(Calendar date) 
	{
		this.date = date;
	}
	
	public int getBalance()
	{
		return balance;
	}
	
	public void setBalance(int balance) 
	{
		this.balance = balance;
	}
	
	public String toString()
	{
		return "" + date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + ":" + balance;
	}
	
}
