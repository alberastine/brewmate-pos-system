package com.example.brewmate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.example.brewmate.R;
import com.example.brewmate.models.Supply;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows a low-stock dialog once per app launch (process).
 */
public class LowStockChecker {

    private static final String SUPPLY_PREFS_NAME = "SupplyPrefs";
    private static final String KEY_SUPPLIES = "supplies_list";
    private static boolean shownThisLaunch = false;

    public static void checkAndShow(Activity activity) {
        if (shownThisLaunch || activity == null || activity.isFinishing()) return;

        List<Supply> supplies = loadSupplies(activity);
        List<Supply> lowSupplies = new ArrayList<>();

        for (Supply s : supplies) {
            double threshold = sanitizeDouble(s.getLowStockThreshold());
            double qty = parseDoubleSafe(s.getQuantity());
            boolean isLowWithThreshold = (threshold > 0 && qty <= threshold);
            boolean isLowWithoutThreshold = (threshold <= 0 && qty <= 0);
            if (isLowWithThreshold || isLowWithoutThreshold) {
                lowSupplies.add(s);
            }
        }

        if (lowSupplies.isEmpty()) return;

        StringBuilder message = new StringBuilder();
        for (Supply s : lowSupplies) {
            double qty = parseDoubleSafe(s.getQuantity());
            message.append("• ")
                    .append(s.getSupplyName())
                    .append(" — Qty: ")
                    .append(qty)
                    .append(" (Threshold: ")
                    .append(sanitizeDouble(s.getLowStockThreshold()))
                    .append(")")
                    .append("\n");
        }

        shownThisLaunch = true;

        new AlertDialog.Builder(activity)
                .setTitle(R.string.low_stock_dialog_title)
                .setMessage(message.toString().trim())
                .setPositiveButton(R.string.dismiss, (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private static List<Supply> loadSupplies(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SUPPLY_PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_SUPPLIES, null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Supply>>() {}.getType();
        List<Supply> list = gson.fromJson(json, type);
        if (list == null) return new ArrayList<>();

        for (Supply s : list) {
            if (Double.isNaN(s.getLowStockThreshold())) {
                s.setLowStockThreshold(0);
            }
        }
        return list;
    }

    private static double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static double sanitizeDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0;
        return value;
    }
}

