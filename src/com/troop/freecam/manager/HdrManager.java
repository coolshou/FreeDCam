package com.troop.freecam.manager;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.freecam.HDR.HdrRenderActivity;
import com.troop.freecam.HDR.SavePictureRunnable;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.PictureTakeFinish;

import java.io.File;
import java.io.IOException;

/**
 * Created by troop on 15.10.13.
 */
public class HdrManager implements PictureTakeFinish
{

    private final String TAG = "freecam.HdrManager";

    public boolean IsActive = false;

    CameraManager cameraManager;

    private Uri[] uris;

    int count = 0;
    int interval = 500;
    boolean takepicture = false;
    boolean working = false;
    boolean setParameters = false;
    PictureTakeFinish pictureTakeFinish;
    SavePictureRunnable saveFirstPic;
    SavePictureRunnable saveSecondPic;
    SavePictureRunnable saveThirdPic;
    boolean finish = false;




    private Handler handler = new Handler();
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            doAction();
        }
    };

    private void doAction() {
        if (count < 3)
        {
            if (!takepicture && !setParameters && !finish)
            {
                Log.d(TAG,"Rdy for Taking Pic:" + count);
                starttakePicture();
            }
            /*else if(takepicture || setParameters)
            {
                Log.d(TAG,"Not Rdy for Taking Pic:" + count);
                doAction();
                //handler.postDelayed(runnable, interval);
            }*/
        }
        else
        {
            while (saveFirstPic.Running || saveSecondPic.Running || saveThirdPic.Running )
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //File sdcardpath = Environment.getExternalStorageDirectory();

            Intent hdractiv = new Intent(cameraManager.activity.getApplicationContext(), HdrRenderActivity.class);
            String[] ar = new String[3];
            ar[0] = uris[0].getPath();
            ar[1] = uris[1].getPath();
            ar[2] = uris[2].getPath();
            hdractiv.putExtra("uris", ar);
            cameraManager.activity.startActivityForResult(hdractiv, 1);
            //cameraManager.parameters.set("video-stabilization", "false");
            //cameraManager.parametersManager.SetExposureCompensation(0);
            //cameraManager.parameters.setAutoExposureLock(false);
            //cameraManager.parameters.setAutoWhiteBalanceLock(false);

            //cameraManager.parametersManager.SetBrightness(100);
            //cameraManager.parametersManager.SetContrast(50);
            //cameraManager.mCamera.startPreview();
        }
    }

    public HdrManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
        uris  = new Uri[3];
        pictureTakeFinish = this;
    }

    public void TakeHDRPictures(boolean reset)
    {
        finish = false;
        //cameraManager.parametersManager.getParameters().set("video-stabilization", "true");
        //cameraManager.mCamera.setParameters(cameraManager.parametersManager.getParameters());
        count = 0;
        doAction();
    }

    private void starttakePicture()
    {
        setParameters = true;
        Log.d(TAG, "Set Parameters to Cam");
        setParameters();
        Log.d(TAG, "Set Parameters to Cam finish");
        setParameters = false;


        takepicture = true;
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Start Taking Picture");
        cameraManager.soundPlayer.PlayShutter();
        cameraManager.mCamera.takePicture(null, null, jpegCallback);

    }

    private void setParameters()
    {
        /*if (cameraManager.parametersManager.getParameters().isAutoWhiteBalanceLockSupported() && cameraManager.parametersManager.getParameters().getAutoWhiteBalanceLock() == false)
            cameraManager.parametersManager.getParameters().setAutoWhiteBalanceLock(true);
        if (cameraManager.parametersManager.getParameters().isAutoExposureLockSupported()&& cameraManager.parametersManager.getParameters().getAutoExposureLock() == false)
            cameraManager.parametersManager.getParameters().setAutoExposureLock(true);*/
        //disable, frame, center, fft and manual.
        //cameraManager.parametersManager.getParameters().set("auto-convergence-mode", "disable");


        int conv  = cameraManager.parametersManager.getParameters().getExposureCompensation();

        //cameraManager.parametersManager.setPictureSize(cameraManager.Settings.PictureSize.Get());
        //cameraManager.mCamera.stopPreview();
        Log.d(TAG, "PictureSize:" + cameraManager.parametersManager.getParameters().getPictureSize().width + "x" + cameraManager.parametersManager.getParameters().getPictureSize().height);
        if (count == 0)
        {
            cameraManager.parametersManager.manualExposure.set(cameraManager.Settings.HDRSettings.getHighExposure());
            cameraManager.parametersManager.Iso.set(cameraManager.Settings.HDRSettings.getHighIso(), true);
            //cameraManager.parametersManager.SetBrightness(60);
            //cameraManager.parametersManager.SetContrast(120);
        }
        else if (count == 1)
        {
            cameraManager.parametersManager.manualExposure.set(cameraManager.Settings.HDRSettings.getNormalExposure());
            cameraManager.parametersManager.Iso.set(cameraManager.Settings.HDRSettings.getNormalIso(),true);
            //cameraManager.parametersManager.SetBrightness(50);
            //cameraManager.parametersManager.SetContrast(100);
        }
        else if (count == 2)
        {
            cameraManager.parametersManager.manualExposure.set(cameraManager.Settings.HDRSettings.getLowExposure());
            cameraManager.parametersManager.Iso.set(cameraManager.Settings.HDRSettings.getLowIso(), true);
            //cameraManager.parametersManager.SetBrightness(40);
            //cameraManager.parametersManager.SetContrast(80);
        }
        //cameraManager.mCamera.startPreview();

    }


    public Camera.PictureCallback jpegCallback = new Camera.PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {

            boolean is3d = false;
            String end;


            if (cameraManager.Settings.Cameras.is3DMode())
            {
                is3d = true;
            }
            if (is3d)
                end = "jps";
            else
                end = "jpg";


            File sdcardpath = Environment.getExternalStorageDirectory();
            if (!sdcardpath.exists())
            {
                Log.e(TAG, "sdcard ist not connected");
                return;

            }else
            {
                //TODO move saving into new thread for faster picture taking not added yet because of oom
                File file = getFilePath(end,sdcardpath);
                uris[count] = Uri.fromFile(file);
                boolean upsidedownfix = cameraManager.Settings.OrientationFix.GET();

                if (count == 0)
                {
                    saveFirstPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveFirstPic);
                    //new Thread(saveFirstPic);

                }
                else if (count == 1)
                {
                    saveSecondPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveSecondPic);
                    //new Thread(saveSecondPic);
                }
                else if (count == 2)
                {
                    saveThirdPic = new SavePictureRunnable(data, file.getAbsolutePath(), count, upsidedownfix);
                    handler.post(saveThirdPic);
                    //new Thread(saveThirdPic);
                    finish = true;
                }
                //savePic(data, end, sdcardpath);
            }
            count++;
            takepicture = false;
            Log.d(TAG,"Picture Taking Finished");

            cameraManager.mCamera.startPreview();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doAction();
            //pictureTakeFinish.PictureTakingFinish();
        }
    };



    private File getFilePath(String end, File sdcardpath) {
        File freeCamImageDirectory = new File(sdcardpath.getAbsolutePath() + "/DCIM/FreeCam/Tmp/");
        if (!freeCamImageDirectory.exists())
        {
            Log.d(TAG, "FreeCamFolder Tmp not exists try to create");
            try
            {
                freeCamImageDirectory.mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }


        }
        File file = new File(String.format(freeCamImageDirectory + "/" + String.valueOf(count) + "." + end));
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        return file;
    }

    public Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

            cameraManager.soundPlayer.PlayShutter();
            Log.d("FreeCam", "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FreeCam", "onPictureTaken - raw");
        }
    };

    @Override
    public void PictureTakingFinish()
    {
        doAction();
    }



}
