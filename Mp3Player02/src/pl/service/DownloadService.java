package pl.service;

import pl.download.HttpDownloader;
import pl.model.Mp3Info;
import pl.mp3player.AppConstant;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {
	private String MP3FILE_PATH=null;
	private String baseUrl= AppConstant.URL.BASE_URL;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//初始调用函数
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		MP3FILE_PATH=intent.getStringExtra("MP3FILE_PATH");
		Mp3Info mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		DownloadThread downloadThread = new DownloadThread(mp3Info);
		Thread thread = new Thread(downloadThread);
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	class DownloadThread implements Runnable {
		private Mp3Info mp3Info = null;

		public DownloadThread(Mp3Info mp3Info) {
			this.mp3Info = mp3Info;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// addr:"http://192.168.2.144:8080/mp3/resources.xml"
			String mp3IdName = mp3Info.getId()+".mp3";
			String lrcIdName = mp3Info.getId()+".lrc";
			String mp3Url = baseUrl
					+ mp3IdName;
			String lrcUrl = baseUrl +lrcIdName;
			HttpDownloader httpDownloader = new HttpDownloader();
			httpDownloader.downFile(lrcUrl, MP3FILE_PATH, lrcIdName);
			int result = httpDownloader.downFile(mp3Url, MP3FILE_PATH,mp3IdName);
			Intent i = new Intent();
			i.putExtra(AppConstant.DOWNLOAD_RESULT, result);
			i.setAction(AppConstant.DOWNLOAD_STATUS_ACTION);
			DownloadService.this.sendBroadcast(i);
			// 使用Notification提示客户下载结果
//			 Toast.makeText(DownloadService.this,resultMessage,
//			 Toast.LENGTH_SHORT).show();
		}
	}
}
