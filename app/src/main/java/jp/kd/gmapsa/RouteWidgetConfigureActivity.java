package jp.kd.gmapsa;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class RouteWidgetConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        root.setGravity(Gravity.CENTER_VERTICAL);
        setContentView(root);

        TextView title = new TextView(this);
        title.setText(R.string.config_title);
        title.setTextSize(22);
        root.addView(title, matchWrap());

        EditText label = addEditText(root, R.string.config_label,
                RouteWidgetProvider.getWidgetString(this, appWidgetId, "label", getString(R.string.widget_label_default)));
        EditText origin = addEditText(root, R.string.config_origin,
                RouteWidgetProvider.getWidgetString(this, appWidgetId, "origin", getString(R.string.origin_default)));
        EditText destination = addEditText(root, R.string.config_destination,
                RouteWidgetProvider.getWidgetString(this, appWidgetId, "destination", getString(R.string.destination_default)));

        TextView colorLabel = new TextView(this);
        colorLabel.setText(R.string.config_color);
        root.addView(colorLabel, matchWrap());
        String[] colorNames = getResources().getStringArray(R.array.widget_color_names);
        Spinner color = new Spinner(this);
        color.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, colorNames));
        String savedColor = RouteWidgetProvider.getWidgetString(this, appWidgetId, "color", RouteWidgetProvider.DEFAULT_COLOR);
        String[] colorValues = getResources().getStringArray(R.array.widget_color_values);
        for (int i = 0; i < colorValues.length; i++) if (colorValues[i].equals(savedColor)) color.setSelection(i);
        root.addView(color, matchWrap());

        TextView opacityLabel = new TextView(this);
        opacityLabel.setText(R.string.config_opacity);
        root.addView(opacityLabel, matchWrap());
        String[] opacityValues = new String[101];
        for (int i = 0; i <= 100; i++) opacityValues[i] = i + "%";
        Spinner opacity = new Spinner(this);
        opacity.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opacityValues));
        opacity.setSelection(RouteWidgetProvider.getWidgetOpacity(this, appWidgetId));
        root.addView(opacity, matchWrap());

        Button save = new Button(this);
        save.setText(R.string.config_save);
        save.setOnClickListener(v -> {
            RouteWidgetProvider.saveWidget(this, appWidgetId, label.getText().toString(), origin.getText().toString(), destination.getText().toString(),
                    colorValues[color.getSelectedItemPosition()], opacity.getSelectedItemPosition());
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            RouteWidgetProvider.updateAppWidget(this, manager, appWidgetId);

            Intent result = new Intent();
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, result);
            finish();
        });
        root.addView(save, matchWrap());
    }

    private EditText addEditText(LinearLayout root, int hintRes, String text) {
        EditText editText = new EditText(this);
        editText.setHint(hintRes);
        editText.setText(text);
        editText.setSingleLine(true);
        root.addView(editText, matchWrap());
        return editText;
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8);
        return params;
    }
}
