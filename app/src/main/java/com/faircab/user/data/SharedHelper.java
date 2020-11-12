package com.faircab.user.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.faircab.user.BuildConfig;
import com.faircab.user.data.network.model.Provider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

public class SharedHelper {
    public static String  apiState="";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static SharedPreferences getSharedPreferences(Context mContext) throws NullPointerException {
        SharedPreferences sharedPreferences = null;
//            String masterKeyAlias = null;

            try {
                MasterKey mainKey = new MasterKey.Builder(mContext)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
//                masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                sharedPreferences = EncryptedSharedPreferences.create(
                        mContext,
                        BuildConfig.APPLICATION_ID,
                        mainKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        return sharedPreferences==null?mContext.getSharedPreferences(BuildConfig.APPLICATION_ID,Context.MODE_PRIVATE):sharedPreferences;
    }
    public static void putKey(Context context, String Key, String Value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();

    }

    public static String getKey(Context context, String Key) {
        sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(Key, "");
    }

    public static void putKey(Context context, String Key, boolean Value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public static void putKey(Context context, String Key, Integer value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putInt(Key, value);
        editor.apply();
    }

    public static Integer getIntKey(Context context, String Key) {
        sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(Key, -1);
    }

    public static boolean getBoolKey(Context context, String Key, boolean defalultValue) {
        sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(Key, defalultValue);
    }

    public static String getKey(Context context, String Key, String defaultValue) {
        sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(Key, defaultValue);
    }

    public static void clearSharedPreferences(Context context) {
        String device_token = String.valueOf(getKey(context, "device_token"));
        String device_id = String.valueOf(getKey(context, "device_id"));

        sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().clear().apply();

        putKey(context, "device_token", device_token);
        putKey(context, "device_id", device_id);
    }

    public static void putProviders(Context context, String Value) {
        sharedPreferences = getSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString("Services", Value);
        editor.apply();
    }

    public static List<Provider> getProviders(Context context) {
        sharedPreferences = getSharedPreferences(context);
        return new Gson().fromJson(sharedPreferences.getString("Services", ""),
                new TypeToken<ArrayList<Provider>>() {
                }.getType());
    }
}
