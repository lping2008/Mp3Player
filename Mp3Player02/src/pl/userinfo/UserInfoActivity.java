package pl.userinfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import pl.mp3player.MainActivity;
import pl.mp3player.R;
import pl.utils.UserFileUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UserInfoActivity extends Activity {
	boolean isLogined=false;
	private TextView tvUserName=null;
	private View vExitLogin ;
	private View vUserHeadImg;
	private String userName;
	private String MP3FILE_PATH = null;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.userinfo_list);
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		tvUserName=(TextView) findViewById(R.id.user_name_info);
		vUserHeadImg=findViewById(R.id.user_head_img);
		
		vUserHeadImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		isLogined=intent.getBooleanExtra("isLogined", isLogined);
		userName=intent.getStringExtra("userName");
		if(isLogined){
			tvUserName.setText(userName);
		}
		vExitLogin= findViewById(R.id.exitlogin);
		vExitLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				System.out.println("UserInfoOnclickDialog--->");
				// TODO Auto-generated method stub
				AlertDialog.Builder exitDialog = new AlertDialog.Builder(UserInfoActivity.this);
				exitDialog.setTitle("退出确认");
				exitDialog.setMessage("是否确认退出登录");
				exitDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
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
						Intent i = new Intent();
						i.setClass(UserInfoActivity.this, MainActivity.class);
						i.putExtra("isLogined", isLogined);
						startActivity(i);
						finish();
					}
				});
				exitDialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return ;
					}
				});
				exitDialog.show();
				
			}
			
		});
	}
	/**
	 * 5分钟实现Android中更换头像功能
	 */
	//http://blog.csdn.net/melodev/article/details/51477369 
	
//	private void showTypeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final AlertDialog dialog = builder.create();
//        View view = View.inflate(this, R.layout.dialog_select_photo, null);
//        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
//        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
//        tv_select_gallery.setOnClickListener(new OnClickListener() {// 在相册中选取
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
//                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent1, 1);
//                dialog.dismiss();
//            }
//        });
//        tv_select_camera.setOnClickListener(new OnClickListener() {// 调用照相机
//            @Override
//            public void onClick(View v) {
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
//                startActivityForResult(intent2, 2);// 采用ForResult打开
//                dialog.dismiss();
//            }
//        });
//        dialog.setView(view);
//        dialog.show();
//    }

	
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
}
	
