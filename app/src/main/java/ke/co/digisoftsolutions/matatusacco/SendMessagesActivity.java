package ke.co.digisoftsolutions.matatusacco;
//imports
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SendMessagesActivity extends AppCompatActivity {
    TextView  getDeviceidTxt, updateDateTxt, CurrentDateTxt;
    private EditText editTextComments, txtContact, editTextIdno;
    String comments, txtContactText, idno,  GetIDText, CurrentDateText;
    private Button btnAddNewCategory;
    final Context context = this;
    //  private Spinner spinnerFood,spinnerFood2;
    JSONObject jsonObject, data;
    JSONArray dataArray;
    SessionManager session;
    JSONParser jsonParser;
    ArrayList < NameValuePair > postParameters;
    String uId;
    //private ArrayList<Category> categoriesList;
    //
    ProgressDialog pDialog;
    ///
    private String URL_NEW_CATEGORY  = "http://192.168.0.16/MatatuSacco/feedback.php";
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_send_messages);
        btnAddNewCategory = findViewById(R.id.buttonSendMessage);
        CurrentDateTxt.setVisibility(View.INVISIBLE);
        getDeviceidTxt.setVisibility(View.INVISIBLE);

        //
        editTextIdno=  findViewById(R.id.editTextIdno);
        editTextComments =  findViewById(R.id.editTextComments);

        long date = System.currentTimeMillis();
        //
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
        String dateString = sdf.format(date);
        CurrentDateTxt.setText(dateString);
        //--------------------------- get phone id--------------------
        @SuppressLint("HardwareIds") String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        Log.d("Android", "Android ID : " + android_id);
        getDeviceidTxt.setText(android_id);
        //  // Add new category click event
        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.buttonSendMessage:
                        if (isOnline(SendMessagesActivity.this))
                        {
                            idno = editTextIdno.getText().toString();
                            comments = editTextComments.getText().toString();
                            txtContactText = txtContact.getText().toString();
                            CurrentDateText = CurrentDateTxt.getText().toString();
                            GetIDText = getDeviceidTxt.getText().toString();

                            if (idno.trim().length() == 0)
                                Toast.makeText(getApplicationContext(), "Please enter your Id No ", Toast.LENGTH_SHORT)
                                        .show();
                            else if (comments.trim().length() == 0)
                                Toast.makeText(getApplicationContext(), "Please enter your Query", Toast.LENGTH_SHORT)
                                        .show();
                            else if (txtContactText.trim()
                                    .length() == 0)
                                Toast.makeText(getApplicationContext(), "Please enter your mobile no", Toast.LENGTH_SHORT)
                                        .show();

                            else if (!isOnline(SendMessagesActivity.this))
                                Toast.makeText(SendMessagesActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG)
                                        .show();
                            else
                                // Call Async task to create new category
                                new AddNewCategory()
                                        .execute(idno, comments, txtContactText, CurrentDateText, GetIDText);
                                editTextIdno.setText("");
                                editTextComments.setText("");
                                txtContact.setText("");
                                Toast.makeText(getApplicationContext(), "Sending Successfully", Toast.LENGTH_LONG)
                                        .show();
                            break;
                        }
                        else
                        {
                            // Internet connection is not present
                            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                }
            }
            //code to check online details
            private boolean isOnline(Context mContext) {

                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                return netInfo != null && netInfo.isConnectedOrConnecting();
            }
            //Close code that check online details
        });
    }
    //-------code for Insert in php main-------------
    @SuppressLint("StaticFieldLeak")
    private class AddNewCategory extends AsyncTask < String, Void, Void > {
        boolean isNewCategoryCreated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SendMessagesActivity.this);
            pDialog.setMessage("Please Wait..");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(String...arg) {
            //-------code for Insert in php 4-------------
            String newIdno = arg[0];
            String newcontact = arg[2];
            String newmessage = arg[3];
            String newmessagedate = arg[4];
            String newDviceID = arg[1];
             // Preparing post params
            List < NameValuePair > params = new ArrayList<>();
            //table name
            params.add(new BasicNameValuePair("name", newIdno));
            params.add(new BasicNameValuePair("contact", newcontact));
            params.add(new BasicNameValuePair("message", newmessage));
            params.add(new BasicNameValuePair("messagedate", newmessagedate));
            params.add(new BasicNameValuePair("deviceID", newDviceID));
            //  //-------Close-------------
            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(URL_NEW_CATEGORY, ServiceHandler.POST, params);
            Log.d("Create Response: ", "> " + json);
            if (json != null) {
                try{
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isNewCategoryCreated = true;
                    }
                    else{
                        Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }
         //-------close-------------
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isNewCategoryCreated) {
                runOnUiThread(() -> {});
            }
        }
    }
}
