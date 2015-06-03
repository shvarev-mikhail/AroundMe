package mikhail.shvarev.app.parseLoadUpload;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.CustomListUserAdapter;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;

/**
 * Created by Mihail on 13.05.2015.
 */
public class LoadListOfUser extends AsyncTask<String,Void,Void> {
    private Activity mActivity;
    private View view;
    private String userFirstLastName, userPosition, userNum;
    Bitmap bmp;
    private List<InfoAboutUserUnit> listUser = new ArrayList<InfoAboutUserUnit>();

    private CustomListUserAdapter adapter;

    public LoadListOfUser(Activity activity,View view){
        this.mActivity = activity;
        this.view      = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

//                        сделать сортировку
    //                      поставить ограничение на выод по растоянию!!!
    // 500 метров поставиил
    @Override
    protected Void doInBackground(String... params) {
        if(ParseUser.getCurrentUser().get("position") != null) {


            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);//dd/MM/yyyy - HH:mm
           // Log.d("ttttt", String.valueOf(formatter.format(ParseUser.getCurrentUser().getUpdatedAt())));
            //SimpleDateFormat format = new SimpleDateFormat();
            //format.format(new Date());
            Date dateMinus = ParseUser.getCurrentUser().getUpdatedAt();

            Date datePlus  = ParseUser.getCurrentUser().getUpdatedAt();

            datePlus.setMinutes(datePlus.getMinutes()+5);
            Log.d("tttt 1" ,dateMinus.getMinutes()+"");
            dateMinus.setMinutes(dateMinus.getMinutes()-5);
            Log.d("tttt 2" ,formatter.format(dateMinus)+"");


            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
            SharedPreferences.Editor ed = sPref.edit();



            ParseQuery<ParseUser> query = ParseUser.getQuery();

           // query.whereLessThanOrEqualTo("updateAt",formatter.format(dateMinus));
            if(!sPref.getString("NAME","").equals("")) {
                String str = sPref.getString("NAME",null);
                Log.d("preff 1" ,str);
                query.whereEqualTo("firstLastName", sPref.getString("NAME", ""));

            }
            // по логину не ищет!!!!
            if(!sPref.getString("LOGIN","").equals("")) {
                String str = sPref.getString("LOGIN",null);
                Log.d("preff 2" ,str);
                query.whereEqualTo("username", sPref.getString("LOGIN", ""));
            }

            if(!sPref.getString("NUMBER","").equals("")) {
                String str = sPref.getString("NUMBER",null);
                Log.d("preff 3" ,str);
                query.whereEqualTo("numberPhone", sPref.getString("NUMBER", ""));
            }

            if(!sPref.getString("FRIENDS","").equals("")){
                ParseQuery<ParseObject> parseQueryFriends = ParseQuery.getQuery("Friends");
                List<String> listFriends = new ArrayList<String>();
                parseQueryFriends.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                try {
                    List<ParseObject> pO = parseQueryFriends.find();
                    for (int i = 0; i < pO.size(); i++) {
                        if(pO.get(i).get("friends") != null)
                        listFriends.add(pO.get(i).get("friends").toString());
                    }
                    query.whereContainedIn("objectId",listFriends);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                query.whereWithinKilometers("position", ParseUser.getCurrentUser().getParseGeoPoint("position"), 0.5);
            }

            query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            try {
                List<ParseUser> list = query.find();
                for (int i = 0; i < list.size(); i++){
                        Date localUserData = list.get(i).getUpdatedAt();
                    if (dateMinus.before(localUserData) && sPref.getString("FRIENDS","").equals(""))///////////////////////////(true) не ограничено время!!!!!
                    {

                        Log.d("tttt 3", "" + list.get(i).getUpdatedAt());
                        if (dateMinus.before(list.get(i).getUpdatedAt()))
                            Log.d("tttt", "ttttt");


                        Log.d("SIZE", list.size() + "  " + list.get(i).getUsername());
                        if (list.get(i).get("firstLastName") == null)
                            userFirstLastName = "Новый пользователь";
                        else
                            userFirstLastName = list.get(i).get("firstLastName").toString();
                        if (list.get(i).get("numberPhone") == null)
                            userNum = "";
                        else
                            userNum = list.get(i).get("numberPhone").toString();
                        //ParseUser.getCurrentUser().getCreatedAt();


                        ParseGeoPoint geoPointMy = (ParseGeoPoint) ParseUser.getCurrentUser().get("position");
                        Log.d("POSITION MY", geoPointMy.getLatitude() + " " + geoPointMy.getLongitude());
                        ParseGeoPoint geoPointsUsers = (ParseGeoPoint) list.get(i).get("position");

                        Location locationMy = new Location("my");
                        locationMy.setLatitude(geoPointMy.getLatitude());
                        locationMy.setLongitude(geoPointMy.getLongitude());

                        Location locationUser = new Location("my");
                        locationUser.setLatitude(geoPointsUsers.getLatitude());
                        locationUser.setLongitude(geoPointsUsers.getLongitude());

                        float results = locationMy.distanceTo(locationUser);

                        //Location.distanceBetween(geoPointMy.getLatitude(),geoPointMy.getLongitude(),geoPointsUsers.getLatitude(),geoPointsUsers.getLongitude(),results);
                        userPosition = results + " м.";

                        if ((ParseFile) list.get(i).get("icon") == null)
                            bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_launcher);
                        else {
                            bmp = BitmapFactory.decodeByteArray(((ParseFile) list.get(i).get("icon")).getData(), 0, ((ParseFile) list.get(i).get("icon")).getData().length);
                        }

                        Log.d("Userr", userFirstLastName + " " + list.get(i).getObjectId() + "  " + userPosition);
                        listUser.add(new InfoAboutUserUnit(userFirstLastName, list.get(i).getObjectId(), userPosition, userNum, bmp));

                        ///////////////////////////////////////////////////////////
                    }else if(!sPref.getString("FRIENDS","").equals("")){
                        // друзья выставить показ тайминга
                        Log.d("tttt 3", "" + list.get(i).getUpdatedAt());
                        if (dateMinus.before(list.get(i).getUpdatedAt()))
                            Log.d("tttt", "ttttt");


                        Log.d("SIZE", list.size() + "  " + list.get(i).getUsername());
                        if (list.get(i).get("firstLastName") == null)
                            userFirstLastName = "Новый пользователь";
                        else
                            userFirstLastName = list.get(i).get("firstLastName").toString();
                        if (list.get(i).get("numberPhone") == null)
                            userNum = "";
                        else
                            userNum = list.get(i).get("numberPhone").toString();
                        //ParseUser.getCurrentUser().getCreatedAt();


                        ParseGeoPoint geoPointMy = (ParseGeoPoint) ParseUser.getCurrentUser().get("position");
                        Log.d("POSITION MY", geoPointMy.getLatitude() + " " + geoPointMy.getLongitude());
                        ParseGeoPoint geoPointsUsers = (ParseGeoPoint) list.get(i).get("position");

                        Location locationMy = new Location("my");
                        locationMy.setLatitude(geoPointMy.getLatitude());
                        locationMy.setLongitude(geoPointMy.getLongitude());

                        Location locationUser = new Location("my");
                        locationUser.setLatitude(geoPointsUsers.getLatitude());
                        locationUser.setLongitude(geoPointsUsers.getLongitude());

                        float results = locationMy.distanceTo(locationUser);

                        //Location.distanceBetween(geoPointMy.getLatitude(),geoPointMy.getLongitude(),geoPointsUsers.getLatitude(),geoPointsUsers.getLongitude(),results);
                        userPosition = results + " м.";

                        if ((ParseFile) list.get(i).get("icon") == null)
                            bmp = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.profile);
                        else {
                            bmp = BitmapFactory.decodeByteArray(((ParseFile) list.get(i).get("icon")).getData(), 0, ((ParseFile) list.get(i).get("icon")).getData().length);
                        }

                        Log.d("Userr", userFirstLastName + " " + list.get(i).getObjectId() + "  " + userPosition);
                        listUser.add(new InfoAboutUserUnit(userFirstLastName, list.get(i).getObjectId(), userPosition, userNum, bmp));

                        ///////////////////////////////////////////////////////////
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            ed.clear();
            ed.commit();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ListView  listViewUser = (ListView)mActivity.findViewById(R.id.listViewUserList);
        adapter = new CustomListUserAdapter(mActivity,listUser);
        listViewUser.setAdapter(adapter);
        if(listViewUser.getCount()>0) {
            ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.progressLoadingList);
            progressBar.setVisibility(View.GONE);
            listViewUser.setVisibility(View.VISIBLE);
        }else{
            ProgressBar progressBar = (ProgressBar) mActivity.findViewById(R.id.progressLoadingList);
            progressBar.setVisibility(View.GONE);
            TextView tvEmpty = (TextView)mActivity.findViewById(R.id.textViewEmpty);
            tvEmpty.setVisibility(View.VISIBLE);
        }

    }
}
