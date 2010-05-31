package com.mindflakes.TeamRED.AndRedMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.joda.time.DateTime;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.mindflakes.TeamRED.MenuXML.Reader;
import com.mindflakes.TeamRED.menuClasses.FoodItem;
import com.mindflakes.TeamRED.menuClasses.MealMenu;
import com.mindflakes.TeamRED.menuClasses.Venue;

public class QuickViewActivity extends TabActivity {
	private TabHost mTabHost;
	private MealMenuDBAdapter  mDbAdapter;
//	private NotesDbAdapter mDbAdapter;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.quick_view);
            String type = "Passed";
            mTabHost = getTabHost();

            try{
            	mDbAdapter = new MealMenuDBAdapter(this);
            	mDbAdapter.open();
            } catch(Exception e){
            	type = e.getClass().toString();
            }
//            readMenus();

            
            mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getResources().getString(R.string.commons_name_short_carrillo)).setContent(R.id.quicklist1));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getResources().getString(R.string.commons_name_short_dlg)).setContent(R.id.quicklist2));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getResources().getString(R.string.commons_name_short_ortega)).setContent(R.id.quicklist3));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getResources().getString(R.string.commons_name_short_portola)).setContent(R.id.quicklist4));
            updateViews();
            mTabHost.setCurrentTab(0);
    }
    
    private ArrayList<String> toArrayHelper(MealMenu menu){
    	if(menu==null) return new ArrayList<String>();
    	ArrayList<String> arr = new ArrayList<String>();
    	for(Venue ven:menu.getVenues()){
    		arr.add(ven.getName());
    		for(FoodItem food:ven.getFoodItems()){
    			arr.add("       "+food.getName());
    		}
    	}
    	return arr;
    }
    
    private void updateViews(){
    	ListView view = (ListView)findViewById(R.id.quicklist1);
        ArrayList<String> toSet = toArrayHelper(getMealMenu(1));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.quick_row,toSet);
        view.setAdapter(adapter);
        view = (ListView)findViewById(R.id.quicklist2);
        toSet = toArrayHelper(getMealMenu(2));
        adapter = new ArrayAdapter<String>(this,R.layout.quick_row,toSet);
        view.setAdapter(adapter);
        view = (ListView)findViewById(R.id.quicklist3);
        toSet = toArrayHelper(getMealMenu(3));
        adapter = new ArrayAdapter<String>(this,R.layout.quick_row,toSet);
        view.setAdapter(adapter);
        view = (ListView)findViewById(R.id.quicklist4);
        toSet = toArrayHelper(getMealMenu(4));
        adapter = new ArrayAdapter<String>(this,R.layout.quick_row,toSet);
        view.setAdapter(adapter);
    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1234, 0, "Clear SQL");
        menu.add(0, 1235, 0, "Update Menus");
        menu.add(0, 1236, 0, "Main View");
        return true;
    }

    protected void readMenus(){
    	try{
			loadMenusToSQL();
    	}catch(FileNotFoundException e){
    		updateMenuFiles();
    	}
    }
    
    private void loadMenusToSQL() throws FileNotFoundException, NotFoundException{
		mDbAdapter.clear();
		ArrayList<MealMenu> menu = Reader.readIS(openFileInput((getResources().getString(R.string.local_file_xml))));
		if (menu == null) {
			throw new RuntimeException("There were no mealmenus!");
		}
		mDbAdapter.addMenus(menu);
	}
    
    protected MealMenu getMealMenu(int mode){
    	switch(mode){
    	case 1:
        	return mDbAdapter.selectFirstMeal("Carrillo", new Long(new DateTime().getMillis()));
    	case 2:
        	return mDbAdapter.selectFirstMeal("De La Guerra", new Long(new DateTime().getMillis()));
    	case 3:
        	return mDbAdapter.selectFirstMeal("Ortega", new Long(new DateTime().getMillis()));
    	case 4:
        	return mDbAdapter.selectFirstMeal("Portola", new Long(new DateTime().getMillis()));
    	}
    	return null;
    }
    
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1234:
        	mDbAdapter.clear();
            return true;
        case 1235:
        	updateMenuFiles();
        	updateViews();
            return true;
        case 1236:
            Intent i = new Intent(this, MainViewActivity.class);
            startActivity(i);
        }
        return false;
    }
    
    protected void updateMenuFiles(){
		try{
			URL remoteFile = new URL(getResources().getString(R.string.combined_two_weeks_menus_gz_url));
			
		    HttpURLConnection c = (HttpURLConnection) remoteFile.openConnection();
		    c.setRequestMethod("GET");
		    c.setDoOutput(true);
		    c.connect();
		    FileOutputStream f = openFileOutput(getResources().getString(R.string.local_file_gz),MODE_PRIVATE);
		    InputStream in = c.getInputStream();

		    byte[] buffer = new byte[1024];
		    int len1 = 0;
		    while ( (len1 = in.read(buffer)) > 0 ) {
		         f.write(buffer,0, len1);
		 }
		    f.close();
		    in.close();
			Reader.uncompressFile(openFileInput(getResources().getString(R.string.local_file_gz)),
					openFileOutput(getResources().getString(R.string.local_file_xml),MODE_PRIVATE));
			loadMenusToSQL();
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