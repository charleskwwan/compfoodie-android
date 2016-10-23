package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.browse_toolbar);
        toolbar.setTitle(getString(R.string.browse_toolbar_title));
        setSupportActionBar(toolbar);
    }
}
