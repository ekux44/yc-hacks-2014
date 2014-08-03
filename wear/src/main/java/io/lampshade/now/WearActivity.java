package io.lampshade.now;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.ArrayList;
import java.util.List;

public class WearActivity extends Activity implements SensorEventListener{

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;
    private static String brightness;
    public long lastBriChangedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        mTextView = (TextView)this.findViewById(R.id.text);


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


      final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        senSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    @Override
    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // Read Y value to figure out device rotation.
            // The larger the y value, the farther the watch is rotated to the right and vice versa.

          if(SystemClock.elapsedRealtime() > (lastBriChangedTime + 500l)){
            /*
            double palmGravity = Math.sqrt(Math.pow(x,2) + Math.pow(z, 2));

            if(palmGravity > 8){
              Log.d("imu", "X: " + x);
              Log.d("imu", "Y: " + y);
              Log.d("imu", "Z: " + z);
              mTextView.setText("X: " + x + " Y: " + y + " Z: " + z + "   palmGrav:"+palmGravity);
            } else {
              Log.d("imu", "rejected");
              mTextView.setText("rejected");
            }
            */

            this.brightness =""+( 50 + 50/10* y);

            mTextView.setText(this.brightness);
            sendSpeech();

            lastBriChangedTime = SystemClock.elapsedRealtime();
          }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

  private List<Node> getNodes() {
    List<Node> nodes = new ArrayList<Node>();
    NodeApi.GetConnectedNodesResult rawNodes =
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
    for (Node node : rawNodes.getNodes()) {
      nodes.add(node);
    }
    return nodes;
  }

  private void sendSpeech() {
    //  This, or at least getNodes() has to be done in the background. Explanation there.
    new AsyncTask<Void, Void, List<Node>>() {

      @Override
      protected List<Node> doInBackground(Void... params) {
        return getNodes();
      }

      @Override
      protected void onPostExecute(List<Node> nodeList) {

        List<Node> nodes = nodeList;
        for (Node node : nodes) {
          PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
              mGoogleApiClient,
              node.getId(),
              WearActivity.brightness,
              null
          );

          result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
              Log.v("wear", "we got something back");
            }
          });
        }
      }
    }.execute();


  }
}
