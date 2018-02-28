package pl.mp3player;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import pl.download.HttpDownloader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

//Android应用的自动升级、更新模块的实现
//http://blog.csdn.net/zml_2015/article/details/50756703
//流程：开始——>服务器侧版本获取->手机客户端版本获取、比较-》需要更新？-》下载新版本-》安装新版本（或结束）
public class UpdateActivity extends Activity {
	// ver.json中的内容为
	// [{"appname":"Mp3Player","apkname":"Mp3Player.apk","verName":1.0.1,"verCode":2}]";
	String verjsonLocal = "[{\"appname\":\"jtapp12\",\"apkname\":\"jtapp-12-updateapksamples.apk\",\"verName\":1.0.1,\"verCode\":2}]";
	// 获取版本信息和版本名称
	private String newVerName;
	private int newVerCode;
	private String MP3FILE_PATH;
	private ProgressDialog pBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		this.MP3FILE_PATH = this.getFilesDir() + File.separator;
		/**
		 * 比较服务器和客户端的版本，并进行更新操作。
		 */
		if (getServerVerCode()) {
			int vercode=Config.getVerCode(this);
			if (newVerCode>vercode) {
				doNewVersionUpdate();
			}else {
				notNewVersionShow();
			}
		}
	}

	// 服务器获取版本信息
	private boolean getServerVerCode() {
		try {
			 String verjson = getContent(Config.UPDATE_SERVER
			 + Config.UPDATE_VERJSON);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);

				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					newVerName = obj.getString("verName");
				} catch (Exception e) {
					newVerCode = -1;
					newVerName = "";
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	void down() {
		new Handler().post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});
	}

	private void notNewVersionShow() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(",/n已是最新版,无需更新!");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("软件更新")
				.setMessage(sb.toString())// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}


	private void doNewVersionUpdate() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(", 发现新版本:");
		sb.append(newVerName);
		sb.append(" Code:");
		sb.append(newVerCode);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
//								pBar = new ProgressDialog(UpdateActivity.this);
//								pBar.setTitle("正在下载");
//								pBar.setMessage("请稍候...");
//								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								// 下载APK文件
								new DownFileAsyncTask().execute(
										Config.UPDATE_SERVER+Config.UPDATE_APKNAME,
										MP3FILE_PATH,Config.UPDATE_SAVENAME);
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	//AsyncTask<Params params,Progress,Result>
//	1、Params：这是一个任务参数，一般我们会定义成String类型的，例如本例子中要获取网络资源的URL地址
//	2、Progress：任务执行的刻度，一般我们会定义成Integer类型,在onProgressUpdate（Progress）中传入参数类型
//	3、Result：返回结果类型，例如本例中是对网络图片进行获取，那么它的返回类型应该是BitMap
	//excecute(params...)
	
	class DownFileAsyncTask extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			return new HttpDownloader().downFile(params[0], params[1], params[2]);
		}
		
		/**
		 * -1：代表下载文件出错; 0：代表下载文件成功; 1：代表文件已经存在
		 */
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String resultMessage;
			if (result == 1) {
				resultMessage = "更新文件已经存在，不需要重复下载";
				Toast.makeText(UpdateActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
			} else if (result == 0) {
				resultMessage = "更新文件下载成功";
				Toast.makeText(UpdateActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
				update() ;
			} else {
				resultMessage = "更新文件下载失败";
				Toast.makeText(UpdateActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(MP3FILE_PATH, Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

	public static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		// 设置网络超时参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();
	}
}
