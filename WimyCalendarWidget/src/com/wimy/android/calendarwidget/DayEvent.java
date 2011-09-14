package com.wimy.android.calendarwidget;

import java.util.ArrayList;

public class DayEvent
{
	public String date;
	private java.util.ArrayList<String> mTitles = new ArrayList<String>();
	
	public void appendTitle(String title)
	{
		mTitles.add(title);
	}
	
	public ArrayList<String> getTitles()
	{
		return mTitles;
	}
}

