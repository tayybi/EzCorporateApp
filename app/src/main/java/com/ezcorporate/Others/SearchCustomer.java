package com.ezcorporate.Others;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;

public class SearchCustomer extends AppCompatActivity {

    String URL_SEARCH="";
    String type="";
    Button search;
    ImageView ivGoBack;
    EditText etCNIC,etPhone;
    Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);
        URL_SEARCH =getIntent().getExtras().getString("SEARCHURL");
        type =getIntent().getExtras().getString("TYPE");
        Log.i("urlsearch",URL_SEARCH);
        init();
        setListener();
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    String cnc=etCNIC.getText().toString();
                    String phn=etPhone.getText().toString();
                    if(cnc.equals("")&&phn.equals("")){
                        Toast.makeText(SearchCustomer.this,"Enter CNIC/PHONE",Toast.LENGTH_SHORT).show();
                    }else if(!cnc.equals("")&&!phn.equals("")){
                        Toast.makeText(SearchCustomer.this,"plese search by one field",Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog=CheckConnectivity.dialogForProgres(SearchCustomer.this);
                        searchUser(cnc,phn);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        });
    }

    private void init() {

        search=findViewById(R.id.search_user);
        ivGoBack=findViewById(R.id.go_back);
        etCNIC=findViewById(R.id.et_customer_cnic);
        etPhone=findViewById(R.id.et_customer_phone);
    }

    public void searchUser(final String cnic, final String phonee){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_SEARCH, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("customer_info");
                    if(type.equals("bioCustomer")){
                        for (int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String customerName= jsonObject1.getString("customer_name");
                            String customerbio= jsonObject1.getString("customer_bio");
                            String customercnic= jsonObject1.getString("customer_cnic");
                            if(customercnic.equals("")||customercnic.equals("null")){
                                Toast.makeText(SearchCustomer.this,"No Record Found",Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent=new Intent();
                                intent.putExtra("personName",customerName);
                                intent.putExtra("personCnic",customercnic);
                                intent.putExtra("personBio",customerbio);
                                setResult(Activity.RESULT_OK,intent);
                                finish();
                                overridePendingTransition(R.anim.in_left, R.anim.out_right);
                                break;
                            }
                        }
                    }else if(type.equals("simpleCustomer")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String customerId = jsonObject1.getString("customer_id");
                            String customerName = jsonObject1.getString("customer_name");
                            String message = jsonObject1.getString("message");
                            if (message.equals("Date Retrieved")) {
                                Log.i("customerid", "" + customerId);
                                Intent intent = new Intent();
                                intent.putExtra("personCode", customerId);
                                intent.putExtra("personName", customerName);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                                overridePendingTransition(R.anim.in_left, R.anim.out_right);
                                break;
                            } else if (message.equals("Invalid CNIC")) {
                                Toast.makeText(SearchCustomer.this, "Invalid CNIC", Toast.LENGTH_SHORT).show();
                            } else if (message.equals("Invalid Mobile")) {
                                Toast.makeText(SearchCustomer.this, "Invalid Mobile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("CCNIC", cnic);
                params.put("CMOB", phonee);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED,getIntent());
        finish();
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
        super.onBackPressed();
    }
}
