package com.example.attendance.Utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.attendance.StaffModule.StaffRVModal;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.List;

public class LocalStorageUtil {

    public static List<StaffRVModal> retrieveStaffDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_staff_data", Context.MODE_PRIVATE);
        String staffDataJson = sharedPreferences.getString("staff_data", "");

        if (!staffDataJson.isEmpty()) {
            return new Gson().fromJson(staffDataJson, new TypeToken<List<StaffRVModal>>() {}.getType());
        } else {
            return null;
        }

    }

    public static List<StaffRVModal> retrieveStudentDataFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("system_staff_data", Context.MODE_PRIVATE);
        String staffDataJson = sharedPreferences.getString("staff_data", "");

        if (!staffDataJson.isEmpty()) {
            return new Gson().fromJson(staffDataJson, new TypeToken<List<StaffRVModal>>() {}.getType());
        } else {
            return null;
        }
    }

}
