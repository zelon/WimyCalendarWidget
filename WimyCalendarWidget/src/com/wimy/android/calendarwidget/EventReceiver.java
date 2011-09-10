package com.wimy.android.calendarwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EventReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        Log.i("zelon", "recv action : " + action);
        if (action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(ScheduleAppWidgetProvider.REFRESH)
                )
        {
			Log.i("zelon", "on recv DateChangeReceiver.onReceive date_changed");

			ScheduleAppWidgetProvider.updateWidget(context);
        }
    }
}
