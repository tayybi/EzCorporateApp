package com.ezcorporate.Authentications;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompnayInfo extends AppCompatActivity {

    Button btnUrlDone;
    EditText etCompanyUrl;
    String Url;
    Dialog progressdialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_company_info);

        init();
        setListener();

    }

    private void setListener() {

        btnUrlDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String s=etCompanyUrl.getText().toString();
                if(s.equals("")){
                    etCompanyUrl.setError("");
                    etCompanyUrl.requestFocus();
                }else{

                    if(CheckConnectivity.checkInternetConnection(CompnayInfo.this)){
                        progressdialog= com.ezcorporate.Authentications.CheckConnectivity.dialogForProgres(CompnayInfo.this);
                        getCompanyNameFromeServer("https://"+s);
                    }else {
                        Toast.makeText(CompnayInfo.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void init() {
        btnUrlDone = findViewById(R.id.btn_companu_url_done);
        etCompanyUrl = findViewById(R.id.et_company_url);
    }

    private void getCompanyNameFromeServer(final String curl) {
        Log.i("url", curl+ServiceUrls.COMPANY_WEB_LINK);

        StringRequest stringRequestforurl = new StringRequest(Request.Method.GET, curl+ServiceUrls.COMPANY_WEB_LINK, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                String cName;
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.i("jsonobject", "" + jsonObject.getString("company_info"));
                    JSONArray jsonArray = jsonObject.getJSONArray("company_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        cName = jsonObject1.getString("companyname");
                        Log.i("companyName", cName);
                        if(!cName.equals("")){
                            Url=curl;
                            SharedPrefManager.getInstance(getApplicationContext()).setCompanyInfo(Url);
                            startActivity(new Intent(CompnayInfo.this,LoginActivity.class));
                            finish();
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            progressdialog.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialog.dismiss();
                Log.i("error", "" + error);
                error.printStackTrace();
            }
        });
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequestforurl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_left,R.anim.out_right);
    }
}