package pl.mp3player;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import pl.download.HttpDownloader;
import pl.model.Mp3Info;
import pl.service.DownloadService;
import pl.utils.FileUtils;
import pl.utils.Mp3SizeUtils;
import pl.xml.Mp3ListContentHandler;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Mp3ListActivity extends ListActivity {
	private List<Mp3Info> mp3Infos = null;
	private String MP3FILE_PATH=null;
//	private ImageView download_img;
//	private TextView percentView;
	private DownloadStatusReciever dsr;
	private int downloadresult=-2;
	List<HashMap<String, Object>> list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_mp3_list);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.MP3FILE_PATH=this.getFilesDir()+File.separator;
		Thread thread = new Thread(updateListThread);
		thread.start();
		
		dsr = new DownloadStatusReciever();
		IntentFilter dsrIntent = new IntentFilter();
		dsrIntent.addAction(AppConstant.DOWNLOAD_STATUS_ACTION);
		registerReceiver(dsr, dsrIntent);
	}
	
	public SimpleAdapter buildSimpleAdapter(List<Mp3Info> Mp3Infos) {
		list = new ArrayList<HashMap<String, Object>>();
		int i=1;
		for (Iterator<Mp3Info> iterator = Mp3Infos.iterator(); iterator.hasNext();) {
			Mp3Info mp3Info = (Mp3Info) iterator.next();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", i++);
			map.put("mp3_name", mp3Info.getMp3Name());
			// 传唤为mb 1兆字节(mb)=1048576字节(b)
			long mp3Sizetemp = (long)Integer.parseInt(mp3Info.getMp3Size());
			map.put("mp3_size", Mp3SizeUtils.getPrintSize(mp3Sizetemp));
			map.put("download_img",R.drawable.download_img);
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, list,
				R.layout.remote_mp3info_item, new String[] { "id","mp3_name", "mp3_size","download_img" },
				new int[] {R.id.mp3list_id, R.id.mp3_name, R.id.mp3_size,R.id.download_img});
		return simpleAdapter;
	}

	public void updateListView() {

		String xml = downloadXML(AppConstant.URL.BASE_URL+"resources.xml");
		mp3Infos = parse(xml);
		Message msg = handler.obtainMessage();
		Bundle b = new Bundle(); 
		b.putSerializable("mp3Infos", (Serializable) mp3Infos);
		msg.setData(b);
		msg.sendToTarget();
	}
	
	
	
	private Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
        	Bundle b = msg.getData();
        	@SuppressWarnings("unchecked")
			List<Mp3Info> mp3Infos = (List<Mp3Info>) b.getSerializable("mp3Infos");
        	SimpleAdapter simpleAdapter = buildSimpleAdapter(mp3Infos);
    		setListAdapter(simpleAdapter);
        }  
    };  
	
	Runnable updateListThread = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateListView();
		}
	};
	public String downloadXML(String urlstr) {
		HttpDownloader httpDownloader = new HttpDownloader();
		String result = httpDownloader.download(urlstr);
		return result;
	}

	public List<Mp3Info> parse(String xmlStr) {
		// 1.创建一个SAXParserFactory和XMLReader
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		List<Mp3Info> infos = new ArrayList<Mp3Info>();
		try {
			XMLReader xmlReader = saxParserFactory.newSAXParser()
					.getXMLReader();
			Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(
					infos);
			// 2.为xmlReader设置内容处理器
			xmlReader.setContentHandler(mp3ListContentHandler);
			// 3.调用parse开始解析字符串
			xmlReader.parse(new InputSource(new StringReader(xmlStr)));
			for (Iterator<Mp3Info> iterator = infos.iterator(); iterator.hasNext();) {
				iterator.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		return infos;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// 根据用户点击列表当中的位置，来得到相应的mp3Info对象
		Mp3Info mp3Info = mp3Infos.get(position);
		System.out.println(mp3Info);
		
		Intent intent = new Intent();
		intent.putExtra("mp3Info", mp3Info);
		intent.putExtra("MP3FILE_PATH", MP3FILE_PATH);
		intent.setClass(this, DownloadService.class); // 启动下载MP3
		startService(intent);
		
		if(!FileUtils.isFileExist(mp3Info.getId()+".mp3",MP3FILE_PATH)){
			DownloadAsyncTask downloadAysncTask = new DownloadAsyncTask(mp3Info,position);
			downloadAysncTask.execute(mp3Info.getMp3Size());
		};
	}
	
	/**
	 * 更新下载进度的线程
	 */
	class DownloadAsyncTask extends AsyncTask<String, Integer, String>{
		Mp3Info mp3Info;
		int position;
		ListView listView ;
		View chosedListItem;
		ImageView download_img;
		TextView percentView;
		public DownloadAsyncTask(Mp3Info mp3Info,int position) {
			this.mp3Info=mp3Info;
			this.position = position;
			listView = getListView();
			chosedListItem= listView.getChildAt(position);
			download_img = (ImageView) chosedListItem.findViewById(R.id.download_img);
			percentView = (TextView) chosedListItem.findViewById(R.id.percent_view);
		}

		@Override
		protected String doInBackground(String... mp3Size) {
			// TODO Auto-generated method stub
			Long mp3SizeInt = Long.parseLong(mp3Size[0]);
			int i = 0;
			int percetSize=0;
			
			String filePath= MP3FILE_PATH + mp3Info.getId()+".mp3";
			for(i=0;i<mp3SizeInt;i++){
				File file = new File(filePath);
				percetSize =(int)(100.0 * file.length()/mp3SizeInt);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("doInback--->"+percetSize);
				publishProgress(percetSize);
				if(percetSize==100){
					break;
				}
			}
			return mp3Info.getMp3Name();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			chosedListItem.setClickable(false);
			download_img.setVisibility(View.INVISIBLE);
			percentView.setText("0%");
			percentView.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			download_img.setVisibility(View.VISIBLE);
			percentView.setVisibility(View.INVISIBLE);
//			chosedListItem.setClickable(true);
//			Toast.makeText(Mp3ListActivity.this, result +" 下载成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			percentView.setText(values[0]+"%");
			System.out.println("AsyncTask--->"+values[0]);
		}
	}
	
	class DownloadStatusReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			downloadresult = intent.getIntExtra(AppConstant.DOWNLOAD_RESULT,-1);
			String resultMessage;
			if (downloadresult == 1) {
				resultMessage = "文件已经存在，不需要重复下载";
			} else if (downloadresult == 0) {
				resultMessage = "文件下载成功";
			} else {
				resultMessage = "下载失败";
			}
			Toast.makeText(Mp3ListActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
		}
	}
}
