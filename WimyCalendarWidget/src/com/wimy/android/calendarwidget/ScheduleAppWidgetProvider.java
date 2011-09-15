package com.wimy.android.calendarwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduleAppWidgetProvider extends AppWidgetProvider
{
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
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
			
			Intent intent = new Intent();
			intent.setClass(context, WidgetService.class);
			context.startService(intent);
		}
	}

    public static void updateWidget(Context context)
    {
		AppWidgetManager wm = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context, ScheduleAppWidgetProvider.class);
		wm.updateAppWidget(widget, CalendarData.makeRemoteViews(context));
    }
}
