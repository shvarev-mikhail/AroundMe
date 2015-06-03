package mikhail.shvarev.app.main;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;


import com.google.android.gms.common.images.ImageManager;
import com.parse.GetDataCallback;

import com.parse.ParseException;
import com.parse.ParseFile;

import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import mikhail.shvarev.app.R;


/**
 * Created by Mihail on 08.05.2015.
 */
public class ItemSettings extends Fragment {
    View rootView;
    private static int LOAD_IMAGE_RESULTS = 1;
    private ImageView iv;
    private Button btnSave;
    private EditText editName, editPass1, editPass2, editNumber;

    public int requestCode, resultCode;
    Intent data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("settings","settings4");
        getActivity().setTitle("Настройки");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("settings","settings1");
        this.requestCode = requestCode;
        this.resultCode  = resultCode;
        this.data        = data;

        new UpLoadDataFromSetting().execute();
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_settings,container,false);
        iv = (ImageView)rootView.findViewById(R.id.imageIcon);
        btnSave = (Button)rootView.findViewById(R.id.buttonSave);
        editName = (EditText)rootView.findViewById(R.id.editTextNewName);
        editPass1 = (EditText)rootView.findViewById(R.id.editTextNewPass1);
        editPass2 = (EditText)rootView.findViewById(R.id.editTextNewPass2);
        editNumber = (EditText)rootView.findViewById(R.id.editTextNewNumber);
        if(ParseUser.getCurrentUser().get("firstLastName") != null)
        editName.setHint(ParseUser.getCurrentUser().get("firstLastName").toString());
        if(ParseUser.getCurrentUser().get("numberPhone") != null)
            editNumber.setHint(ParseUser.getCurrentUser().get("numberPhone").toString());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editNumber.getText().toString().trim().equals(""))
                    ParseUser.getCurrentUser().put("numberPhone", editNumber.getText().toString());
                if (!editName.getText().toString().trim().equals(""))
                    ParseUser.getCurrentUser().put("firstLastName", editName.getText().toString());
                if (editPass1.getText().toString().equals(editPass2.getText().toString()) && !editPass1.getText().toString().trim().equals(""))
                    ParseUser.getCurrentUser().setPassword(editPass1.getText().toString());
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(), "Изменение произошло успешно.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "click 222", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                  startActivityForResult(intent, LOAD_IMAGE_RESULTS);
            }
        });
        ParseFile parseFile = (ParseFile) ParseUser.getCurrentUser().get("icon");
        if(parseFile!=null) {
            parseFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        iv.setImageBitmap(bmp);
                    }
                }
            });
        }else{
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
            iv.setImageBitmap(bmp);
        }
        Log.d("settings","settings3");
        return rootView;

    }

    class UpLoadDataFromSetting extends AsyncTask<Void,Void,Void>{
        String picturePath;
        @Override
        protected Void doInBackground(Void... params) {
            if(requestCode == LOAD_IMAGE_RESULTS &&  data != null && resultCode == getActivity().RESULT_OK)
            {
                Log.d("settings","settings2");
                Uri pickedImg = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(pickedImg,filePath,null,null,null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                picturePath = cursor.getString(columnIndex);


                cursor.close();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/4,bitmap.getHeight()/4,true);
                newBitmap.compress(Bitmap.CompressFormat.JPEG,10,stream);

                byte[] bytes = stream.toByteArray();


                ParseFile file = new ParseFile("icon.jpg",bytes);
                file.saveInBackground();
                ParseUser parseUser = ParseUser.getCurrentUser();
                parseUser.put("icon",file);
                try {
                    parseUser.save();
                   // Toast.makeText(getActivity(), "Загружен 222", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//                parseUser.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if(e == null)
//                            Toast.makeText(getActivity(), "Загружен 222", Toast.LENGTH_SHORT).show();
//                    }
//                });



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
            Log.d("settings","settings2 "+picturePath);
            iv = (ImageView)rootView.findViewById(R.id.imageIcon);
            iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }
}
