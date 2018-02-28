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

//AndroidӦ�õ��Զ�����������ģ���ʵ��
//http://blog.csdn.net/zml_2015/article/details/50756703
//���̣���ʼ����>��������汾��ȡ->�ֻ��ͻ��˰汾��ȡ���Ƚ�-����Ҫ���£�-�������°汾-����װ�°汾���������
public class UpdateActivity extends Activity {
	// ver.json�е�����Ϊ
	// [{"appname":"Mp3Player","apkname":"Mp3Player.apk","verName":1.0.1,"verCode":2}]";
	String verjsonLocal = "[{\"appname\":\"jtapp12\",\"apkname\":\"jtapp-12-updateapksamples.apk\",\"verName\":1.0.1,\"verCode\":2}]";
	// ��ȡ�汾��Ϣ�Ͱ汾����
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
		 * �ȽϷ������Ϳͻ��˵İ汾�������и��²�����
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

	// ��������ȡ�汾��Ϣ
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
		sb.append("��ǰ�汾:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(",/n�������°�,�������!");
		Dialog dialog = new AlertDialog.Builder(this).setTitle("�������")
				.setMessage(sb.toString())// ��������
				.setPositiveButton("ȷ��",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}


	private void doNewVersionUpdate() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:");
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(", �����°汾:");
		sb.append(newVerName);
		sb.append(" Code:");
		sb.append(newVerCode);
		sb.append(", �Ƿ����?");
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("�������")
				.setMessage(sb.toString())
				// ��������
				.setPositiveButton("����",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
//								pBar = new ProgressDialog(UpdateActivity.this);
//								pBar.setTitle("��������");
//								pBar.setMessage("���Ժ�...");
//								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								// ����APK�ļ�
								new DownFileAsyncTask().execute(
										Config.UPDATE_SERVER+Config.UPDATE_APKNAME,
										MP3FILE_PATH,Config.UPDATE_SAVENAME);
							}
						})
				.setNegativeButton("�ݲ�����",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ���"ȡ��"��ť֮���˳�����
								finish();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	
	//AsyncTask<Params params,Progress,Result>
//	1��Params������һ�����������һ�����ǻᶨ���String���͵ģ����籾������Ҫ��ȡ������Դ��URL��ַ
//	2��Progress������ִ�еĿ̶ȣ�һ�����ǻᶨ���Integer����,��onProgressUpdate��Progress���д����������
//	3��Result�����ؽ�����ͣ����籾�����Ƕ�����ͼƬ���л�ȡ����ô���ķ�������Ӧ����BitMap
	//excecute(params...)
	
	class DownFileAsyncTask extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			return new HttpDownloader().downFile(params[0], params[1], params[2]);
		}
		
		/**
		 * -1�����������ļ�����; 0�����������ļ��ɹ�; 1�������ļ��Ѿ�����
		 */
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			String resultMessage;
			if (result == 1) {
				resultMessage = "�����ļ��Ѿ����ڣ�����Ҫ�ظ�����";
				Toast.makeText(UpdateActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
			} else if (result == 0) {
				resultMessage = "�����ļ����سɹ�";
				Toast.makeText(UpdateActivity.this, resultMessage, Toast.LENGTH_SHORT).show();
				update() ;
			} else {
				resultMessage = "�����ļ�����ʧ��";
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
		// �������糬ʱ����
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
