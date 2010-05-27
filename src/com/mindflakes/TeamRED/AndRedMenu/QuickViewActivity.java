package com.mindflakes.TeamRED.AndRedMenu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import android.app.TabActivity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

import com.mindflakes.TeamRED.MenuXML.Reader;
import com.mindflakes.TeamRED.menuClasses.MealMenu;
import com.mindflakes.TeamRED.menuUtils.MealMenuSearchQuery;

public class QuickViewActivity extends TabActivity {
	private TabHost mTabHost;
	private ArrayList<MealMenu> mMenus;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.quick_view);
            mTabHost = getTabHost();
            readMenus();
            TextView current = (TextView) findViewById(R.id.quickview1);
            current.setMovementMethod(new ScrollingMovementMethod());
            current.setText((mMenus!=null)?getMealMenu(1).toString():"null");
            current=(TextView) findViewById(R.id.quickview2);
            current.setMovementMethod(new ScrollingMovementMethod());
            current.setText((mMenus!=null)?getMealMenu(2).toString():"null");
            current=(TextView) findViewById(R.id.quickview3);
            current.setMovementMethod(new ScrollingMovementMethod());
            current.setText((mMenus!=null)?getMealMenu(3).toString():"null");
            current=(TextView) findViewById(R.id.quickview4);
            current.setMovementMethod(new ScrollingMovementMethod());
            current.setText((mMenus!=null)?getMealMenu(4).toString():"null");
            mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getResources().getString(R.string.commons_name_short_carrillo)).setContent(R.id.quickview1));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getResources().getString(R.string.commons_name_short_dlg)).setContent(R.id.quickview2));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getResources().getString(R.string.commons_name_short_ortega)).setContent(R.id.quickview3));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getResources().getString(R.string.commons_name_short_portola)).setContent(R.id.quickview4));
            
            mTabHost.setCurrentTab(0);
    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1234, 0, "New Game");
        menu.add(0, 1235, 0, "Update Menus");
        return true;
    }

    protected void readMenus(){
    	if(mMenus==null){
    	try{
			loadMenus();
    	}catch(FileNotFoundException e){
    		updateMenus();
    	}
    	}
    }
    
    private void loadMenus() throws FileNotFoundException, NotFoundException{
		mMenus = Reader.readSerialized(openFileInput(getResources().getString(R.string.local_file_serialized)));
    }
    
    protected MealMenu getMealMenu(int mode){
    	MealMenuSearchQuery query = new MealMenuSearchQuery(mMenus).findEndingAt(new Interval(new DateTime(), new DateTime().plusDays(1)));
    	switch(mode){
    	case 1:
        	return query.findCommons("Carrillo").sortByEndTime(true).returnResults().get(0);
    	case 2:
    		return query.findCommons("De La Guerra").sortByEndTime(true).returnResults().get(0);
    	case 3:
    		return query.findCommons("Ortega").sortByEndTime(true).returnResults().get(0);
    	case 4:
    		return query.findCommons("Portola").sortByEndTime(true).returnResults().get(0);
    	}
    	return null;
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1234:
            return true;
        case 1235:
        	updateMenus();
            return true;
        }
        return false;
    }
    
    protected void updateMenus(){
		try{
			URL remoteFile = new URL(getResources().getString(R.string.serialized_menus_remote));
			
		    HttpURLConnection c = (HttpURLConnection) remoteFile.openConnection();
		    c.setRequestMethod("GET");
		    c.setDoOutput(true);
		    c.connect();
		    FileOutputStream f = openFileOutput(getResources().getString(R.string.local_file_serialized_zipped),MODE_PRIVATE);
		    InputStream in = c.getInputStream();

		    byte[] buffer = new byte[1024];
		    int len1 = 0;
		    while ( (len1 = in.read(buffer)) > 0 ) {
		         f.write(buffer,0, len1);
		 }
		    f.close();
		    in.close();
			Reader.uncompressFile(openFileInput(getResources().getString(R.string.local_file_serialized_zipped)),
					openFileOutput(getResources().getString(R.string.local_file_serialized),MODE_PRIVATE));
			loadMenus();
		}catch(IOException e){
			e.printStackTrace();
		}
    }
//    
//    public void switchToListView() {
//      super.onCreate(savedInstanceState);
//
//      setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, COUNTRIES));
//
//      ListView lv = getListView();
//      lv.setTextFilterEnabled(true);
//
//      lv.setOnItemClickListener(new OnItemClickListener() {
//        public void onItemClick(AdapterView<?> parent, View view,
//            int position, long id) {
//          // When clicked, show a toast with the TextView text
//          Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
//              Toast.LENGTH_SHORT).show();
//        }
//      });
//    }
}