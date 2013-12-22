package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.OEM.Samsung;
import com.troop.freecam.manager.ParametersManager;

/**
 * Created by George on 12/6/13.
 */
public class MeteringMenu extends BaseMenu   {

    public MeteringMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String [] modes;
    Samsung sam;

    @Override
    public void onClick(View v)
    {




        View canvasView = super.GetPlaceHolder();
        PopupMenu popupMenu = new PopupMenu(activity, canvasView);

        if(camMan.Running && camMan.parametersManager.getSupportAutoExposure())
            if (CameraManager.isExynos5() == true){
               modes = sam.getSupported_exposure_modes_exynos5();
            }
            if (CameraManager.isSony() == true)
            {
                modes = camMan.parametersManager.getParameters().get("sony-metering-mode-values").split(",");
            }
            else
            {
                modes = camMan.parametersManager.getParameters().get("auto-exposure-values").split(",");
            }





        if (modes != null)
        {
            //PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            for (int i = 0; i < modes.length; i++) {
                popupMenu.getMenu().add((CharSequence) modes[i]);
            }

            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_focus, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    if (CameraManager.isSony() == true)
                    {
                        camMan.parametersManager.getParameters().set("sony-metering-mode", tmp);
                    }
                    if (CameraManager.isExynos5() == true)
                    {
                        camMan.parametersManager.getParameters().set("metering", tmp);
                    }
                    else
                    {
                        camMan.parametersManager.getParameters().set("auto-exposure", tmp);
                    }
                    activity.OnScreeMeterValue.setText(tmp);
                    activity.buttonMetering.setText(tmp);

                    String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                        preferences.edit().putString(ParametersManager.Preferences_MTRValue, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
                        preferences.edit().putString(ParametersManager.Preferences_MTRValue, tmp).commit();
                    //preferences.edit().putString("focus", tmp).commit();

                    camMan.autoFocusManager.StartFocus();
                    camMan.Restart(false);
                    return true;
                }
            });

            popupMenu.show();
            activity.appViewGroup.removeView(canvasView);
        }
    }

}
