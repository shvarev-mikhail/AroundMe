package mikhail.shvarev.app.customRequestList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;

/**
 * Created by Mihail on 21.05.2015.
 */
public class CustomRequestListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<InfoAboutUserUnit> list;

    View rootView;
    public CustomRequestListAdapter(Activity activity,List<InfoAboutUserUnit> list){
        this.activity = activity;
        this.list     = list;

    }

    @Override
    public int getCount() {
        if(list.size()<=0)
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null)
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.custom_item_list_request_user,null);

        rootView = convertView;
        TextView userFirst    = (TextView)convertView.findViewById(R.id.userNameFirstLastListItemInfo);
        //TextView userPosition = (TextView)convertView.findViewById(R.id.userDistanceListItemInfo);
        ImageView userIcon    = (ImageView)convertView.findViewById(R.id.imageIconListItemInfo);
        TextView userID       =  (TextView)convertView.findViewById(R.id.userIdinvise);
        Button btnAdd         = (Button)convertView.findViewById(R.id.btnADD);
        Button btnCancel      = (Button)convertView.findViewById(R.id.btnCancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAdd();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCancel();
            }
        });

        InfoAboutUserUnit unit = list.get(position);

        userFirst.setText(unit.getUserName());
       // userPosition.setText(unit.getUserDistance());
        userIcon.setImageBitmap(unit.getUserIcon());
        userID.setText(unit.getUseId());
        Log.d("CUSTOM", unit.getUserName());// + "  " + unit.getUserDistance());//+" "+ unit.getUserIcon().toString());
        return convertView;
    }




    public  void clickAdd(){
        TextView tvUserId = (TextView)rootView.findViewById(R.id.userIdinvise);
        Log.d("request",tvUserId.getText().toString());

        Button btnADD = (Button)rootView.findViewById(R.id.btnADD);
        btnADD.setVisibility(View.GONE);
        Button btnCancel = (Button)rootView.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);

        ParseQuery<ParseObject> parseAdd = ParseQuery.getQuery("Friends");
        parseAdd.whereEqualTo("request",tvUserId.getText().toString());
        parseAdd.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        try {
            ParseObject parseObjectAdd = parseAdd.getFirst();
            parseObjectAdd.put("friends",tvUserId.getText().toString());
            parseObjectAdd.put("request","");
            parseObjectAdd.saveInBackground();

            ParseObject parseAdd2 = new ParseObject("Friends");
            parseAdd2.put("userId",tvUserId.getText().toString());
            parseAdd2.put("friends",ParseUser.getCurrentUser().getObjectId());
            parseAdd2.saveInBackground();



        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    public  void clickCancel(){
        TextView tvUserId = (TextView)rootView.findViewById(R.id.userIdinvise);
        Log.d("request",tvUserId.getText().toString());

        Button btnADD = (Button)rootView.findViewById(R.id.btnADD);
        btnADD.setVisibility(View.GONE);
        Button btnCancel = (Button)rootView.findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);

        ParseQuery<ParseObject> parseCancel = ParseQuery.getQuery("Friends");
        parseCancel.whereEqualTo("request",tvUserId.getText().toString());
        parseCancel.whereEqualTo("userId",ParseUser.getCurrentUser().getObjectId());
        try {
            ParseObject parseObjectCancel = parseCancel.getFirst();
            parseObjectCancel.delete();
            parseObjectCancel.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}