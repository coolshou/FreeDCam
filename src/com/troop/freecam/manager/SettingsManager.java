package com.troop.freecam.manager;

import android.content.SharedPreferences;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 04.01.14.
 */
public class SettingsManager
{
    public class Preferences
    {
        public static final String SwitchCamera = "switchcam";
        public static final String MODE_3D = "3D";
        public static final String MODE_2D = "2D";
        public static final String MODE_Front = "Front";

        public static final String IPP2D = "2d_ipp";
        public static final String IPP3D = "3d_ipp";
        public static final String IPPFront = "front_ipp";

        public static final String ZSL2D = "2d_zsl";
        public static final String ZSL3D = "3d_zsl";
        public static final String ZSLFront = "front_zsl";

        public static final String Flash3D = "3d_flash";
        public static final String Flash2D = "2d_flash";
        public static final String FlashFront = "front_flash";

        public static final String Focus2D = "2d_focus";
        public static final String Focus3D = "3d_focus";
        public static final String FocusFront = "front_focus";

        public static final String PictureSize2D = "2d_picturesize";
        public static final String PictureSize3D = "3d_picturesize";
        public static final String PictureSizeFront = "front_picturesize";

        public static final String Exposure2D = "2d_exposure";
        public static final String Exposure3D = "3d_exposure";
        public static final String ExposureFront = "front_exposure";

        public static final String MTRValueFront = "front_meter_priority";
        public static final String MTRValue2D = "2d_meter_priority";
        public static final String MTRValue3D = "3d_meter_priority";

        public static final String WhiteBalanceFront = "front_whitebalance";
        public static final String WhiteBalance3D = "3d_whitebalance";
        public static final String WhiteBalance2D = "2d_whitebalance";

        public static final String SceneFront = "front_scene";
        public static final String Scene3D = "3d_scene";
        public static final String Scene2D = "2d_scene";

        public static final String Color2D = "2d_color";
        public static final String Color3D = "3d_color";
        public static final String ColorFront = "front_color";

        public static final String IsoFront = "front_iso";
        public static final String Iso3D = "3d_iso";
        public static final String Iso2D = "2d_iso";

        public static final String PreviewSize2D = "2d_previewsize";
        public static final String PreviewSize3D = "3d_previewsize";
        public static final String PreviewSizeFront = "front_previewsize";

        public static final String VideoSize2D = "2d_videosize";
        public static final String VideoSize3D = "3d_videosize";
        public static final String VideoSizeFront = "front_videosize";

        public static final String PreviewFormat2D = "2d_previewformat";
        public static final String PreviewFormat3D = "3d_previewformat";
        public static final String PreviewFormatFront = "front_previewformat";

        public static final String AfPriority2D = "2d_afpriority";
        public static final String AfPriority3D = "3d_afpriority";
        public static final String AfPriorityFront = "front_afpriority";
    }

    enum CameraValues
    {
        Front,
        Back2D,
        Back3D,
    }

    SharedPreferences preferences;
    public CamerasClass Cameras;
    public ImagePostProcessingClass ImagePostProcessing;
    public ZeroShutterLagClass ZeroShutterLag;
    public FlashModeClass FlashMode;
    public OrientationFixClass OrientationFix;
    public CropImageClass CropImage;
    public FocusModeClass FocusMode;
    public PictureSizeClass PictureSize;
    public ExposureModeClass ExposureMode;
    public MeteringModeClass MeteringMode;
    public WhiteBalanceModeClass WhiteBalanceMode;
    public SceneModeClass SceneMode;
    public ColorModeClass ColorMode;
    public IsoModeClass IsoMode;
    public PreviewSizeClass PreviewSize;
    public VideoSizeClass VideoSize;
    public PreviewFormatClass PreviewFormat;
    public AfPriorityClass afPriority;

    public int CameraCount = 0;
    public int CurrentCamera = 0;


    public SettingsManager(SharedPreferences preferences) {
        this.preferences = preferences;
        Cameras = new CamerasClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        ZeroShutterLag = new ZeroShutterLagClass();
        FlashMode = new FlashModeClass();
        OrientationFix = new OrientationFixClass();
        CropImage = new CropImageClass();
        FocusMode = new FocusModeClass();
        PictureSize = new PictureSizeClass();
        ExposureMode = new ExposureModeClass();
        MeteringMode = new MeteringModeClass();
        WhiteBalanceMode = new WhiteBalanceModeClass();
        SceneMode = new SceneModeClass();
        ColorMode = new ColorModeClass();
        IsoMode = new IsoModeClass();
        PreviewSize = new PreviewSizeClass();
        VideoSize = new VideoSizeClass();
        PreviewFormat = new PreviewFormatClass();
        afPriority = new AfPriorityClass();
    }

    public class CamerasClass
    {
        public void SetCamera(String value)
        {
            preferences.edit().putString(Preferences.SwitchCamera, value).commit();
        }

        public String GetCamera()
        {
            return preferences.getString(Preferences.SwitchCamera, Preferences.MODE_Front);
        }

        public CameraValues GetCameraEnum()
        {
            if (GetCamera() == Preferences.MODE_3D)
                return CameraValues.Back3D;
            else if (GetCamera() == Preferences.MODE_2D)
                return CameraValues.Back2D;
            else
                return CameraValues.Front;
        }

