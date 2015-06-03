package mikhail.shvarev.app.customListMessaging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.customListUser.InfoAboutUserUnit;

/**
 * Created by Mihail on 18.05.2015.
 */
public class CustomListMessagingAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<InfoMessageUnit> list;

    public CustomListMessagingAdapter(Activity activity,List<InfoMessageUnit> list){
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
            convertView = inflater.inflate(R.layout.custom_item_list_mess_info,null);

        TextView userFirst    = (TextView)convertView.findViewById(R.id.userNameFirstLastItemListMessInfo);
        TextView userLastMess = (TextView)convertView.findViewById(R.id.userLastMessInfo);
        ImageView userIcon    = (ImageView)convertView.findViewById(R.id.imageIconItemListMessInfo);
        TextView userID       =  (TextView)convertView.findViewById(R.id.userIdinvise);

        InfoMessageUnit unit = list.get(position);

        userFirst.setText(unit.getUserName());
        userLastMess.setText(unit.getUserLastMess());
        userIcon.setImageBitmap(unit.getUserIcon());
        userID.setText(unit.getUseId());
        Log.d("CUSTOM list Mess", unit.getUserName() + "  " + unit.getUserLastMess());//+" "+ unit.getUserIcon().toString());
        return convertView;
    }
}
