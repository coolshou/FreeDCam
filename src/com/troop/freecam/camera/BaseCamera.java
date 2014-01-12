package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;

import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 18.10.13.
 */
public class BaseCamera
{
    public Camera mCamera;
    public SettingsManager Settings;
    final  String TAG = "freecam.BaseCamera";
    //protected byte[] rawbuffer = new byte[52428800];

    public BaseCamera(SettingsManager Settings)
    {
        this.Settings = Settings;
    }

    protected void OpenCamera(SurfaceHolder holder)
    {
        String tmp = Settings.Cameras.GetCamera();
        Settings.CameraCount = Camera.getNumberOfCameras();
        if (Camera.getNumberOfCameras() == 3 || DeviceUtils.isEvo3d())
        {
            Log.d(TAG, "Device Model: " + Build.MODEL);
            if (tmp.equals(SettingsManager.Preferences.MODE_3D))
            {
                if (DeviceUtils.isEvo3d())
                {
                    Log.d(TAG, "try open sense 3D camera");
                    try
                    {
                        mCamera = Camera.open(100);
                        Settings.CurrentCamera = 100;
                        Log.d(TAG, "sense 3D camera open");
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Log.e(TAG, "error open sense 3D Camera");
                        mCamera.release();
                    }
                }
                else
                {
                    mCamera = Camera.open(2);
                    Settings.CurrentCamera = 2;
                }
            }
            else if(tmp.equals(SettingsManager.Preferences.MODE_2D))
            {
                mCamera = Camera.open(0);
                Settings.CurrentCamera = 0;
            }
            else if (tmp.equals(SettingsManager.Preferences.MODE_Front))
            {
                mCamera = Camera.open(1);
                Settings.CurrentCamera = 1;
            }
        }
        else if (Camera.getNumberOfCameras() == 2)
        {
            if(tmp.equals(SettingsManager.Preferences.MODE_2D))
            {
                mCamera = Camera.open(0);
                Settings.CurrentCamera = 0;
            }
            if (tmp.equals(SettingsManager.Preferences.MODE_Front))
            {
                mCamera = Camera.open(1);
                Settings.CurrentCamera = 1;
            }
        }
        else if (Camera.getNumberOfCameras() == 1)
        {
            mCamera = Camera.open(0);
            Settings.CurrentCamera = 0;
        }
        //mCamera.addCallbackBuffer(rawbuffer);
    }

    protected  void CloseCamera()
    {
        mCamera.release();
        mCamera = null;
    }
}
