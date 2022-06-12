/*
 * File: 		EnrollmentActivity.java
 * Created:		2013/05/03
 *
 * copyright (c) 2013 DigitalPersona Inc.
 */

package com.ezcorporate.URUpersona;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
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
import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Fmd.Format;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.Reader.Priority;
import com.digitalpersona.uareu.UareUGlobal;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.DefinationInfoCustomer.ExistingCustomer;
import com.ezcorporate.R;

public class EnrollNew extends Activity
{
	private ImageView m_back;
	private String m_deviceName = "";
	private String m_enginError;

	private Reader m_reader = null;
	private int m_DPI = 0;
	private Bitmap m_bitmap = null;
	private ImageView m_imgView;
	private TextView m_selectedDevice;
	private boolean m_reset = false;

	private TextView m_text;
	private Engine m_engine = null;
	private int m_current_fmds_count = 0;
	private boolean m_success = false;
	private Fmd m_enrollment_fmd = null;
	EnrollmentCallback enrollThread = null;
	private Reader.CaptureResult cap_result = null;
	DataBaseHelper dataBaseHelper;
	Button doneBioMatric;
	byte[] data;
	String finalId="";

	private void initializeActivity()
	{
		dataBaseHelper=new DataBaseHelper(this);
		doneBioMatric=findViewById(R.id.done_bio_verif);
		m_enginError = "";
		m_selectedDevice = (TextView) findViewById(R.id.selected_device);
		Log.i("idinEnroll",m_deviceName);
		m_deviceName = getIntent().getExtras().getString("device_name");
		m_selectedDevice.setText("Device: " + m_deviceName);

		m_imgView = (ImageView) findViewById(R.id.bitmap_image);
		m_bitmap = Globals.GetLastBitmap();
		if (m_bitmap == null) m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black);
		m_imgView.setImageBitmap(m_bitmap);
		m_back = (ImageView) findViewById(R.id.go_back);
		m_back.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				onBackPressed();
				overridePendingTransition(R.anim.in_left,R.anim.out_right);
			}
		});
		m_text = (TextView) findViewById(R.id.text);
		UpdateGUI();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_engine);
		initializeActivity();
		dataBaseHelper=new DataBaseHelper(this);
		doneBioMatric.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if(finalId.equals("")){
						Toast.makeText(EnrollNew.this,"Enter Customer Id",Toast.LENGTH_SHORT).show();
					}else {
						dataBaseHelper.insertFmdInDB(finalId);
						Log.i("fmd","register"+finalId);
						Intent intent=new Intent();
						intent.putExtra("fingerid",finalId);
						setResult(Activity.RESULT_OK,intent);
						onBackPressed();
						finish();
						overridePendingTransition(R.anim.in_left,R.anim.out_right);
					}
				}catch (Exception e){

				}

			}
		});

		// initiliaze dp sdk
		try
		{
			//Context applContext = getApplicationContext();
			m_reader = Globals.getInstance().getReader(m_deviceName, EnrollNew.this);
			m_reader.Open(Priority.EXCLUSIVE);
			m_DPI = Globals.GetFirstDPI(m_reader);
			m_engine = UareUGlobal.GetEngine();

			Log.i("UareUSampleJava", "Initilized sdk");
		}
		catch (Exception e)
		{
			Log.i("UareUSampleJava", "error during init of reader"+e);
			m_deviceName = "";
			onBackPressed();
			return;
		}

		// loop capture on a separate thread to avoid freezing the UI
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					m_current_fmds_count = 0;
					m_reset = false;
					enrollThread = new EnrollmentCallback(m_reader, m_engine);
					while (!m_reset)
					{
						try
						{

							m_enrollment_fmd = m_engine.CreateEnrollmentFmd(Format.ANSI_378_2004, enrollThread);
							if (m_success = (m_enrollment_fmd != null))
							{
								data = m_enrollment_fmd.getData();
								finalId= Base64.encodeToString(data, Base64.DEFAULT);
								m_current_fmds_count = 0;	// reset count on success
                                m_reset=true;
							}
						}
						catch (Exception e)
						{
							m_current_fmds_count = 0;
						}
					}
				}
				catch (Exception e)
				{
					if(!m_reset)
					{
						Log.i("UareUSampleJava", "error during capture");
						m_deviceName = "";
						onBackPressed();
					}
				}
			}
		}).start();
	}

	public void UpdateGUI()
	{
			m_imgView.setImageBitmap(m_bitmap);
			m_imgView.invalidate();
			m_text.setText(""+m_current_fmds_count);
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
		super.onBackPressed();
	}

	// called when orientation has changed to manually destroy and recreate activity
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_engine);
		initializeActivity();
	}

	public class EnrollmentCallback extends Thread implements Engine.EnrollmentCallback
	{
		private Reader m_reader = null;
		private Engine m_engine = null;

		public EnrollmentCallback(Reader reader, Engine engine)
		{
			m_reader = reader;
			m_engine = engine;
		}

		// callback function is called by dp sdk to retrieve fmds until a null is returned
		@Override
		public PreEnrollmentFmd GetFmd(Format format)
		{
			PreEnrollmentFmd result = null;
			while (!m_reset)
			{
				try
				{
					cap_result = m_reader.Capture(Fid.Format.ANSI_381_2004, Globals.DefaultImageProcessing, m_DPI, -1);
				}
				catch (Exception e)
				{
					Log.i("UareUSampleJava", "error during capture: " + e.toString());
					m_deviceName = "";
					onBackPressed();
				}

				// an error occurred
				if (cap_result == null || cap_result.image == null) continue;

				try
				{
					m_enginError = "";
					m_bitmap = Globals.GetBitmapFromRaw(cap_result.image.getViews()[0].getImageData(), cap_result.image.getViews()[0].getWidth(), cap_result.image.getViews()[0].getHeight());
					PreEnrollmentFmd prefmd = new PreEnrollmentFmd();
					prefmd.fmd = m_engine.CreateFmd(cap_result.image, Format.ANSI_378_2004);
					prefmd.view_index = 0;
					m_current_fmds_count++;
					if(m_current_fmds_count==4){
                        m_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.done_fingerprint);
                    }
					Log.i("fmds",""+m_current_fmds_count);

					result =  prefmd;
					break;
				}
				catch (Exception e)
				{
					m_enginError = e.toString();
					Log.i("UareUSampleJava", "Engine error: " + e.toString());
				}
			}

			runOnUiThread(new Runnable()
			{
				@Override public void run()
				{
					UpdateGUI();
				}
			});
			return result;
		}
	}


}
