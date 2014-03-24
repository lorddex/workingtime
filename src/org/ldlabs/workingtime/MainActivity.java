package org.ldlabs.workingtime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import org.ldlabs.workingtime.R;
import android.os.Bundle;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

public class MainActivity extends FragmentActivity
{
	
	private TextView lastInText;
	private TextView balance;
	private Calendar lastIn;
	private int lastBalance;
	private Button viewButton;
	private MyBadgeDatabase db;
    
	@Override
	protected void onPause()
	{
		if (lastIn != null)
			db.addIn(lastIn);
		if (lastBalance != -1)
			db.addTempBal(lastBalance);
		super.onPause();
		
	}
	
	private void printToast(String txt)
	{
		CharSequence text = txt;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{		
		super.onCreate(savedInstanceState);
		
		// RICORDARSI DI CANCELLARE QUESTA LINEA!!!!!!!!!!!!!! WARNING!!!!!!!!!!!!!!!!!
		//Log.e("MainActivity.onCreate", "DELETE THE DATABASE!!!!!!!!");
		//this.deleteDatabase("MyBadgeDatabase.db"); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		setContentView(R.layout.activity_main);
		db = new MyBadgeDatabase(this);

		viewButton = (Button) findViewById(R.id.viewButton);
		lastInText = (TextView) findViewById(R.id.textViewLastIn);
		balance = (TextView) findViewById(R.id.textViewBalance);
		findViewById(R.id.storeButton).setEnabled(false);
		
		lastIn = null;
		lastBalance = -1;
		
		List<TimeEntry> l = db.getIns();		
		if (l.size()>0)
		{	
			printToast(getString(R.string.restoredFromDB));
			TimeEntry t = l.get(l.size()-1);
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			DateFormat sdf = DateFormat.getDateTimeInstance();
			String currentDateandTime = sdf.format(t.getDate().getTime().getTime());
			lastInText.setText(currentDateandTime);
			findViewById(R.id.badgeOutButton).setEnabled(true);
			findViewById(R.id.badgeInButton).setEnabled(false);
		}
		else
		{
			findViewById(R.id.badgeOutButton).setEnabled(false);
		}
		
		List<BalanceEntry> b = db.getTempBals();
		if (b.size()>0)
		{
			printToast(getString(R.string.restoredFromDB));
			BalanceEntry lb = b.get(b.size()-1);
			String currentDateandTime = String.format("%02d:%02d:%02d", lb.getBalance()/3600, (lb.getBalance()%3600)/60, (lb.getBalance()%3600)%60);
			/*Date d = new Date();
			d.setTime(s*1000);
			DateFormat sdf = DateFormat.getTimeInstance();
			String currentDateandTime = sdf.format(d);*/
			balance.setText(currentDateandTime);
			lastBalance = (int)lb.getBalance();
			findViewById(R.id.badgeOutButton).setEnabled(false);
			findViewById(R.id.badgeInButton).setEnabled(true);
			findViewById(R.id.storeButton).setEnabled(true);
		}			
		
		viewButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) 
			{
				showDatePicker();
			}
		});
			
	}
	
	private void showDatePicker() {
		DatePickerFragment date = new DatePickerFragment();
		/**
		 * Set Up Current Date Into dialog
		**/
		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		date.setArguments(args);
		/**
		 * Set Call back to capture selected date
		**/
		date.setCallBack(ondate);
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			
			//Toast.makeText(MainActivity.this, String.valueOf(year) + "-" + String.valueOf(monthOfYear) + "-" + String.valueOf(dayOfMonth),
			//		Toast.LENGTH_LONG).show();
			
			String report = "Day: ";
			
			BalanceEntry be = db.getBalsOf(""+year, ""+monthOfYear, ""+dayOfMonth);
			report += be.getBalance() + "\n";
			
			be = db.getBalsOf(""+year, ""+monthOfYear, null);
			report += "Month: " + be.getBalance() + "\n";
			
			be = db.getBalsOf(""+year, null, null);
			report += "Year: " + be.getBalance() + "\n";
			AlertDialog ad = new AlertDialog.Builder(view.getContext()).create();
			ad.setTitle(getString(R.string.badgeOutAlertTitle));
			ad.setMessage(report);
			ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK",  new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int which) {}});
			ad.show();
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void badgeIn(View view)
	{
		DateFormat sdf = DateFormat.getDateTimeInstance();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
		lastIn = Calendar.getInstance();
		String currentDateandTime = sdf.format(lastIn.getTime().getTime());
		lastInText.setText(currentDateandTime);
		findViewById(R.id.badgeOutButton).setEnabled(true);
		findViewById(R.id.badgeInButton).setEnabled(false);
	}
	
	public void badgeOut(View view)
	{
		
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setTitle(getString(R.string.badgeOutAlertTitle));
		ad.setMessage(getString(R.string.badgeOutAlertMsg));
		ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.badgeOutAlertOK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Calendar now = Calendar.getInstance();
				long s = now.getTime().getTime();
				if (lastIn == null)
				{
					printToast(getString(R.string.restoredFromDB));
					List<TimeEntry> l = db.getInOfTheDay(now);
					if (l.size()>0)
					{
						TimeEntry t = l.get(l.size()-1);
						s -= t.getDate().getTime().getTime();
						Log.d("", "Size of TimeEntries " + l.size());
						while(l.size()>0)
						{
							t = l.get(l.size()-1);
							Log.d("", "Delete Temp with id " + t.getID());
							l.remove(l.size()-1);
							db.delFromTemp(t.getID());
						}
					} 
					else
					{
						printToast("ERROR WHILE RECOVERING LASTIN!");
						return;
					}
				}
				else
				{
					s -= lastIn.getTime().getTime(); 
				}
				s /= 1000;
				String currentDateandTime = String.format("%02d:%02d:%02d", s/3600, (s%3600)/60, (s%3600)%60);
				/*Date d = new Date();
				d.setTime(s*1000);
				DateFormat sdf = DateFormat.getTimeInstance();
				String currentDateandTime = sdf.format(d);*/
				balance.setText(currentDateandTime);
				lastBalance = (int)s;
				findViewById(R.id.badgeOutButton).setEnabled(false);
				findViewById(R.id.badgeInButton).setEnabled(true);
				findViewById(R.id.storeButton).setEnabled(true);
			}
		});
		ad.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.badgeOutAlertNO), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) { 
				// DO NOTHING 
			}
		});
		ad.show();
		
	}
	
	public void store(View view)
	{
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setTitle(getString(R.string.storeAlertTitle));
		ad.setMessage(getString(R.string.storeAlertMsg));
		ad.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.storeAlertOK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Calendar now = Calendar.getInstance();
				if (lastBalance == -1)
				{
					printToast(getString(R.string.restoredFromDB));
					List<BalanceEntry> lb = db.getTempBals();
					BalanceEntry b = lb.get(lb.size()-1);
					lastBalance = b.getBalance();
					Log.d("", "Size of BalanceEntries " + lb.size());
					while (lb.size()>0)
					{
						b = lb.get(lb.size()-1);
						Log.d("", "Delete Temp with id " + b.getID());
						lb.remove(lb.size()-1);
						db.delFromTemp(b.getID());
					}
				}
				db.addBalance(now, lastBalance);
				
				lastBalance = -1;
				lastIn = null;
				
				db.truncTimesTable();
				
				findViewById(R.id.badgeOutButton).setEnabled(false);
				findViewById(R.id.badgeInButton).setEnabled(true);
				findViewById(R.id.storeButton).setEnabled(false);
				
				lastInText.setText("-");
				balance.setText("-");
			}
		});
		ad.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.storeAlertNO), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// NOTHING TO DO
			}
		});
		ad.show();
	}
	
}