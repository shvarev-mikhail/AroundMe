package mikhail.shvarev.app.users;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.main.Messaging;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mihail on 20.05.2015.
 */
public class DialogUser  implements View.OnClickListener{
    Activity activity;
    Dialog dialog;
    String username;
    String name, number, address;
    Bitmap bmp;
    ImageView imageViewIcon;

    Context ctx;
    Button btnMess, btnAdd;
    Button btnDelete,btnConfirm,btnNotConfirm;
    TextView tvName,tvNumber, tvAddress, tvUserName;
    boolean friend = false,request = false,requestToMe = false;

    String realUserName;

    FragmentManager manager;
    ParseUser parseUser;
    public DialogUser(Activity activity,String username,FragmentManager manager){
        this.activity = activity;
        this.username = username;
        this.manager  = manager;
        new LoadInfo().execute(username);
        initDialog();
    }

    private void  initDialog(){

        dialog = new Dialog(activity);
        dialog.setTitle("Профиль");
        dialog.setContentView(R.layout.item_profile);
        tvName        = (TextView)dialog.findViewById(R.id.userNameFirstLastProfile);
        tvNumber      = (TextView)dialog.findViewById(R.id.textViewNumberProfile);
        tvAddress     = (TextView)dialog.findViewById(R.id.addressProfile);
        tvUserName    = (TextView)dialog.findViewById(R.id.userNameProfile);
        imageViewIcon = (ImageView)dialog.findViewById(R.id.imageIconProfile);
        btnMess       = (Button)dialog.findViewById(R.id.buttonMessages);
        btnMess.setOnClickListener(this);
        btnAdd        = (Button)dialog.findViewById(R.id.buttonAddToFriendList);
        btnAdd.setOnClickListener(this);

        btnDelete = (Button)dialog.findViewById(R.id.btnDeleteFriend);
        btnDelete.setOnClickListener(this);
        btnConfirm = (Button)dialog.findViewById(R.id.btnConfirmFriends);
        btnConfirm.setOnClickListener(this);
        btnNotConfirm = (Button)dialog.findViewById(R.id.btnNotConfirmFriends);
        btnNotConfirm.setOnClickListener(this);


    }

    public void show(){
        dialog.show();
    }
    @Override
    public void onClick(View v) {

    }


    private class LoadInfo extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {

