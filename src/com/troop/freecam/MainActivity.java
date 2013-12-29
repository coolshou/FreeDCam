package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.troop.freecam.manager.Drawing.DrawingOverlaySurface;
import com.troop.freecam.manager.ManualSaturationManager;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.manager.interfaces.ParametersChangedInterface;
import com.troop.freecam.manager.sliders.ManualISO;
import com.troop.freecam.manager.interfaces.IStyleAbleSliderValueHasChanged;
import com.troop.freecam.utils.DeviceUtils;


import java.io.File;

public class MainActivity extends Activity implements ParametersChangedInterface
{

    //:::::::::::::::::::::::::Buttons::::::::::::::::::::::::::::::::::::::::;;

    Button ex_button_settings_view;
    Button ex_button_motion_frame_view;
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //::::::::::::::::::::::::::Layouts:::::::::::::::::::::::::::::::::::::::::
    LinearLayout ex_layout_quicksettings;

    FrameLayout ex_layout_settings;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::;:


    //::::::::::::::::::::::::::Seekbars:::::::::::::::::::::::::

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //::::::::::::::::::::::::::::::Imageviews::::::::::::::::::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //::::::::::::::::::::::::: Parameters ::::::::::::::::::::::
    int StateQuickSet = 0;
    int StateSettings = 0 ;
    int StateOnScreenInfo = 0;
    ManualISO slider;
    TextView values;


    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



	public CamPreview mPreview;
    public DrawingOverlaySurface drawSurface;
	public ImageButton shotButton;
    //*******************
	Camera.Parameters paras;
    SurfaceHolder holder;
    CameraManager camMan;
    //************************Text Views Add****************05-12-13
    TextView OnScreenBrightnessText;
    TextView OnScreenBrightnessValue;
    TextView OnScreenContrastText;
    TextView OnScreenContrastValue;
    TextView OnScreenEVText;
    TextView OnScreenEVValue;
    TextView OnScreenFlashText;
    TextView OnScreenFlashValue;
    TextView OnScreenEffectText;
    TextView OnScreenEffectValue;
    public TextView OnScreenFocusText;
    public TextView OnScreenFocusValue;
    TextView OnScreeISOText;
    TextView OnScreeISOValue;
    TextView OnScreeMeterText;
    public TextView OnScreeMeterValue;
    TextView OnScreenSaturationText;
    TextView OnScreeSaturationValue;
    TextView OnScreeSceneText;
    TextView OnScreeSceneValue;
    TextView OnScreenPictureText;
    TextView OnScreenPictureValue;
    TextView OnScreeSharpnessText;
    TextView OnScreenSharpnessValue;
    TextView OnScreenWBText;
    TextView OnScreenWBValue;

    //******************************************************

    public  ViewGroup appViewGroup;
    public SeekBar exposureSeekbar;
    public ImageButton thumbButton;


    public CheckBox manualExposure;
    TableRow exposureRow;

    public CheckBox manualShaprness;
    public CheckBox manualFocus;

    TableRow sharpnessRow;
    public SeekBar sharpnessSeekBar;
    TableLayout tableLayout;
    public TextView sharpnessTextView;
    public TextView exposureTextView;

    public  TextView contrastTextView;
    TableRow contrastRow;
    public CheckBox contrastcheckBox;
    public SeekBar contrastSeekBar;

    public TextView brightnessTextView;
    public SeekBar brightnessSeekBar;
    public SeekBar focusSeekBar;
    TableRow brightnessRow;
    TableRow focusRow;
    CheckBox brightnessCheckBox;

    public TextView saturationTextView;
    public SeekBar saturationSeekBar;
    public CheckBox saturationCheckBox;
    TableRow saturationRow;

    Button switchVideoPicture;

    SharedPreferences preferences;
    CheckBox upsidedown;
    public boolean recordVideo = false;

    CheckBox crop_box;

    int currentZoom = 0;
    SensorManager sensorManager;
    Sensor sensor;

    TextView recordingTimerTextView;
    RelativeLayout mainlayout;

    MyTimer recordTimer;

    CheckBox checkboxHDR;
    boolean HDRMode = false;

    View view;

    //private final int DEFAULT_SYSTEM_UI_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recordVideo = preferences.getBoolean("recordVideo", false);

        setContentView(R.layout.main);
        drawSurface = (DrawingOverlaySurface) findViewById(R.id.view);
		mPreview = (CamPreview) findViewById(R.id.camPreview1);
        mPreview.setKeepScreenOn(true);
        holder = mPreview.getHolder();
        camMan = new CameraManager(mPreview, this, preferences);
        camMan.parametersManager.setParametersChanged(this);

