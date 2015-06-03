package mikhail.shvarev.app.messaging;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
import mikhail.shvarev.app.customMessagingUser.CustomMessagingAdapter;
import mikhail.shvarev.app.customMessagingUser.InfoMessaging;

/**
 * Created by Mihail on 17.05.2015.
 */
public class MessagingReceive extends AsyncTask<String,Void,Void> {

    List<InfoMessaging> customList = new ArrayList<InfoMessaging>();
    CustomMessagingAdapter customMessagingAdapter;
    Activity mActivity;
    ListView list;
    String userName;

    public MessagingReceive(Activity activity, String userName){
        mActivity     = activity;
        this.userName = userName;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(String... params) {

        ParseQuery<ParseObject> queryId = ParseQuery.getQuery("Messages");
        queryId.whereEqualTo("userId",params[0]);
        try {
            ParseObject parseObject = queryId.getFirst();
            if(parseObject != null && parseObject.get("newMessInBox") != null){
                try{
                    Log.d("RECEIVER", "first   " + parseObject.get("newMessInBox").toString());

                    JSONArray jsonArray = new JSONArray(parseObject.get("newMessInBox").toString());//////////////////
                    // jsonArray =  parseObject.get("newMessInBox");
                    Log.d("RECEIVER","start");
                    for(int i = 0; i<jsonArray.length(); i++){

                        Log.d("RECEIVER",i+"");
                        JSONObject jsonObject =  jsonArray.getJSONArray(i).getJSONObject(0);
                        Log.d("RECEIVER",jsonObject.get("userId") + "   " + jsonObject.get("messages"));
                        String name;
                        if(jsonObject.get("userId").toString().equals(ParseUser.getCurrentUser().getObjectId().toString()))
                            name = ParseUser.getCurrentUser().get("firstLastName").toString();
                        else
                            name = userName;
                        customList.add(new InfoMessaging(name,jsonObject.get("userId").toString(),jsonObject.get("messages").toString(),null));
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        queryId.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                if(parseObject != null){
//                    try{
//                        JSONArray jsonArray = new JSONArray(parseObject.get("newMessInBox").toString());//////////////////
//                       // jsonArray =  parseObject.get("newMessInBox");
//                        for(int i = 0; i<jsonArray.length(); i++){
//                            JSONObject jsonObject = jsonArray.getJSONArray(i).getJSONObject(0);
//Log.d("RECEIVER",jsonObject.get("userId") + "   " + jsonObject.get("messages"));
//                            customList.add(new InfoMessaging(jsonObject.get("userId").toString(),null,jsonObject.get("messages").toString(),null));
//                        }
//                    } catch (JSONException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d("RECEIVER","onpost");
        list = (ListView)mActivity.findViewById(R.id.allMessages);
        customMessagingAdapter = new CustomMessagingAdapter(mActivity,customList);
        //list.smoothScrollToPosition(list.getCount()-1);
        if(list !=null  && customMessagingAdapter !=null)//костылищеее не помог
        {
            list.setAdapter(customMessagingAdapter);
            list.setSelection(list.getCount());
        }
       // ListView listView = (ListView)rootView.findViewById(R.id.allMessages);
       // ListView listView = (ListView)mActivity.findViewById(R.id.allMessages);
        //list.smoothScrollToPosition(list.getCount());

    }

}
