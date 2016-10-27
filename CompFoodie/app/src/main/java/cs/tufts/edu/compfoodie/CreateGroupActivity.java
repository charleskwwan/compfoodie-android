package cs.tufts.edu.compfoodie;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.R.attr.author;
import static android.R.attr.category;
import static android.R.attr.data;
import static android.R.attr.enabled;
import static android.R.attr.key;
import static android.R.attr.name;
import static android.R.attr.text;

public class CreateGroupActivity extends AppCompatActivity  {

    private FirebaseDatabase database;
    private DatabaseReference groupRef;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.create_group_toolbar);
        toolbar.setTitle(getString(R.string.create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // todo: need to set parent in manifest

        // Wait Time Picker
        final Calendar cal = Calendar.getInstance();
        orderHour = cal.get(Calendar.HOUR);
        orderMinute = cal.get(Calendar.MINUTE);
        orderUnix = new Date().getTime();
        ImageButton wtbutton = (ImageButton)findViewById(R.id.wait_time_pick_button);
        wtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaitTimePickerFragment wtpfrag = WaitTimePickerFragment
                        .newInstance(CreateGroupActivity.this);
                wtpfrag.show(getFragmentManager(), "order_time_picker");
            }
        });
        // Create Button clicked
        Button crtbutton = (Button) findViewById(R.id.create_button);

        crtbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ((EditText)findViewById(R.id.message_input)).getText().toString();
                Double partyCap = (double) Integer.parseInt(((EditText)findViewById(R.id.party_size_input)).getText().toString());
                Double partySize = 0.0;
                String location = ((EditText)findViewById(R.id.location_input)).getText().toString();

                // Get order time
                String orderTimeStr = ((TextView)findViewById(R.id.order_time_output)).getText()
                        .toString();
                Long orderTime = getOrderTime(orderTimeStr);
                //Integer orderTime = 0;
                List<String> guests = new ArrayList<>();
                FirebaseUser creator = FirebaseAuth.getInstance().getCurrentUser();
                String userID = creator.getUid();
                guests.add(userID);
                Group group = new Group(location, partyCap, partySize, orderTime, message, creator.getUid(), guests);

                database = FirebaseDatabase.getInstance();
                groupRef = database.getReference("groups");

                //groupRef.push().setValue(group);
                String groupID = groupRef.push().getKey();
                groupRef.child(groupID).setValue(group);
                Log.v("groupID", groupID);

                userRef = database.getReference("users");
                userRef.child(userID).child("groups").push().setValue(groupID);

                // Redirect to status page

                Intent statusPage = new Intent(getApplicationContext(), GroupStatusActivity.class);
                statusPage.putExtra("userID", userID);
                startActivity(statusPage);
            }
        });
    }

    // Set hour and time
    @Override
    public void onTimeSet(TimePicker tp, int hour, int minute) {
        orderHour = hour;
        orderMinute = minute;

    }


    // Returns order time in Unix
    private Long getOrderTime(String timeStr) {
        Log.v("timeStr", timeStr);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        Calendar today = Calendar.getInstance();

        Integer hr = Integer.parseInt(timeStr.substring(0, 2));
        Integer min = Integer.parseInt(timeStr.substring(3, 5));
        Integer ampm = timeStr.substring(6) == "AM" ? Calendar.AM : Calendar.PM;

        today.set(Calendar.HOUR, hr);
        today.set(Calendar.MINUTE, min);
        today.set(Calendar.AM_PM, ampm);

        Calendar rightNow = Calendar.getInstance();

        if (rightNow.getTime().before(today.getTime())) {
            Log.v("Order Time", dateFormat.format(today.getTime()));
            return today.getTimeInMillis();
        }

        today.add(Calendar.DATE, +1);
        Log.v("Order Time", dateFormat.format(today.getTime()));
        return today.getTimeInMillis();
    }
}
