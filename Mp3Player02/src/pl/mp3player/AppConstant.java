package pl.mp3player;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public interface AppConstant {
	public static final String DOWNLOAD_RESULT="download_result";
	public static final String DOWNLOAD_STATUS_ACTION = "downlod_status_action";
	public class PlayerMsg{
		public static final int PLAY_MSG= 1;
		public static final int PAUSE_MSG=2;
		public static final int STOP_MSG=3;
	}
	public class URL{
//		public static final String BASE_URL="http://192.168.2.144:8080/mp3";
		public static final String BASE_URL = "http://103.44.145.245:34725/mp3/";
	}
	public class MP3FILE{
//		public static final String PATH= File.separator+"mp3"+File.separator;
	}
}
