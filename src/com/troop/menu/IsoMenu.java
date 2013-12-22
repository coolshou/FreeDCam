package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.OEM.Samsung;
import com.troop.freecam.manager.ParametersManager;


/**
 * Created by troop on 02.09.13.
 */
public class IsoMenu extends BaseMenu {
    public IsoMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] isos;
    Samsung sam;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try
            {
                if(CameraManager.isOmap() == true){
                    isos = camMan.parametersManager.getParameters().get("iso-mode-values").split(",");
                }
                if(CameraManager.isQualcomm() == true){
                    isos = camMan.parametersManager.getParameters().get("iso-values").split(",");
                }
                if(CameraManager.isSony() == true){
                    isos = camMan.parametersManager.getParameters().get("sony-iso-values").split(",");
                }
                if(CameraManager.isExynos5() == true){
                    isos = sam.getSupported_iso_exynos5();
                }

            }
            catch (Exception ex)
            {

            }
        }
        if (isos != null && !isos.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < isos.length; i++) {
                popupMenu.getMenu().add((CharSequence) isos[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    if(CameraManager.isSony() == true)
                        camMan.parametersManager.getParameters().set("sony-iso", tmp);
                    else
                        camMan.parametersManager.getParameters().set("iso", tmp);
                    String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_3D))
                        preferences.edit().putString(ParametersManager.Preferences_Iso3D, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                        preferences.edit().putString(ParametersManager.Preferences_Iso2D, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
                        preferences.edit().putString(ParametersManager.Preferences_IsoFront, tmp).commit();
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
