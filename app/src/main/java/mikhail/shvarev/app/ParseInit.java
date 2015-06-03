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
        Parse.initialize(this, "WWRSaFlLREJQ7hIjdjzkR5cJdDHmKQWWJU1Lj2i5", "EcZzIY0D15i5GlyvwxNea57j23P2eFV0lfYXowFk");
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
//public class ParseInit {
//    public void initParse(Context ctx) {
//        Parse.initialize(ctx, "WWRSaFlLREJQ7hIjdjzkR5cJdDHmKQWWJU1Lj2i5", "EcZzIY0D15i5GlyvwxNea57j23P2eFV0lfYXowFk");
//        PushService.setDefaultPushCallback(ctx, Main.class);
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//
//    }
//}
