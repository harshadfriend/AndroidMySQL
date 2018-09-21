package karjatonline.androidmysql;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etName,etMobile;
    Button btnSubmit;
    JSONArray products = null;
    ArrayAdapter<String> contactList;
    String url = "https://wwwkarjatonlinecom.000webhostapp.com/add.php";
    String url2 = "https://wwwkarjatonlinecom.000webhostapp.com/getdata.php";

    ListView listview;

    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName=findViewById(R.id.etName);
        etMobile=findViewById(R.id.etMobile);
        btnSubmit=findViewById(R.id.btnSubmit);

        listview=findViewById(R.id.lv);

        new CountDownTimer(10000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
            //    getJSON(url);
            }

            @Override
            public void onFinish() {

            }
        }.start();

        getJSON(url2);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList nameValuePairs = new ArrayList();

                nameValuePairs.add(new BasicNameValuePair("name",etName.getText().toString().trim()));

                nameValuePairs.add(new BasicNameValuePair("mobile",etMobile.getText().toString().trim()));


//        Log.d(“well2”, “msg”);
                StrictMode.setThreadPolicy(policy);

//        Log.d(“well3”, “msg”);
//http post
                try{
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpPost httppost = new HttpPost(url);

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
//            Log.d(“well4”, “msg”);

//            Log.e(“log_tag”, “connection success “);
                    //  Toast.makeText(getApplicationContext(), "Please Wait….", Toast.LENGTH_SHORT).show();
                }

                catch(Exception e)
                {
//            Log.e(“log_tag”, “Error in http connection “+e.toString());
                    Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
//            Log.d(“well5”, “msg”);

                }
                getJSON(url2);
            }
        });



    }

    private void getJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
     //           Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
              //  tv.setText(s);


                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] heroes = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            heroes[i] = obj.getString("name")+" "+obj.getString("mobile");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, heroes);
        listview.setAdapter(arrayAdapter);
    }
}

