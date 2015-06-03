package mikhail.shvarev.app.customListUser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mikhail.shvarev.app.R;

/**
 * Created by Mihail on 13.05.2015.
 */
public class CustomListUserAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<InfoAboutUserUnit> list;

    public CustomListUserAdapter(Activity activity,List<InfoAboutUserUnit> list){
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
            convertView = inflater.inflate(R.layout.custom_item_user_info,null);

        TextView userFirst    = (TextView)convertView.findViewById(R.id.userNameFirstLastListItemInfo);
        TextView userPosition = (TextView)convertView.findViewById(R.id.userDistanceListItemInfo);
        ImageView userIcon    = (ImageView)convertView.findViewById(R.id.imageIconListItemInfo);
        TextView userID       =  (TextView)convertView.findViewById(R.id.userIdinvise);

        InfoAboutUserUnit unit = list.get(position);

        userFirst.setText(unit.getUserName());
        userPosition.setText(unit.getUserDistance());
        userIcon.setImageBitmap(unit.getUserIcon());
        userID.setText(unit.getUseId());
        Log.d("CUSTOM",unit.getUserName()+ "  "+ unit.getUserDistance());//+" "+ unit.getUserIcon().toString());
        return convertView;
    }
}
