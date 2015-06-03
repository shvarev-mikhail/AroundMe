package mikhail.shvarev.app.Authorization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import mikhail.shvarev.app.R;
import mikhail.shvarev.app.main.Main;

/**
 * Created by Mihail on 04.05.2015.
 */
public class Authorization extends ActionBarActivity {

    Button btnlogIn,btnRegistration;
    EditText logInText,passText;
    ProgressDialog dialog;
    Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);




        mActivity = this;

        logInText = (EditText)findViewById(R.id.editUserName);
        passText  = (EditText)findViewById(R.id.editUserPass);

        btnlogIn  = (Button)findViewById(R.id.btnLogIn);
        btnlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!logInText.getText().toString().trim().equals("") && !passText.getText().toString().trim().equals("")){
                    startShowDialogAuthorization("Авторизация...");
                    ParseUser.logInInBackground(logInText.getText().toString(), passText.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                stopShowDialogAuthorization();
                                //Toast toast = Toast.makeText(getApplicationContext(), "Good " + ParseUser.getCurrentUser().getSessionToken(), Toast.LENGTH_SHORT);
                                //toast.show();
                                Intent intent = new Intent(mActivity, Main.class);
                                startActivity(intent);
                                finish();
                                //parseUser.getSessionToken();
                            } else {
                                stopShowDialogAuthorization();
                                Toast toast = Toast.makeText(getApplicationContext(), "Неправильнно введены логин и/или пароль!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
            }
        });

        btnRegistration = (Button)findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Authorization.this,Registration.class);
                startActivity(intent);
            }
        });
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
