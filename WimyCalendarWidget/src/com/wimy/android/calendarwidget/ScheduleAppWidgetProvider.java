package com.wimy.android.calendarwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

class CalendarDataProviderObserver extends ContentObserver
{
	private Context mContext;

	public CalendarDataProviderObserver(Handler handler, Context context)
	{
		super(handler);
		
		Log.i("zelon", "CalendarDataProviderObserver()");
		
		mContext = context;
	}

	@Override
	public void onChange(boolean selfChange)
	{
		Log.i("zelon", "onChange");

		ScheduleAppWidgetProvider.updateWidget(mContext);
	}
}

public class ScheduleAppWidgetProvider extends AppWidgetProvider
{
	// To get notification when calendar event changed
	private CalendarDataProviderObserver mObserver;
	static final String REFRESH = "com.wimy.android.calendarwidget.refresh";

	@Override
	public void onDisabled(Context context)
	{
		Log.i("zelon","onDisabled");
		
		if ( null != mObserver )
		{
			context.getContentResolver().unregisterContentObserver(mObserver);
		}
		
		super.onDisabled(context);
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		Log.i("zelon", "onUpdate");
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++)
		{
			Log.i("zelon", "update app widget");

			int appWidgetId = appWidgetIds[i];

			// Tell the AppWidgetManager to perform an update on the current App Widget
			appWidgetManager.updateAppWidget(appWidgetId, CalendarData.makeRemoteViews(context));
			
			mObserver = new CalendarDataProviderObserver(new Handler(), context);

			context.getContentResolver().registerContentObserver(Uri.parse("content://com.android.calendar/events"), true, mObserver);

			setUpdateAlarm(context);
		}
	}

	private void setUpdateAlarm(Context context)
	{
		Intent intent = new Intent();
		intent.setAction(REFRESH);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		long nextUpdateTime = CalendarData.getTodayStartTimeInMillis() + CalendarData.ONE_DAY_MILLI;
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, nextUpdateTime, CalendarData.ONE_DAY_MILLI, pendingIntent);
	}
	
    public static void updateWidget(Context context)
    {
		AppWidgetManager wm = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context, ScheduleAppWidgetProvider.class);
		wm.updateAppWidget(widget, CalendarData.makeRemoteViews(context));
    }
}
