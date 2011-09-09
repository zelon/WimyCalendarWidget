package com.wimy.android.calendarwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class WimyCalendarSettingActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.setting_activity);
	    
	    findViewById(R.id.bt_refresh).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				ScheduleAppWidgetProvider.updateWidget(WimyCalendarSettingActivity.this);
				
				Intent i = new Intent();
				i.setAction(ScheduleAppWidgetProvider.REFRESH);
				
				sendBroadcast(i);
			}
		});
	}

}
