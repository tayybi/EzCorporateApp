package com.ezcorporate.DefinationInfoCustomer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.ezcorporate.Authentications.MapsActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.SearchCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.URUpersona.EnrollNew;
import com.ezcorporate.URUpersona.GetReaderActivity;
import com.ezcorporate.URUpersona.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.MapsActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.SearchCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.URUpersona.EnrollNew;
import com.ezcorporate.URUpersona.GetReaderActivity;
import com.ezcorporate.URUpersona.Globals;

public class ExistingCustomer extends AppCompatActivity {

    String URL_PREFIX_DOMAIN = "";
    Button btnSearchCustomer,btnDone;
    ImageButton ibLocationGet, ibTakePhoto, ibTakeFingerPrint;
    ImageView ivGoBack, ivPhoto;
    TextView tvcustomerCode, tvcustomerName, tvpickedLocation, tvpickedFinger;
    public int photoRequestCode = 4;
    public int fingerIdRequestCode = 2;
    public int locationRequestCode = 3;
    public int serchDataRequestCode = 5;
    String finalLatLong = "no";
    String finalFID = "no";
    String finalPix = "no";
    Dialog progressdialog;
    Context context;
    Uri file;
    File fileasli;
    private String pictureFilePath;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_existing_customer);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        System.setProperty("DPTRACE_ON", "1");
        URL_PREFIX_DOMAIN = SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        init();
        setListener();
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try{
                    String pCode=tvcustomerCode.getText().toString();
                    if (pCode.equals("")) {
                        Toast.makeText(context, "Select Customer First", Toast.LENGTH_SHORT).show();
                    }else {
                        progressdialog = CheckConnectivity.dialogForProgres(context);
                        Log.i("uploadeddata", finalFID + "/" + finalLatLong + "/" + finalPix);
                        uploadExistingCustomerInfo(pCode, finalPix, finalLatLong, finalFID);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        btnSearchCustomer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(ExistingCustomer.this, SearchCustomer.class);
                    intent.putExtra("SEARCHURL",URL_PREFIX_DOMAIN+ServiceUrls.URL_SEARCH_REGISTERD_CUSTOMER);
                    intent.putExtra("TYPE","simpleCustomer");
                    startActivityForResult(intent,serchDataRequestCode);
                    overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        ibTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ExistingCustomer.this, new String[]{Manifest.permission.CAMERA}, 100);
                    } else {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(cameraIntent, photoRequestCode);
                            overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                        }

                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }


            }
        });
        ibTakeFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (m_deviceName.equals("")) {
                        launchGetReader();
                    } else {
                        enRollNew();
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        ibLocationGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ContextCompat.checkSelfPermission(ExistingCustomer.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ExistingCustomer.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(ExistingCustomer.this, "Please Turn On GPS", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(ExistingCustomer.this, MapsActivity.class);
                        startActivityForResult(intent, locationRequestCode);
                        overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

    }

    private void init() {

        context = this;

        btnDone = findViewById(com.ezcorporate.R.id.register_user);
        btnSearchCustomer = findViewById(com.ezcorporate.R.id.btn_searh);

        ivGoBack = findViewById(com.ezcorporate.R.id.go_back);
        ivPhoto = findViewById(com.ezcorporate.R.id.iv_photo);

        ibLocationGet = findViewById(com.ezcorporate.R.id.ibtn_map);
        ibTakeFingerPrint = findViewById(com.ezcorporate.R.id.ibtn_fingerprint);
        ibTakePhoto = findViewById(com.ezcorporate.R.id.ibtn_photo);

        tvpickedFinger = findViewById(com.ezcorporate.R.id.tv_finger_id);
        tvpickedLocation = findViewById(com.ezcorporate.R.id.tv_latlong);
        tvcustomerCode = findViewById(com.ezcorporate.R.id.tv_customer_code);
        tvcustomerName = findViewById(com.ezcorporate.R.id.tv_customer_name);

    }

    public void uploadExistingCustomerInfo(final String cid, final String cpic, final String cmap, final String cbio) {
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_UPLOAD_EXISTING_CUSTOMER_RECORD, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String jsonObjresult = jsonObject.getString("customer_info");
                    Log.i("jsonobject", jsonObjresult);
                    JSONArray jsonArray = jsonObject.getJSONArray("customer_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String messagea = jsonObject1.getString("message");
                        if (messagea.equals("Date Updated")) {
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressdialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialog.dismiss();
                Log.i("error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("CID", cid);
                params.put("CPicture", cpic);
                params.put("CMap", cmap);
                params.put("CBio", cbio);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);

    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GENERAL_ACTIVITY_RESULT) {
                Globals.ClearLastBitmap();
                m_deviceName = (String) data.getExtras().getString("device_name");
                if ((m_deviceName != null) && !m_deviceName.isEmpty()) {

                    try {
                        Context applContext = getApplicationContext();
                        m_reader = Globals.getInstance().getReader(m_deviceName, applContext);

                        {
                            PendingIntent mPermissionIntent;
                            mPermissionIntent = PendingIntent.getBroadcast(applContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                            applContext.registerReceiver(mUsbReceiver, filter);

                            if (DPFPDDUsbHost.DPFPDDUsbCheckAndRequestPermissions(applContext, mPermissionIntent, m_deviceName)) {
                                CheckDevice();
                            }
                        }
                    } catch (UareUException e1) {
                        displayReaderNotFound();
                    } catch (DPFPDDUsbException e) {
                        displayReaderNotFound();
                    }
                } else {
                    displayReaderNotFound();
                }
            } else if (requestCode == photoRequestCode) {
                try {
                    if(data.getData()!=null){
                    Bitmap image=(Bitmap) data.getExtras().get("data");
                        image=RotateBitmap(image,90);
                        ivPhoto.setVisibility(View.VISIBLE);
                        ivPhoto.setImageBitmap(image);
                        finalPix=encodeTobase64(image);
                        Log.i("image","found="+finalPix);
                    }else{
                        Log.i("image","Not found");
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            } else if (requestCode == locationRequestCode) {
                try {
                    String s = data.getExtras().getString("latlong");
                    finalLatLong = s;
                    tvpickedLocation.setText(s);
                    tvpickedLocation.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            } else if (requestCode == fingerIdRequestCode) {
                try {
                    String fid = data.getExtras().getString("fingerid");
                    tvpickedFinger.setText("Finger Id Done");
                    finalFID = fid;
                    tvpickedFinger.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
            else if (requestCode == serchDataRequestCode) {
                try {
                    String idCode = data.getExtras().getString("personCode");
                    String pName = data.getExtras().getString("personName");
                    tvcustomerCode.setText(idCode);
                    tvcustomerName.setText(pName);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        }

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    ///////////////////////UareU DATA /////////////////////////////////

    private final int GENERAL_ACTIVITY_RESULT = 1;
    private static final String ACTION_USB_PERMISSION = "com.digitalpersona.uareu.dpfpddusbhost.USB_PERMISSION";
    private String m_deviceName = "";
    Reader m_reader;

    protected void launchGetReader() {
        Intent i = new Intent(ExistingCustomer.this, GetReaderActivity.class);
        i.putExtra("device_name", m_deviceName);
        startActivityForResult(i, GENERAL_ACTIVITY_RESULT);
    }

    protected void enRollNew() {
        Intent i = new Intent(ExistingCustomer.this, EnrollNew.class);
        i.putExtra("device_name", m_deviceName);
        startActivityForResult(i, fingerIdRequestCode);
        overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
    }

    protected void setButtonsEnabled_Capture(Boolean enabled) {
        ibTakeFingerPrint.setEnabled(enabled);
    }

    protected void CheckDevice() {
        try {
            m_reader.Open(Reader.Priority.EXCLUSIVE);
            Reader.Capabilities cap = m_reader.GetCapabilities();
            setButtonsEnabled_Capture(cap.can_capture);
            m_reader.Close();
        } catch (UareUException e1) {
            displayReaderNotFound();
        }
    }

    private void displayReaderNotFound() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Reader Not Found");
        alertDialogBuilder.setMessage("Plug in a reader and try again.").setCancelable(false).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            CheckDevice();
                        }
                    }
                }
            }
        }
    };

}///main
