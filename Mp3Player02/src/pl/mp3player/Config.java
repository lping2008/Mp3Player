package pl.mp3player;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class Config {

	public static final String UPDATE_SERVER = AppConstant.URL.BASE_URL;
	protected static final String UPDATE_APKNAME = "Mp3Player.apk";
	public static final String UPDATE_SAVENAME = "Mp3Player¡£apk";
	public static final String APP_PACKEG="pl.mp3player";
	public static final String UPDATE_VERJSON = "ver.json";

	public static int getVerCode(Context context) {
		// TODO Auto-generated method stub
		int verCode = -1;
		try {
			verCode = context.getPackageManager()
					.getPackageInfo(APP_PACKEG, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		// TODO Auto-generated method stub
		String verName = "";
		try {
			verName = context.getPackageManager()
					.getPackageInfo(APP_PACKEG, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}
	public static String getAppName(Context context){
		String verName=context.getResources().getText(R.string.app_name).toString();
		return verName;
	}
}
