/*
 * File: 		IdentificationActivity.java
 * Created:		2013/05/03
 *
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.ezcorporate.URUpersona;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.Candidate;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUGlobal;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.R;

public class VerifyInDB extends Activity
{
	private ImageView m_back;
	Button btnDone;
	private String m_deviceName = "";

	private String m_enginError;
	private Reader m_reader = null;
	private int m_DPI = 0;
	private Bitmap m_bitmap = null;
	private ImageView m_imgView;
	private TextView m_selectedDevice;
	private TextView m_title;
	private boolean m_reset = false;

	private String m_textString;
	private String m_text_conclusionString;
	private Engine m_engine = null;
	private Candidate[] results = null;
	private Fmd m_fmd1 = null;
	private boolean m_first = true;
	private int m_score = -1;
	boolean m=true;
	String fmdFromServer="";
	Fmd m_emp= null;
	String uId;
	String checkMatching="";
	boolean uiUpdate=false;


	Cursor cursor,cursor2;
	DataBaseHelper dataBaseHelper;
	byte[] data;
	private Reader.CaptureResult cap_result = null;

	private void initializeActivity()
	{
		dataBaseHelper=new DataBaseHelper(this);
		m_title = (TextView) findViewById(R.id.title);
		m_enginError = "";
		//dbData();
		fmdFromServer=getIntent().getExtras().getString("fIdExisting");
		Log.i("fmdfromapi",fmdFromServer);
		m_selectedDevice = (TextView) findViewById(R.id.selected_device);
		m_deviceName = getIntent().getExtras().getString("device_name");

		m_selectedDevice.setText("Device: " + m_deviceName);

		m_imgView = (ImageView) findViewById(R.id.bitmap_image);
		m_bitmap = Globals.GetLastBitmap();
		if (m_bitmap == null) m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
		m_imgView.setImageBitmap(m_bitmap);
		m_back = (ImageView) findViewById(R.id.go_back);
		btnDone = findViewById(R.id.done_bio_verif);

		m_back.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		btnDone.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if(checkMatching.equals("")){
					Toast.makeText(VerifyInDB.this,"Not Verified",Toast.LENGTH_SHORT).show();
				}else {
					onDonpress();
				}
			}
		});

		//UpdateGUI();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_thumb);
		m_textString = "Place your thumb on the reader";
		initializeActivity();
		// initiliaze dp sdk
		try
		{
			Context applContext = getApplicationContext();
			m_reader = Globals.getInstance().getReader(m_deviceName, applContext);
			m_reader.Open(Priority.EXCLUSIVE);
			m_DPI = Globals.GetFirstDPI(m_reader);
			m_engine = UareUGlobal.GetEngine();

		} catch (Exception e)
		{
			Log.w("UareUSampleJava", "error during init of reader");
			m_deviceName = "";
			onBackPressed();
			return;
		}
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				m_reset = false;
				while (!m_reset) {
					try {
						cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
					} catch (Exception e) {
						if (!m_reset) {
							Log.w("UareUSampleJava", "error during capture: " + e.toString());
							m_deviceName = "";
							onBackPressed();
						}
					}

					// an error occurred
					if (cap_result == null || cap_result.image == null) continue;

					try {
						m_enginError = "";

						// save bitmap image locally
						m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());

						Fmd m_temp = m_engine.CreateFmd(cap_result.image, Fmd.Format.ANSI_378_2004);
						data = Base64.decode(fmdFromServer, Base64.DEFAULT);
						m_emp = UareUGlobal.GetImporter().ImportFmd(data, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
						m_score = m_engine.Compare(m_emp, 0, m_temp, 0);
						Log.i("IdFmd/Score", m_emp+"//"+m_score );
						if (m_score < (0x7FFFFFFF / 100000)) {
							uiUpdate=true;
						}
						else {
							uiUpdate=false;
						}

//						cursor2=dataBaseHelper.showTableData(AllFields.TABLE_OF_FINGRECORD_NEW);
//						if (cursor2.moveToFirst()) {
//							do {
//								String fmId1 =cursor2.getString(cursor2.getColumnIndex(AllFields.FIDN1));
//								data = Base64.decode(fmId1, Base64.DEFAULT);
//								m_emp = UareUGlobal.GetImporter().ImportFmd(data, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
//								m_score = m_engine.Compare(m_emp, 0, m_temp, 0);
//								Log.i("IdFmd/Score", m_emp+"//"+m_score );
//								if (m_score < (0x7FFFFFFF / 100000)) {
//									uId =cursor2.getString(cursor2.getColumnIndex(AllFields.UIDN1));
//									Log.i("result", uId+"="+fmId1 );
//									uiUpdate=true;
//									break;
//								}
//								else {
//									uiUpdate=false;
//								}
//							} while (cursor2.moveToNext());
//
//						}


//						results = m_engine.Identify(m_temp, 0, m_fmds_temp, 100000, 2);
//						Log.i("result", "" + results);
//						if (results.length != 0) {
//							m_score = m_engine.Compare(m_fmds_temp[results[0].fmd_index], 0, m_temp, 0);
//							Log.i("score", "" + m_score);
//							m=true;
//
//
//						} else {
//							m=false;
//							m_score = -1;
//
//						}

//						}

					} catch (Exception e) {
						m_enginError = e.toString();
						Log.w("UareUSampleJava", "Engine error: " + e.toString());
					}

					m_text_conclusionString = Globals.QualityToString(cap_result);

					if (!m_enginError.isEmpty()) {
						m_text_conclusionString = "Engine: " + m_enginError;
					}
					runOnUiThread(new Runnable()
					{
						@Override
                        public void run()
						{

								UpdateGUI();

						}
					});

				}
			}

		}).start();
	}

	public void dbData(){
		cursor2=dataBaseHelper.showTableData(AllFields.TABLE_OF_FINGRECORD_NEW);
		if (cursor2.moveToFirst()) {
			do {
				String fmuid = cursor2.getString(cursor2.getColumnIndex(AllFields.UIDN1));
				String fmId1 = cursor2.getString(cursor2.getColumnIndex(AllFields.FIDN1));
				data = Base64.decode(fmId1, Base64.DEFAULT);
				//try {
				//	m_emp = UareUGlobal.GetImporter().ImportFmd(data, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
					Log.i("AllData", fmuid + "=" + fmId1);

				//} catch (UareUException e) {
				//	e.printStackTrace();
				//}

			} while (cursor2.moveToNext());
		}
	}

	public void UpdateGUI()
	{

		if(uiUpdate==true){
			m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.done_fingerprint);
			checkMatching="updated";
		}else {
			checkMatching="";
		}
		m_imgView.setImageBitmap(m_bitmap);
		m_imgView.invalidate();

	}


	@Override
	public void onBackPressed()
	{
		try
		{
			m_reset = true;
			try { m_reader.CancelCapture(); } catch (Exception e) {}
			m_reader.Close();
		}
		catch (Exception e)
		{
			Log.w("UareUSampleJava", "error during reader shutdown");
		}

		Intent i = new Intent();
		i.putExtra("device_name", m_deviceName);
		i.putExtra("status","nothing");
		setResult(Activity.RESULT_OK, i);
		finish();
		overridePendingTransition(R.anim.in_left,R.anim.out_right);
	}
	public void onDonpress()
	{
		try
		{
			m_reset = true;
			try { m_reader.CancelCapture(); } catch (Exception e) {}
			m_reader.Close();
		}
		catch (Exception e)
		{
			Log.w("UareUSampleJava", "error during reader shutdown");
		}
		Intent i=new Intent();
		i.putExtra("status",checkMatching);
		i.putExtra("device_name", m_deviceName);
		setResult(Activity.RESULT_OK, i);
		finish();
		overridePendingTransition(R.anim.in_left,R.anim.out_right);
	}

	// called when orientation has changed to manually destroy and recreate activity
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_engine);
		initializeActivity();
	}
}