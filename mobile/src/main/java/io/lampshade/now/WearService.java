package io.lampshade.now;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WearService extends WearableListenerService {

  private GoogleApiClient mGoogleApiClient;

  @Override
  public void onCreate() {
    super.onCreate();
    //  Needed for communication between watch and device.
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
          @Override
          public void onConnected(Bundle connectionHint) {
            Log.d("wear", "onConnected: " + connectionHint);
          }

          @Override
          public void onConnectionSuspended(int cause) {
            Log.d("wear", "onConnectionSuspended: " + cause);
          }
        })
        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
          @Override
          public void onConnectionFailed(ConnectionResult result) {
            Log.d("wear", "onConnectionFailed: " + result);
          }
        })
        .addApi(Wearable.API)
        .build();

    mGoogleApiClient.connect();
  }

  @Override
  public void onPeerConnected(Node peer) {
    super.onPeerConnected(peer);

    String id = peer.getId();
    String name = peer.getDisplayName();

    Log.d("wear", "Connected peer name & ID: " + name + "|" + id);
  }

  @Override
  public void onMessageReceived(MessageEvent messageEvent) {
    final Intent actionIntent = new Intent("com.twofortyfouram.locale.intent.action.FIRE_SETTING");

    actionIntent.setComponent(new ComponentName("com.kuxhausen.huemore",
                                                "com.kuxhausen.huemore.automation.FireReceiver"));


    Log.v("wear", "msg rcvd");
    Log.v("wear", messageEvent.getPath());
    Integer brightness = Integer.parseInt(messageEvent.getPath());

    if(brightness == null)
      brightness = 0;

    LegacyGMB gmb = new LegacyGMB();
    gmb.group = "Office";
    gmb.mood = "On";
    gmb.brightness = brightness;
    Bundle bundle = new Bundle();
    Gson gson = new Gson();
    bundle.putString("com.kuxhausen.huemore.EXTRA_BUNDLE_SERIALIZED_BY_NAME", gson.toJson(gmb));

    actionIntent.putExtra("com.twofortyfouram.locale.intent.extra.BUNDLE", bundle);

    this.sendBroadcast(actionIntent);
  }
}