            ParseQuery<ParseUser> user = ParseUser.getQuery();
            user.whereEqualTo("username",params[0]);
            try {
                parseUser = user.getFirst();

                ParseGeoPoint geoPoint = (ParseGeoPoint) parseUser.get("position");
                Geocoder geo = new Geocoder(activity, Locale.getDefault());

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
                            address = addresses.get(0).getLocality() + ", "
                                    + addresses.get(0).getThoroughfare() + ", "
                                    + addresses.get(0).getFeatureName();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ParseFile parseFile = (ParseFile) parseUser.get("icon");
                if(parseFile!=null) {
                    parseFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            if (e == null) {
                                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                // imageIconProfile.setImageBitmap(bmp);
                            }
                        }
                    });
                }else{
                    bmp = BitmapFactory.decodeResource(activity.getResources(),R.drawable.profile);
                    // imageIconProfile.setImageBitmap(bmp);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            List<ParseObject> listFriend = new ArrayList<ParseObject>();
            ParseQuery<ParseObject> pQuery = ParseQuery.getQuery("Friends");
            pQuery.whereEqualTo("userId",parseUser.getObjectId());
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
            queryReq.whereEqualTo("request",parseUser.getObjectId());

            try {
                ParseObject userReq = queryReq.getFirst();
                if(userReq.get("request").toString().equals(parseUser.getObjectId())){
                    requestToMe = true;
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

            tvName.setText(parseUser.get("firstLastName").toString());
            if(parseUser.get("numberPhone") != null)
                tvNumber.setText(parseUser.get("numberPhone").toString());
            tvAddress.setText(address);
            tvUserName.setText(parseUser.getUsername());
            imageViewIcon.setImageBitmap(bmp);

            btnMess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragMess = new Messaging();
                    Bundle bundle1 = new Bundle();
                    // Log.d("Messaging", bundle.getString("objectID").toString());//objextID
                    bundle1.putString("objectID",parseUser.getObjectId());
                    bundle1.putString("userName",parseUser.getUsername());
                    fragMess.setArguments(bundle1);

                    manager.beginTransaction().replace(R.id.container,fragMess).commit();
                    dialog.cancel();
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseObject parseObject = new ParseObject("Friends");

                    parseObject.put("userId",parseUser.getObjectId());
                    parseObject.put("request", ParseUser.getCurrentUser().getObjectId());
                    parseObject.saveInBackground();
                  //  Toast.makeText(dialog.getOwnerActivity(), "Запрос отправлен", Toast.LENGTH_SHORT).show();
                 //   btnAddToFriendsList.setVisibility(View.GONE);


                    JSONObject object = new JSONObject();
                    try {
                        object.put("from",ParseUser.getCurrentUser().get("firstLastName").toString());
                        object.put("mess","Хочет добавть вас в друзья");
                        object.put("object",ParseUser.getCurrentUser().getObjectId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ParsePush push = new ParsePush();
                    push.setChannel(parseUser.getUsername());
                   // Log.d("username",bundle.getString("userId"));
                    push.setData(object);//setMessage("text");//setData(object);
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                            btnAdd.setVisibility(View.GONE);

//                                Toast.makeText(dialog.getOwnerActivity(), "good send! "+parseUser.get("firstLastName"), Toast.LENGTH_SHORT).show();
                            }// else {
//                                Toast.makeText(dialog.getOwnerActivity(), "bad send", Toast.LENGTH_SHORT).show();
//                            }
                        }
                    });



                }

            });

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseQuery<ParseObject> parseAdd = ParseQuery.getQuery("Friends");
                    parseAdd.whereEqualTo("request",parseUser.getObjectId().toString());
                    parseAdd.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
                    try {
                        ParseObject parseObjectAdd = parseAdd.getFirst();
                        parseObjectAdd.put("friends",parseUser.getObjectId().toString());
                        parseObjectAdd.put("request","");
                        parseObjectAdd.saveInBackground();

                        ParseObject parseAdd2 = new ParseObject("Friends");
                        parseAdd2.put("userId",parseUser.getObjectId().toString());
                        parseAdd2.put("friends",ParseUser.getCurrentUser().getObjectId());
                        parseAdd2.saveInBackground();
                        btnAdd.setVisibility(View.GONE);
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
                    parseCancel.whereEqualTo("request",parseUser.getObjectId());
                    parseCancel.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                    try {
                        ParseObject parseObjectCancel = parseCancel.getFirst();
                        parseObjectCancel.delete();
                        parseObjectCancel.saveInBackground();
                        btnAdd.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.GONE);
                        btnNotConfirm.setVisibility(View.GONE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseQuery<ParseObject> parseCancel = ParseQuery.getQuery("Friends");
                    parseCancel.whereEqualTo("friends",parseUser.getObjectId().toString());
                    parseCancel.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
                    try {
                        ParseObject parseObjectCancel = parseCancel.getFirst();
                        parseObjectCancel.delete();
                        parseObjectCancel.saveInBackground();
                        btnAdd.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.GONE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ParseQuery<ParseObject> parseCancel1 = ParseQuery.getQuery("Friends");
                    parseCancel1.whereEqualTo("friends",ParseUser.getCurrentUser().getObjectId());
                    parseCancel1.whereEqualTo("userId",parseUser.getObjectId().toString());
                    try {
                        ParseObject parseObjectCancel = parseCancel1.getFirst();
                        parseObjectCancel.delete();
                        parseObjectCancel.saveInBackground();
                        btnAdd.setVisibility(View.VISIBLE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });


            if(parseUser.getUsername().toString().equals(ParseUser.getCurrentUser().getUsername().toString()) ){
                LinearLayout linearLayout = (LinearLayout)dialog.findViewById(R.id.main4);
                linearLayout.setVisibility(View.GONE);
            }
            else if(friend) {
                btnAdd.setVisibility(View.GONE);
                btnDelete.setVisibility(View.VISIBLE);
            }else if(request){
                btnAdd.setVisibility(View.GONE);
            }
            else if(requestToMe){
                btnAdd.setVisibility(View.GONE);
                btnNotConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
            }
        }
    }
}
