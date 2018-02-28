package pl.utils;

import pl.mp3player.R;
import android.app.Activity;
import android.content.Context;


public class AnimationUtil {
	public static void finishActivityAnimation(Context context) {
		((Activity) context).finish();
		((Activity) context).overridePendingTransition(R.anim.zoom_enter,
				R.anim.zoom_exit);
	}
}
