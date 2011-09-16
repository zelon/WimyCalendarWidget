package com.wimy.android.calendarwidget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class CalendarData {

	public static final int ONE_DAY_MILLI = 1000 * 60 * 60 * 24;

	public static String getCalendarUri()
	{
		boolean com_android_calendar = true;
		
		if ( com_android_calendar )
		{
			return "content://com.android.calendar/";
		}
		return "content://calendar/";
	}
	
	public static long getTodayStartTimeInMillis()
	{
		Calendar cal_today = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
		cal_today.setTimeInMillis(getTodayStartTime());
		
		return cal_today.getTimeInMillis() + (cal_today.getTimeZone().getOffset(cal_today.getTimeInMillis()));
	}
	
	public static int getJulianToday()
	{
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
		int day = Time.getJulianDay(c.getTimeInMillis(), TimeZone.getTimeZone("GMT+09:00").getRawOffset() / 1000);
		
		return day;
	}
	
	public static ArrayList<DayEvent> getCalendarEvents(Context context)
	{
		final int CHECK_DAY_DURATION = 7;

		long today = CalendarData.getTodayStartTimeInMillis();

		// the day before yesterday
		long startUnixtime = today - (CalendarData.ONE_DAY_MILLI * 2);
		long endUnixtime = today + (CalendarData.ONE_DAY_MILLI * CHECK_DAY_DURATION) - 1;

		String stringUri = CalendarData.getCalendarUri();
		Builder builder = Uri.parse(stringUri + "instances/when/").buildUpon();
		ContentUris.appendId(builder, startUnixtime + 1);
		ContentUris.appendId(builder, endUnixtime);
		Uri uri = builder.build();

		int julianToday = CalendarData.getJulianToday();

		// from yesterday
		--julianToday;

		Calendar dateForString = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
		dateForString.add(Calendar.DATE, -1);
		
		ArrayList<DayEvent> ret = new ArrayList<DayEvent>();
		
		for ( int i=0; i<CHECK_DAY_DURATION; ++i )
		{
			int checkJulianDay = julianToday + i;
			boolean bFound = false;
			
			DayEvent dayEvent = new DayEvent();
			dayEvent.date = CalendarData.getDateString(dateForString.getTime());
			
			Cursor c = context.getContentResolver()
					.query(uri, null , null , null , "startDay ASC, startMinute ASC");

			if (null == c)
			{
				dayEvent.appendTitle("There is no calendar in this uri : " + uri.toString());
			}
			else
			{
				while ( c.moveToNext() )
				{
					if ( c.getInt(c.getColumnIndex("startDay")) <= checkJulianDay
							&& c.getInt(c.getColumnIndex("endDay")) >= checkJulianDay )
					{
						dayEvent.appendTitle(c.getString(c.getColumnIndex("title")));
						
						bFound = true;
					}
				}
				
				c.close();
			}
			
			if (bFound == false)
			{
				dayEvent.appendTitle(context.getResources().getString(R.string.no_event));
			}

			ret.add(dayEvent);
			
			dateForString.add(Calendar.DATE, +1);
		}
		
		return ret;
	}

	public static long getTodayStartTime()
	{
		Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+09:00"));
		rightNow.set(Calendar.HOUR, 0);
		rightNow.set(Calendar.SECOND, 0);
		rightNow.set(Calendar.MINUTE, 0);
		rightNow.set(Calendar.MILLISECOND, 0);
		rightNow.set(Calendar.AM_PM, Calendar.AM);
		
		return rightNow.getTime().getTime();
	}

	public static String getDateString(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return DateFormat.format("yyyy-MM-dd EEEE", c).toString();
	}

	public static void ShowEventLog(Cursor c)
	{
		String title = c.getString(c.getColumnIndex("title"));
		Long begin = c.getLong(c.getColumnIndex("begin"));
		Long end = c.getLong(c.getColumnIndex("end"));
		
		String log = String.format("%s from %s to %s", title, CalendarData.getCommaString(begin.toString()), CalendarData.getCommaString(end.toString()));
		
		Log.i("zelon_test", log);
	}
	
	public static RemoteViews makeRemoteViews(Context context) {
		// Get the layout for the App Widget and attach an on-click listener
		// to the button
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.schedule_appwidget);
		{// Create an Intent to launch Calendar Activity
			
			Intent intent = makeIntentForStartingCalendar(context);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			views.setOnClickPendingIntent(R.id.content, pendingIntent);
		}
		{// Create an Intent forcing to refresh
			Intent intent = new Intent();
			intent.setAction(EventReceiver.REFRESH);
			views.setOnClickPendingIntent(R.id.bt_refresh, PendingIntent.getBroadcast(context, 0, intent, 0));
		}
		views.setCharSequence(R.id.bt_refresh, "setText", "Refreshed time : " + Calendar.getInstance().getTime().toLocaleString());
		views.removeAllViews(R.id.content);
		
		ArrayList<DayEvent> dayEvents = CalendarData.getCalendarEvents(context);
		
		if ( dayEvents.size() == 0 )
		{
			DayEvent dayEvent = new DayEvent();
			dayEvent.date = "";
			dayEvent.appendTitle("There is no calendar." + Calendar.getInstance().getTime().toGMTString());
			
			dayEvents.add(dayEvent);
		}

		boolean bPastEvent = true;
		for ( DayEvent dayEvent : dayEvents )
		{
			RemoteViews newView = new RemoteViews(context.getPackageName(), R.layout.widget_small_view);
			
			StringBuilder sb = new StringBuilder();
			sb.append(dayEvent.date);
			
			for ( String title : dayEvent.getTitles())
			{
				sb.append("\r\n    - ");
				sb.append(title);
			}
			
			newView.setTextViewText(R.id.small_textview, sb.toString());
			
			if ( bPastEvent )
			{
				newView.setTextColor(R.id.small_textview, Color.DKGRAY);
				bPastEvent = false;
			}
			
			views.addView(R.id.content, newView);
		}
		
		return views;
	}
	
	private static Intent makeIntentForStartingCalendar(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.setClassName("com.google.android.calendar", "com.android.calendar.AgendaActivity");

		return intent;
	}
	
	public static String getCommaString(String input)
	{
		String ret = "";
		String temp = "";
		
		for ( int i=0; i<input.length(); ++i )
		{
			temp += input.charAt(input.length() - 1 - i);
			
			if ( i > 0 && i < (input.length()-1)
					&& (i+1) % 3 == 0 )
			{
				temp += ",";
			}
		}
		
		for ( int i=0; i<temp.length(); ++i )
		{
			ret += temp.charAt(temp.length() - 1 - i);
		}
		
		return ret;
	}
}
