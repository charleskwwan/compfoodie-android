package cs.tufts.edu.compfoodie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GroupStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_status);

        BottomMenuBar bottombar = new BottomMenuBar(this, savedInstanceState);

        // ToolBar
        Toolbar toolbar = (Toolbar)findViewById(R.id.group_status_toolbar);
        toolbar.setTitle(getString(R.string.group_status_title));
        setSupportActionBar(toolbar);

        // groups handling
        Group group = (Group) getIntent().getSerializableExtra("group");

        TextView locationStatus = (TextView)findViewById(R.id.location_status);
        TextView partySizeStatus = (TextView)findViewById(R.id.party_size_status);
        TextView orderTimeStatus = (TextView)findViewById(R.id.order_time_status);
        TextView messageStatus = (TextView)findViewById(R.id.message_status);

        locationStatus.setText(String.format("Meet at %s", group.getLocation()));
        partySizeStatus.setText(String.format("%d out of %d people have joined you", group.getPartySize(), group.getPartyCap()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        orderTimeStatus.setText("Order at " + dateFormat.format(group.getOrderTime()));
        messageStatus.setText(group.getMessage());
    }
}
