package com.fantasticfour.esurvey.Global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.fantasticfour.esurvey.Interface.RequestInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by earl on 9/17/2016.
 */
public class GlobalFunctions {

    public static RequestInterface getInterface(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RequestInterface.class);
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void noInternetToast(Context context){
        Toast.makeText(context, "No Internet Connection. Please try again Later", Toast.LENGTH_SHORT).show();
    }
}
