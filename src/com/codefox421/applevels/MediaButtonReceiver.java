package com.codefox421.applevels;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;

public class MediaButtonReceiver extends BroadcastReceiver {
	
	Context mContext;
	
	public MediaButtonReceiver(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		KeyEvent ke = (KeyEvent)intent.getExtras().get(Intent.EXTRA_KEY_EVENT); 
        switch (ke.getKeyCode()) {
        case (KeyEvent.KEYCODE_VOLUME_DOWN):
        case (KeyEvent.KEYCODE_VOLUME_UP):
        case (KeyEvent.KEYCODE_VOLUME_MUTE):
        	// Find the current volume
        	AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        	int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        	
        	/* Post a notification with the current volume
        	NotificationCompat.Builder builder = new NotificationCompat
        			.Builder(mContext);
        	builder.setSmallIcon(R.drawable.ic_launcher)
        			.setContentTitle("Media Volume")
        			.setContentText(Integer.toString(currentVolume));
        	NotificationManager notificationManager =
        			(NotificationManager) mContext
        			.getSystemService(Context.NOTIFICATION_SERVICE);
        	notificationManager.notify(0, builder.build());*/
        	
        	Toast.makeText(mContext, "Volume Level: " + currentVolume, Toast.LENGTH_SHORT).show();
        }
	}

}
