package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class CreateGroupActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomMenuBar bottombar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // ToolBar
        toolbar = (Toolbar)findViewById(R.id.create_group_toolbar);
        toolbar.setTitle(getString(R.string.create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // need to set parent in manifest

        bottombar = new BottomMenuBar(this, savedInstanceState);
    }
}
