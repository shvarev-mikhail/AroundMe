package mikhail.shvarev.app.customMessagingUser;

import android.graphics.Bitmap;

/**
 * Created by Mihail on 14.05.2015.
 */
public class InfoMessaging {
    private String userName, userMess, useId;
    private Bitmap userIcon;

    public InfoMessaging(String userName,String userId,String userMess, Bitmap userIcon){
        this.userName     = userName;
        this.useId        = userId;
        this.userMess     = userMess;
        this.userIcon     = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMess() {
        return userMess;
    }

    public String getUseId() {
        return useId;
    }

    public Bitmap getUserIcon() {
        return userIcon;
    }
}
