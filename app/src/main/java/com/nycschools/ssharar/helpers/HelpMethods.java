package com.nycschools.ssharar.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
/* Author: Steven Sharar
   Date: 3/3/2023
   With more time I would add a OnConnectivity receiver. 
 */
public class HelpMethods {
    Context context;

    public HelpMethods(Context context){
        this.context = context;
    }

    /**
     * Checks if there is a network connection.
     *
     * @return True if there is a network connection. Otherwise, returns false.
     */
    public boolean hasNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        Network network = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            network = connectivityManager.getActiveNetwork();
        }
        NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
