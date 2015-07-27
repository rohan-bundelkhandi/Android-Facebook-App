package com.itraveller.myapp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

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

public class MainActivity extends Activity {

    private static String APP_ID = "777860655655658"; // Replace with your App ID

    // Instance of Facebook Class
    private Facebook facebook = new Facebook(APP_ID);
    private AsyncFacebookRunner mAsyncRunner;
    String FILENAME = "AndroidSSO_data";
    private SharedPreferences mPrefs;

    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    // Buttons
    Button btnFbLogin;
    Button btnFbGetProfile;
    Button btnPostToWall;
    Button btnShowAccessTokens;
    Button btnFbLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        cd=new ConnectionDetector(getApplicationContext());

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);


        btnFbLogin = (Button) findViewById(R.id.btn_fblogin);
        btnFbGetProfile = (Button) findViewById(R.id.btn_get_profile);
        btnPostToWall = (Button) findViewById(R.id.btn_fb_post_to_wall);
        btnShowAccessTokens = (Button) findViewById(R.id.btn_show_access_tokens);
        btnFbLogout=(Button) findViewById(R.id.btn_fblogout);
        mAsyncRunner = new AsyncFacebookRunner(facebook);



        /**
         * Login button Click event
         * */
        btnFbLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                isInternetPresent = cd.isConnectingToInternet();

                // check for Internet status
                if (isInternetPresent) {
                    // Internet Connection is Present
                    // make HTTP requests
                    Log.d("Image Button", "button Clicked");
                    loginToFacebook();

                } else {
                    // Internet connection is not present
                    // Ask user to connect to Internet

                    showAlertDialog(MainActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }


            }
        });

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
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void loginToFacebook() {

        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);

        if (access_token != null) {
            facebook.setAccessToken(access_token);

            btnFbLogin.setVisibility(View.INVISIBLE);

            // Making get profile button visible
            btnFbGetProfile.setVisibility(View.VISIBLE);

            // Making post to wall visible
            btnPostToWall.setVisibility(View.VISIBLE);

            // Making show access tokens button visible
            btnShowAccessTokens.setVisibility(View.VISIBLE);

            btnFbLogout.setVisibility(View.VISIBLE);
        }

        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        if (!facebook.isSessionValid()) {
            facebook.authorize(this,
                    new String[] { "email", "public_profile" },
                    new DialogListener() {

                        @Override
                        public void onCancel() {
                            // Function to handle cancel event
                        }

                        @Override
                        public void onComplete(Bundle values) {
                            // Function to handle complete event
                            // Edit Preferences and update facebook acess_token
                            SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires",
                                    facebook.getAccessExpires());
                            editor.commit();

                            // Making Login button invisible
                            btnFbLogin.setVisibility(View.INVISIBLE);


                            // Making logout Button visible
                            btnFbGetProfile.setVisibility(View.VISIBLE);

                            // Making post to wall visible
                            btnPostToWall.setVisibility(View.VISIBLE);
                            // Making show access tokens button visible
                            btnShowAccessTokens.setVisibility(View.VISIBLE);

                            btnFbLogout.setVisibility(View.VISIBLE);
                            Intent i=new Intent(getApplicationContext(),EntryActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void onError(DialogError error) {
                            // Function to handle error

                        }

                        @Override
                        public void onFacebookError(FacebookError fberror) {
                            // Function to handle Facebook errors

                        }

                    });
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }


    /**
     * Get Profile information by making request to Facebook Graph API
     * */
    public void getProfileInformation() {
        // get information about the currently logged in user
        mAsyncRunner.request("me", new RequestListener() {

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
            public void onComplete(String responseString, Object state) {

                try {
                    JSONObject response = new JSONObject(responseString);
                    final String name = response.getString("name");
                    final String email = response.getString("email");
                    final String birthday = response.getString("birthday");

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Hello " + name + ", if today is " + birthday + ", then Happy Birthday! " +
                                    "If not, then I'll sign " + email + " up for spam", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    return;
                }


            }
        });

        // get the logged-in user's friends
        mAsyncRunner.request("me/friends", new RequestListener() {

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
        });

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
                Log.i("logout", "malformed");
            }

            @Override
            public void onIOException(IOException e, Object state) {
                Log.i("logout", "IO");
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
                Log.i("logout", "file not found");
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
                Log.i("logout", "error");
            }

            @Override
            public void onComplete(String response, Object state) {


                SharedPreferences.Editor editor = mPrefs.edit();
                editor.remove("access_token");
                editor.remove("access_expires");
                editor.commit();
                Intent i = new Intent(getApplicationContext(), TempActivity.class);
                startActivity(i);
            }
        });
    }

}