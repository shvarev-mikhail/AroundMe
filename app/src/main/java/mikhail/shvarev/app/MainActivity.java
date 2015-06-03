package mikhail.shvarev.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

import mikhail.shvarev.app.Authorization.Authorization;
import mikhail.shvarev.app.main.Main;


public class MainActivity extends ActionBarActivity {
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        ////INIT PARSE
//        new ParseInit().initParse(this);
        ////
        ctx = this;
        ParseUser parseUser = ParseUser.getCurrentUser();
        if(parseUser!=null){
            parseUser.getUsername();
          //  Toast toast = Toast.makeText(getApplicationContext(),parseUser.getSessionToken()+ " Good " + parseUser.getUsername(), Toast.LENGTH_SHORT);
         //   toast.show();
            ParseUser.becomeInBackground(parseUser.getSessionToken().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        //Toast toast = Toast.makeText(getApplicationContext(), "Good " + user.getUsername(), Toast.LENGTH_SHORT);
                        //toast.show();
                        Intent intent = new Intent(ctx, Main.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Intent intent = new Intent(ctx, Authorization.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }else {
            //Toast toast = Toast.makeText(getApplicationContext(), "bad ", Toast.LENGTH_SHORT);
            // toast.show();
            Intent intent = new Intent(ctx, Authorization.class);
            startActivity(intent);
            finish();
        }

       // PushService.setDefaultPushCallback(this, Authorization.class);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
