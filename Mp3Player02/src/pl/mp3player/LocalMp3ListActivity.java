package pl.mp3player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import pl.model.Mp3Info;
import pl.userinfo.UserInfoActivity;
import pl.userinfo.UserLoginActivity;
import pl.utils.FileUtils;
import pl.utils.Mp3SizeUtils;
import pl.utils.UserFileUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LocalMp3ListActivity extends Activity {
	private List<Mp3Info> mp3Infos = null;
	private Button loginBtn = null;// ��ʾ��¼��ť
	private ImageButton headImageBtn = null;// ��ʾͷ��
	private TextView textView;// ��ʾĳ�û��ѵ�¼
	private boolean isLogined = false;
	private String userName;
	private String MP3FILE_PATH = null;
	private SimpleAdapter simpleAdapter;
	private List<HashMap<String, Object>> list ;
	private PullToRefreshListView pullToRefreshView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_mp3_list);
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		isLogined = getIsLogined();// ���ļ��л�ȡ�Ƿ��¼���¼���û���
		loginBtn = (Button) findViewById(R.id.login_btn);
		headImageBtn = (ImageButton) findViewById(R.id.head_image);
		textView = (TextView) findViewById(R.id.user_logined_infoview);
		loginBtn.setOnClickListener(new LoginBtnListener());
		headImageBtn.setOnClickListener(new UserInfoListener());
		
		pullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // Do work to refresh the list here.
		        new GetDataTask().execute();
		    }
		});
		//�����������MP3
		pullToRefreshView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mp3Infos != null) {
					Mp3Info mp3Info = mp3Infos.get(position);
					Intent intent = new Intent();
					intent.putExtra("mp3Info", mp3Info);
					intent.setClass(LocalMp3ListActivity.this, PlayerActivity.class);
					startActivity(intent);
				}
			}
		});

	}
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		@Override
		protected void onPostExecute(String[] result) {
			// Call onRefreshComplete when the list has been refreshed.
			pullToRefreshView.onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public boolean getIsLogined() {
		Properties loginPro = new Properties();
		File isLoginFile = new File(MP3FILE_PATH + "isLoginFile.properties");
		try {
			openFileOutput("isLoginFile.properties", MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserFileUtils.loadPro(loginPro, isLoginFile);
		String isLoginedStr = "loginedIsTrue";
		if (isLoginFile.length() != 0) {
			if (loginPro.containsKey("isLogined")) {
				if (isLoginedStr.equals(loginPro.getProperty("isLogined"))) {
					userName = loginPro.getProperty("loginedIs");
					return true;
				} else
					return false;
			} else
				return false;
		} else
			return false;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	
	private void itemLongClick(){
		pullToRefreshView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0,0,0,"ɾ��");
				//menu.add(0,0,0,"ɾ������");
			}
		});
	}
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int MID = (int) info.id;//�����info.id��Ӧ�ľ������ݿ���_id��ֵ
		switch (item.getItemId()) {
		case 0:
			System.out.println("Longclick-clicked id is->>"+info.id);
			String fileName = MP3FILE_PATH+mp3Infos.get(MID).getId()+".mp3";
			FileUtils.deleteFile(fileName);
			Toast.makeText(this, list.get((int) info.id).get("mp3_name")+"�ѱ�ɾ��", Toast.LENGTH_SHORT).show();
			onResume();
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	class LoginBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!isLogined) {
				Intent intent = new Intent();
				intent.setClass(LocalMp3ListActivity.this,
						UserLoginActivity.class);
				startActivity(intent);
			}
		}
	}

	class UserInfoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (isLogined) {
				Intent intent = new Intent();
				intent.putExtra("isLogined", isLogined);
				intent.putExtra("userName", userName);
				intent.setClass(LocalMp3ListActivity.this,
						UserInfoActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(LocalMp3ListActivity.this, "��δ��¼�����ȵ�¼������ͷ��",
						Toast.LENGTH_LONG).show();
				System.out.println("");
			}
		}

	}

	@Override
	protected void onResume() {
		FileUtils fileUtils = new FileUtils();
		mp3Infos = fileUtils.getMp3Files(MP3FILE_PATH);
//		mp3Infos = fileUtils.getMp3FilesFromPush(MP3FILE_PATH);
		if (mp3Infos != null) {
			list = new ArrayList<HashMap<String, Object>>();
			int i=1;
			for (Iterator<Mp3Info> iterator = mp3Infos.iterator(); iterator.hasNext();) {
				Mp3Info mp3Info = (Mp3Info) iterator.next();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", i++);
				map.put("mp3_name", mp3Info.getMp3Name());
				// ����Ϊmb 1���ֽ�(mb)=1048576�ֽ�(b)
				long mp3Sizetemp = (long) Integer
						.parseInt(mp3Info.getMp3Size());
				map.put("mp3_size", Mp3SizeUtils.getPrintSize(mp3Sizetemp));
				list.add(map);
			}
			simpleAdapter = new SimpleAdapter(this, list,
					R.layout.local_mp3info_item, new String[] { "id","mp3_name",
							"mp3_size" }, new int[] { R.id.mp3list_id,R.id.mp3_name,
							R.id.mp3_size });
			pullToRefreshView.setAdapter(simpleAdapter);
			itemLongClick();//����ɾ��
		}
		isLogined = getIsLogined();// ���ļ��л�ȡ�Ƿ��¼���¼���û���
		if (isLogined) {
			loginBtn.setVisibility(View.INVISIBLE);
			if (userName != null) {
				textView.setText(userName + "�ѵ�¼");
			}
			textView.setVisibility(View.VISIBLE);
		} else {
			loginBtn.setVisibility(View.VISIBLE);
			textView.setVisibility(View.INVISIBLE);
		}
		super.onResume();
	}
}
