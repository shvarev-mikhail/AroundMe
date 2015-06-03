package mikhail.shvarev.app.customListUser;

import android.graphics.Bitmap;

/**
 * Created by Mihail on 13.05.2015.
 */
public class InfoAboutUserUnit {
    private String userName, userNum, userDistance, useId;
    private Bitmap userIcon;

    public InfoAboutUserUnit(String userName,String userId,String userDistance, String userNum,Bitmap userIcon){
        this.userName     = userName;
        this.useId        = userId;
        this.userDistance = userDistance;
        this.userIcon     = userIcon;
        this.userNum      = userNum;
    }

    public String getUserNum() {
        return userNum;
    }

    public String getUseId() {
        return useId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDistance() {
        return userDistance;
    }

    public Bitmap getUserIcon() {
        return userIcon;
    }
}
