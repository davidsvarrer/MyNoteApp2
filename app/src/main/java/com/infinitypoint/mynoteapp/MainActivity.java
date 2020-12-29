package com.infinitypoint.mynoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /*
    * What we will cover in this tutorial
    * (i) Types of datatype in java
    * (ii)Functions/Method
    * (iii)Conversion from one datatype to another
    * (iv)Designing our user interface(XML), working with the following widgets:
    *      (a) using Different layouts eg Relative layouts, Linearlayout and so on
    *      (b) working with Buttons
    *      (c) EditText
    *      (d) ImageView etc.
    * (v) Use of libraries
    * (vi)Control statement in java eg if-else statement etc
    * (vii) Variables
    * (viii) Error Handling in java*/

    Button btnSubmit,btnhtml;
    EditText etName,passward;
    RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting specific widget from our activity_main xml layout using their unique ids (identifiers)
        btnSubmit = findViewById(R.id.btnSubmit);
        btnhtml = findViewById(R.id.btnhtml);
        etName = findViewById(R.id.etName);
        passward = findViewById(R.id.passward);

        requestQueue = Volley.newRequestQueue(this);


        // We set our Button to respond to the clicks made by the user using 'setOnClickListener()' function
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here we are getting what the user has entered in the EditText widget on our activity_main xml
                // we convert the text to a String datatype using 'toString()' function
                // Then we remove all the white spaces  from value entered by the user using 'trim()' function
                //Checking what the user has entered
                //We use control statement to do this for this case we will use if-else statement

                 String nameValueEntered = etName.getText().toString().trim();
                 String passward2 =  passward.getText().toString().trim();
                if(nameValueEntered.equals("") ||passward2.equals("")){
                    Toast.makeText(MainActivity.this, "You have not entered anything.Please try again", Toast.LENGTH_SHORT).show();
                }else{
                    //We now use a function/method here
                    addUserRecordToTheDatabase(nameValueEntered);
                }



            }
        });
        btnhtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,htmlActivity.class));
            }
        });
    }

    public  void addUserRecordToTheDatabase(final String userName){
         String url = "http://choop.one/Training/registerUser.php";

        StringRequest stringRequestToServer= new StringRequest(Request.Method.POST, url,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {

                       try {
                           JSONArray jsonArrayResponses = new JSONArray(response);
                           JSONObject jsonObject = jsonArrayResponses.getJSONObject(0);
                           String result = jsonObject.getString("code");
                           if (result.equals("success")){
                               Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                           }else if (result.equals("fail")){
                               Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                           }else {
                               Toast.makeText(MainActivity.this, "Error :try again later."+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                           }
                       } catch (JSONException e) {
                           Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               Toast.makeText(MainActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
           }
       }) {
           @Override
           protected Map<String, String> getParams() throws AuthFailureError {
               Map<String,String> params = new HashMap<String,String>();
                params.put("name",userName);
               return params;
           }
       };
       requestQueue.add(stringRequestToServer);

    }



}