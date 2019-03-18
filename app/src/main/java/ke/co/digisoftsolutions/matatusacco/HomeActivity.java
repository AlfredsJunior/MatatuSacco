package ke.co.digisoftsolutions.matatusacco;
//imports
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {
    String result = null;
    InputStream is = null;
    StringBuilder sb = null;
    ArrayList<String> al = new ArrayList<String>();
    ArrayList<String> al1 = new ArrayList<String>();
    ArrayList<String> al2 = new ArrayList<String>();
    //	int responseCode;
    //int listItemCount=0;
    ListView listview ;
    Button button;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTheme(Color.WHITE);
        setTitleColor(Color.rgb(0x74, 0, 0x37));
        setTitle(R.string.rukagina_sacco);
        requestWindowFeature(Window.FEATURE_RIGHT_ICON);
        setContentView(R.layout.activity_home);

        //set onclicklistener
        button = findViewById(R.id.sendMessage);
        button.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent i = new Intent(getApplicationContext(),SendMessagesActivity.class);
            startActivity(i);
        });

        listview = findViewById(R.id.listView1);
        new LoadData().execute();
        //Fetch last 5 transactions
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.0.16/payments.php");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            Log.e("log_tag" , "connection success ");
            //   Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("log_tag" , "Error in http connection " + e.toString());
            Toast.makeText(getApplicationContext() , "Connection fail" , Toast.LENGTH_SHORT).show();

        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is , StandardCharsets.ISO_8859_1) , 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                //  Toast.makeText(getApplicationContext(), "Input Reading pass", Toast.LENGTH_SHORT).show();
            }
            is.close();

            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag" , "Error converting result " + e.toString());
            Toast.makeText(getApplicationContext() , " Input reading fail" , Toast.LENGTH_SHORT).show();

        }

        //parse json data
        try {

            JSONArray jArray = new JSONArray(result);
            String re = jArray.getString(jArray.length() - 1);

            TableLayout tv = findViewById(R.id.table);
            tv.removeAllViewsInLayout();
            int flag = 1;

            for (int i = -1; i < jArray.length() - 1; i++) {
                TableRow tr = new TableRow(HomeActivity.this);

                tr.setLayoutParams(new LayoutParams(
                        LayoutParams.FILL_PARENT ,
                        LayoutParams.WRAP_CONTENT));


                if (flag == 1) {

                    TextView b6 = new TextView(HomeActivity.this);
                    b6.setText("Type of Payment");
                    b6.setTextColor(Color.BLUE);
                    b6.setTextSize(15);
                    tr.addView(b6);


                    TextView b19 = new TextView(HomeActivity.this);
                    b19.setPadding(10 , 0 , 0 , 0);
                    b19.setTextSize(15);
                    b19.setText("Amount");
                    b19.setTextColor(Color.BLUE);
                    tr.addView(b19);

                    TextView b29 = new TextView(HomeActivity.this);
                    b29.setPadding(10 , 0 , 0 , 0);
                    b29.setText("Date");
                    b29.setTextColor(Color.BLUE);
                    b29.setTextSize(15);
                    tr.addView(b29);

                    tv.addView(tr);

                    final View vline = new View(HomeActivity.this);
                    vline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT , 2));
                    vline.setBackgroundColor(Color.BLUE);

                    tv.addView(vline);
                    flag = 0;

                } else {

                    JSONObject json_data = jArray.getJSONObject(i);
                    Log.i("log_tag" , "id: " + json_data.getInt("f1") +
                            ", Username: " + json_data.getString("f2") +
                            ", No: " + json_data.getInt("f3"));

                    TextView b = new TextView(HomeActivity.this);
                    String stime = String.valueOf(json_data.getInt("f1"));
                    b.setText(stime);
                    b.setTextColor(Color.RED);
                    b.setTextSize(15);
                    tr.addView(b);

                    TextView b1 = new TextView(HomeActivity.this);
                    b1.setPadding(10 , 0 , 0 , 0);
                    b1.setTextSize(15);
                    String stime1 = json_data.getString("f2");
                    b1.setText(stime1);
                    b1.setTextColor(Color.WHITE);
                    tr.addView(b1);

                    TextView b2 = new TextView(HomeActivity.this);
                    b2.setPadding(10 , 0 , 0 , 0);
                    String stime2 = String.valueOf(json_data.getInt("f3"));
                    b2.setText(stime2);
                    b2.setTextColor(Color.RED);
                    b2.setTextSize(15);
                    tr.addView(b2);

                    tv.addView(tr);

                    final View vline1 = new View(HomeActivity.this);
                    vline1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT , 1));
                    vline1.setBackgroundColor(Color.WHITE);
                    tv.addView(vline1);
                }
            }
        } catch (JSONException e) {
            Log.e("log_tag" , "Error parsing data " + e.toString());
            Toast.makeText(getApplicationContext() , "JsonArray fail" , Toast.LENGTH_SHORT).show();
        }
    }
    //fetch the details, shares, savings &loan
    private class LoadData extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        @Override
        // can use UI thread here
        protected void onPreExecute() {
            this.progressDialog = ProgressDialog.show(HomeActivity.this, ""," Loading...");
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... params) {
            /*
            TODO Auto-generated method stub
            HTTP post
            */
            try {
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                HttpClient httpclient = new DefaultHttpClient();
                try{
                    HttpPost httppost = new HttpPost("http://192.168.0.16/MatatuSacco/selectdetails.php");
                    StringEntity se = new StringEntity("envelope", HTTP.UTF_8);
                    httppost.setEntity(se);
                    HttpParams httpParameters = new BasicHttpParams();
                    // Set the timeout in milliseconds until a connection is established.
                    int timeoutConnection = 3000;
                    HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                    // Set the default socket timeout (SO_TIMEOUT)
                    // in milliseconds which is the timeout for waiting for data.
                    int timeoutSocket = 3000;
                    HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                //buffered reader
                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, StandardCharsets.ISO_8859_1), 80);
                    sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

            } catch (ParseException e) {
                //	Log.e("log_tag", "Error in http connection" + e.toString());
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                //	Log.e("log_tag", "Error in http connection" + e.toString());
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}