        mPreview.SetCameraManager(camMan);
        drawSurface.SetCameraManager(camMan);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //Sliders//
        values = (TextView)findViewById(R.id.textView);
        slider = (ManualISO)findViewById(R.id.viewSlider);

        slider.valueHasChanged = new IStyleAbleSliderValueHasChanged()
        {
            @Override
            public void ValueHasChanged(int value)
            {
                String s = value + "";
                values.setText(s);
            }
        };
        //end//

        initButtons();
        initMenu();
        recordTimer = new MyTimer(recordingTimerTextView);



        onScreenText(StateOnScreenInfo);



	}


    public void initMenu()
    {

        ex_layout_quicksettings = (LinearLayout)findViewById(R.id.LayoutQuickMenu);
        ex_layout_settings = (FrameLayout)findViewById(R.id.Settings);

        ex_button_settings_view = (Button)findViewById(R.id.buttonSettingsView);
        ex_button_settings_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (StateQuickSet == 0)
                {
                    ex_layout_quicksettings.setVisibility(View.VISIBLE);
                    findViewById(R.id.QuickISO).setVisibility(View.VISIBLE);
                    StateQuickSet = 1;
                }
                else
                {
                    ex_layout_quicksettings.setVisibility(View.INVISIBLE);
                    findViewById(R.id.QuickISO).setVisibility(View.INVISIBLE);
                    StateQuickSet = 0;
                }

            }


        });
        ex_button_settings_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean  onLongClick(View v) {
                if (StateSettings == 0) {
                    ex_layout_settings.setVisibility(View.VISIBLE);
                    StateSettings = 1;
                } else {
                    ex_layout_settings.setVisibility(View.INVISIBLE);
                    StateSettings = 0;
                }
                return true;

            }


        });

        if(!preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            crop_box.setVisibility(View.GONE);
        }
        else
        {
            crop_box.setChecked(true);
        }
    }


    public void initButtons()
    {

        shotButton = (ImageButton) findViewById(R.id.imageButton1);
        shotButton.setOnClickListener(shotListner);



        crop_box = (CheckBox)findViewById(R.id.checkBox_crop);
        crop_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (crop_box.isChecked())
                {
                    camMan.crop = true;
                    preferences.edit().putBoolean("crop", true).commit();
                }
                else
                {
                    camMan.crop = false;
                    preferences.edit().putBoolean("crop", false).commit();
                }
            }
        });

        thumbButton = (ImageButton)findViewById(R.id.imageButton_thumb);
        thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (camMan.lastPicturePath != null)
                {
                    Uri uri = Uri.parse("file:/" + camMan.lastPicturePath);

                    Intent i=new Intent(Intent.ACTION_VIEW);
                    if (camMan.lastPicturePath.endsWith("mp4"))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    startActivity(i);
                }
            }
        });

       upsidedown = (CheckBox) findViewById(R.id.button_fixupsidedown);

        if (camMan.parametersManager.isOrientationFIX())
            upsidedown.setChecked(true);
        upsidedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (upsidedown.isChecked())
                {
                    camMan.parametersManager.setOrientationFix(true);
                    camMan.Stop();
                    camMan.Start();

                    camMan.Restart(true);
                }
                else
                {
                    camMan.parametersManager.setOrientationFix(false);
                    camMan.Stop();
                    camMan.Start();
                    camMan.Restart(true);
                }

            }
        });

        switchVideoPicture = (Button)findViewById(R.id.button_switchVideoPicture);
        setSwitchVideoPictureBackground();
        switchVideoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordVideo)
                {
                    recordVideo = false;

                    preferences.edit().putBoolean("recordVideo", false).commit();
                }
                else
                {
                    recordVideo = true;

                    preferences.edit().putBoolean("recordVideo", true).commit();
                }
                setSwitchVideoPictureBackground();
            }
        });

        recordingTimerTextView = (TextView)findViewById(R.id.textView_timerRecording);
        mainlayout = (RelativeLayout)findViewById(R.id.mainRelativLayout);
        mainlayout.removeView(recordingTimerTextView);





    }

    public void onScreenText(int x)
    {
        try {
            OnScreenBrightnessText = (TextView) findViewById(R.id.textViewBrightnessText);
            OnScreenBrightnessValue = (TextView) findViewById(R.id.textViewBrightnessValue);
            OnScreenContrastText = (TextView) findViewById(R.id.textViewContrastText);
            OnScreenContrastValue = (TextView) findViewById(R.id.textViewContrastValue);
            OnScreenEVText = (TextView) findViewById(R.id.textViewEVText);
            OnScreenEVValue = (TextView) findViewById(R.id.textViewEvValue);
            OnScreenFlashText = (TextView) findViewById(R.id.textViewFlashtext);
            OnScreenFlashValue = (TextView) findViewById(R.id.textViewFlashValue);
            OnScreenEffectText = (TextView) findViewById(R.id.textViewEffetText);
            OnScreenEffectValue = (TextView) findViewById(R.id.textViewEffectValue);
            OnScreenFocusText = (TextView) findViewById(R.id.textViewFocusText);
            OnScreenFocusValue = (TextView) findViewById(R.id.textViewFocusValue);
            OnScreeISOText = (TextView) findViewById(R.id.textViewISOText);
            OnScreeISOValue = (TextView) findViewById(R.id.textViewISOValue);
            OnScreeMeterText = (TextView) findViewById(R.id.textViewMeterText);
            OnScreeMeterValue = (TextView) findViewById(R.id.textViewMeterValue);
            OnScreenSaturationText = (TextView) findViewById(R.id.textViewSatuText);
            OnScreeSaturationValue = (TextView) findViewById(R.id.textViewSatuValue);
            OnScreeSceneText = (TextView) findViewById(R.id.textViewSceneText);
            OnScreeSceneValue = (TextView) findViewById(R.id.textViewSceneValue);
            OnScreenPictureText = (TextView) findViewById(R.id.textViewPictureText);
            OnScreenPictureValue = (TextView) findViewById(R.id.textViewPictureValue);
            OnScreeSharpnessText = (TextView) findViewById(R.id.textViewSharpText);
            OnScreenSharpnessValue = (TextView) findViewById(R.id.textViewSharpValue);
            OnScreenWBText = (TextView) findViewById(R.id.textViewWBText);
            OnScreenWBValue = (TextView) findViewById(R.id.textViewWBValue);
            ProbeScreenText(x);
        }
        catch (NullPointerException ex)
        {
        }
    }


    public void ProbeScreenText (int x)
    {
        if (x == 0){
            findViewById(R.id.linearLayoutOnScreenText).setVisibility(View.VISIBLE);
            showtext();
        }
        else
        {
            findViewById(R.id.linearLayoutOnScreenText).setVisibility(View.INVISIBLE);

        }
    }

