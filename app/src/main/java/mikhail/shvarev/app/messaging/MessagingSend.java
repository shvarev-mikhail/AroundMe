package mikhail.shvarev.app.messaging;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mihail on 17.05.2015.
 */
public class MessagingSend {
    ParseQuery<ParseObject> queryId;
    String[] fromToToFrom = new String[2];
    String idToUser;
    String idFrom;
    ParseObject[] parseObjectsForMessages = new ParseObject[2];
    //создание строк для обмена сообщениями
    //2 строки
    public void createRowForMessages(String idToUserNew){
        idFrom  = ParseUser.getCurrentUser().getObjectId().toString();
        idToUser = idToUserNew;
        fromToToFrom[0] = idFrom   + idToUser;
        fromToToFrom[1] = idToUser + idFrom;

        queryId = ParseQuery.getQuery("Messages");
//        queryId.whereEqualTo("userId",fromToToFrom[0]);
//        try {
//            ParseObject parseObject = queryId.getFirst();
//            ParseObject pObject = new ParseObject("Messages");
//            if(parseObject == null){
//
//                pObject.put("userId",fromToToFrom[0]);
//                pObject.saveInBackground();
//                parseObjectsForMessages[0] = parseObject;
//            }else {
//                parseObjectsForMessages[0] =  parseObject ;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        queryId = ParseQuery.getQuery("Messages");
//        queryId.whereEqualTo("userId",fromToToFrom[1]);
//        try {
//            ParseObject parseObject = queryId.getFirst();
//            ParseObject pObject = new ParseObject("Messages");
//            if(parseObject == null){
//
//                pObject.put("userId",fromToToFrom[1]);
//                pObject.saveInBackground();
//                parseObjectsForMessages[1] = parseObject;
//            }else {
//                parseObjectsForMessages[1] =  parseObject ;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        queryId.whereEqualTo("userId",fromToToFrom[0]);
        queryId.getFirstInBackground( new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ParseObject pObject = new ParseObject("Messages");
                if(parseObject == null){

                    pObject.put("userId",fromToToFrom[0]);
                    pObject.saveInBackground();
                    parseObjectsForMessages[0] = pObject;
                }else {
                    parseObjectsForMessages[0] =  parseObject ;
                }
            }
        });
        queryId = ParseQuery.getQuery("Messages");
        queryId.whereEqualTo("userId",fromToToFrom[1]);
        queryId.getFirstInBackground( new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ParseObject pObject = new ParseObject("Messages");
                if(parseObject == null ){

                    pObject.put("userId",fromToToFrom[1]);
                    pObject.saveInBackground();
                    parseObjectsForMessages[1] = pObject;
                }else {
                    parseObjectsForMessages[1] = parseObject ;
                }
            }
        });

    }
    public void sendMessage(String message){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = null;
        try{
            jsonObject.put("userId",idFrom);
            jsonObject.put("messages",message);
            jsonObject.put("object",ParseUser.getCurrentUser().getObjectId());

        Log.d("RECEIVER","send" + jsonObject.toString());
        jsonArray = new JSONArray();
        jsonArray.put(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //jsonArray.put(jsonObject);
        parseObjectsForMessages[0].add("newMessInBox",jsonArray);
        parseObjectsForMessages[0].saveInBackground();
        parseObjectsForMessages[1].add("newMessInBox",jsonArray);
        parseObjectsForMessages[1].saveInBackground();

    }


}
