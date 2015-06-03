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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import mikhail.shvarev.app.R;

/**
 * Created by Mihail on 04.05.2015.
 */
public class Registration extends ActionBarActivity {
    Button btnRegNewUser;
    EditText textlogIn, passFirst, passSecond;
    Activity mActivity;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mActivity = this;
      //  dialog = new ProgressDialog(this);

        textlogIn  = (EditText)findViewById(R.id.newEditUserName);
        passFirst  = (EditText)findViewById(R.id.newEditUserPass);
        passSecond = (EditText)findViewById(R.id.confirmNewEditUserPass);

        btnRegNewUser = (Button)findViewById(R.id.btnRegistrationNewUser);
        btnRegNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //проверка на правильность ввода
                if(     !textlogIn.getText().toString().trim().equals("") &&
                        passFirst.getText().toString().equals(passSecond.getText().toString()) &&
                        !passFirst.getText().toString().equals(textlogIn.getText().toString())){
                    //регистрация
                    startShowDialogRegistration("Регистрация...");

                    ParseUser user = new ParseUser();
                    user.setUsername(textlogIn.getText().toString());
                    user.setPassword(passFirst.getText().toString());

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                stopShowDialogRegistration();
                                startActivity(new Intent(getApplication(),Authorization.class));
                                finish();
                            }else{
                                stopShowDialogRegistration();
                                Toast toast = Toast.makeText(getApplicationContext(),"Пользователь с таким логином уже есть!"+e.toString(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });

                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Неправильнно введены логин и/или пароль!", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

    }
    protected void startShowDialogRegistration(String mess){
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage(mess);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    protected void stopShowDialogRegistration(){
        dialog.dismiss();
        dialog = null;
    }
}
