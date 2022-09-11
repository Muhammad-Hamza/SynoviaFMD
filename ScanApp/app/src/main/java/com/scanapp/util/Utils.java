package com.scanapp.util;

import android.content.Context;

import java.util.HashMap;

public class Utils {

    public static HashMap<String, String> getSupplyHeader(Context context) {
        String token = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE).getString("accessToken", "");
        HashMap<String, String> map = new HashMap<>();
        map.put("emvs-data-entry-mode", "non-manual");
        map.put("emvs-api-version", "2.4");
        map.put("Content-Type", "application/json");
        map.put("Authorization", "bearer " + token);
        return map;
    }
}
