package jp.kd.gmapsa;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class LaunchRouteActivity extends Activity {
    static final String EXTRA_ROUTE_URL = "route_url";
    static final String GMAPS_WV_PACKAGE = "us.spotco.maps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra(EXTRA_ROUTE_URL);
        if (url == null || !url.startsWith("https://www.google.com/maps/dir/")) {
            finish();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(GMAPS_WV_PACKAGE);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.official_not_found, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
