package mikhail.shvarev.app.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.app.ToolbarActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.parseLoadUpload.LoadListOfRequest;
import mikhail.shvarev.app.parseLoadUpload.LoadListOfUser;
import mikhail.shvarev.app.search.SearchDialog;
import mikhail.shvarev.app.users.DialogUser;
import mikhail.shvarev.app.users.UserProfile;

/**
 * Created by Mihail on 08.05.2015.
 */
public class ItemList extends Fragment implements ActionBar.OnNavigationListener{
    public static  View rootView;
    ListView listView;

    String[] typeList = new String[]{"Рядом","Друзья","Запросы"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        //ActionBar actionBar = getActivity().getSupport
        getActivity().setTitle("");

       // getActivity().getActionBar().setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
        Main.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Main.actionBar.setListNavigationCallbacks(adapter,this);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Main.actionBar.setNavigationMode(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_list,container,false);





        listView = (ListView)rootView.findViewById(R.id.listViewUserList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView viewById = (TextView) view.findViewById(R.id.userIdinvise);
                Toast.makeText(getActivity(),position + "  "+ viewById.getText(),Toast.LENGTH_SHORT).show();
                TextView viewUserName = (TextView)view.findViewById(R.id.userNameFirstLastListItemInfo);
                Fragment userFrag = new UserProfile();
                Bundle bundle = new Bundle();
                bundle.putString("userName",viewUserName.getText().toString());
                bundle.putString("userId",viewById.getText().toString());
                userFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,userFrag).commit();




            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item_list,menu);


         super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_list)
        {
                Log.d("SEARCH","SEARCH");
            new SearchDialog(getActivity(),getActivity().getSupportFragmentManager()).show();


        }
        if(item.getItemId() == R.id.menu_item_list_refresh){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,new ItemList()).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {

        TextView tvEmpty = (TextView)getActivity().findViewById(R.id.textViewEmpty);
        tvEmpty.setVisibility(View.GONE);
        if(i == 0){
            Log.d("LISTITEM",typeList[i]);
            listView.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.progressLoadingList);
            progressBar.setVisibility(View.VISIBLE);
            new LoadListOfUser(getActivity(),rootView).execute("");
        }else if(i == 1){
            Log.d("LISTITEM",typeList[i]);
            listView.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.progressLoadingList);
            progressBar.setVisibility(View.VISIBLE);
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("FRIENDS","FRIENDS");
            ed.commit();
            new LoadListOfUser(getActivity(),rootView).execute("");
        }else if(i == 2){
            Log.d("LISTITEM",typeList[i]);
            listView.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.progressLoadingList);
            progressBar.setVisibility(View.VISIBLE);
            new LoadListOfRequest(getActivity(),rootView).execute("");
        }
        return false;
    }




}
