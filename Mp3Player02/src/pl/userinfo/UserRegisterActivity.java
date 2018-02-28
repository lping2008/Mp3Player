package pl.userinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import pl.mp3player.LocalMp3ListActivity;
import pl.mp3player.MainActivity;
import pl.mp3player.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserRegisterActivity extends Activity {
	private Button btnRegister;
	private Button btnLogin;
	private EditText etUserName;
	private EditText etUserPW;
	private EditText etUserPWagain;
	private boolean isRegistered;
	private String MP3FILE_PATH = null;
	FileOutputStream fileOutputStream = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		btnRegister = (Button) findViewById(R.id.btn_register_inregister);
		btnLogin = (Button) findViewById(R.id.btn_login_inregister);
		etUserName = (EditText) findViewById(R.id.et_userName_inregister);
		etUserPW = (EditText) findViewById(R.id.et_password_inregister);
		etUserPWagain = (EditText) findViewById(R.id.et_password_ag_inregister);
		btnRegister.setOnClickListener(new BtnRegisterListener());
		btnLogin.setOnClickListener(new BtnLoginListener());
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		System.out.println("UserRegister--->" + MP3FILE_PATH);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class BtnRegisterListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String userName = etUserName.getText().toString();
			String userPW = etUserPW.getText().toString();
			String userPWag = etUserPWagain.getText().toString();
			Properties userPro = new Properties();
			File file = new File(MP3FILE_PATH + "Users.properties");
			System.out.println("UserRegister--->" + MP3FILE_PATH
					+ "Users.properties");
			try {
				fileOutputStream = openFileOutput("Users.properties",
						MODE_APPEND);
				// UserFileUtils.loadPro(userPro, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 判断用户名是否在普通用户中已存在
			if (userName.length() != 0) {

				if (userPro.containsKey(userName)) {
					Toast.makeText(UserRegisterActivity.this, "用户名已存在",
							Toast.LENGTH_SHORT).show();
				} else {
					isPassword(userPro, file, userName, userPW, userPWag);
				}
			} else {
				Toast.makeText(UserRegisterActivity.this, "用户名不能为空",
						Toast.LENGTH_SHORT).show();
			}
		}

		private void isPassword(Properties userPro, File file, String userName,
				String userPW, String userPWag) {
			// TODO Auto-generated method stub
			if (userPW.equals(userPWag)) {
				if (userPW.length() != 0) {
					userPro.setProperty(userName, userPWag);
					try {
						userPro.store(fileOutputStream,
								"Copyright (c) Reserved");
						// 返回登陆界面
						Toast.makeText(UserRegisterActivity.this, "注册成功",
								Toast.LENGTH_SHORT).show();
						Intent intent = new Intent();
						intent.putExtra("isRegistered", isRegistered);
						intent.setClass(UserRegisterActivity.this,
								UserLoginActivity.class);
						startActivity(intent);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					Toast.makeText(UserRegisterActivity.this, "密码为空！",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(UserRegisterActivity.this, "密码不一致！",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class BtnLoginListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("isRegistered", isRegistered);
			intent.setClass(UserRegisterActivity.this, UserLoginActivity.class);
			startActivity(intent);
		}
	}
}
