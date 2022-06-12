package com.ezcorporate.REM;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.LeadsList;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;
import com.ezcorporate.URUpersona.GetReaderActivity;
import com.ezcorporate.URUpersona.Globals;
import com.ezcorporate.URUpersona.VerifyInDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.URUpersona.GetReaderActivity;
import com.ezcorporate.URUpersona.Globals;
import com.ezcorporate.URUpersona.VerifyInDB;

import static com.ezcorporate.CRM.LeadsList.leadlist;
import static com.ezcorporate.REM.ListForTransfor.listForTransfor;

public class Transfer extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    Button btnVerifyFrom,btnVerifyTo,btnDon;
    ImageView ivGoBack,ivTransfrorFrom,ivTransforTo;
    TextView tvNameFrom,tvCNICFrom,tvNameTo,tvCNICTo,tvProductIt;
    String picF="";
    String picT="";
    String bioF="";
    String bioT="";
    Dialog  progressDialog;
    String statusBioF="";
    String statusBioT="";
    int requestCodeF=202;
    int requestCodeT=201;
    String checkstatus="";
    String agrementId;
    SwipeRefreshLayout swipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        Log.i("productid",URL_PREFIX_DOMAIN+"URL+InActivity"+agrementId);
        try {
            agrementId=getIntent().getExtras().getString("agrementId");
            progressDialog=CheckConnectivity.dialogForProgres(this);
            getTransfoerData(agrementId);
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
        init();
        setlistener();
    }

    private void setlistener() {
        btnVerifyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(m_deviceName.equals("")){
                        launchGetReader();
                    }else {
                        verifyF();
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }


            }
        });
        btnVerifyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(m_deviceName.equals("")) {
                        launchGetReader();
                    }else {
                        verifyT();
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        btnDon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    if(!statusBioF.equals("updated")){
                        Toast.makeText(Transfer.this,"Verify First Person",Toast.LENGTH_SHORT).show();
                    }else if(!statusBioT.equals("updated")){
                        Toast.makeText(Transfer.this,"Verify Second Person",Toast.LENGTH_SHORT).show();
                    }else {
                        if(CheckConnectivity.checkInternetConnection(Transfer.this)){
                            progressDialog=CheckConnectivity.dialogForProgres(Transfer.this);
                            transforUpdate(agrementId);
                        }else {
                            Toast.makeText(Transfer.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
                overridePendingTransition(R.anim.in_left,R.anim.out_right);
            }
        });
    }

    private void init() {
        btnVerifyFrom=findViewById(R.id.btn_verify_from);
        btnVerifyTo=findViewById(R.id.btn_verify_to);
        btnDon=findViewById(R.id.don);

        ivGoBack=findViewById(R.id.go_back);
        ivTransforTo=findViewById(R.id.iv_transfor_to);
        ivTransfrorFrom=findViewById(R.id.iv_transfor_from);

        tvNameFrom=findViewById(R.id.tv_name_from);
        tvCNICFrom=findViewById(R.id.tv_cnic_from);
        tvNameTo=findViewById(R.id.tv_name_to);
        tvCNICTo=findViewById(R.id.tv_cnic_to);
        tvProductIt=findViewById(R.id.tv_agrement_id);

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                getTransfoerData(agrementId);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void getTransfoerData(final String tid) {
        Log.i("check",tid);
        StringRequest stringRequestforurl = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_INFO_FOR_TRANSOFER, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("rem_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String namef = jsonObject1.getString("customer_namef");
                        String namet = jsonObject1.getString("customer_namet");
                        String cnicf = jsonObject1.getString("cust_cnicf");
                        String cnict = jsonObject1.getString("cust_cnict");
                        String producid = jsonObject1.getString("item_complete_name");
                        bioF= jsonObject1.getString("cust_biof");
                        bioT= jsonObject1.getString("cust_biot");
                        picF= jsonObject1.getString("cust_picf");
                        picT= jsonObject1.getString("cust_pict");
                        if(namef!=null&&bioF!=null){
                            tvNameFrom.setText(namef);
                            tvNameTo.setText(namet);
                            tvCNICFrom.setText(cnicf);
                            tvCNICTo.setText(cnict);
                            tvProductIt.setText(producid);
                            Log.i("checkpix",picF+"///"+picT);
                        }else{
                            Toast.makeText(Transfer.this,"Required Data Not Found",Toast.LENGTH_SHORT).show();
                        }


                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("error", "" + error);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("AgrID", tid);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequestforurl);
    }

    private void transforUpdate(final String tid) {
        Log.i("check",tid);
        StringRequest stringRequestforurl = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_TRANSFOR_BIO_UPDATE, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("rem_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String message = jsonObject1.getString("message");
                        if(message.equals("Data has been Updated")){

                            listForTransfor.finish();
                            startActivity(new Intent(Transfer.this,ListForTransfor.class));
                            finish();
                            overridePendingTransition(R.anim.in_left,R.anim.out_right);
                        }
                    }

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("error", "" + error);
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("AgrID", tid);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequestforurl);
    }

    public Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==GENERAL_ACTIVITY_RESULT){
                Globals.ClearLastBitmap();
                m_deviceName = (String) data.getExtras().getString("device_name");
                if((m_deviceName != null) && !m_deviceName.isEmpty())
                {
                    try {
                        Context applContext = getApplicationContext();
                        m_reader = Globals.getInstance().getReader(m_deviceName, applContext);

                        {
                            PendingIntent mPermissionIntent;
                            mPermissionIntent = PendingIntent.getBroadcast(applContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                            applContext.registerReceiver(mUsbReceiver, filter);

                            if(DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(applContext, mPermissionIntent, m_deviceName))
                            {
                                CheckDevice();
                            }
                        }
                    } catch (UareUException e1)
                    {
                        displayReaderNotFound();
                    }
                    catch (DPFPDDUsbException e)
                    {
                        displayReaderNotFound();
                    }
                } else
                {
                    displayReaderNotFound();
                }
            }else if (requestCode==requestCodeF){
                try {
                    m_deviceName = (String) data.getExtras().getString("device_name");
                    checkstatus=data.getExtras().getString("status");
                    if(checkstatus.equals("updated")){
                        statusBioF=checkstatus;
                        if(picF.equals(null)){

                        }else {
                            ivTransfrorFrom.setImageBitmap(StringToBitMap(picF));
                        }
                        checkstatus="";
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }else if(requestCode==requestCodeT){
                try {
                    m_deviceName = (String) data.getExtras().getString("device_name");
                    checkstatus = data.getExtras().getString("status");
                    if(checkstatus.equals("updated")){
                        statusBioT=checkstatus;
                        if(picT.equals("")){

                        }else {
                            ivTransforTo.setImageBitmap(StringToBitMap(picT));
                        }
                        checkstatus="";
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        }

    }

    ///////////////////////UareU DATA /////////////////////////////////

    private final int GENERAL_ACTIVITY_RESULT = 1;
    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
    private String m_deviceName = "";
    Reader m_reader;

    protected void launchGetReader() {
        Intent i = new Intent(Transfer.this, GetReaderActivity.class);
        i.putExtra("device_name", m_deviceName);
        startActivityForResult(i, GENERAL_ACTIVITY_RESULT);
    }

    protected void verifyF(){
        try {
            if(bioF.equals("")){
                Toast.makeText(Transfer.this,"Fid Not Found",Toast.LENGTH_SHORT).show();
            }else {
                Intent i = new Intent(Transfer.this, VerifyInDB.class);
                i.putExtra("device_name", m_deviceName);
                i.putExtra("fIdExisting", bioF);
                startActivityForResult(i, requestCodeF);
                overridePendingTransition(R.anim.in_right,R.anim.out_left);
            }
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
    }

    protected void verifyT(){
        try {
            if(bioT.equals("")){
                Toast.makeText(Transfer.this,"Fid Not Found",Toast.LENGTH_SHORT).show();
            }else {
                Intent i = new Intent(Transfer.this, VerifyInDB.class);
                i.putExtra("device_name", m_deviceName);
                i.putExtra("fIdExisting", bioT);
                startActivityForResult(i, requestCodeT);
                overridePendingTransition(R.anim.in_right,R.anim.out_left);
            }
        }catch (Exception e){
            Log.i("Exception",""+e);
        }

    }

    protected void setButtonsEnabled_Capture(Boolean enabled) {
        btnVerifyTo.setEnabled(enabled);
        btnVerifyFrom.setEnabled(enabled);
    }

    protected void CheckDevice() {
        try
        {
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            Reader.Capabilities cap = m_reader.GetCapabilities();
            setButtonsEnabled_Capture(cap.can_capture);
            m_reader.Close();
        }
        catch (UareUException e1)
        {
            displayReaderNotFound();
        }
    }

    private void displayReaderNotFound() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Reader Not Found");
        alertDialogBuilder.setMessage("Plug in a reader and try again.").setCancelable(false).setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        if(device != null)
                        {
                            CheckDevice();
                        }
                    }
                }
            }
        }
    };




} ////main
