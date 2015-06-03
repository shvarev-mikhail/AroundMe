package mikhail.shvarev.app.parseLoadUpload;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListMessaging.CustomListMessagingAdapter;
import mikhail.shvarev.app.customListMessaging.InfoMessageUnit;
import mikhail.shvarev.app.customListUser.CustomListUserAdapter;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
import mikhail.shvarev.app.customMessagingUser.CustomMessagingAdapter;
import mikhail.shvarev.app.customMessagingUser.InfoMessaging;
import mikhail.shvarev.app.main.ItemMessages;


/**
* Created by Mihail on 14.05.2015.
*/
public class LoadListMessagingOfUser extends AsyncTask<String,Void,Void> {
    List<InfoMessageUnit> list = new ArrayList<InfoMessageUnit>();
    private Activity mActivity;
    private CustomListMessagingAdapter adapter;

    Bitmap bmp;
    //private List<InfoAboutUserUnit> listUser = new ArrayList<InfoAboutUserUnit>();

    //private CustomListUserAdapter adapter;

    public LoadListMessagingOfUser(Activity activity){
        this.mActivity = activity;


    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.orderByDescending("updatedAt");
        try {
            List<ParseObject> listObject = query.find();
            for(int i = 0;i<listObject.size();i++){

                if(listObject.get(i).get("userId").toString().indexOf(params[0]) == 0) {

                    Log.d("loadlistmessaging", listObject.get(i).get("userId").toString());
                    String userId = listObject.get(i).get("userId").toString().substring(params[0].length());
                    Log.d("loadlistmessaging", "userId  "+userId);
                  //  ParseQuery<ParseUser> query = ParseUser.getQuery();

                    ParseQuery<ParseUser> parseU = ParseUser.getQuery();
                    ParseUser messFromUser = parseU.get(userId);

                    String userName;
                    if(messFromUser.get("firstLastName") == null)
                        userName = "Новый пользователь";
                    else
                        userName = messFromUser.get("firstLastName").toString();

                    Log.d("loadlistmessaging", "userName  "+userName);
                    if(listObject.get(i).get("newMessInBox") != null) {
                        Log.d("loadlistmessaging", "mess  " + listObject.get(i).get("newMessInBox").toString());
                        JSONArray jsonArray = new JSONArray(listObject.get(i).get("newMessInBox").toString());//////////////////
                        // jsonArray =  parseObject.get("newMessInBox");
                        JSONObject jsonObject = jsonArray.getJSONArray(jsonArray.length() - 1).getJSONObject(0);

                        if ((ParseFile) messFromUser.get("icon") == null)
                            bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile);
                        else {
                            bmp = BitmapFactory.decodeByteArray(((ParseFile) messFromUser.get("icon")).getData(), 0, ((ParseFile) messFromUser.get("icon")).getData().length);
                        }
                        Log.d("loadlistmessaging", userId + "  " + userName + " " + jsonObject.get("messages").toString());
                        list.add(new InfoMessageUnit(userName,userId,jsonObject.get("messages").toString(),bmp));
                    }

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(ItemMessages.window == true) {
            ListView listViewUser = (ListView) mActivity.findViewById(R.id.listViewListOfMessages);
            adapter = new CustomListMessagingAdapter(mActivity, list);
            listViewUser.setAdapter(adapter);

            ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.progressBarLoadMess);
            progressBar.setVisibility(View.GONE);
            if (listViewUser.getCount() < 1) {
                TextView tvEmpty = (TextView) mActivity.findViewById(R.id.messagesEmpty1);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                TextView tvEmpty = (TextView) mActivity.findViewById(R.id.messagesEmpty1);
                tvEmpty.setVisibility(View.GONE);
            }
        }
    }

}
