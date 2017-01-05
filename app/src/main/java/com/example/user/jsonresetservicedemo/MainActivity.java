package com.example.user.jsonresetservicedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity
{

    /** Called when the activity is first created. */

    EditText ed1;
    EditText ed2;
    EditText ed3;
    EditText ed4;

    Button btn_click;

    StringBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_click = (Button)findViewById(R.id.button1);

        btn_click.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // TODO Auto-generated method stub

				/*
				{

					"destination_addresses" : [ "Pimpri Chinchwad, Maharashtra, India" ],

					 "origin_addresses" : [ "Pune, Maharashtra, India" ],

					 "rows" : [

									{ "elements" :
														[

																{
									  									"distance" : { "text" : "15.1 km", "value" : 15094 },
				                        		  						"duration" : { "text" : "24 mins", "value" : 1456	},
				 					  									 "status" : "OK"
																}

														]

						              }

						     ],
				        "status" : "OK"
				}

*/


                ed1 = (EditText) findViewById(R.id.editText1);
                ed2 = (EditText) findViewById(R.id.editText2);

                ed3 =(EditText)findViewById(R.id.editText3);
                ed4 =(EditText)findViewById(R.id.editText4);



                JsonAsync ja = new JsonAsync();
                ja.execute();




            }
        });


    }



    public class JsonAsync extends AsyncTask<Void, Void, StringBuilder>
    {

        ProgressDialog dialog;


        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(MainActivity.this, "", "Loading Data...");

        }

        @Override
        protected StringBuilder doInBackground(Void... params)
        {
            // TODO Auto-generated method stub
            System.out.println("In doinbackground");


            builder = new StringBuilder();



            System.out.println("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ed1.getText().toString()+"&destinations="+ ed2.getText().toString()+"&mode=driving&sensor=false");

            HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ed1.getText().toString()+"&destinations="+ ed2.getText().toString()+"&mode=driving&sensor=false");
            HttpClient client = new DefaultHttpClient();


            try
            {
                HttpResponse response = client.execute(httpGet);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                System.out.println("before status");

                if (statusCode == 200)

                {
                    System.out.println("In status");

                    HttpEntity entity = response.getEntity();

                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line);
                        System.out.println("in while");

                    }
                }

                else
                {
                    Log.e(MainActivity.class.toString(), "Failed to download file");
                }
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }



            //For dialog we create a new thread


            return builder;



        }

        @Override
        protected void onPostExecute(StringBuilder result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();

            showResult();
        }



    }

    private void showResult()
    {
        try
        {
            JSONObject	 jsonObject = new JSONObject(builder.toString());

            System.out.println(jsonObject);

            //to access Array rows use below statement
            JSONArray rowArray = jsonObject.getJSONArray("rows");

            //now pass entire array to string variable
            String elestr = rowArray.getString(0);

            // then pass string to once again to json object
            JSONObject	 jsonObjectele = new JSONObject(elestr);

            //now elements array extract from the string & pass to the array
            JSONArray elementArray = jsonObjectele.getJSONArray("elements");

            System.out.println(elementArray);

            //array contain object extract value and assign to string variable
            String eledetails1,eledetails2;
            JSONObject  textelement1,textelement2;

            for(int i=0;i<elementArray.length();i++)
            {

                eledetails1 = elementArray.getJSONObject(i).getString("distance").toString();
                eledetails2 = elementArray.getJSONObject(i).getString("duration").toString();
                if(!eledetails1.equals(null))
                {
                    textelement1 = new JSONObject(eledetails1);
                    String  km=  textelement1.getString("text").toString();
                    ed3.setText(km);
                }
                if(!eledetails2.equals(null))
                {
                    textelement2 = new JSONObject(eledetails2);
                    String  time=  textelement2.getString("text").toString();
                    ed4.setText(time);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
