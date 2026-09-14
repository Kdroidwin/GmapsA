package jp.kd.gmapsa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/** App entry point for privacy-related display preferences. */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setGravity(Gravity.CENTER_VERTICAL);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText(R.string.settings_title);
        title.setTextSize(22);
        root.addView(title, matchWrap());

        TextView description = new TextView(this);
        description.setText(R.string.settings_description);
        root.addView(description, matchWrap());

        CheckBox hideRoute = new CheckBox(this);
        hideRoute.setText(R.string.settings_hide_route);
        hideRoute.setChecked(RouteWidgetProvider.isRouteHidden(this));
        hideRoute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences(RouteWidgetProvider.SETTINGS_NAME, MODE_PRIVATE).edit()
                    .putBoolean(RouteWidgetProvider.HIDE_ROUTE_KEY, isChecked).apply();
            RouteWidgetProvider.updateAllWidgets(this);
        });
        root.addView(hideRoute, matchWrap());
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8);
        return params;
    }
}
