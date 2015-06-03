package mikhail.shvarev.app.search;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences.Editor;
import mikhail.shvarev.app.R;
import mikhail.shvarev.app.main.ItemList;
import mikhail.shvarev.app.parseLoadUpload.LoadListOfUser;

/**
 * Created by Mihail on 20.05.2015.
 */
public class SearchDialog implements View.OnClickListener {
    SharedPreferences sPref;

    Activity activity;
    FragmentManager manager;
    Dialog dialog;

    Button btnSearch;
    EditText edName,edLogin,edNumber;
    public SearchDialog(Activity activity,FragmentManager manager){
        this.activity = activity;
        this.manager  = manager;
        sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        initDialog();

    }
    private void initDialog(){
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_search);
        dialog.setTitle("Поиск");

        edName = (EditText)dialog.findViewById(R.id.idEditTextName);
        edLogin = (EditText)dialog.findViewById(R.id.idEditTextLogin);
        edNumber = (EditText)dialog.findViewById(R.id.idEditTextNumber);
        btnSearch = (Button)dialog.findViewById(R.id.idButtonSearch);
        btnSearch.setOnClickListener(this);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editor ed = sPref.edit();
                ed.putString("NAME",edName.getText().toString().trim());
                ed.commit();
                ed.putString("LOGIN",edLogin.getText().toString().trim());
                ed.commit();
                ed.putString("NUMBER",edNumber.getText().toString().trim());
                ed.commit();



                manager.beginTransaction().replace(R.id.container,new ItemList()).commit();
                dialog.cancel();
            }
        });
    }
    public void show(){
        dialog.show();
    }
    @Override
    public void onClick(View v) {

    }
}
