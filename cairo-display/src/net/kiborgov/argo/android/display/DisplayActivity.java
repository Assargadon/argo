package net.kiborgov.argo.android.display;

import android.app.Activity;
import android.os.Bundle;

public class DisplayActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        setContentView(new DisplayView(this));
    }
}
