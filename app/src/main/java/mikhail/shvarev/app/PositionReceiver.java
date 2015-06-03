package mikhail.shvarev.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.concurrent.TimeUnit;

/**
 * Created by Mihail on 22.05.2015.
 */
public class PositionReceiver extends Service{
    NotificationManager nm;
    LocationManager locationManager;
    String provider;
    LocationListener locationListener;
    static public boolean statusGPS = false;
    static public CameraUpdate cameraUpdate = null;
    static public boolean save = false;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BACKGROUND", "create");
        try {
            MapsInitializer.initialize(this);
        }
        catch (Exception e) {
            Log.e("MAPP", "Have GoogleMap but then error", e);
            //return;
        }

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.d("BACKGROUND", "thread      111");
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getApplicationContext().getSystemService(serviceString);
        provider = LocationManager.GPS_PROVIDER;
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d("BACKGROUND", "change local");
                // sendNotif("App","Местоположение определено");
                statusGPS = true;
                if(ParseUser.getCurrentUser() != null) {

                    Log.d("LOGg",save+" 0");
                    if(!save) {
                        save = true;
                        Log.d("LOGg",save+" 1");
                        ParseUser.getCurrentUser().put("position", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("receiverSave", "save");
                                save = false;
                                Log.d("LOGg",save+" 2");
                            }
                        });
                    }
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("BACKGROUND", "change status");
                statusGPS = false;
                // sendNotif("App","Поиск спутнико...");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("BACKGROUND", "provider on");
                statusGPS = false;
                //  sendNotif("App","Поиск спутнико...");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("BACKGROUND", "provider off");
                statusGPS = false;
                //  sendNotif("App","Поиск спутнико...");
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,99,10,locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BACKGROUND", "destroy");
        locationManager.removeUpdates(locationListener);
        nm.cancel(0);

    }

    public int onStartCommand(Intent intent, int flags, final int startId) {
      //  new Thread(new Runnable() {
      //      @Override
      //      public void run() {


         //   }
       // }).run();
        sendNotif("AroundMe","Передача местоположения...");
        return super.onStartCommand(intent, flags, startId);
    }

    void sendNotif(String title,String text) {
        // 1-я часть
        Notification notif = new Notification(R.drawable.icon_my, "AroundMe",
                System.currentTimeMillis());

        // 3-я часть
        Intent intent = new Intent(this, MainActivity.class);
       // intent.putExtra(MainActivity.FILE_NAME, "somefile");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // 2-я часть
        notif.setLatestEventInfo(this, title, text, pIntent);

        // ставим флаг, чтобы уведомление пропало после нажатия
       // notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.flags |= Notification.FLAG_ONGOING_EVENT;

        // отправляем
        nm.notify(0, notif);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
