package mikhail.shvarev.app.parseLoadUpload;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.CustomListUserAdapter;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
import mikhail.shvarev.app.customRequestList.CustomRequestListAdapter;

/**
 * Created by Mihail on 21.05.2015.
 */
public class LoadListOfRequest extends AsyncTask<String,Void,Void> {
    private CustomRequestListAdapter adapter;

    private String userFirstLastName, userPosition, userNum;
    Bitmap bmp;
    List<ParseUser> requestParseUser = new ArrayList<ParseUser>();

    private List<InfoAboutUserUnit> listUser = new ArrayList<InfoAboutUserUnit>();
    private Activity mActivity;
    private View view;

    public LoadListOfRequest(Activity activity,View view){
        this.mActivity = activity;
        this.view      = view;
    }
    @Override
    protected Void doInBackground(String... params) {


        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Friends");
        parseQuery.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        List<ParseObject> listRequest = new ArrayList<ParseObject>();
        List<String> userIdRequest = new ArrayList<String>();
        try {
            listRequest = parseQuery.find();
            for(int i = 0; i<listRequest.size(); i++){
                if(listRequest.get(i).get("request") != null && listRequest.get(i).get("friends") == null) {
                    //Log.d("request", listRequest.get(i).get("request").toString());
                    userIdRequest.add(listRequest.get(i).get("request").toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
        userParseQuery.whereContainedIn("objectId",userIdRequest);
        try {
            requestParseUser = userParseQuery.find();
            for(int i = 0;i<requestParseUser.size();i++) {
                Log.d("request", requestParseUser.get(i).getUsername());



                    Log.d("SIZE", requestParseUser.size() + "  " + requestParseUser.get(i).getUsername());
                    if (requestParseUser.get(i).get("firstLastName") == null)
                        userFirstLastName = "Новый пользователь";
                    else
                        userFirstLastName = requestParseUser.get(i).get("firstLastName").toString();



                    if ((ParseFile) requestParseUser.get(i).get("icon") == null)
                        bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile);
                    else {
                        bmp = BitmapFactory.decodeByteArray(((ParseFile) requestParseUser.get(i).get("icon")).getData(), 0, ((ParseFile) requestParseUser.get(i).get("icon")).getData().length);
                    }

                    Log.d("Userr", userFirstLastName + " " + requestParseUser.get(i).getObjectId());
                    listUser.add(new InfoAboutUserUnit(userFirstLastName, requestParseUser.get(i).getObjectId(), null, null, bmp));

                    ///////////////////////////////////////////////////////////


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ListView listViewUser = (ListView)mActivity.findViewById(R.id.listViewUserList);
        adapter = new CustomRequestListAdapter(mActivity,listUser);
        listViewUser.setAdapter(adapter);
        ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.progressLoadingList);
        progressBar.setVisibility(View.GONE);

        if(listViewUser.getCount()<1){
            TextView tvEmpty = (TextView)mActivity.findViewById(R.id.textViewEmpty);
            tvEmpty.setVisibility(View.VISIBLE);
        }else{
            listViewUser.setVisibility(View.VISIBLE);
        }

    }
}
