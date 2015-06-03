package mikhail.shvarev.app.users;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
import mikhail.shvarev.app.main.Messaging;

/**
 * Created by Mihail on 13.05.2015.
 */
public class UserProfile extends Fragment {

    ProgressDialog dialog;
    Activity mActivity;
    View rootView;
    Bundle bundle;

    String realUserName;


    boolean friend = false;
    boolean request = false;
    boolean requestToMe = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        startShowDialogAuthorization("Загрузка данных...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_profile,container,false);

        bundle = this.getArguments();
        Log.d("UserProfile",bundle.getString("userName").toString());
        Log.d("UserProfile",bundle.getString("userId"));//objextID
        new LoadDataFromParse().execute(bundle.getString("userId"));




        Button btnMessaging = (Button)rootView.findViewById(R.id.buttonMessages);
        btnMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragMess = new Messaging();
                Bundle bundle1 = new Bundle();
                Log.d("Messaging", bundle.getString("userName").toString());//objextID
                bundle1.putString("objectID",bundle.getString("userId"));
                bundle1.putString("userName",bundle.getString("userName"));
                fragMess.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragMess).commit();
            }
        });
        final Button btnAddToFriendsList = (Button)rootView.findViewById(R.id.buttonAddToFriendList);
        btnAddToFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseObject parseObject = new ParseObject("Friends");

                    parseObject.put("userId",bundle.getString("userId"));
                    parseObject.put("request", ParseUser.getCurrentUser().getObjectId());
                    parseObject.saveInBackground();
                    Toast.makeText(getActivity(),"Запрос отправлен",Toast.LENGTH_SHORT).show();
                    btnAddToFriendsList.setVisibility(View.GONE);


                JSONObject object = new JSONObject();
                try {
                    object.put("from",ParseUser.getCurrentUser().get("firstLastName").toString());
                    object.put("mess","Хочет добавть вас в друзья");
                    object.put("object",ParseUser.getCurrentUser().getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ParsePush push = new ParsePush();
                push.setChannel(realUserName);
                Log.d("username",bundle.getString("userId"));
                push.setData(object);//setMessage("text");//setData(object);
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "good send! "+bundle.getString("userName"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "bad send", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

        final Button btnDelete = (Button)rootView.findViewById(R.id.btnDeleteFriend);
        final Button btnConfirm = (Button)rootView.findViewById(R.id.btnConfirmFriends);
        final Button btnNotConfirm = (Button)rootView.findViewById(R.id.btnNotConfirmFriends);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> parseCancel = ParseQuery.getQuery("Friends");
                parseCancel.whereEqualTo("friends",bundle.getString("userId").toString());
                parseCancel.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                try {
                    ParseObject parseObjectCancel = parseCancel.getFirst();
                    parseObjectCancel.delete();
                    parseObjectCancel.saveInBackground();
                    btnAddToFriendsList.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ParseQuery<ParseObject> parseCancel1 = ParseQuery.getQuery("Friends");
                parseCancel1.whereEqualTo("friends",ParseUser.getCurrentUser().getObjectId());
                parseCancel1.whereEqualTo("userId",bundle.getString("userId").toString());
                try {
                    ParseObject parseObjectCancel = parseCancel1.getFirst();
                    parseObjectCancel.delete();
                    parseObjectCancel.saveInBackground();
                    btnAddToFriendsList.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> parseAdd = ParseQuery.getQuery("Friends");
                parseAdd.whereEqualTo("request",bundle.getString("userId").toString());
                parseAdd.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                try {
                    ParseObject parseObjectAdd = parseAdd.getFirst();
                    parseObjectAdd.put("friends",bundle.getString("userId").toString());
                    parseObjectAdd.put("request","");
                    parseObjectAdd.saveInBackground();

                    ParseObject parseAdd2 = new ParseObject("Friends");
                    parseAdd2.put("userId",bundle.getString("userId").toString());
                    parseAdd2.put("friends",ParseUser.getCurrentUser().getObjectId());
                    parseAdd2.saveInBackground();
                    btnAddToFriendsList.setVisibility(View.GONE);
                    btnConfirm.setVisibility(View.GONE);
                    btnNotConfirm.setVisibility(View.GONE);


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btnNotConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> parseCancel = ParseQuery.getQuery("Friends");
                parseCancel.whereEqualTo("request",bundle.getString("userId").toString());
                parseCancel.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                try {
                    ParseObject parseObjectCancel = parseCancel.getFirst();
                    parseObjectCancel.delete();
                    parseObjectCancel.saveInBackground();
                    btnAddToFriendsList.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.GONE);
                    btnNotConfirm.setVisibility(View.GONE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }
    protected void startShowDialogAuthorization(String mess){
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mess);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void stopShowDialogAuthorization(){
        dialog.dismiss();
        dialog = null;
    }

    private class LoadDataFromParse extends AsyncTask<String,Void,List>{
        Bitmap userIcon;
        String addressStr = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(String... params) {
            List<InfoAboutUserUnit> list = new ArrayList<InfoAboutUserUnit>();

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            Log.d("UserProfile",params[0] +"   params");
            query.whereEqualTo("objectId",params[0]);
            ParseUser findUser = null;
            String userFirst,userNum;

            try {
                findUser = query.getFirst();
                Log.d("UserProfile",findUser.getUsername() +"    0");
                realUserName = findUser.getUsername();

                if(findUser.get("firstLastName")!=null)
                    userFirst = findUser.get("firstLastName").toString();
                else
                    userFirst = "Новый пользователь";
                if(findUser.get("numberPhone")!=null)
                    userNum = findUser.get("numberPhone").toString();
                else
                    userNum = "";
                ParseFile parseFile = (ParseFile) findUser.get("icon");
                if(parseFile!=null) {
                    parseFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null) {
                                userIcon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                               // imageIconProfile.setImageBitmap(bmp);
                            }
                        }
                    });
                }else{
                    userIcon = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
                   // imageIconProfile.setImageBitmap(bmp);
                }
                if(findUser.get("position")!=null) {
                    ParseGeoPoint geoPoint = (ParseGeoPoint) findUser.get("position");
                    Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());

                    try {
                        List<Address> addresses = geo.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
                        if (addresses.isEmpty())
                            Log.d("PROFILE", "empty!!!!");
                        else {
                            if (addresses.size() > 0) {
//                        Log.d("PROFILE",
//                                addresses.get(0).getLocality() +", "
//                                        + addresses.get(0).getThoroughfare () + ", "
//                                        + addresses.get(0).getFeatureName());
                                addressStr = addresses.get(0).getLocality() + ", "
                                        + addresses.get(0).getThoroughfare() + ", "
                                        + addresses.get(0).getFeatureName();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                list.add(new InfoAboutUserUnit(userFirst, findUser.getObjectId(), addressStr, userNum, userIcon));

            } catch (ParseException e) {
                e.printStackTrace();
            }

           // Log.d("UserProfile",findUser.getUsername() +"    0");

            List<ParseObject> listFriend = new ArrayList<ParseObject>();
            ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("Friends");
            pQuery.whereEqualTo("userId",bundle.getString("userId"));
            try {
                listFriend = pQuery.find();
                for(int i = 0;i<listFriend.size();i++){
                    if(listFriend.get(i).get("request") != null)
                    if(listFriend.get(i).get("request").equals(ParseUser.getCurrentUser().getObjectId().toString()) )
                        request = true;
                    if(listFriend.get(i).get("friends") != null)
                    if(listFriend.get(i).get("friends").equals(ParseUser.getCurrentUser().getObjectId().toString()) ){
                        friend = true;
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            ParseQuery<ParseObject> queryReq = ParseQuery.getQuery("Friends");
            queryReq.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
            queryReq.whereEqualTo("request",bundle.getString("userId"));

            try {
                ParseObject userReq = queryReq.getFirst();
                if(userReq.get("request").toString().equals(bundle.getString("userId"))){
                    requestToMe = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            if(friend || request) {
                Button btnAdd = (Button) rootView.findViewById(R.id.buttonAddToFriendList);
                btnAdd.setVisibility(View.GONE);
            }
            if(friend){
                Button btnDelete = (Button)rootView.findViewById(R.id.btnDeleteFriend);
                btnDelete.setVisibility(View.VISIBLE);
            }
            if(requestToMe){
                Button btnConfirm = (Button)rootView.findViewById(R.id.btnConfirmFriends);
                Button btnNotConfirm = (Button)rootView.findViewById(R.id.btnNotConfirmFriends);
                Button btnAdd = (Button) rootView.findViewById(R.id.buttonAddToFriendList);
                btnAdd.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.VISIBLE);
                btnNotConfirm.setVisibility(View.VISIBLE);
            }
           // if()
            InfoAboutUserUnit unit = (InfoAboutUserUnit) list.get(0);
            TextView userFirstLast = (TextView)rootView.findViewById(R.id.userNameFirstLastProfile);
            userFirstLast.setText(unit.getUserName());
            TextView userNum = (TextView)rootView.findViewById(R.id.textViewNumberProfile);
            userNum.setText(unit.getUserNum());
            TextView userAddress = (TextView)rootView.findViewById(R.id.addressProfile);
            userAddress.setText(unit.getUserDistance());
            TextView userName = (TextView)rootView.findViewById(R.id.userNameProfile);
            userName.setText(realUserName);
            ImageView userIcon = (ImageView)rootView.findViewById(R.id.imageIconProfile);
            userIcon.setImageBitmap(unit.getUserIcon());



            stopShowDialogAuthorization();

        }
    }



}
