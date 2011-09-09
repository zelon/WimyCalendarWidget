package com.wimy.android.calendarwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
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
	public IBinder peekService(Context myContext, Intent service)
	{
		Log.i("zelon","peekService");

		return super.peekService(myContext, service);
	}

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

	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
		Log.i("zelon","onEnabled");
		
	}
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		Log.i("zelon", "onUpdate");
		final int N = appWidgetIds.length;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(REFRESH);
        context.getApplicationContext().registerReceiver(mIntentReceiver, filter);
		
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
		}
	}

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.i("zelon", "recv action : " + action);
            if (action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_DATE_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                    || action.equals(REFRESH)
                    )
            {
    			Log.i("zelon", "on recv DateChangeReceiver.onReceive date_changed");

				updateWidget(context);
            }
        }
    };
    
    public static void updateWidget(Context context)
    {
		AppWidgetManager wm = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context, ScheduleAppWidgetProvider.class);
		wm.updateAppWidget(widget, CalendarData.makeRemoteViews(context));
    }
}
