package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity  {

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
                String message = ((EditText)findViewById(R.id.message_input)).getText().toString();
                Integer partyCap = Integer.parseInt(((EditText)findViewById(R.id.party_size_input)).getText().toString());
                Integer partySize = 0;
                String location = ((EditText)findViewById(R.id.location_input)).getText().toString();

                // Get order time
                String orderTimeStr = ((TextView)findViewById(R.id.order_time_output)).getText().toString();
                Long orderTime = getOrderTime(orderTimeStr);

                //Integer orderTime = 0;
                List<User> party = new ArrayList<User>();
                User creator = new User();

                Group group = new Group(creator, location, partyCap, partySize, orderTime, message, party);
                JSONObject groupJSON = group.getJSON();

//                String addGroupURL = "https://compfoodie-server.herokuapp.com/api/addgroup";
                String addGroupURL = "http://10.0.2.2/api/addgroup";

                // Sending JSON new group to server
                VolleyUtils.POST(addGroupURL, groupJSON);
            }
        });
    }

    // Returns order time in Unix
    private Long getOrderTime(String timeStr) {
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
