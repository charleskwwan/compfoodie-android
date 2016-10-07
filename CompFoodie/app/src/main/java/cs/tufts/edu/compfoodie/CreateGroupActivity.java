package cs.tufts.edu.compfoodie;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGroupActivity extends AppCompatActivity {

    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        BottomMenuBar bottombar = new BottomMenuBar(this, savedInstanceState);

        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.create_group_toolbar);
        toolbar.setTitle(getString(R.string.create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // need to set parent in manifest

        // Wait Time Picker
        ImageButton wtbutton = (ImageButton)findViewById(R.id.wait_time_pick_button);
        wtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaitTimePickerFragment wtpfrag = new WaitTimePickerFragment();
                wtpfrag.show(getFragmentManager(), "wait_time_picker_fragment");
            }
        });

        // Create Button clicked
        Button crtbutton = (Button) findViewById(R.id.create_button);

        crtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuisine = ((EditText)findViewById(R.id.cuisine_input)).getText().toString();
                Integer partySize = Integer.parseInt(((EditText)findViewById(R.id.party_size_input)).getText().toString());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("group");
                myRef.setValue("Hello, World!");
            }
        });
    }
}
