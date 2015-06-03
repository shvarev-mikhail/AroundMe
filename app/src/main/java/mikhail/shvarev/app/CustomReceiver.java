package mikhail.shvarev.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import mikhail.shvarev.app.main.ItemMessages;
import mikhail.shvarev.app.main.Messaging;
//
//import mikhail.shvarev.app.dataBaseHelper.DBHelper;
//import mikhail.shvarev.app.main.Main;
//import mikhail.shvarev.app.main.Messaging;

/**
 * Created by Mihail on 12.05.2015.
 */
public class CustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    String msg    = "";
    String from   = "";
    String object = "";
    Bitmap userIcon;

    @Override
    public void onReceive(final Context ctx, Intent intent) {
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString(ParseUser.getCurrentUser().getUsername());
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
                // msg = json.getString(key);
                if(key.equals("mess")){
                    msg = json.getString(key);
                }
                if(key.equals("from")){
                    from = json.getString(key);
                }
                if(key.equals("object")){
                    object = json.getString(key);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }

        Messaging.check = true;
        ItemMessages.check = true;


//        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
//        userParseQuery.whereEqualTo("objectId",object);
//        try {
//            ParseUser user = userParseQuery.getFirst();
//            Log.d("reciever",user.getUsername());
//            ParseFile parseFile = (ParseFile) user.get("icon");
//            if(parseFile!=null) {
//                parseFile.getDataInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] bytes, ParseException e) {
//                        if (e == null) {
//                            userIcon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            // imageIconProfile.setImageBitmap(bmp);
//                        }
//                    }
//                });
//            }else{
//                userIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.header_new);
//                // imageIconProfile.setImageBitmap(bmp);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.ic_launcher);

        Intent launchActivity = new Intent(ctx,MainActivity.class);
        launchActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //  launchActivity.putExtra("fragment","1");
        //  launchActivity.putExtra("contact",from);

        PendingIntent pi = PendingIntent.getActivity(ctx, 0, launchActivity, PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification noti = new NotificationCompat.Builder(ctx)
//                .setContentTitle("New message from ")
//                .setContentText(msg)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(icon)
//                .setContentIntent(pi)
//                .setAutoCancel(false)
//                .build();

        long vibr[] = {250};
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.icon_my)
              //  .setLargeIcon(userIcon)
                .setContentTitle("Уведомление от " + from)
                .setContentText(msg)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pi)
                .setTicker("("+from +") "+msg)
                .setVibrate(vibr)
               // .setNumber(1)

                ;

        NotificationManager nm = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1,mBuilder.build());
    }
}