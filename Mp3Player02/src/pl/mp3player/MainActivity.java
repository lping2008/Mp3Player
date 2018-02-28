package pl.mp3player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import pl.utils.UserFileUtils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private String MP3FILE_PATH = null;
	private String userName=null;
	private boolean isLogined=false;
	private final static int EXIT_MENU=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.actionbar_tittle);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		Intent intent = getIntent();
		userName=intent.getStringExtra("userName");
		isLogined=intent.getBooleanExtra("isLogined", isLogined);
		TabHost tabHost = getTabHost();
		
		Intent remoteIntent = new Intent();
		remoteIntent.setClass(this, Mp3ListActivity.class);
		TabHost.TabSpec remoteSpec = tabHost.newTabSpec("remote");
		remoteSpec.setIndicator("乐库");
		remoteSpec.setContent(remoteIntent);
		tabHost.addTab(remoteSpec);
		
		Intent localIntent = new Intent();
		localIntent.setClass(this, LocalMp3ListActivity.class);
		TabHost.TabSpec localSpec = tabHost.newTabSpec("local");
		localSpec.setIndicator("本地音乐");
		localSpec.setContent(localIntent);
		tabHost.addTab(localSpec);
		
		Intent infoIntent = new Intent();
		infoIntent.setClass(this, SoftInfoActivity.class);
		TabHost.TabSpec infoSpec = tabHost.newTabSpec("softInfo");
		infoSpec.setIndicator("关于软件");
		infoSpec.setContent(infoIntent);
		tabHost.addTab(infoSpec);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
        menu.add(0, EXIT_MENU, 0, R.string.anction_exit_user);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(EXIT_MENU==item.getItemId()){
			System.out.println("Main-getItemId--->"+item.getItemId());
			isLogined=false;
			Properties loginPro = new Properties();
			File isLoginFile = new File(MP3FILE_PATH+"isLoginFile.properties");
			try {
				openFileOutput("isLoginFile.properties",MODE_WORLD_WRITEABLE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UserFileUtils.loadPro(loginPro, isLoginFile);
			loginPro.clear();
			System.out.println("proClear?--->"+loginPro.isEmpty());
		}
		return super.onOptionsItemSelected(item);
	}
}
