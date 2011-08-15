package com.wimy.android.fastfinder.test;


import java.util.Date;

import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.wimy.android.calendar.widget.MainActivity;
import com.wimy.android.calendar.widget.ScheduleAppWidgetProvider;

public class MyTest extends ActivityInstrumentationTestCase2<MainActivity>
{
	private MainActivity mActivity;

	public MyTest()
	{
		super("com.wimy.android.fastfinder.MainActivity", MainActivity.class);
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		mActivity = this.getActivity();
	}

	public void testGetCal()
	{
		//ScheduleAppWidgetProvider.getCalendarEvents(mActivity);
	}
	
	public void testAAA()
	{
		
	}
	
	public void testTodayStartTime()
	{
		Log.i("zelon", "testZZZGetTodayStartTime");

		long theTime = 1302825600000l;
		Date date = new Date(theTime);
		date.setDate(11);
		ShowTestMsg("GMT : " + date.toGMTString());
		ShowTestMsg("locale : " + date.toLocaleString());

		Date today = new Date(ScheduleAppWidgetProvider.getTodayStartTime());
		ShowTestMsg("From getTodayStartTime() GMT : " + today.toGMTString());
		ShowTestMsg("From getTodayStartTime() locale : " + today.toLocaleString());
		
		assertEquals(ScheduleAppWidgetProvider.getTodayStartTime(), date.getTime());
	}
	
	public void ShowTestMsg(String msg)
	{
		Log.i("zelon_test", msg);
	}
}
