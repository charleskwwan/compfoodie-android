package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateGroupActivity extends AppCompatActivity {
    private EditText cuisine_input;
    private EditText location_input;
    private EditText party_size_input;
    private EditText wait_time_input;
    private EditText short_message_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        cuisine_input = (EditText)findViewById(R.id.cuisine_input);
        location_input = (EditText)findViewById(R.id.location_input);
        party_size_input = (EditText)findViewById(R.id.party_size_input);
        wait_time_input = (EditText)findViewById(R.id.wait_time_input);
        short_message_input = (EditText)findViewById(R.id.short_message_input);

        // TEST SPACE
    }
}
