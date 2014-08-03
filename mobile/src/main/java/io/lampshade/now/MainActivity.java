package io.lampshade.now;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

  public static void showNotification(Context c, String group, String mood) {
    final Intent actionIntent = new Intent("com.twofortyfouram.locale.intent.action.FIRE_SETTING");
    actionIntent.setComponent(new ComponentName("com.kuxhausen.huemore",
                                                "com.kuxhausen.huemore.automation.FireReceiver"));

    LegacyGMB gmb = new LegacyGMB();
    gmb.group = group;
    gmb.mood = mood;
    Bundle bundle = new Bundle();
    Gson gson = new Gson();
    bundle.putString("com.kuxhausen.huemore.EXTRA_BUNDLE_SERIALIZED_BY_NAME", gson.toJson(gmb));

    actionIntent.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE", bundle);

    PendingIntent actionPendingIntent =
        PendingIntent.getBroadcast(c, 0, actionIntent,
                                   PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Action
        a =
        new NotificationCompat.Action(R.drawable.ic_action_play, "action", actionPendingIntent);

    Notification notif = new NotificationCompat.Builder(c)
        .setContentText(group + " \u2190 " + mood)
        .setSmallIcon(R.drawable.ic_launcher_lampshade)
        .setContentIntent(actionPendingIntent)
        .setAutoCancel(true)
        .extend(new NotificationCompat.WearableExtender()
                    .addAction(a)

                    .setHintHideIcon(true)
                    .setContentAction(0)
                    .setContentIcon(R.drawable.ic_action_lampshade))
        .build();
    notif.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
    NotificationManagerCompat.from(c).notify(0, notif);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //this.showNotification(this, "Home", "Party");
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
