package pics.sift.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GCMReceiver extends BroadcastReceiver {
    private static final String PARAMETER_ALBUM_ID = "album";
    private static final String PARAMETER_TITLE = "title";

    private static final int NOTIFICATION_REQUEST = 1;
    private static final int DEFAULT_OFFSET = 9000;

    @Override
    public void onReceive(Context rc, Intent ri) {
        Context context = rc.getApplicationContext();
        Bundle extras = ri.getExtras();

        if(extras != null) {
            String albumId = extras.getString(PARAMETER_ALBUM_ID);
            String title = extras.getString(PARAMETER_TITLE);

            if(albumId == null) {
                int albumId_ = extras.getInt(PARAMETER_ALBUM_ID, 0);

                if(albumId_ > 0) {
                    albumId = Integer.toString(albumId_);
                }
            }

            if(albumId != null && title != null) {
                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                if(notificationManager != null) {
                    String message = context.getString(R.string.notification_new_album, title);
                    Notification notification = new NotificationCompat.Builder(context).setContentTitle(context.getString(R.string.app_name)).setContentText(message).setSmallIcon(R.mipmap.ic_launcher).build();
                    Intent ni = AlbumActivity.getStartIntent(context, albumId, title);
                    int offset = DEFAULT_OFFSET;

                    try {
                        offset = Integer.parseInt(albumId);
                    }
                    catch(Exception e) {
                    }

                    notification.contentIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST + offset, ni, PendingIntent.FLAG_CANCEL_CURRENT);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(NOTIFICATION_REQUEST + offset, notification);
                }
            }
        }
    }
}
