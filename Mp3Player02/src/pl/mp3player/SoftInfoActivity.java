package pl.mp3player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SoftInfoActivity extends Activity {
	Button btnCheckUpdate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.softinfo);
		btnCheckUpdate = (Button) findViewById(R.id.btn_update);
		btnCheckUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(SoftInfoActivity.this, UpdateActivity.class));
			}
		});
	}
}
