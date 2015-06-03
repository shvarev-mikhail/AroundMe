package mikhail.shvarev.app.main;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mikhail.shvarev.app.R;

/**
 * Created by Mihail on 08.05.2015.
 */
public class ItemProfile  extends Fragment{

    TextView textViewNameProfile,textViewUserNameProfile,textViewNumberProfile, textViewAddressProfile;
    ImageView imageIconProfile;
    View rootView;

    ProgressDialog dialog;
    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        getActivity().setTitle("Профиль");
        startShowDialogAuthorization("Загрузка данных...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.item_profile,container,false);

        LinearLayout linearLayout = (LinearLayout)rootView.findViewById(R.id.main4);
        linearLayout.setVisibility(View.GONE);

        textViewNameProfile = (TextView)rootView.findViewById(R.id.userNameFirstLastProfile);
        textViewUserNameProfile = (TextView)rootView.findViewById(R.id.userNameProfile);
        textViewNumberProfile = (TextView)rootView.findViewById(R.id.textViewNumberProfile);
        imageIconProfile = (ImageView)rootView.findViewById(R.id.imageIconProfile);
        textViewAddressProfile = (TextView)rootView.findViewById(R.id.addressProfile);
        if(ParseUser.getCurrentUser().get("firstLastName")!=null)
            textViewNameProfile.setText(ParseUser.getCurrentUser().get("firstLastName").toString());
        else
            textViewNameProfile.setText("Новый пользователь, введите имя в настройках");
        if(ParseUser.getCurrentUser().get("numberPhone")==null)
            textViewNumberProfile.setText("Введите номер в настройках");
        else
            textViewNumberProfile.setText(ParseUser.getCurrentUser().get("numberPhone").toString());
        textViewUserNameProfile.setText(ParseUser.getCurrentUser().getUsername().toString());

        ParseFile parseFile = (ParseFile) ParseUser.getCurrentUser().get("icon");
        if(parseFile!=null) {
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageIconProfile.setImageBitmap(bmp);
                    }
                }
            });
        }else{
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
            imageIconProfile.setImageBitmap(bmp);
        }

        if(ParseUser.getCurrentUser().get("position")!=null) {
            Log.d("PROFILE", ParseUser.getCurrentUser().get("position").toString());
            ParseGeoPoint geoPoint = (ParseGeoPoint) ParseUser.getCurrentUser().get("position");
            Log.d("PROFILE", geoPoint.getLatitude() + " " + geoPoint.getLongitude());
            new LoadAddressFromCoord().execute(geoPoint);
        }
        else{
            stopShowDialogAuthorization();
        }



        return rootView;
    }

    private class LoadAddressFromCoord extends AsyncTask<ParseGeoPoint,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }



        @Override
        protected String doInBackground(ParseGeoPoint... params) {
            Geocoder geo  = new Geocoder(getActivity(),Locale.getDefault());
            String addressStr = "";
            try {
                List<Address> addresses = geo.getFromLocation((double)params[0].getLatitude(),(double)params[0].getLongitude(),1);
                if(addresses.isEmpty())
                    Log.d("PROFILE","empty!!!!");
                else{
                    if(addresses.size()>0){
                        Log.d("PROFILE",
                                addresses.get(0).getLocality() +", "
                                        + addresses.get(0).getThoroughfare () + ", "
                                        + addresses.get(0).getFeatureName());
                        addressStr = addresses.get(0).getLocality() +", "
                                + addresses.get(0).getThoroughfare () + ", "
                                + addresses.get(0).getFeatureName();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addressStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("PROFILE",s);
            textViewAddressProfile.setText(s);
            stopShowDialogAuthorization();

        }
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
}
