package com.ezcorporate.Authentications;


import android.annotation.TargetApi;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Others.SplashScreen;
import com.ezcorporate.R;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    Button btnLogIn;
    EditText etEmail, etPassword;
    String Url,macadd;
    Dialog progressdialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        init();
        setListener();
    }

    private void setListener() {
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String un=etEmail.getText().toString();
                String pas=etPassword.getText().toString();
                if(un.equals("")){
                    etEmail.setError("");
                    etEmail.requestFocus();
                }else if(pas.equals("")){
                    etPassword.setError("");
                    etPassword.requestFocus();
                }else {
                    if(CheckConnectivity.checkInternetConnection(LoginActivity.this)){
                        progressdialog=CheckConnectivity.dialogForProgres(LoginActivity.this);
                        macadd=getMacAddr();
                        logInService(un,pas,macadd);
                        Log.i("macc", macadd);
                    }else {
                        Toast.makeText(LoginActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

    }

    private void init() {
        btnLogIn = findViewById(R.id.btn_email_signin);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
    }

    public void logInService(final String uName, final String pass, final String macadd){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_LOG_IN, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                     JSONObject jsonObject = new JSONObject(response);
                    Log.i("jsonobject", ""+jsonObject.getString("User_Ver"));
                    JSONArray jsonArray=jsonObject.getJSONArray("User_Ver");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String reslut= jsonObject1.getString("Result");
                        if(reslut.equals("Login Successfully")){
                            Log.i("result", reslut);
                            String id= jsonObject1.getString("ID");
                            String user= jsonObject1.getString("admin_user");
                            SharedPrefManager.getInstance(getApplicationContext()).userDetail(id,user,macadd);
                            SharedPrefManager.getInstance(getApplicationContext()).setVCCValue("yes");
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            Log.i("detail", id+"/"+user+"/"+macadd+"/"+Url);
                            finish();
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                        }else if(reslut.equals("Invalid Password")){
                            Log.i("result", reslut);
                            Toast.makeText(LoginActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();

                        }else if(reslut.equals("Invalid Mac Address")){
                            Log.i("result", reslut);
                            Toast.makeText(LoginActivity.this,"Device Not Recognized",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.i("result", reslut);
                        }
                        progressdialog.dismiss();
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
                Log.i("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Username", uName);
                params.put("Password", pass);
                params.put("Macadd", macadd);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_left,R.anim.out_right);
    }
}
