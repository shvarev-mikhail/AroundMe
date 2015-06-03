package mikhail.shvarev.app.main;



import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mikhail.shvarev.app.PositionReceiver;
import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;
import mikhail.shvarev.app.users.DialogUser;

/**
 * Created by Mihail on 05.05.2015.
 */
public class ItemMap extends Fragment {
    ProgressDialog dialog;
    static boolean statusGPS = true;
    static boolean mapOpen = false;

    static boolean bgLoad = false;
    static GoogleMap map;
    MapView mapView;
    View rootView;
    LocationManager locationManager;
    String provider;
    LocationListener locationListener;

    TextView tv1,tv2;

    Button btnMyPosition;

    static RelativeLayout linearLayout;
    CameraUpdate cameraUpdate = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapOpen = true;
       // startShowDialogAuthorization("Поиск спутников...");////////////////////////////////////////////////////////
        getActivity().setTitle("Карта");

        getActivity().startService(new Intent(getActivity(), ServiceForGps.class));
//        Log.d("MAPP","MAPP1");
//        String serviceString = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager)getActivity().getSystemService(serviceString);
//        provider = LocationManager.GPS_PROVIDER;
//
//        locationListener = new LocationListener() {
//
//
//            @Override
//            public void onLocationChanged(Location location) {
//                if(!statusGPS) {
//                    linearLayout.setVisibility(View.GONE);
//                    //stopShowDialogAuthorization();/////////////////////////////////////////////////////////////
//                    statusGPS = true;
//                }
//                //map.clear();
//                new LoadingPeopleAroundMe().execute(ParseUser.getCurrentUser());
//
//                //map.mar
//                Log.d("MAPP","MAPP change");
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
//
//
//                Log.d("MAPP",location.getLatitude() + "  " + location.getLongitude());
//                ParseUser.getCurrentUser().put("position",new ParseGeoPoint(location.getLatitude(),location.getLongitude()));
//                ParseUser.getCurrentUser().saveInBackground();
//
//
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                statusGPS = false;
//                linearLayout.setVisibility(View.VISIBLE);
//                //startShowDialogAuthorization("Поиск спутников...");////////////////////////////////////////////
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                statusGPS = false;
//                linearLayout.setVisibility(View.VISIBLE);
//                //startShowDialogAuthorization("Поиск спутников...");/////////////////////////////////////////
//            }
//        };
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_map,container,false);
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        linearLayout = (RelativeLayout)rootView.findViewById(R.id.findGPS);
        linearLayout.setVisibility(View.GONE);
        btnMyPosition = (Button)rootView.findViewById(R.id.myPosition);
        btnMyPosition.setVisibility(View.VISIBLE);
        btnMyPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PositionReceiver.cameraUpdate!=null && PositionReceiver.statusGPS) {
                    map.moveCamera(PositionReceiver.cameraUpdate);
                    map.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("Marker","  Marker "+marker.getId());

                new DialogUser(getActivity(),marker.getTitle(),getActivity().getSupportFragmentManager()).show();
                return false;
            }
        });
        map.getUiSettings().setCompassEnabled(true);
        try {
            MapsInitializer.initialize(getActivity());
        }
        catch (Exception e) {
            Log.e("MAPP", "Have GoogleMap but then error", e);
            //return;
        }

        return rootView;
    }






    @Override
    public void onResume() {

        super.onResume();
        getActivity().startService(new Intent(getActivity(), ServiceForGps.class));

        mapOpen = true;
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), ServiceForGps.class));
        mapOpen = false;
        mapView.onDestroy();
      //  locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        getActivity().stopService(new Intent(getActivity(), ServiceForGps.class));

       // mapOpen = false;
        mapView.onLowMemory();
    }


//    protected void startShowDialogAuthorization(String mess){
//        dialog = new ProgressDialog(getActivity());
//        dialog.setMessage(mess);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        dialog.setCancelable(false);
//        dialog.show();
//    }
//
//    protected void stopShowDialogAuthorization(){
//        dialog.dismiss();
//        dialog = null;
//    }
public static class ServiceForGps extends IntentService{


