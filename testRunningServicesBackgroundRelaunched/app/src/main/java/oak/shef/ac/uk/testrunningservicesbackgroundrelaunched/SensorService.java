package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    public int counter=0;
    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        connectChrome();
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void connectChrome() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", 1);
            jo.put("method", "Page.enable");
        }
        catch (JSONException e) {
            Log.e("Error", e.toString());
        }

        LocalSocket s = new LocalSocket();
        try {
            s.connect(new LocalSocketAddress("chrome_devtools_remote"));
            Log.i("Chrome", "After local socket connect");
            OutputStream oss = s.getOutputStream();
            oss.write(jo.toString().getBytes("utf-8"));
            InputStream iss = s.getInputStream();
            Integer i;
            String res = "";
            while ((i = iss.read()) != -1) {
                res += i.toString();
            }
        } catch (Exception e) {
            Log.e("Error", "Connecting Local Socket "+e.toString());
        }
    }
    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}