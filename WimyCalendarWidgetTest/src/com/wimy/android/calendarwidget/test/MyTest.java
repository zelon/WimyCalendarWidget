package com.wimy.android.calendarwidget.test;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Before;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.test.ActivityInstrumentationTestCase2;
import android.text.format.Time;
import android.util.Log;

import com.wimy.android.calendarwidget.CalendarData;
import com.wimy.android.calendarwidget.R;
import com.wimy.android.calendarwidget.WimyCalendarSettingActivity;
import com.wimy.android.calendarwidget.DayEvent;

public class MyTest extends ActivityInstrumentationTestCase2<WimyCalendarSettingActivity>
{
	private WimyCalendarSettingActivity mActivity;

	public MyTest()
	{
		super("com.wimy.android.calendarwidget.WimyCalendarSettingActivity", WimyCalendarSettingActivity.class);
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		mActivity = getActivity();
	}

	public void testGetCal()
	{
		CalendarData.getCalendarEvents(mActivity);
	}
	
	public void test_getCommaString()
	{
		assertEquals("123,456", CalendarData.getCommaString("123456"));
		assertEquals("1,123,456", CalendarData.getCommaString("1123456"));
		assertEquals("123", CalendarData.getCommaString("123"));
	}
	
	public void testTodayStartTime()
	{
		Date now = new Date(System.currentTimeMillis());
		Date startOfToday = new Date(CalendarData.getTodayStartTimeInMillis());
		
		assertEquals(now.getMonth(), startOfToday.getMonth());
		assertEquals(now.getDate(), startOfToday.getDate());
	}
	
	public void show(ArrayList<DayEvent> events)
	{
		for ( DayEvent event : events )
		{
			ArrayList<String> titles = event.getTitles();
			StringBuilder builder = new StringBuilder();
			
			builder.append(event.date + "\n");
			for ( String title : titles )
			{
				builder.append(" - " + title + "\n");
			}
			Log.i("zelon_test", builder.toString());
		}
	}
	
	public void test_current_day()
	{
		Log.i("zelon_test2", "Julianday : " + CalendarData.getJulianToday());
	}
}
