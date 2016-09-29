package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText cuisine_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // TEST SPACE
        cuisine_input = (EditText)findViewById(R.id.cuisine_input);

        cuisine_input.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ((EditText)v).setText("");
    }
}
