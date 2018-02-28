package pl.mp3player;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pl.lrc.LrcProcessor2Map;
import pl.model.Mp3Info;
import pl.service.Mp3PlayerService;
import pl.service.Mp3PlayerService.Mp3PlayerBinder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerActivity extends Activity {
	private ImageButton beginButton = null;
	private ImageButton pauseButton = null;
	private ImageButton stopButton = null;
	private TextView tvPlayStats = null;
	private TextView tvPayingMp3Name = null;

	private List<Map<Long, String>> maps = null;
	private TextView lrcTextView = null;
	private Mp3Info mp3Info = null;
	// private Mp3Info mp3InfoNext = null;
	private List<Mp3Info> mp3Infos;
	private String oldMp3Name;
	private LrcProcessor2Map lrcProcessor2Map = null;
	private UpdateTimeCallback updateTimeCallback = null;
	private Handler handler;
	private Mp3PlayerBinder mpBinder;
	private long begin = 0;// 开始时系统时间
	private long currentTimeMill = 0;// 目前播放时间
	private long pauseTimeMills = 0;// 暂停时间
	private boolean isPlaying = false;
	private boolean isServing = false;
	private Intent intentPlayService;

	Mp3PlayerService mp3PlayerService;
	Mp3BroadcastReceiver mp3BroadcastReceiver;

	private String MP3FILE_PATH = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		mp3Info = (Mp3Info) intent.getSerializableExtra("mp3Info");
		// mp3Infos = intent.getSerializableExtra(name)
		
		mp3BroadcastReceiver = new Mp3BroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ISPLAYING");
		registerReceiver(mp3BroadcastReceiver, intentFilter);

		beginButton = (ImageButton) findViewById(R.id.begin);
		pauseButton = (ImageButton) findViewById(R.id.pause);
		stopButton = (ImageButton) findViewById(R.id.stop);
		beginButton.setOnClickListener(new BeginButtonListener());
		pauseButton.setOnClickListener(new PauseButtonListener());
		stopButton.setOnClickListener(new StopButtonListener());
		tvPlayStats = (TextView) findViewById(R.id.playing_stats);
		tvPayingMp3Name = (TextView) findViewById(R.id.playing_mp3name);
		lrcTextView = (TextView) findViewById(R.id.lrcText);
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		tvPayingMp3Name.setText(mp3Info.getMp3Name());
		lrcProcessor2Map = new LrcProcessor2Map();
		// String lrcString = lrcProcessor2Map.getLrcString(MP3FILE_PATH);
		// lrcTextView.setText(lrcString);

		maps = lrcProcessor2Map.parse(MP3FILE_PATH + mp3Info.getId() + ".lrc");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		unregisterReceiver(mp3BroadcastReceiver);
		super.onStop();
	}

	class Mp3BroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			isServing = intent.getBooleanExtra("isServing", isServing);
			oldMp3Name = intent.getStringExtra("oldMp3Name");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = this.getParentActivityIntent();
			if (this.shouldUpRecreateTask(upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.navigateUpTo(upIntent);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	class BeginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 创建一个Intent对象，用于同时Service开始播放MP3
			if(isServing){
				Intent stopService = new Intent();
				stopService.setClass(PlayerActivity.this, Mp3PlayerService.class);
				stopService(stopService);
			}
			tvPlayStats.setText("正在播放");
			intentPlayService = new Intent();
			intentPlayService.setClass(PlayerActivity.this,
					Mp3PlayerService.class);
			intentPlayService.putExtra("mp3Info", mp3Info);
			intentPlayService.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
//			bindService(intentPlayService, conn, BIND_AUTO_CREATE);
			// 启动Service
			startService(intentPlayService);
			// 将begin的值置为当前毫秒数
			begin = System.currentTimeMillis();
			currentTimeMill = 0;
			updateTimeCallback = new UpdateTimeCallback(maps);
			handler = new Handler();
			handler.post(updateTimeCallback);
			isPlaying = true;
		}
	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mpBinder = (Mp3PlayerBinder) service;
		}
	};

	class PauseButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 通知Service暂停播放MP3
			tvPlayStats.setText("暂停");
			Intent intent = new Intent();
			intent.setClass(PlayerActivity.this, Mp3PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
			startService(intent);
			//
			if (isPlaying) {
				handler.removeCallbacks(updateTimeCallback);
				pauseTimeMills = System.currentTimeMillis();
			} else {
				handler.postDelayed(updateTimeCallback, 5);
				begin = System.currentTimeMillis() - pauseTimeMills + begin;
			}
			isPlaying = isPlaying ? false : true;
		}
	}

	class StopButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 通知Service停止播放MP3文件
			tvPlayStats.setText("停止");
			Intent intent = new Intent();
			intent.setClass(PlayerActivity.this, Mp3PlayerService.class);
			intent.putExtra("MSG", AppConstant.PlayerMsg.STOP_MSG);
			startService(intent);
			stopService(intent);
			// 从Handler当中移除updateTimeCallback
			if (updateTimeCallback != null && handler != null) {
				handler.removeCallbacks(updateTimeCallback);
			}
		}

	}

	// 播放下一首歌曲
	private void playNextMp3() {
		// stopService(intentPlayService);
		// Intent intentNextService = new Intent();
		// intentNextService = new Intent();
		// intentNextService.setClass(PlayerActivity.this,
		// Mp3PlayerService.class);
		// intentNextService.putExtra("mp3Info", mp3InfoNext);
		// intentNextService.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
		// bindService(intentNextService, conn, BIND_AUTO_CREATE);
		// // 启动Service
		// startService(intentNextService);
	}

	class UpdateTimeCallback implements Runnable {
		List<Map<Long, String>> maps;

		// ArrayList<Queue> queues;
		public UpdateTimeCallback(List<Map<Long, String>> maps) {
			this.maps = maps;
		}

		@Override
		public void run() {
			if (mpBinder != null) {
				if (mpBinder.getIsCompletion()) {
					playNextMp3();
					System.out.println("第一首播放结束");
				}
			}

			// 计算偏移量，也就是说从开始播放MP3到现在为止，共消耗了多少时间，以毫秒为单位
			long offset = System.currentTimeMillis() + currentTimeMill - begin;
			if (maps == null || maps.isEmpty()) {
				System.out.println("没有解析歌词信息！Runnable");
			} else {
				for (Map<Long, String> map : maps) {
					for (Entry<Long, String> entry : map.entrySet()) {
						if (offset - entry.getKey() <= 200
								&& offset >= entry.getKey()) {
							lrcTextView.setText(entry.getValue());
							System.out.println(entry.getKey() + "---->"
									+ entry.getValue());
							break;
						}
					}
				}
			}
			handler.postDelayed(updateTimeCallback, 100);
		}
	}
}
