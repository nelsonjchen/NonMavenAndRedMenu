package com.mindflakes.TeamRED.AndRedMenu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.joda.time.DateTime;

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
            updateViews();

            mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getResources().getString(R.string.commons_name_short_carrillo)).setContent(R.id.quickview1));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getResources().getString(R.string.commons_name_short_dlg)).setContent(R.id.quickview2));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator(getResources().getString(R.string.commons_name_short_ortega)).setContent(R.id.quickview3));
            mTabHost.addTab(mTabHost.newTabSpec("tab_test4").setIndicator(getResources().getString(R.string.commons_name_short_portola)).setContent(R.id.quickview4));
            
            mTabHost.setCurrentTab(0);
    }
    
    private void updateViews(){
        TextView current = (TextView) findViewById(R.id.quickview1);      
        MealMenu toSet = getMealMenu(1);
        current.setMovementMethod(new ScrollingMovementMethod());
        current.setText((toSet!=null)?toSet.toString():"null");
//        current=(TextView) findViewById(R.id.quickview2);
//        toSet = getMealMenu(2);
//        current.setMovementMethod(new ScrollingMovementMethod());
//        current.setText((toSet!=null)?toSet.toString():"null");
//        current=(TextView) findViewById(R.id.quickview3);
//        toSet = getMealMenu(3);
//        current.setMovementMethod(new ScrollingMovementMethod());
//        current.setText((toSet!=null)?toSet.toString():"null");
//        current=(TextView) findViewById(R.id.quickview4);
//        toSet = getMealMenu(4);
//        current.setMovementMethod(new ScrollingMovementMethod());
//        current.setText((toSet!=null)?toSet.toString():"null");
    }
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1234, 0, "New Game");
        menu.add(0, 1235, 0, "Update Menus");
        return true;
    }

    protected void readMenus(){
    	try{
			loadMenus();
    	}catch(FileNotFoundException e){
    		updateMenus();
    	}
    }
    
    private void loadMenus() throws FileNotFoundException, NotFoundException{
    	mDbAdapter.clear();
		mDbAdapter.addMenus(Reader.readSerialized(openFileInput(getResources().getString(R.string.local_file_serialized))));
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
        	updateMenus();
        	updateViews();
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