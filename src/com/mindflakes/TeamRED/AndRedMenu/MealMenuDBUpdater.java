package com.mindflakes.TeamRED.AndRedMenu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.mindflakes.TeamRED.MenuXML.Reader;

public class MealMenuDBUpdater {

	MealMenuDBAdapter mDbAdapter;
	Resources rsrc;

	public  MealMenuDBUpdater(Context ctx, Resources rsrc) {
		mDbAdapter = new MealMenuDBAdapter(ctx);
		this.rsrc = rsrc;
	}
	
	private Resources getResources() {
		return rsrc;
	}

	protected void loadMenusToSQL() throws FileNotFoundException, NotFoundException{
		mDbAdapter.clear();
		mDbAdapter.addMenus(Reader.readSerialized(openFileInput(getResources().getString(R.string.local_file_serialized))));
	}

	protected void updateMenuFiles(){
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
			loadMenusToSQL();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	//    
}
