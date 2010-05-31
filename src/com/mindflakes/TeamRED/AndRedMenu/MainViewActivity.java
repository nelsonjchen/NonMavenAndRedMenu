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

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;

import com.mindflakes.TeamRED.MenuXML.Reader;
import com.mindflakes.TeamRED.menuClasses.FoodItem;
import com.mindflakes.TeamRED.menuClasses.MealMenu;
import com.mindflakes.TeamRED.menuClasses.Venue;

public class MainViewActivity extends TabActivity {


	private TabHost mTabHost;
	private MealMenuDBAdapter  mDbAdapter;
//	private NotesDbAdapter mDbAdapter;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
//            class MainListActivity extends ListActivity{
//                
//                @Override
//                protected void onListItemClick(ListView l, View v, int position, long id) {
//                    super.onListItemClick(l, v, position, id);
//                    Intent i = new Intent(this, MenuViewActivity.class);
//                    i.putExtra(MealMenuDBAdapter.KEY_ROWID, id);
//                    startActivity(i);
//                }
//            }
//            
            String type = "Passed";
            mTabHost = getTabHost();

            try{
            	mDbAdapter = new MealMenuDBAdapter(this);
            	mDbAdapter.open();
            } catch(Exception e){
            	type = e.getClass().toString();
            }

            mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getResources().getString(R.string.commons_name_short_carrillo)).setContent(R.id.mainlist1));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getResources().getString(R.string.commons_name_short_dlg)).setContent(R.id.mainlist2));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getResources().getString(R.string.commons_name_short_ortega)).setContent(R.id.mainlist3));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getResources().getString(R.string.commons_name_short_portola)).setContent(R.id.mainlist4));
            updateViews();
            mTabHost.setCurrentTab(0);
//            ListActivity tmp = new ListActivity((ListView));
//            this.get
    }
    
    private ArrayList<String> toArrayHelper(MealMenu menu){
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
    	ListView view = (ListView)findViewById(R.id.mainlist1);
    	Cursor c = mDbAdapter.fetchMenusForMainList("Carrillo");
    	startManagingCursor(c);
    	String[] from = new String[]{
    			MealMenuDBAdapter.KEY_MEALMENU_MEALNAME,MealMenuDBAdapter.KEY_MEALMENU_STARTSTRING
    	};
    	int[] to = new int[]{
    			R.id.maintext1,R.id.maintext2};
    	SimpleCursorAdapter menus = new SimpleCursorAdapter(this,R.layout.main_row,c,from,to);
        view.setAdapter(menus);
        view = (ListView)findViewById(R.id.mainlist2);
        c = mDbAdapter.fetchMenusForMainList("De La Guerra");
        menus = new SimpleCursorAdapter(this,R.layout.main_row,c,from,to);
        view.setAdapter(menus);
        view = (ListView)findViewById(R.id.mainlist3);
        c = mDbAdapter.fetchMenusForMainList("Ortega");
        menus = new SimpleCursorAdapter(this,R.layout.main_row,c,from,to);
        view.setAdapter(menus);
        view = (ListView)findViewById(R.id.mainlist4);
        c = mDbAdapter.fetchMenusForMainList("Portola");
        menus = new SimpleCursorAdapter(this,R.layout.main_row,c,from,to);
        view.setAdapter(menus);
    }
//    
//    
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        Intent i = new Intent(this, NoteEdit.class);
//        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
//        startActivityForResult(i, ACTIVITY_EDIT);
//    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1234, 0, "Clear SQL");
        menu.add(0, 1235, 0, "Update Menus");
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
		mDbAdapter.addMenus(Reader.readIS(openFileInput((getResources().getString(R.string.local_file_xml)))));
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

}
