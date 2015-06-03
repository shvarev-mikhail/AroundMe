package mikhail.shvarev.app.main;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

//import mikhail.shvarev.app.customMessagingUser.InfoMessaging;
//import mikhail.shvarev.app.dataBaseHelper.*;
import mikhail.shvarev.app.R;
import mikhail.shvarev.app.messaging.MessagingReceive;
import mikhail.shvarev.app.messaging.MessagingSend;

//import mikhail.shvarev.app.messaging.ServiceToLoadNewMess;
//import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
//import mikhail.shvarev.app.parseLoadUpload.LoadMessagingOfUser;

/**
 * Created by Mihail on 13.05.2015.
 */
public class Messaging extends Fragment {
    public static boolean check = false;

    View rootView;
    public static Bundle bundle;
    EditText newMessSend;
    ParseUser parseUsermess;

    MessagingSend messagingSend = new MessagingSend();

    public static Activity mActivity;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

      //  getActivity().startService(new Intent(getActivity(),ServiceToLoadNewMess.class));


       // Intent i = new Intent(this.getActivity(),Messaging.ServiceToLoadNewMess.class);
      //  getActivity().startService(i);
        getActivity().startService(new Intent(getActivity(), ServiceToLoadNewMess.class));




        bundle = this.getArguments();
        Log.d("Messaging", bundle.getString("objectID").toString());//objextID
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId",bundle.getString("objectID").toString());
        messagingSend.createRowForMessages(bundle.getString("objectID").toString());

        new MessagingReceive(getActivity(),bundle.getString("userName").toString()).execute(ParseUser.getCurrentUser().getObjectId() + bundle.getString("objectID").toString());
        //new LoadMessagingOfUser(getActivity()).execute(bundle.getString("objectID").toString());

        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                parseUsermess = parseUser;
            }



        });




    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_messaging,container,false);
        newMessSend = (EditText)rootView.findViewById(R.id.editTextSendNewMess);

        Button btnSend = (Button)rootView.findViewById(R.id.btnSendNewMess);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

    //                        DBHelper dbHelper = new DBHelper(getActivity(),bundle.getString("objectID").toString());
    //                        ContentValues cv = new ContentValues();
    //                        SQLiteDatabase db = dbHelper.getWritableDatabase();
    //                        cv.put("object",ParseUser.getCurrentUser().getObjectId().toString());
    //                        cv.put("name", ParseUser.getCurrentUser().get("firstLastName").toString());
    //                        cv.put("mess", newMessSend.getText().toString());
    //
    //                        db.insert(bundle.getString("objectID").toString(), null, cv);
    //                        dbHelper.close();

                    if (!newMessSend.getText().toString().trim().equals("")){
                    messagingSend.sendMessage(newMessSend.getText().toString());
                    JSONObject object = new JSONObject();
                    try {
                        object.put("from", ParseUser.getCurrentUser().get("firstLastName").toString());
                        object.put("mess", newMessSend.getText().toString());
                        object.put("object", ParseUser.getCurrentUser().getObjectId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    ParsePush push = new ParsePush();
                    push.setChannel(parseUsermess.getUsername());
                    push.setData(object);//setMessage("text");//setData(object);
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "good send! " + parseUsermess.getUsername(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "bad send", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    newMessSend.setText("");
                    new MessagingReceive(getActivity(), bundle.getString("userName").toString()).execute(ParseUser.getCurrentUser().getObjectId() + bundle.getString("objectID").toString());
                    //  ListView listView = (ListView)rootView.findViewById(R.id.allMessages);
                    //  listView.smoothScrollToPosition(listView.getCount());
                }
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
       getActivity().stopService(new Intent(getActivity(), ServiceToLoadNewMess.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().startService(new Intent(getActivity(), ServiceToLoadNewMess.class));
    }

    //если сервис вложенный (inner class) необходимо его делать статическим!!
    public static class ServiceToLoadNewMess extends IntentService{


        public ServiceToLoadNewMess() {
            super("Service To Load New Mess");
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("service","create");
        }

//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            Log.d("service","start");
//            return super.onStartCommand(intent, flags, startId);
//
//
//        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.d("service","destroy");
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.d("service","start");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(check){
                            new MessagingReceive(mActivity,bundle.getString("userName").toString()).execute(ParseUser.getCurrentUser().getObjectId() + bundle.getString("objectID").toString());
                            check = false;
                        }
                    }
                }
            }).run();
        }


    }
}
