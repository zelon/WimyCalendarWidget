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
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

public class CalendarData {

	public static ArrayList<DayEvent> getCalendarEvents(Context context) {
		ArrayList<DayEvent> ret = new ArrayList<DayEvent>();

		String[] stringUris = new String[] { "content://com.android.calendar/", // For 2.3 and above
								// "content://calendar/" ///< For 2.1 and below
		};

		int index = 0;

		final int ONE_DAY_MILLI = 1000 * 60 * 60 * 24;

		Calendar cal_today = Calendar.getInstance(TimeZone
				.getTimeZone("GMT+09:00"));
		cal_today.setTimeInMillis(getTodayStartTime());

		for (String stringUri : stringUris) {
			for (index = 0; index < 7; ++index) {
				//Log.i("zelon", "{");
				boolean bFound = false;
				Uri uri = Uri.parse(stringUri);

				long today = cal_today.getTimeInMillis()
						+ (cal_today.getTimeZone().getOffset(cal_today
								.getTimeInMillis()));

				long startUnixtime = today + (ONE_DAY_MILLI * index);
				long endUnixtime = startUnixtime + (ONE_DAY_MILLI) - 1;

				Calendar startDay = Calendar.getInstance(TimeZone
						.getTimeZone("GMT+09:00"));
				startDay.setTimeInMillis(startUnixtime);
				Calendar endDay = Calendar.getInstance(TimeZone
						.getTimeZone("GMT+09:00"));
				endDay.setTimeInMillis(endUnixtime);

				//Log.i("zelon", "StartDay : " + startDay.getTime().toGMTString());
				//Log.i("zelon", "EndDay : " + endDay.getTime().toGMTString());

				Builder builder = Uri.parse(stringUri + "instances/when")
						.buildUpon();
				ContentUris.appendId(builder, startUnixtime + 1);
				ContentUris.appendId(builder, endUnixtime);
				uri = builder.build();
				//Log.i("zelon", "URI : " + uri.toString());

				Cursor c = context.getContentResolver()
						.query(uri,
								new String[] { "_id", "title", "begin", "end",
										"allDay" }, null, null,
								"startDay ASC, startMinute ASC");

				if (null == c) {
					Log.i("zelon",
							"There is no calendar in this uri : "
									+ uri.toString());

					continue;
				}

				DayEvent dayEvent = new DayEvent();
				dayEvent.date = getDateString(startDay.getTime());

				while (c.moveToNext()) {
					ShowEventLog(c);
					bFound = true;

					dayEvent.appendTitle(c.getString(c.getColumnIndex("title")));

					if (c.getLong(c.getColumnIndex("begin")) > endUnixtime) {
						assert (false);
					}
					if (c.getLong(c.getColumnIndex("end")) < startUnixtime) {
						assert (false);
					}
				}

				if (bFound == false) {
					dayEvent.appendTitle(context.getResources().getString(
							R.string.no_event));
				}
				c.close();

				ret.add(dayEvent);

				//Log.i("zelon", "}");
			}
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
		
		return rightNow.getTime().getTime();
	}

	public static String getDateString(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return DateFormat.format("yyyy-MM-dd EEEE", c).toString();
	}

	private static void ShowEventLog(Cursor c)
	{
		String title = c.getString(c.getColumnIndex("title"));
		long begin = c.getLong(c.getColumnIndex("begin"));
		long end = c.getLong(c.getColumnIndex("end"));
		
		String log = String.format("%s from %d to %d", title, begin, end);
		
		Log.i("zelon", log);
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
			intent.setAction(ScheduleAppWidgetProvider.REFRESH);
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
}
