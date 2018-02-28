package pl.service;

import java.io.File;

import pl.model.Mp3Info;
import pl.mp3player.AppConstant;
import pl.mp3player.AppConstant.MP3FILE;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageButton;

public class Mp3PlayerService extends Service {
	ImageButton beginButton = null;
	ImageButton pauseButton = null;
	ImageButton stopButton = null;
	MediaPlayer mediaPlayer = null;
	private boolean isCompletion;
	private boolean isServing;

	private boolean isPlaying = false;
	private boolean isPause = false;
	private boolean isReleased = false;

	private Mp3Info mp3Info = null;
	private String MP3FILE_PATH=null;
	Mp3PlayerBinder iBinder;
	private Handler handler;
	private SendServingStatus sendServingStatus;
	
	public Mp3PlayerService(){
		isServing  = true;
		handler =new Handler();
		sendServingStatus = new SendServingStatus();
		handler.post(sendServingStatus);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Mp3PlayerBinder iBinder = new Mp3PlayerBinder();
		return iBinder;
	}
	public class Mp3PlayerBinder extends Binder{
		public long getMediaStats(){
			if(mediaPlayer!=null){
				long mediaTime= mediaPlayer.getCurrentPosition();
				return mediaTime;
			}else return -1;
		}
		public long getMediaDuration(){
			return mediaPlayer.getDuration();
		}
		public void killService(){
			mediaPlayer.stop();
			stopSelf();
		}
		public boolean getIsCompletion(){
			return isCompletion;
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		MP3FILE_PATH=getFilesDir()+File.separator;
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		int MSG = intent.getIntExtra("MSG", 0);
		if (mp3Info != null) {
			if (MSG == AppConstant.PlayerMsg.PLAY_MSG) {
				play(mp3Info);
			}
		} else {
			if (MSG == AppConstant.PlayerMsg.PAUSE_MSG) {
				pause();
			} else if (MSG == AppConstant.PlayerMsg.STOP_MSG) {
				stop();
			}
		}
		new MediaPlayer.OnCompletionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				isCompletion = true;
			}
		};
		return super.onStartCommand(intent, flags, startId);
	}
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mediaPlayer!=null){
			mediaPlayer.release();
		}
		if(sendServingStatus!=null){
			handler.removeCallbacks(sendServingStatus);
		}
		super.onDestroy();
	}


	private void play(final Mp3Info mp3Info) {
		if (!isPlaying) {
			String path = getMp3Path(mp3Info);
			mediaPlayer = MediaPlayer.create(Mp3PlayerService.this,
					Uri.parse("file://" + path));
			mediaPlayer.setLooping(false);
			mediaPlayer.start();
			isPlaying = true;
			isReleased = false;
			isCompletion = false;
			
		}
	}
	
	class SendServingStatus implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isReleased){
				Intent i = new Intent();
				i.putExtra("isServing", isServing);
				System.out.println("mp3Servie  mp3Info--->"+mp3Info);
				if(mp3Info!=null){
					i.putExtra("oldMp3Name",mp3Info.getMp3Name());
				}
				i.setAction("ISPLAYING");
				Mp3PlayerService.this.sendBroadcast(i);
			}
			handler.postDelayed(sendServingStatus,100);
		}
	}

	private void pause() {
		if (mediaPlayer != null) {
			if (!isReleased) {
				if (!isPause) {
					mediaPlayer.pause();
					isPause = true;
					isPlaying = true;
				} else {
					mediaPlayer.start();
					isPause = false;
				}
			}
		}
	}

	private void stop() {
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (!isReleased) {
					mediaPlayer.stop();
					// mediaPlayer.release();
					isReleased = true;
				}
				isPlaying = false;
				handler.removeCallbacks(sendServingStatus);
			}
		}
	}

	private String getMp3Path(Mp3Info mp3Info) {
		String path = MP3FILE_PATH + mp3Info.getId()+".mp3";
		return path;
	}
	
}
