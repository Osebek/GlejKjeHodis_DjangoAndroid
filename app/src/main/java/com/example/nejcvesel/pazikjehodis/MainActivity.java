package com.example.nejcvesel.pazikjehodis;

//import android.app.FragmentManager;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nejcvesel.pazikjehodis.retrofitAPI.BackendAPICall;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.FileUpload;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.Location;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.Path;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by brani on 12/18/2016.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LocationFragment.OnListFragmentInteractionListener,
        LocationDetailFragment.OnFragmentInteractionListener{
    public static String authToken;
    public Marker currentMarker = null;
    public Uri url = null;
    public CallbackManager callbackManager;
    public AccessTokenTracker accessTokenTracker;
    public AccessToken accessToken;
    public ProfileTracker profileTracker;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    public ArrayList<String> pathLocations = new ArrayList<String>();
    public Profile profile;
    public HashMap<String,String> locationsToAddToPath = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//
//        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile profile = Profile.getCurrentProfile();
                        authToken = loginResult.getAccessToken().getToken();
                        AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

       accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View header=navigationView.getHeaderView(0);
                TextView name = (TextView)header.findViewById(R.id.textView);

                if (currentAccessToken == null){
                    name.setText("Anonymous");
                }
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                if (currentProfile != null)
                {
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    View header=navigationView.getHeaderView(0);
                    TextView name = (TextView)header.findViewById(R.id.textView);
                    name.setText(currentProfile.getFirstName() + " " + currentProfile.getLastName());
                    profile = currentProfile;

                }

                }
        };

        this.profile = Profile.getCurrentProfile();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("fab1");
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               System.out.println("fab2");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView)header.findViewById(R.id.textView);
        if (AccessToken.getCurrentAccessToken() != null) {
            name.setText(this.profile.getFirstName() + " " + this.profile.getLastName());
        }
        else
        {
            name.setText("Anonymous");

        }

        BackendAPICall bra = new BackendAPICall();
        //bra.getAllLocations(authToken);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new MapsFragment(),"MapFragment").addToBackStack("MapFragment").commit();
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {

            getFragmentManager().popBackStack();
        }


        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

          //  if (drawer.isDrawerOpen(GravityCompat.START)) {
          //  drawer.closeDrawer(GravityCompat.START);
       // } else {
         //   super.onBackPressed();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation vixzxew item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();
        FloatingActionButton floatA = (FloatingActionButton)findViewById(R.id.fab);
        FloatingActionButton floatB = (FloatingActionButton)findViewById(R.id.fab1);
        FloatingActionButton floatC = (FloatingActionButton)findViewById(R.id.fab2);



        switch (id){
            case R.id.nav_map:
                floatA.setVisibility(View.VISIBLE);
                floatB.setVisibility(View.VISIBLE);
                floatC.setVisibility(View.VISIBLE);
                pathLocations.clear();
                fm.beginTransaction().replace(R.id.content_frame, new MapsFragment(), "MapFragment").addToBackStack("MapFragment").commit();
                break;
            case R.id.nav_login:
                floatA.setVisibility(View.GONE);
                floatB.setVisibility(View.GONE);
                floatC.setVisibility(View.GONE);


                fm.beginTransaction().replace(R.id.content_frame, new LogInFragment(), "LogInFragment").addToBackStack("LogInFragment").commit();
                break;
            case R.id.nav_details:
                floatA.setVisibility(View.GONE);
                floatB.setVisibility(View.GONE);
                floatC.setVisibility(View.GONE);
                fm.beginTransaction().replace(R.id.content_frame, new LocationFragment(), "LocationFragment").addToBackStack("LocationFragment").commit();
                break;
            case R.id.nav_paths:
                AccessToken at =  AccessToken.getCurrentAccessToken();
                this.authToken = at.getToken().toString();

                floatA.setVisibility(View.GONE);
                floatB.setVisibility(View.GONE);
                floatC.setVisibility(View.GONE);
                fm.beginTransaction().replace(R.id.content_frame, new PathListFragment(), "PathListFragment").addToBackStack("PathListFragment").commit();
                break;
            case R.id.nav_add_path:
                floatA.setVisibility(View.GONE);
                floatB.setVisibility(View.GONE);
                floatC.setVisibility(View.GONE);
                this.locationsToAddToPath.clear();
                fm.beginTransaction().replace(R.id.content_frame, new PathAddFragment(), "PathAddFragment").addToBackStack("PathAddFragment").commit();
                break;


            case R.id.nav_home:
                Intent intent = new Intent(getApplicationContext(),MainMenuActivity.class);
                startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OpenGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    public void OpenFormFragment(){
        if(currentMarker != null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame, new LocationFormFragment(),"FormFragment").addToBackStack("FormFragment").commit();
        }
    }

    public void CancelForm (View view){
        onBackPressed();
    }

    public void SaveForm (View view){
        AccessToken at =  AccessToken.getCurrentAccessToken();
        authToken = at.getToken().toString();
        LocationFormFragment form = (LocationFormFragment) getFragmentManager().findFragmentByTag("FormFragment");
        TextView name = (TextView) form.getView().findViewById(R.id.inputName);
        EditText address = (EditText) form.getView().findViewById(R.id.inputAddress);
        EditText title = (EditText) form.getView().findViewById(R.id.inputTitle);
        EditText description = (EditText) form.getView().findViewById(R.id.inputDescription);
        TextView imageUrl = (TextView) form.getView().findViewById(R.id.imageURL);
        Uri url = Uri.parse(imageUrl.getText().toString());
        if(currentMarker != null) {
            LatLng latlng = currentMarker.getPosition();
            FileUpload sendRequest = new FileUpload();
            sendRequest.uploadFile(url, ((float) latlng.latitude), ((float) latlng.longitude), name.getText().toString(),
                    address.getText().toString(),title.getText().toString(), description.getText().toString(),
                    authToken, this.getBaseContext());
        }
        OpenMapsFragment();
        //FragmentManager fm = getFragmentManager();
        //fm.beginTransaction().replace(R.id.content_frame, new LocationFragment(), "LocationFragment").addToBackStack("LocationFragment").commit();



    }

    public void AttachPic (View view){
        AccessToken at =  AccessToken.getCurrentAccessToken();
        authToken = at.getToken().toString();
        System.out.println(authToken);
       //BackendAPICall kliciAPI = new BackendAPICall();
        //kliciAPI.getAllPaths(authToken);
        //kliciAPI.addPath(authToken);
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    public void OpenMapsFragment(){
        FragmentManager fm = getFragmentManager();
        boolean fragmentPopped = fm.popBackStackImmediate("MapsFragment", 0);
        if (!fragmentPopped) {
            fm.beginTransaction().replace(R.id.content_frame, new MapsFragment(), "MapFragment").addToBackStack("MapFragment").commit();
        }
//        fm.beginTransaction().replace(R.id.content_frame, new MapsFragment()).commit();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    System.out.println("Fotka fotka");
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    url = selectedImage;
                    LocationFormFragment form = (LocationFormFragment) getFragmentManager().findFragmentByTag("FormFragment");
                    TextView picText = (TextView) form.getView().findViewById(R.id.imageURL);
                    picText.setText(selectedImage.toString());


//                    System.out.println(selectedImage.toString());
//                    FileUpload sendRequest = new FileUpload();
//                    System.out.println("Do tuki rpidem");
//                    if(currentMarker != null){
//                        LatLng latlng= currentMarker.getPosition();
//                        sendRequest.uploadFile(selectedImage, ((float) latlng.latitude), ((float) latlng.longitude),
//                                "To je teksts","something","some Name",
//                                authToken,this.getApplicationContext());
//                    }
                }
                break;
        }
        callbackManager.onActivityResult(requestCode,resultCode,imageReturnedIntent);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Location item) {

    }



    //Map interaction method
    public boolean isMarker (){
        return currentMarker != null;
    }
    public void RemoveMarker(){
        currentMarker.remove();
    }
    public void AddMarker(Marker marker){
        currentMarker = marker;
    }

    public void showLocationOnMap(View v, String pathLocations)
    {
        this.pathLocations.clear();
        pathLocations  = pathLocations.replaceAll("\\[","");
        pathLocations  = pathLocations.replaceAll("\\]","");
        pathLocations = pathLocations.replaceAll(" ","");
        String[] pathLocationArray = pathLocations.split(",");

        for (int i = 0; i < pathLocationArray.length; i++)
        {
            this.pathLocations.add(pathLocationArray[i]);
        }

        System.out.println(Arrays.toString(pathLocationArray));
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new MapsFragment(), "MapFragment").addToBackStack("MapFragment").commit();

    }

    public void uploadPath(Path path)
    {
        AccessToken at =  AccessToken.getCurrentAccessToken();
        authToken = at.getToken().toString();
        BackendAPICall api = new BackendAPICall();
        api.addPath(path,authToken);


    }
    public static String[] stringToStringArray(String pathLocations)
    {

            pathLocations  = pathLocations.replaceAll("\\[","");
            pathLocations  = pathLocations.replaceAll("\\]","");
            pathLocations = pathLocations.replaceAll(" ","");
            String[] pathLocationArray = pathLocations.split(",");
        return pathLocationArray;

    }


}