//:::::::::::::::::::::::::::: Sliders :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::



//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public void setSwitchVideoPictureBackground()
    {
        if (recordVideo)
        {
            switchVideoPicture.setBackgroundResource(R.drawable.icon_video_mode);
            shotButton.setBackgroundResource(R.drawable.icon_record_thanos_blast);
        }
        else
        {
            switchVideoPicture.setBackgroundResource(R.drawable.icon_picture_mode);
            shotButton.setBackgroundResource(R.drawable.icon_shutter_thanos_blast);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();

            if(key == KeyEvent.KEYCODE_VOLUME_UP)
            {
                camMan.zoomManager.setZoom(1);
            }
            else if(key == KeyEvent.KEYCODE_VOLUME_DOWN)
            {
                camMan.zoomManager.setZoom(-1);
            }
            else if(key == KeyEvent.KEYCODE_3D_MODE ||key == KeyEvent.KEYCODE_POWER )
            {
                camMan.StartTakePicture();
            }
            else
            {
                super.dispatchKeyEvent(event);
            }

        return true;
    }


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	View.OnClickListener shotListner = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			thumbButton.setImageBitmap(null);
            if(recordVideo == false)
            {
                if (HDRMode == false)
			        camMan.StartTakePicture();
                else
                    camMan.HdrRender.TakeHDRPictures(true);
                    showtext();

            }
            else
            {
                if (camMan.IsRecording == false)
                {
                    camMan.StartRecording();
                    recordTimer.Start();
                    shotButton.setBackgroundResource(R.drawable.icon_stop_thanos_blast);
                    mainlayout.addView(recordingTimerTextView);
                    OnScreenPictureText.setText("Video Size:");
                    OnScreenPictureValue.setText(camMan.parametersManager.getParameters().get("video-size"));
                }
                else
                {

                    camMan.StopRecording();
                    recordTimer.Stop();
                    shotButton.setBackgroundResource(R.drawable.icon_record_thanos_blast);
                    thumbButton.setImageBitmap(ThumbnailUtils.createVideoThumbnail(camMan.lastPicturePath,MediaStore.Images.Thumbnails.MINI_KIND));
                    mainlayout.removeView(recordingTimerTextView);
                }
            }
		}
	};

    public void SwitchCropButton()
    {
        if(!preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            //settingsMenuLayout.removeView(crop_box);
        }
        else
        {
            try
            {
            //settingsMenuLayout.addView(crop_box);
            }
            catch (Exception ex)
            {
                Log.d("MainActivity", "CropBox is already added");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(camMan, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(camMan);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                camMan.onPictureSaved(new File(result));
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    ///this updates the complete ui. its called everytime the camera parameters are changed
    @Override
    public void parametersHasChanged(boolean restarted)
    {
        try{
           if (camMan.parametersManager.getSupportSharpness())
                sharpnessTextView.setText("Sharpness: " + camMan.parametersManager.getParameters().getInt("sharpness"));
            //if (!parameters.get("exposure").equals("manual"))
            exposureTextView.setText("Exposure: " + camMan.parametersManager.getParameters().getExposureCompensation());
            //else
            //activity.exposureTextView.setText("Exposure: " + parameters.getInt("manual-exposure"));

            contrastTextView.setText("Contrast: " + camMan.parametersManager.getParameters().get("contrast"));
            saturationTextView.setText("Saturation: " + camMan.parametersManager.getParameters().get("saturation"));
            brightnessTextView.setText("Brightness: " + camMan.parametersManager.getParameters().get("brightness"));
            //buttonPreviewFormat.setText(camMan.parametersManager.getParameters().get("preview-format"));
            //sceneButton.setText(camMan.parametersManager.getParameters().getSceneMode());
            //previewSizeButton.setText(camMan.parametersManager.getParameters().getPreviewSize().width + "x" + camMan.parametersManager.getParameters().getPreviewSize().height);
            String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);

            camMan.manualExposureManager.SetMinMax(camMan.parametersManager.getParameters().getMinExposureCompensation(), camMan.parametersManager.getParameters().getMaxExposureCompensation());
            camMan.manualExposureManager.ExternalSet = true;
            camMan.manualExposureManager.SetCurrentValue(camMan.parametersManager.getParameters().getExposureCompensation());
            if (camMan.parametersManager.getSupportSharpness())
            {
                sharpnessSeekBar.setMax(180);
                sharpnessSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("sharpness"));
            }
            if (camMan.parametersManager.getSupportContrast())
            {
                contrastSeekBar.setMax(180);
                camMan.manualContrastManager.ExternalSet = true;
                contrastSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("contrast"));

            }
            if (camMan.parametersManager.getSupportBrightness())
            {
                brightnessSeekBar.setMax(100);
                brightnessSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("brightness"));
            }
            if (camMan.parametersManager.getSupportSaturation())
            {
                saturationSeekBar.setMax(180);
            }
            if (camMan.parametersManager.is3DMode())
                crop_box.setVisibility(View.VISIBLE);
            else
                crop_box.setVisibility(View.GONE);
            crop_box.setChecked(camMan.parametersManager.doCropping());

        }
        catch (NullPointerException ex)
        {

        }
    }
    public void showtext()
    {
        try
        {
            OnScreenBrightnessValue.setText(camMan.parametersManager.getParameters().get("brightness"));
            OnScreenContrastValue.setText(camMan.parametersManager.getParameters().get("contrast"));
            OnScreenSharpnessValue.setText(camMan.parametersManager.getParameters().get("saturation"));
            OnScreeSaturationValue.setText(camMan.parametersManager.getParameters().get("sharpness"));
            OnScreenEVValue.setText(camMan.parametersManager.getParameters().get("exposure-compensation"));
            OnScreenEffectValue.setText(camMan.parametersManager.getParameters().get("effect"));
            OnScreeISOValue.setText(camMan.parametersManager.getParameters().get("iso"));
            OnScreenFlashValue.setText(camMan.parametersManager.getParameters().get("flash-mode"));
            OnScreenFocusValue.setText(camMan.parametersManager.getParameters().get("focus-mode"));
            String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);
            OnScreenPictureValue.setText(size1);
            OnScreeSceneValue.setText(camMan.parametersManager.getParameters().get("scene-mode"));
            OnScreenWBValue.setText(camMan.parametersManager.getParameters().get("whitebalance"));
            if (DeviceUtils.isOmap())
                OnScreeMeterValue.setText(camMan.parametersManager.getParameters().get("auto-exposure"));
        }
        catch (Exception ex)
        {

        }
    }
}
	




