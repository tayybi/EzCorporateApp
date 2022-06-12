package com.ezcorporate.DefinationInfoCustomer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezcorporate.Authentications.MapsActivity;

import com.ezcorporate.Authentications.MapsActivity;

public class NewCustomer extends AppCompatActivity {

    ImageView ivGoBack,ivPhoto;
    Button registerUser,bioMatricVerif;
    ImageButton ibLocation,ibPhoto,ibFingerData;
    EditText name,contactPersone,address,phoneNo,otherPhone;
    TextView tvLocation,tvFinger;
    int requestCodePhoto=301;
    int reguestCodeLocation=302;
    int requestCodeFinderId=303;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_new_customer);
        init();
        setListener();
    }

    private void setListener() {
        ibLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new  Intent(NewCustomer.this, MapsActivity.class);
               startActivityForResult(intent,reguestCodeLocation);
                overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
            }
        });
        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(NewCustomer.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(NewCustomer.this, new String[] {Manifest.permission.CAMERA}, requestCodePhoto);
                }
                else {
                    Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,requestCodePhoto);
                    overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                }
            }
        });
        ibFingerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
            }
        });
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
    }

    private void init() {
        ivGoBack=findViewById(com.ezcorporate.R.id.go_back);
        ivPhoto=findViewById(com.ezcorporate.R.id.iv_photo);
        registerUser=findViewById(com.ezcorporate.R.id.register_user);
        name=findViewById(com.ezcorporate.R.id.name);
        contactPersone=findViewById(com.ezcorporate.R.id.contact_person);
        address=findViewById(com.ezcorporate.R.id.address);
        phoneNo=findViewById(com.ezcorporate.R.id.phone_no);
        otherPhone=findViewById(com.ezcorporate.R.id.other_phone);

        ibFingerData=findViewById(com.ezcorporate.R.id.ibtn_fingerprint);
        ibPhoto=findViewById(com.ezcorporate.R.id.ibtn_photo);
        ibLocation=findViewById(com.ezcorporate.R.id.ibtn_map);

        tvFinger=findViewById(com.ezcorporate.R.id.tv_finger_id);
        tvLocation=findViewById(com.ezcorporate.R.id.tv_latlong);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if (requestCode==reguestCodeLocation){
                String s=data.getExtras().getString("latlong");
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText(s);
            }else if(requestCode==requestCodeFinderId){
                tvFinger.setVisibility(View.VISIBLE);
                tvFinger.setText("F id Done");
            }else if(requestCode==requestCodePhoto){
                Bitmap image=(Bitmap) data.getExtras().get("data");
                ivPhoto.setVisibility(View.VISIBLE);
                ivPhoto.setImageBitmap(image);
            }
            else if(requestCode==RESULT_CANCELED){
                Toast.makeText(NewCustomer.this,"You didn't choose location",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
