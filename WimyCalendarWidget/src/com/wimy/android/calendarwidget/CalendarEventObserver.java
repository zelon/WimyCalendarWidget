package com.wimy.android.calendarwidget;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class CalendarEventObserver extends ContentObserver
{
	private Context mContext;

	public CalendarEventObserver(Handler handler, Context context)
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
