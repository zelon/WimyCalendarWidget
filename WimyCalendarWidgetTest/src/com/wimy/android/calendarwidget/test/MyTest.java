package com.wimy.android.calendarwidget.test;


import java.util.Date;

import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;

import com.wimy.android.calendarwidget.CalendarData;
import com.wimy.android.calendarwidget.WimyCalendarSettingActivity;

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
}
