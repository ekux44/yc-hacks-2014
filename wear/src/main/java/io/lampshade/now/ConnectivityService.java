package io.lampshade.now;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;

public class ConnectivityService extends Service {

  /**
   * Class used for the client Binder. Because we know this service always runs in the same process
   * as its clients, we don't need to deal with IPC.
   */
  public class LocalBinder extends Binder {

    public ConnectivityService getService() {
      // Return this instance of LocalService so clients can call public methods
      return ConnectivityService.this;
    }
  }

  // Binder given to clients
  private final IBinder mBinder = new LocalBinder();
  private final static int notificationId = 1337;

  private boolean mBound;
  private WakeLock mWakelock;
  private long mCreatedTime = 0;
  private boolean mDestroyed;

  private Handler mDelayedCalculateHandler;
  private Runnable myDelayedCalculateRunner;

  @Override
  public void onCreate() {
    super.onCreate();
    mCreatedTime = SystemClock.elapsedRealtime();
    mDestroyed = false;

    // acquire wakelock needed till everything initialized
    PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
    mWakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getString(R.string.app_name));
    mWakelock.acquire();

  }

  @Override
  /**
   * Called after onCreate when service (re)started independently
   */
  public int onStartCommand(Intent intent, int flags, int startId) {


    return super.onStartCommand(intent, flags, startId);
  }




  @Override
  public void onDestroy() {
    mDestroyed = true;

    if (mWakelock != null) {
      mWakelock.release();
    }
    super.onDestroy();
  }

  /**
   * Return the communication channel to the service.  May return null if clients can not bind to
   * the service.  The returned {@link android.os.IBinder} is usually for a complex interface that
   * has been <a href="{@docRoot}guide/components/aidl.html">described using aidl</a>.
   *
   * <p><em>Note that unlike other application components, calls on to the IBinder interface
   * returned here may not happen on the main thread of the process</em>.  More information about
   * the main thread can be found in <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes
   * and Threads</a>.</p>
   *
   * @param intent The Intent that was used to bind to this service, as given to {@link
   *               android.content.Context#bindService Context.bindService}.  Note that any extras
   *               that were included with the Intent at that point will <em>not</em> be seen here.
   * @return Return an IBinder through which clients can call on to the service.
   */
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
