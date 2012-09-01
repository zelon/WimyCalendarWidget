package com.wimy.android.calendarwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class WidgetService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		setUpdateAlarm();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setUpdateAlarm()
	{
		Intent intent = new Intent();
		intent.setAction(EventReceiver.REFRESH);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		long nextUpdateTime = CalendarData.getTodayStartTimeInMillis() + CalendarData.ONE_DAY_MILLI;
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, nextUpdateTime, CalendarData.ONE_DAY_MILLI / 2, pendingIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	

}
