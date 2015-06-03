package mikhail.shvarev.app.main;


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.messaging.MessagingReceive;
import mikhail.shvarev.app.parseLoadUpload.LoadListMessagingOfUser;
import mikhail.shvarev.app.users.UserProfile;

/**
 * Created by Mihail on 05.05.2015.
 */
public class ItemMessages extends Fragment {
    View rootView;
    TextView tvEmpty;
     public static ProgressBar progressBar;
    public static boolean check = false;
    public static boolean window;

    public static Activity mActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        window = true;
        mActivity = getActivity();
        getActivity().setTitle("Сообщения");
    }

    @Override
    public void onPause() {
        super.onPause();
        window = false;
        getActivity().stopService(new Intent(getActivity(), ServiceToLoadNewChat.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        window = true;
        getActivity().startService(new Intent(getActivity(), ServiceToLoadNewChat.class));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_messages,container,false);
        getActivity().startService(new Intent(getActivity(), ServiceToLoadNewChat.class));
        tvEmpty = (TextView)getActivity().findViewById(R.id.messagesEmpty1);
        // tvEmpty.setVisibility(View.GONE);




        new LoadListMessagingOfUser(getActivity()).execute(ParseUser.getCurrentUser().getObjectId());

        ListView listView = (ListView)rootView.findViewById(R.id.listViewListOfMessages);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView viewById = (TextView) view.findViewById(R.id.userIdinvise);
                Toast.makeText(getActivity(), position + "  " + viewById.getText(), Toast.LENGTH_SHORT).show();
                TextView viewUserName = (TextView)view.findViewById(R.id.userNameFirstLastItemListMessInfo);

                Fragment fragMess = new Messaging();
                Bundle bundle1 = new Bundle();
                // Log.d("Messaging", bundle.getString("objectID").toString());//objextID
                bundle1.putString("objectID",viewById.getText().toString());
                bundle1.putString("userName",viewUserName.getText().toString());
                fragMess.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragMess).commit();
            }
        });
        return rootView;
    }



    public static class ServiceToLoadNewChat extends IntentService{

        public ServiceToLoadNewChat() {
            super("Service to load new chat");
        }
        @Override
        public void onCreate() {
            super.onCreate();
            Log.d("service", "create");
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
        protected void onHandleIntent(Intent intent) {
            Log.d("service","start");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if(check){
                          //  progressBar = (ProgressBar)mActivity.findViewById(R.id.progressBarLoadMess);
                          //  progressBar.setVisibility(View.VISIBLE);
                            new LoadListMessagingOfUser(mActivity).execute(ParseUser.getCurrentUser().getObjectId());
                            //new MessagingReceive(mActivity,bundle.getString("userName").toString()).execute(ParseUser.getCurrentUser().getObjectId() + bundle.getString("objectID").toString());
                            check = false;
                        }
                    }
                }
            }).run();
        }
    }
}
