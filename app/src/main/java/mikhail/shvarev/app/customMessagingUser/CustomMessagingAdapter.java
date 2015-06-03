package mikhail.shvarev.app.customMessagingUser;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import mikhail.shvarev.app.R;

/**
 * Created by Mihail on 14.05.2015.
 */
public class CustomMessagingAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<InfoMessaging> list;

    public CustomMessagingAdapter(Activity activity,List<InfoMessaging> list){
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
        InfoMessaging unit = list.get(position);
        //if(convertView == null) {
            if(!unit.getUserName().toString().equals(ParseUser.getCurrentUser().get("firstLastName").toString()))
                convertView = inflater.inflate(R.layout.custom_user_mess_left, null);
            else
                convertView = inflater.inflate(R.layout.custom_user_mess_right, null);
       // }
        TextView userFirst    = (TextView)convertView.findViewById(R.id.userNameMessaging);
        TextView userMess = (TextView)convertView.findViewById(R.id.userMessBubleMessaging);
      //  ImageView userIcon    = (ImageView)convertView.findViewById(R.id.imageIconListItemInfo);




        userFirst.setText(unit.getUserName());
        userMess.setText(unit.getUserMess());
        Log.d("RECEIVER","custom "+unit.getUserName() + "   " + unit.getUserMess());
        //userIcon.setImageBitmap(unit.getUserIcon());
       // userID.setText(unit.getUseId());
        Log.d("CUSTOM Messaging", unit.getUserName() + "  " + unit.getUserMess());//+" "+ unit.getUserIcon().toString());
        return convertView;
    }
}
