package com.itraveller.myapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.Util;
import com.itraveller.myapp.R;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class EntryActivity extends Activity {

    private static String APP_ID = "777860655655658"; // Replace with your App ID

    // Instance of Facebook Class
    private Facebook facebook = new Facebook(APP_ID);
    private AsyncFacebookRunner mAsyncRunner;
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;



    // Connection detector class


    // Buttons

    Button btnFbGetProfile;
    Button btnPostToWall;
    Button btnShowAccessTokens;
    Button btnFbLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);


        btnFbGetProfile = (Button) findViewById(R.id.btn_get_profile);
        btnPostToWall = (Button) findViewById(R.id.btn_fb_post_to_wall);
        btnShowAccessTokens = (Button) findViewById(R.id.btn_show_access_tokens);
        btnFbLogout=(Button) findViewById(R.id.btn_fblogout);
        mAsyncRunner = new AsyncFacebookRunner(facebook);


        btnFbGetProfile.setVisibility(View.VISIBLE);

        // Making post to wall visible
        btnPostToWall.setVisibility(View.VISIBLE);

        // Making show access tokens button visible
        btnShowAccessTokens.setVisibility(View.VISIBLE);

        btnFbLogout.setVisibility(View.VISIBLE);
        /**
         * Login button Click event
         * */


        /**
         * Getting facebook Profile info
         * */
        btnFbGetProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getProfileInformation();
            }
        });

        /**
         * Posting to Facebook Wall
         * */
        btnPostToWall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                postToWall();
            }
        });

        /**
         * Showing Access Tokens
         * */
        btnShowAccessTokens.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showAccessTokens();
            }
        });


        btnFbLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFromFacebook();
            }
        });
        // Listening to register new account link

    }



        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }


    /**
     * Get Profile information by making request to Facebook Graph API
     * */
    public void getProfileInformation() {
        String method = "GET";
        Bundle params = new Bundle();
                /*
                 * this will revoke 'publish_stream' permission
                 * Note: If you don't specify a permission then this will de-authorize the application completely.
                 */
        params.putString("permission", "email");
        params.putString("permission","public_profile");

        mAsyncRunner.request("/me/permissions", params, method, new RequestListener() {

            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
            }

            @Override
            public void onIOException(IOException e, Object state) {
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }

            @Override
            public void onComplete(String response, Object state) {
                Log.d("Profile", response);
                String json = response;
                try {
                    // Facebook Profile JSON data
                    JSONObject profile = new JSONObject(json);

                    // getting name of the user
                    final String name = profile.getString("name");

                    // getting email of the user
                    final String email = profile.getString("email");
                    Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Name: " + name + "\nEmail: " + email, Toast.LENGTH_LONG).show();
                        }

                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, null);


    }

    /**
     * Function to post to facebook wall
     * */
    public void postToWall() {
        // post on user's wall.
        facebook.dialog(this, "feed", new DialogListener() {

            @Override
            public void onFacebookError(FacebookError e) {
            }

            @Override
            public void onError(DialogError e) {
            }

            @Override
            public void onComplete(Bundle values) {
            }

            @Override
            public void onCancel() {
            }
        });

    }

    /**
     * Function to show Access Tokens
     * */
    public void showAccessTokens() {
        String access_token = facebook.getAccessToken();

        Toast.makeText(getApplicationContext(),
                "Access Token: " + access_token, Toast.LENGTH_LONG).show();
    }

    /**
     * Function to Logout user from Facebook
     * */
    public void logoutFromFacebook() {

        String method = "DELETE";
        Bundle params = new Bundle();
                /*
                 * this will revoke 'publish_stream' permission
                 * Note: If you don't specify a permission then this will de-authorize the application completely.
                 */
        params.putString("permission", "email");
        params.putString("permission", "publish_checkins");
        params.putString("permission", "user_birthday");

        mAsyncRunner.request("/me/permissions", params, method, new RequestListener() {

            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
            }

            @Override
            public void onIOException(IOException e, Object state) {
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }

            @Override
            public void onComplete(String response, Object state) {
                response.charAt(0);
            }
        }, null);



        mAsyncRunner.logout(getApplicationContext(), new RequestListener() {

            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
                Log.i("logout","malformed");
            }

            @Override
            public void onIOException(IOException e, Object state) {
                Log.i("logout","IO");
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
                Log.i("logout","file not found");
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                Log.i("logout","error");
            }

            @Override
            public void onComplete(String response, Object state) {


                SharedPreferences.Editor editor = mPrefs.edit();
                editor.remove("access_token");
                editor.remove("access_expires");
                editor.commit();
                Intent i=new Intent(getApplicationContext(),TempActivity.class);
                startActivity(i);
            }
        });
    }


}