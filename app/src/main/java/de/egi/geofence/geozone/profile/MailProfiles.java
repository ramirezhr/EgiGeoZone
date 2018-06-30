/*
* Copyright 2014 - 2015 Egmont R. (egmontr@gmail.com)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/		

package de.egi.geofence.geozone.profile;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import de.egi.geofence.geozone.R;
import de.egi.geofence.geozone.db.DbContract;
import de.egi.geofence.geozone.db.DbMailHelper;
import de.egi.geofence.geozone.utils.Constants;
import de.egi.geofence.geozone.utils.Utils;

@SuppressWarnings("deprecation")
public class MailProfiles extends AppCompatActivity implements OnItemClickListener{
	private ListView list;
	private DbMailHelper datasource;
	private Cursor cursorMerk = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.onActivityCreateSetTheme(this);
		setContentView(R.layout.profile_alle);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Utils.changeBackGroundToolbar(this, toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_profiles);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(MailProfiles.this, MailProfile.class);
				i.putExtra("action", "new");
				startActivityForResult(i, 4711);
			}
		});


		datasource = new DbMailHelper(this);
		
	    list = (ListView) findViewById (R.id.list);  
        registerForContextMenu(list);
        fillList();
	}
	
	private void fillList(){
		final Cursor cursor = datasource.getCursorAllMailSorted();
		cursorMerk = cursor;
		  
//		  int[] colors = {0, 0xff9933cc, 0xff9933cc}; // Lila
//		  list.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
//		  list.setDividerHeight(2);

		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.profile_list_item, cursor,
				new String[]{DbContract.MailEntry.CN_NAME, DbContract.MailEntry.CN_TO},
				new int[]{R.id.profName, R.id.profWert}, 0) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final View row = super.getView(position, convertView, parent);

//					cursor.moveToPosition(position);
//		            String s = cursor.getString(cursor.getColumnIndex(DbContract.MailEntry.CN_ACTIV));
//
//		            if (s.equalsIgnoreCase("1")){
//		            	((TextView)row.findViewById(R.id.profName)).setTextColor(Color.parseColor("#ff669900"));
//		            	((TextView)row.findViewById(R.id.profWert)).setTextColor(Color.parseColor("#ff669900"));
//		            }else{
//		            	((TextView)row.findViewById(R.id.profName)).setTextColor(Color.BLACK);
//		            	((TextView)row.findViewById(R.id.profWert)).setTextColor(Color.BLACK);
//		            }

				if (position % 2 == 0)
					row.setBackgroundColor(Color.parseColor("#F7F7F7"));
				else
					row.setBackgroundColor(Color.parseColor("#E7E7E7"));
				return row;
			}
		};

		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
    	// Eintrag evtl. ändern
		cursorMerk.moveToPosition(position);
		String ind = cursorMerk.getString(cursorMerk.getColumnIndex(DbContract.MailEntry._ID));
		
		Intent is = new Intent(this, MailProfile.class);
		is.putExtra("action", "update");
		is.putExtra("ind", ind);
		startActivityForResult(is, 4711);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Choose what to do based on the request code
        switch (requestCode) {
            case 4711 :
                fillList();
                break;
            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(Constants.APPTAG, getString(R.string.unknown_activity_request_code, requestCode));
               break;
        }
    }
    
}

















