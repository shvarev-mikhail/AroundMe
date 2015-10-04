package mikhail.shvarev.app;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

import mikhail.shvarev.app.main.Main;

/**
 * Created by Mihail on 04.05.2015.
 */

public class ParseInit extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "key", "key");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}

