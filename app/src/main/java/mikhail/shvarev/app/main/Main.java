package mikhail.shvarev.app.main;




import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import mikhail.shvarev.app.Authorization.Authorization;
import mikhail.shvarev.app.ParseInit;
import mikhail.shvarev.app.PositionReceiver;
import mikhail.shvarev.app.R;


public class Main extends ActionBarActivity  {
    AlertDialog.Builder alertDialog;
    public static Activity activity;
    public static ActionBar actionBar;
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Карта","Сообщения","Списки","Настройки","Выход"};
    int ICONS[] = {R.drawable.map,
            R.drawable.messages,
            R.drawable.list,
            R.drawable.settings,
            R.drawable.exit};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String firtLastName;// = ParseUser.getCurrentUser().get("firstLastName").toString();//"Mikhail Shvarev";
    String userName;// = ParseUser.getCurrentUser().getUsername().toString();//"akash.bangad@android4devs.com";
    int PROFILE = R.mipmap.ic_launcher;

    public Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle



    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        activity = this;

        startService(new Intent(this, PositionReceiver.class));


       // PushService.setDefaultPushCallback(this,Main.class);///////////////

        ParsePush.subscribeInBackground(ParseUser.getCurrentUser().getUsername(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "good sub!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "bad sub", Toast.LENGTH_SHORT).show();
                }
            }
        });
/////////////////////////////////////////////////////////////////
        ParseFile parseFile = (ParseFile) ParseUser.getCurrentUser().get("icon");

        try {
            if(parseFile!=null)
                bmp = BitmapFactory.decodeByteArray(parseFile.getData(),0,parseFile.getData().length);
            else
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
        } catch (ParseException e) {
            e.printStackTrace();
            bmp = BitmapFactory.decodeResource(getResources(),R.drawable.profile);
        }
//////////////////////////////////////////////////////////////////////////

      //  new ImageLoad().execute();

        //фоновая загруза опаздывает за drawer и изображение не подгружается
//        final ImageView imageViewIcon = (ImageView)findViewById(R.id.circleView);
//        parseFile.getDataInBackground(new GetDataCallback() {
//            @Override
//            public void done(byte[] bytes, ParseException e) {
//                if(e == null){
//                    bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    Toast.makeText(getApplicationContext(),"good",Toast.LENGTH_SHORT).show();
//
//                    imageViewIcon.setImageBitmap(bmp);
//
//
//
//                }
//            }
//        });
    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
      //  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.header_new);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

     //   if(!ParseUser.getCurrentUser().get("firstLastName").getClass().toString().equals(null))
        try {
            firtLastName = ParseUser.getCurrentUser().get("firstLastName").toString();
        }catch (Exception e) {
            firtLastName = "Новый пользователь";//"Mikhail Shvarev";
        }
        userName = ParseUser.getCurrentUser().getUsername().toString();//"akash.bangad@android4devs.com";


        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS, firtLastName, userName,bmp,this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(Main.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());



                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();
                    Toast.makeText(Main.this,"The Item 9Clicked is: "+recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    onTouchDrawer(recyclerView.getChildPosition(child));
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        openFragment(new ItemMessages());

    }
    private void openFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onTouchDrawer(final int position) {
       // if (lastMenu == position) return;

        switch ( position) {
            case 0:
                openFragment(new ItemProfile());
                break;
            case 1:
                openFragment(new ItemMap());
                break;
            case 2:
                openFragment(new ItemMessages());
                break;
            case 3:
                openFragment(new ItemList());
                break;
            case 4:
                openFragment(new ItemSettings());
                break;
            case 5:
                //openFragment(new ItemExit());
                alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Выход");
                alertDialog.setMessage("Уверены, что хотите выйти?");
                alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParsePush.unsubscribeInBackground(ParseUser.getCurrentUser().getUsername());
                        ParseUser.logOut();
                        Intent intent = new Intent(Main.this, Authorization.class);
                        stopService(new Intent(Main.this, PositionReceiver.class));
                        startActivity(intent);

                        finish();
                    }
                });
                alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();

                break;
            default:
                return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openFragment(new ItemSettings());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class ImageLoad extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            ParseFile parseFile = (ParseFile) ParseUser.getCurrentUser().get("icon");

        try {
            if(parseFile!=null)
                bmp = BitmapFactory.decodeByteArray(parseFile.getData(),0,parseFile.getData().length);
            else
                bmp = BitmapFactory.decodeResource(getResources(),R.drawable.header_new1);
        } catch (ParseException e) {
            e.printStackTrace();
            bmp = BitmapFactory.decodeResource(getResources(),R.drawable.header_new1);
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

            ImageView imageView = (ImageView)activity.findViewById(R.id.circleView);
            imageView.setImageBitmap(bmp);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, PositionReceiver.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

//    public void clickAdd(View v){
//        Log.d("request", "click add ");
//        ItemList.clickAdd();
//    }
//    public void clickCancel(View v){
//        Log.d("request", "click cancel");
//        ItemList.clickCancel();
//    }



}
