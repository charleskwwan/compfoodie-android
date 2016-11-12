package cs.tufts.edu.compfoodie;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private FirebaseDatabase database;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;
    private User user;
    private Group group;
    private String groupId;
    private double unixTime;
    private EditText locationText;
    private EditText partyCapText;
    private TextView orderTimeText;
    private EditText messageText;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_group_toolbar);
        toolbar.setTitle(getString(R.string.create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get user from intent
        user = (User) getIntent().getSerializableExtra(getString(R.string.currentUserKey));

        // get text fields
        locationText = (EditText) findViewById(R.id.location_input);
        partyCapText = (EditText) findViewById(R.id.party_size_input);
        orderTimeText = (TextView) findViewById(R.id.order_time_output);
        messageText = (EditText) findViewById(R.id.message_input);

        // set wait time picker button listener
        ImageButton wtbutton = (ImageButton) findViewById(R.id.wait_time_pick_button);
        wtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog wtpfrag = new TimePickerDialog(CreateGroupActivity.this,
                        R.style.CompFoodie_TimePickerDialogTheme, CreateGroupActivity.this, 0, 0, true);
                wtpfrag.show();
            }
        });

        // Create Button clicked
        Button crtbutton = (Button) findViewById(R.id.create_button);
        crtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verifyInput();
                } catch (FormException missing) {
                    TextView alertText = (TextView) findViewById(R.id.alert_text);
                    String alert = "You need to supply the " + missing.getMessage();
                    alertText.setText(alert);
                    return;
                }
                sendInfoToFirebase();
                goToStatus();
            }
        });
    }

    // add user back when going back to parent
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(getString(R.string.currentUserKey), user); // add user
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) { // not part of app tasks
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent); // part of app, just go
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // sets the order time variables and the order time output
    @Override
    public void onTimeSet(TimePicker tp, int hour, int minute) {
        // set unix time for group
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, minute);
        cal.add(Calendar.HOUR, hour);
        unixTime = cal.getTime().getTime() / 1000L;

        // set display
        TextView order_time_output = (TextView) findViewById(R.id.order_time_output);
        String orderTimeString = "Order in " + Integer.toString(hour) + "h " +
                Integer.toString(minute) + "m";
        order_time_output.setText(orderTimeString);
    }

    // checks that there is input in all required text fields
    // message field is optional
    private void verifyInput() {
        if (locationText.getText().toString().equals("")) {
            throw new FormException("location");
        } else if (partyCapText.getText().toString().equals("")) {
            throw new FormException("party size");
        } else if (orderTimeText.getText().toString().equals("")) {
            throw new FormException("order time");
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void sendInfoToFirebase() {
        // get values for group creation
        String location = locationText.getText().toString();
        double partyCap = (double) Integer.parseInt(partyCapText.getText().toString());
        double partySize = 1.0; // creator is the first member
        String message = messageText.getText().toString();
        // get user for creator
        FirebaseUser creator = FirebaseAuth.getInstance().getCurrentUser();
        String creatorId = creator.getUid();
        List<String> guests = new ArrayList<>();
        guests.add(creatorId);
        // create group
        group = new Group(location, partyCap, partySize, message, creatorId, guests, unixTime);

        // add to database
        database = FirebaseDatabase.getInstance();
        groupRef = database.getReference("groups");
        groupId = groupRef.push().getKey();
//        groupRef.child(groupId).setValue(group);
        groupRef.child(groupId).updateChildren(group.toMap());
        Log.v("*** New Group Added", groupId);

        // adds the group to the users entry
        userRef = database.getReference("users");
        user.groups.add(groupId);
//        userRef.child(creatorId).setValue(user);
        userRef.child(creatorId).updateChildren(user.toMap());
    }

    private void goToStatus() {
        Intent statusPage = new Intent(getApplicationContext(), GroupStatusActivity.class);
        statusPage.putExtra(getString(R.string.currentUserKey), user);
        statusPage.putExtra(getString(R.string.currentGroupKey), group);
        statusPage.putExtra(getString(R.string.currentGroupIdKey), groupId);
        startActivity(statusPage);
    }
}
