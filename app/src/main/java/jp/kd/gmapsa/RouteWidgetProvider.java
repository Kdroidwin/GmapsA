package jp.kd.gmapsa;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RouteWidgetProvider extends AppWidgetProvider {
    static final String PREFS_NAME = "route_widgets";
    static final String SETTINGS_NAME = "route_widget_settings";
    static final String HIDE_ROUTE_KEY = "hide_route";
    static final String DEFAULT_COLOR = "#0F172A";
    static final int DEFAULT_OPACITY = 100;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        for (int appWidgetId : appWidgetIds) {
            editor.remove(key(appWidgetId, "label"));
            editor.remove(key(appWidgetId, "origin"));
            editor.remove(key(appWidgetId, "destination"));
            editor.remove(key(appWidgetId, "color"));
            editor.remove(key(appWidgetId, "opacity"));
        }
        editor.apply();
    }

    static void updateAppWidget(Context context, AppWidgetManager manager, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String label = prefs.getString(key(appWidgetId, "label"), context.getString(R.string.widget_label_default));
        String origin = prefs.getString(key(appWidgetId, "origin"), context.getString(R.string.origin_default));
        String destination = prefs.getString(key(appWidgetId, "destination"), context.getString(R.string.destination_default));
        String color = prefs.getString(key(appWidgetId, "color"), DEFAULT_COLOR);
        int opacity = prefs.getInt(key(appWidgetId, "opacity"), DEFAULT_OPACITY);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.route_widget);
        views.setTextViewText(R.id.routeWidgetLabel, label);
        views.setTextViewText(R.id.routeWidgetRoute, origin + " -> " + destination);
        views.setViewVisibility(R.id.routeWidgetRoute,
                isRouteHidden(context) ? View.GONE : View.VISIBLE);
        views.setInt(R.id.routeWidgetRoot, "setBackgroundColor", withOpacity(color, opacity));

        Intent launch = new Intent(context, LaunchRouteActivity.class);
        launch.putExtra(LaunchRouteActivity.EXTRA_ROUTE_URL, buildDirectionsUrl(origin, destination));
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                launch,
                PendingIntent.FLAG_UPDATE_CURRENT | immutableFlag()
        );
        views.setOnClickPendingIntent(R.id.routeWidgetRoot, pendingIntent);
        manager.updateAppWidget(appWidgetId, views);
    }

    static void saveWidget(Context context, int appWidgetId, String label, String origin,
                           String destination, String color, int opacity) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(key(appWidgetId, "label"), emptyToDefault(label, context.getString(R.string.widget_label_default)))
                .putString(key(appWidgetId, "origin"), emptyToDefault(origin, context.getString(R.string.origin_default)))
                .putString(key(appWidgetId, "destination"), emptyToDefault(destination, context.getString(R.string.destination_default)))
                .putString(key(appWidgetId, "color"), color)
                .putInt(key(appWidgetId, "opacity"), Math.max(0, Math.min(100, opacity)))
                .apply();
    }

    static String getWidgetString(Context context, int appWidgetId, String name, String fallback) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(key(appWidgetId, name), fallback);
    }

    static int getWidgetOpacity(Context context, int appWidgetId) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(key(appWidgetId, "opacity"), DEFAULT_OPACITY);
    }

    static boolean isRouteHidden(Context context) {
        return context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
                .getBoolean(HIDE_ROUTE_KEY, false);
    }

    static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new android.content.ComponentName(context, RouteWidgetProvider.class));
        for (int id : ids) updateAppWidget(context, manager, id);
    }

    static String key(int appWidgetId, String name) {
        return appWidgetId + "_" + name;
    }

    private static String buildDirectionsUrl(String origin, String destination) {
        return "https://www.google.com/maps/dir/"
                + encodePath(origin)
                + "/"
                + encodePath(destination)
                + "?hl=ja&gl=JP";
    }

    private static String encodePath(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String emptyToDefault(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        return value.trim();
    }

    private static int withOpacity(String colorValue, int opacity) {
        try {
            int color = Color.parseColor(colorValue);
            return Color.argb(Math.max(0, Math.min(100, opacity)) * 255 / 100,
                    Color.red(color), Color.green(color), Color.blue(color));
        } catch (IllegalArgumentException ignored) {
            return Color.parseColor(DEFAULT_COLOR);
        }
    }

    private static int immutableFlag() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
    }
}