    public ServiceForGps(){
        super("ServiceForGps");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapOpen = false;
        Log.d("sleep","sleep des" );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(mapOpen && PositionReceiver.statusGPS) {
                        if(!statusGPS) {
                            new dialogShow().execute();
                            statusGPS = true;
                        }
                        //linearLayout.setVisibility(View.GONE);
                        if(!bgLoad) {
                            bgLoad = true;
                            new LoadingPeopleAroundMe().execute(ParseUser.getCurrentUser());
                        }
                        try {
                            Thread.sleep(5000);
                            Log.d("sleep", "sleep");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{

                      //linearLayout.setVisibility(View.VISIBLE);
                        if(statusGPS) {
                            new dialogShow().execute();
                            statusGPS = false;
                        }
                    }
                }
            }
        }).run();

    }
}
    private static class dialogShow extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!PositionReceiver.statusGPS )
                linearLayout.setVisibility(View.VISIBLE);
            else
                linearLayout.setVisibility(View.GONE);
        }
    }
    private static class LoadingPeopleAroundMe extends AsyncTask<ParseUser,Void,ParseUser>{

       // List<InfoAboutUserUnit> listUnits = new ArrayList<InfoAboutUserUnit>();
        List<ParseUser> users;
        List<ParseUser> friends;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  map.clear();
        }


        @Override
        protected ParseUser doInBackground(ParseUser... params) {

            if (ParseUser.getCurrentUser() != null){
                users = new ArrayList<ParseUser>();
                friends = new ArrayList<ParseUser>();
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereWithinKilometers("position", params[0].getParseGeoPoint("position"), 0.5);
                query.whereNotEqualTo("username", params[0].getUsername());

                ParseQuery<ParseUser> queryFriends = ParseUser.getQuery();


                ParseQuery<ParseObject> parseQueryFriends = ParseQuery.getQuery("Friends");
                List<String> listFriends = new ArrayList<String>();
                if (ParseUser.getCurrentUser().getObjectId() != null) {
                    parseQueryFriends.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId().toString());
                    try {
                        List<ParseObject> pO = parseQueryFriends.find();
                        for (int i = 0; i < pO.size(); i++) {
                            if (pO.get(i).get("friends") != null)
                                listFriends.add(pO.get(i).get("friends").toString());
                        }
                        query.whereNotContainedIn("objectId", listFriends);
                        queryFriends.whereContainedIn("objectId", listFriends);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        friends = queryFriends.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        users = query.find();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(ParseUser vOid) {
            super.onPostExecute(vOid);
            map.clear();
            Date dateMinus;
            if(ParseUser.getCurrentUser() != null){
                dateMinus = ParseUser.getCurrentUser().getUpdatedAt();
                dateMinus.setMinutes(dateMinus.getMinutes() - 5);

                ParseGeoPoint geoPointMy = new ParseGeoPoint();
                geoPointMy = vOid.getParseGeoPoint("position");
                LatLng latLngMy = new LatLng(geoPointMy.getLatitude(), geoPointMy.getLongitude());
                map.addMarker(new MarkerOptions()
                        .title(vOid.getUsername().toString())
                        .position(latLngMy));
                //  map1.ma
                for (int i = 0; i < users.size(); i++) {


                    if (dateMinus.before(users.get(i).getUpdatedAt())) {
                        Log.d("Marker", "set marker");
                        ParseGeoPoint geoPoint = new ParseGeoPoint();
                        geoPoint = users.get(i).getParseGeoPoint("position");
                        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        //map1
                        map.addMarker(new MarkerOptions()
                                        .title(users.get(i).getUsername().toString())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        );


                        //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    }
                }
                for (int i = 0; i < friends.size(); i++) {


                    if (dateMinus.before(friends.get(i).getUpdatedAt())) {
                        Log.d("Marker", "set marker");
                        ParseGeoPoint geoPoint = new ParseGeoPoint();
                        geoPoint = friends.get(i).getParseGeoPoint("position");
                        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        //map1
                        map.addMarker(new MarkerOptions()
                                        .title(friends.get(i).getUsername().toString())
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        );


                        //icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    }
                }
            }
            bgLoad =false;
        }
    }
}
