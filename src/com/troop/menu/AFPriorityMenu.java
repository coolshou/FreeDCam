package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

/**
 * Created by George on 12/6/13.
 */
public class AFPriorityMenu extends BaseMenu  {

    public AFPriorityMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String [] modes;

    @Override
    public void onClick(View v)
    {



        View canvasView = super.GetPlaceHolder();
        PopupMenu popupMenu = new PopupMenu(activity, canvasView);

        if(camMan.Running && camMan.parametersManager.getSupportAfpPriority())
            if (CameraManager.isQualcomm() == true){
            modes = camMan.parametersManager.getParameters().get("selectable-zone-af-values").split(",");
            }
            if (CameraManager.isOmap() == true){
                modes = camMan.parametersManager.getParameters().get("auto-convergence-mode-values").split(",");
            }
            if (CameraManager.isSony() == true)
            {
                modes = camMan.parametersManager.getParameters().get("sony-focus-area-values").split(",");
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
                    if (CameraManager.isQualcomm() == true){
                        camMan.parametersManager.getParameters().set("selectable-zone-af", tmp);
                    }
                    if (CameraManager.isOmap() == true){
                        camMan.parametersManager.getParameters().set("auto-convergence-mode", tmp);
                    }
                    if (CameraManager.isSony() == true)
                    {
                        camMan.parametersManager.getParameters().set("sony-focus-area", tmp);
                    }

                    activity.OnScreenFocusValue.setText("AFP:"+ tmp);
                    activity.buttonAfPriority.setText(tmp);

                    String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                        preferences.edit().putString(ParametersManager.Preferences_AFPValue, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
                        preferences.edit().putString(ParametersManager.Preferences_AFPValue, tmp).commit();
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
