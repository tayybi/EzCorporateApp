package com.ezcorporate.Authentications;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ezcorporate.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String  customLatlong="";
    ImageView goBack;
    Button don;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE},10);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
        setListener();
    }

    private void setListener() {
        don.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customLatlong.equals("")){
                    Toast.makeText(MapsActivity.this,"Select Location",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("latlong", customLatlong);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.in_left,R.anim.out_right);
                }
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_left,R.anim.out_right);
            }
        });
    }

    private void init() {
        goBack=findViewById(R.id.go_back);
        don=findViewById(R.id.don);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                customLatlong=""+latLng;
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                try {
                    LatLng loc = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,15));
                        mMap.addMarker(new MarkerOptions().position(loc));
                        Log.i("locbutton",""+loc);
                        customLatlong= String.valueOf(loc);

                }catch (Exception  e){
                    Log.i("error",""+e);
                }

                return true;
            }
        });

    }
}
