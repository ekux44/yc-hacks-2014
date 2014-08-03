package io.lampshade.now;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


      Intent actionIntent = new Intent(this, MainActivity.class);
      PendingIntent actionPendingIntent =
          PendingIntent.getActivity(this, 0, actionIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

      NotificationCompat.Action a = new NotificationCompat.Action(R.drawable.ic_action_play, "action", actionPendingIntent);

      /*NotificationCompat.BigPictureStyle bigStyle = new NotificationCompat.BigPictureStyle();
      bigStyle.bigPicture(BitmapFactory.decodeResource(this.getResources(),
                                                         R.drawable.ic_launcher_lampshade));
      */
      Notification notif = new NotificationCompat.Builder(this)
          .setContentTitle("New title")
          .setContentText("subject")
          .setSmallIcon(R.drawable.ic_launcher_lampshade)
      //    .setStyle(bigStyle)
          .extend(new NotificationCompat.WearableExtender()
                      .addAction(a)
                      .setContentAction(0)
                      .setContentIcon(R.drawable.ic_action_lampshade))
          .build();
      NotificationManagerCompat.from(this).notify(0, notif);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
