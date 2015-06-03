package mikhail.shvarev.app.customListMessaging;

import android.graphics.Bitmap;

/**
 * Created by Mihail on 18.05.2015.
 */
public class InfoMessageUnit {
    private String userName,  userLastMess, useId;
    private Bitmap userIcon;

    public InfoMessageUnit(String userName,String userId,String userLastMess,Bitmap userIcon){
        this.userName     = userName;
        this.useId        = userId;
        this.userLastMess = userLastMess;
        this.userIcon     = userIcon;

    }

    public String getUserLastMess() {
        return userLastMess;
    }

    public String getUseId() {
        return useId;
    }

    public String getUserName() {
        return userName;
    }

    public Bitmap getUserIcon() {
        return userIcon;
    }
}