        public void SetCameraEnum(CameraValues values)
        {
            if (values == CameraValues.Back3D)
                SetCamera(Preferences.MODE_3D);
            if (values == CameraValues.Back2D)
                SetCamera(Preferences.MODE_2D);
            if (values == CameraValues.Front)
                SetCamera(Preferences.MODE_Front);
        }

        public boolean is3DMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_3D))
            {
                return true;
            }
            else
                return false;
        }

        public boolean is2DMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_2D))
            {
                return true;
            }
            else
                return false;
        }

        public boolean isFrontMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_Front))
            {
                return true;
            }
            else
                return false;
        }
    }

    public class BaseClass
    {
        public String threeD;
        protected String twoD;
        protected String front;
        protected String defaultVal;

        public BaseClass(String threeD, String twoD, String front, String defaultVal) {
            this.threeD = threeD;
            this.twoD = twoD;
            this.front = front;
            this.defaultVal = defaultVal;
        }

        public void Set(String val)
        {
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    preferences.edit().putString(threeD, val).commit();
                    break;
                case Front:
                    preferences.edit().putString(front, val).commit();
                    break;
                case Back2D:
                    preferences.edit().putString(twoD, val).commit();
                    break;
            }
        }

        public String Get()
        {
            String val;
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    val = preferences.getString(threeD, defaultVal);
                    break;
                case Front:
                    val = preferences.getString(front, defaultVal);
                    break;
                case Back2D:
                    val =  preferences.getString(twoD, defaultVal);
                    break;
                default:
                    val =  preferences.getString(SettingsManager.Preferences.IPPFront, defaultVal);
            }
            return val;
        }
    }

    public class WhiteBalanceModeClass extends BaseClass
    {
        public WhiteBalanceModeClass()
        {
            super(Preferences.WhiteBalance3D, Preferences.WhiteBalance2D, Preferences.WhiteBalanceFront, "");
        }
    }

    public class PreviewSizeClass extends BaseClass
    {
        public PreviewSizeClass()
        {
            super(Preferences.PreviewSize3D, Preferences.PreviewSize2D, Preferences.PreviewSizeFront, "");
        }
    }

    public class AfPriorityClass extends BaseClass
    {
        public AfPriorityClass()
        {
            super(Preferences.AfPriority3D, Preferences.AfPriority2D, Preferences.AfPriorityFront, "");
        }
    }

    public class PreviewFormatClass extends BaseClass
    {
        public PreviewFormatClass()
        {
            super(Preferences.PreviewFormat3D, Preferences.PreviewFormat2D, Preferences.PreviewFormatFront, "");
        }
    }

    public class VideoSizeClass extends BaseClass
    {
        public VideoSizeClass()
        {
            super(Preferences.VideoSize3D, Preferences.VideoSize2D, Preferences.VideoSizeFront, "");
        }
    }

    public class SceneModeClass extends BaseClass
    {
        public SceneModeClass()
        {
            super(Preferences.Scene3D, Preferences.Scene2D, Preferences.SceneFront, "");
        }
    }

    public class IsoModeClass extends BaseClass
    {
        public IsoModeClass()
        {
            super(Preferences.Iso3D, Preferences.Iso2D, Preferences.IsoFront, "");
        }
    }

    public class ColorModeClass extends BaseClass
    {
        public ColorModeClass()
        {
            super(Preferences.Color3D, Preferences.Color2D, Preferences.ColorFront, "");
        }
    }

    public class MeteringModeClass extends BaseClass
    {
        public MeteringModeClass()
        {
            super(Preferences.MTRValue3D, Preferences.MTRValue2D, Preferences.MTRValueFront, "");
        }
    }

    public class ImagePostProcessingClass extends BaseClass
    {
        public ImagePostProcessingClass()
        {
            super(SettingsManager.Preferences.IPP3D, SettingsManager.Preferences.IPP2D, SettingsManager.Preferences.IPPFront, "");
        }
    }

    public class ZeroShutterLagClass extends BaseClass
    {
        public ZeroShutterLagClass()
        {
            super(SettingsManager.Preferences.ZSL3D, SettingsManager.Preferences.ZSL2D, SettingsManager.Preferences.ZSLFront, "");
        }
    }

    public class FlashModeClass extends BaseClass
    {
        public FlashModeClass()
        {
            super(Preferences.Flash3D, Preferences.Flash2D, Preferences.FlashFront, "");
        }
    }

    public class FocusModeClass extends BaseClass
    {
        public FocusModeClass()
        {
            super(Preferences.Focus3D, Preferences.Focus2D, Preferences.FocusFront, "");
        }
    }

    public class PictureSizeClass extends BaseClass
    {
        public PictureSizeClass()
        {
            super(Preferences.PictureSize3D, Preferences.PictureSize2D, Preferences.PictureSizeFront, "");
        }
    }

    public class ExposureModeClass extends BaseClass
    {
        public ExposureModeClass()
        {
            super(Preferences.Exposure3D, Preferences.Exposure2D, Preferences.ExposureFront, "");
        }
    }

    public class OrientationFixClass
    {
        public boolean GET()
        {
            if(preferences.getBoolean("upsidedown", false))
                return true;
            else
                return false;
        }

        public void Set(boolean value)
        {
            preferences.edit().putBoolean("upsidedown", value).commit();
        }
    }

    public class CropImageClass
    {
        public boolean GET()
        {
            if(preferences.getBoolean("crop", false))
                return true;
            else
                return false;
        }

        public void Set(boolean value)
        {
            preferences.edit().putBoolean("crop", value).commit();
        }
    }
}
