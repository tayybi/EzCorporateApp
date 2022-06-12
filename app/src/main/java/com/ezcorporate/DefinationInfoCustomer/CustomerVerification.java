package com.ezcorporate.DefinationInfoCustomer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbException;
import com.digitalpersona.uareu.dpfpddusbhost.DPFPDDUsbHost;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.SearchCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.R;
import com.ezcorporate.URUpersona.GetReaderActivity;
import com.ezcorporate.URUpersona.Globals;
import com.ezcorporate.URUpersona.VerifyInDB;

public class CustomerVerification extends AppCompatActivity {

    String URL_PREFIX_DOMAIN = "";
    Button btnSearchCustomer,btnDone,btnVerification;
    ImageView ivGoBack, ivPhoto;
    TextView tvcustomerCode, tvcustomerName,tvStatus;
    public int verificationRequestCode = 2;
    public int serchDataRequestCode = 5;
    Dialog progressdialog;
    String bio="";
    Context context;
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_verification);
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
                overridePendingTransition(R.anim.in_left, R.anim.out_right);
            }
        });

        btnSearchCustomer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent=new Intent(CustomerVerification.this, SearchCustomer.class);
                    intent.putExtra("SEARCHURL",URL_PREFIX_DOMAIN+ServiceUrls.URL_SEARCH_REGISTERD_CUSTOMER_USER);
                    intent.putExtra("TYPE","bioCustomer");
                    startActivityForResult(intent,serchDataRequestCode);
                    overridePendingTransition(R.anim.in_right,R.anim.out_left);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        btnVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (m_deviceName.equals("")) {
                        launchGetReader();
                    } else {
                        verifycustomer();
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
    }

    private void init() {

        context = this;

        btnDone = findViewById(R.id.register_user);
        btnSearchCustomer = findViewById(R.id.btn_searh);
        btnVerification = findViewById(R.id.btn_verify_customer);

        ivGoBack = findViewById(R.id.go_back);
        ivPhoto = findViewById(R.id.iv_photo);

        tvcustomerCode = findViewById(R.id.tv_customer_code);
        tvcustomerName = findViewById(R.id.tv_customer_name);
        tvStatus = findViewById(R.id.tv_status);

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
            } else if (requestCode == verificationRequestCode) {
                try {
                    String fid = data.getExtras().getString("status");
                    Log.i("fiddta",fid);
                    if(fid.equals("updated")){
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("Verified");
                    }else {
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("Not Verified");
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
            else if (requestCode == serchDataRequestCode) {
                try {
                    String idCode = data.getExtras().getString("personCnic");
                    String pName = data.getExtras().getString("personName");
                    bio = data.getExtras().getString("personBio");
                    Log.i("bio",bio);
                    tvcustomerCode.setText(idCode);
                    tvcustomerName.setText(pName);
                    if(bio.equals("")||bio.equals("null")){
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("Bio Not Found");
                    }else {
                        tvStatus.setVisibility(View.VISIBLE);
                        tvStatus.setText("Bio Found");
                    }

                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        }
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
        Intent i = new Intent(CustomerVerification.this, GetReaderActivity.class);
        i.putExtra("device_name", m_deviceName);
        startActivityForResult(i, GENERAL_ACTIVITY_RESULT);
    }

    protected void verifycustomer(){
        try {
            if(bio.equals("")|| bio.equals("null")){
                Toast.makeText(CustomerVerification.this,"Fid Not Found",Toast.LENGTH_SHORT).show();
            }else {
                Intent i = new Intent(CustomerVerification.this, VerifyInDB.class);
                i.putExtra("device_name", m_deviceName);
                i.putExtra("fIdExisting", bio);
                startActivityForResult(i, verificationRequestCode);
                overridePendingTransition(R.anim.in_right,R.anim.out_left);
            }
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
    }

    protected void setButtonsEnabled_Capture(Boolean enabled) {
        btnVerification.setEnabled(enabled);
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
