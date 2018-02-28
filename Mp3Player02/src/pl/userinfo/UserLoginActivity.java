package pl.userinfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import pl.download.HttpDownloader;
import pl.model.UserInfo;
import pl.mp3player.LocalMp3ListActivity;
import pl.mp3player.MainActivity;
import pl.mp3player.R;
import pl.utils.UserFileUtils;
import pl.xml.UserXmlContentHandler;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLoginActivity extends Activity {
	private EditText etUserName = null;
	private EditText etUserPW = null;
	private Button btnLogin = null;
	private Button btnRegister = null;
	private boolean isLogined;
	private String userName;
	private String userPW;
	private String MP3FILE_PATH = null;
	FileOutputStream fileOutputStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		etUserName = (EditText) findViewById(R.id.et_userName);
		etUserPW = (EditText) findViewById(R.id.et_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnLogin.setOnClickListener(new BtnLoginListener());
		btnRegister.setOnClickListener(new BtnRegisterListener());
		this.MP3FILE_PATH = this.getFilesDir()+ File.separator;
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

	class BtnLoginListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			userName = etUserName.getText().toString();
			userPW = etUserPW.getText().toString();
			if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPW)) {
				Toast.makeText(UserLoginActivity.this, "用户名或密码不能为空",
						Toast.LENGTH_SHORT).show();
			} else {

				// parseFromServerXml();// 通过服务器端的XML判断用户名和密码
				// 将用户名和密码保存在本地的文件中
				Properties userPro = new Properties();
				Properties loginPro = new Properties();
				File file = new File(MP3FILE_PATH + "Users.properties");
				File isLoginFile = new File(MP3FILE_PATH +"isLoginFile.properties");
				try {
					UserFileUtils.loadPro(userPro, file);
					UserFileUtils.loadPro(loginPro, isLoginFile);
					fileOutputStream = openFileOutput("isLoginFile.properties",MODE_APPEND);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (file.length() != 0) {
					System.out.println("UserLogin--->" + file.length());
					if (userPro.containsKey(userName)) {
						if (userPW.equals(userPro.getProperty(userName))) {
							loginPro.setProperty("isLogined", "loginedIsTrue");
							loginPro.setProperty("loginedIs", userName);
							try {
								loginPro.store(fileOutputStream, "CopyRight Reserved");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(loginPro.getProperty("isLogined")+"--->"+loginPro.getProperty("loginedIs"));
							Toast.makeText(UserLoginActivity.this, "登录成功",
									Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							boolean isLogined = true;
							intent.putExtra("isLogined", isLogined);
							intent.putExtra("userName", userName);
							intent.setClass(UserLoginActivity.this,
									MainActivity.class);
							startActivity(intent);

						} else {
							Toast.makeText(UserLoginActivity.this, "您输入的密码有误！",
									Toast.LENGTH_SHORT).show();
							etUserName.setText("");
							etUserPW.setText("");
							etUserName.requestFocus();
						}
					} else {
						Toast.makeText(UserLoginActivity.this, "您输入的用户不存在！",
								Toast.LENGTH_SHORT).show();
						etUserName.setText("");
						etUserPW.setText("");
						etUserName.requestFocus();
					}
				} else {
					Toast.makeText(UserLoginActivity.this, "您输入的用户不存在！",
							Toast.LENGTH_SHORT).show();
					etUserName.setText("");
					etUserPW.setText("");
					etUserName.requestFocus();
				}
			}
		}
	}

	class BtnRegisterListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(UserLoginActivity.this, UserRegisterActivity.class);
			startActivity(intent);
		}
	}

	public void parseFromServerXml() {
		HttpDownloader httpDownloader = new HttpDownloader();
		String userXml = httpDownloader
				.download("http://192.168.2.144:8080/mp3/userinfo.xml");
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		UserInfo userInfo = new UserInfo();
		try {
			XMLReader xmlReader = saxParserFactory.newSAXParser()
					.getXMLReader();
			UserXmlContentHandler userXmlParse = new UserXmlContentHandler(
					userInfo);
			xmlReader.setContentHandler(userXmlParse);
			xmlReader.parse(new InputSource(new StringReader(userXml)));
			System.out.println("为什么会打印这个登录userName--->"
					+ userInfo.getUserName());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (userName.equals(userInfo.getUserName())
				&& userPW.equals(userInfo.getUserPW())) {
			Intent intent = new Intent();
			isLogined = true;
			intent.putExtra("isLogined", isLogined);
			intent.putExtra("userName", userInfo.getUserName());
			intent.setClass(UserLoginActivity.this, LocalMp3ListActivity.class);
			startActivity(intent);
		}
	}
}
