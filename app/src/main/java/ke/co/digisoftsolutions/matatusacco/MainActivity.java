package ke.co.digisoftsolutions.matatusacco;
//imports
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class MainActivity extends AppCompatActivity {
    //Progress Dialog
    private ProgressDialog pDialog;
    //json parser
    final JSONParser jsonParser = new JSONParser();
    //editext && button
    private EditText editTextIdno, editTextPin, editTextConfirmPin;
    private Button buttonSignUp;
    private String idno, pin, confirmPin;

    //URL to create a new User
    private static final String url_create_user = "http://192.168.0.16/MatatuSacco/register.php";
    //private static final String url_detail_user = "http://192.168.0.16/MatatuSacco/userdetails.php";

    //JSON Node names
    private static final String TAG_SUCCESS = "success";
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIdno = findViewById(R.id.editTextIdno);
        editTextPin = findViewById(R.id.editTextPin);
        editTextConfirmPin = findViewById(R.id.editTextConfirmPin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        //sign up-Button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
         @Override
           public void onClick(View v) {
            //Get Id No
            idno = editTextIdno.getText().toString().trim();
            //Get Pin
            pin = editTextPin.getText().toString().trim();
            //Get Confirmation Pin
            confirmPin = editTextConfirmPin.getText().toString().trim();

            if (editTextIdno.equals("") || editTextPin.equals("") || editTextConfirmPin.equals("")) {

                Toast.makeText(MainActivity.this ,
                        "You need to type in Your Id No/ Pin/ Confirm Pin" , Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(i);
                }

            else {
                //creating new users in background thread
                new CreateNewUser().execute();

            }}
        });
    }
        /**
         * Background Async Task to registering new user
         * */
        public class CreateNewUser extends AsyncTask<String, String, String> {

            //Before starting background thread Show Progress Dialog
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //commented by me  pDialog = new ProgressDialog(HomeActivity.this);
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Creating new User...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

            //Creating new User
            @Override
            protected String doInBackground(String... args) {


                //Get serialnumber
                String serialnumber = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!Objects.equals(Build.SERIAL , Build.UNKNOWN)) serialnumber = Build.SERIAL;
                    else
                        serialnumber = Settings.Secure.getString(getContentResolver() , Settings.Secure.ANDROID_ID);
                }
                //Get Macaddress
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macaddress = wInfo.getMacAddress();
                //Get Id No
                @SuppressLint("WrongThread") String idno = editTextIdno.getText().toString();
                //Get Pin
                @SuppressLint("WrongThread") String pin = editTextPin.getText().toString();
                //Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("serialnumber" , serialnumber));
                params.add(new BasicNameValuePair("macaddress" , macaddress));
                params.add(new BasicNameValuePair("idno" , idno));
                params.add(new BasicNameValuePair("pin" , pin));

                //getting JSON Object
                //Note that create user urls accepts Post method
                JSONObject json = jsonParser.makeHttpRequest(url_create_user ,
                        "POST" , params);

                // check log cat fro response
                Log.d("Create Response" , json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        //successful created User
                        Intent i = new Intent(getApplicationContext() , HomeActivity.class);
                        startActivity(i);
                        //closing this screen
                        finish();
                    } else {
                        //String Definition
                        String error444 = "Failed to create User. Maybe the User exists already, please try another one!";
                        //New Intent
                        Intent i = new Intent(getApplicationContext() , MainActivity.class);
                        //String to Intent
                        i.putExtra("error" , error444);
                        //Start ErrorActivity
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             * **/
            protected void onPostExecute(String file_url) {
                //dismiss the dialog once done
                pDialog.dismiss();
            }

        }
    }





