package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateGroupActivity extends AppCompatActivity {
    private BottomMenuBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // TEST SPACE
        bottomBar = new BottomMenuBar(this, savedInstanceState);
    }
}
