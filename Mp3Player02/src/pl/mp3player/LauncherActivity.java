package pl.mp3player;

import pl.utils.AnimationUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

public class LauncherActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher);
//		LinearLayout layout = (LinearLayout) findViewById(R.id.launcher_layout);
//		layout.setSystemUiVisibility(View.INVISIBLE);
		startMainMp3Activity();
	}

	private void startMainMp3Activity() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				startActivity(new Intent(LauncherActivity.this,
						MainActivity.class));
				AnimationUtil.finishActivityAnimation(LauncherActivity.this);
			}
		}, 2000);
	}
}
