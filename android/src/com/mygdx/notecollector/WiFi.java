package com.mygdx.notecollector;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class WiFi  implements IWiFi
{
    private Context ctx;
    WifiManager wifiM;
    public WiFi(Context context)
    {
        ctx=context;
        wifiM = (WifiManager) this.ctx.getSystemService(ctx.WIFI_SERVICE);
    }
    @Override
    public boolean isNetworkConnected()
    {

        if (wifiM.isWifiEnabled())
        {
            return true;
            //wifi is enabled
        }
        else
        {
            return false;
        }
    }
}
