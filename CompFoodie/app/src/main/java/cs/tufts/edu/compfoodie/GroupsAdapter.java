package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.data;

/**
 * Created by charlw on 10/31/16.
 *      for populating a list view with groups
 */

public class GroupsAdapter extends ArrayAdapter<String> {
    private Context context;
    private String groupIds[] = null;
    private DatabaseReference groupsRef;
    private DatabaseReference dtb = FirebaseDatabase.getInstance().getReference();

    // constructor
    public GroupsAdapter(Context context, String[] groupIds) {
        super(context, 0, groupIds); // 0 for null item layout, all in item_user
        this.context = context;
        this.groupIds = groupIds;
        groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // group id at position
        final String groupID = getItem(position);

        // user id
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("groups");

        // check if view is reused, otherwise inflate
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        }

        // get all textviews
        final TextView msgOutput = (TextView)convertView.findViewById(R.id.group_message);
        final TextView orderTimeLocationOutput = (TextView)convertView.findViewById(R.id.group_order_time_location);
        final TextView partyCntOutput = (TextView)convertView.findViewById(R.id.group_party_cnt);
        final ImageView creatorPicOutput = (ImageView)convertView.findViewById(R.id.creator_pic);
        final TextView creatorNameOutput = (TextView)convertView.findViewById(R.id.creator_name);

        // group join button
        final Button groupJoinBtn = (Button)convertView.findViewById(R.id.group_join_btn);

        // get group from firebase
        final DatabaseReference groupRef = groupsRef.child(groupID);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                msgOutput.setText(group.message);

                // assign party cnt
                String partyCnt = String.format(Locale.ENGLISH, "%d/%d",
                        group.partySize.intValue(), group.partyCap.intValue());
                partyCntOutput.setText(partyCnt);

                // assign order time and location
                String location = group.location;
                int orderHour = group.hour.intValue();
                String tformat = orderHour < 12 ? "AM" : "PM";
                String hstr = String.format(Locale.ENGLISH, "%02d", orderHour % 12);
                String mstr = String.format(Locale.ENGLISH, "%02d", group.minute.intValue());
                orderTimeLocationOutput.setText(location + " at " + hstr + ":" + mstr + " " + tformat);

                Log.v("GROUP ID", groupID);
                Log.v("CREATOR ", group.creator);
                DatabaseReference creatorRef = dtb.child("users").child(group.creator);
                creatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String creatorPicURL = (String)dataSnapshot.child("picUrl").getValue();
                        VolleyUtils.getImage(creatorPicURL, context,
                                new VolleyCallback<Bitmap>() {
                                    @Override
                                    public void onSuccessResponse(Bitmap response) {
                                        Bitmap rounded = ImageTransform.getRoundedCornerBitmap(response,
                                                response.getWidth() / 2);
                                        creatorPicOutput.setImageBitmap(rounded);
                                    }
                                });
                        creatorNameOutput.setText((String) dataSnapshot.child("name").getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("*** User Adapter", databaseError.toString());
                    }
                });

                // if the user is the creator, disable button press
                // if the user is in the guests, press the button
                if (group.creator.equals(userID)) {
                    groupJoinBtn.setPressed(true);
                    groupJoinBtn.setEnabled(false);
                    groupJoinBtn.setClickable(false);
                } else if (group.guests == null || !group.guests.contains(userID)) {
                    groupJoinBtn.setPressed(false);
                } else {
                    groupJoinBtn.setPressed(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** Group Adapter", databaseError.toString());
            }
        });


        groupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Clicked button", "clicked button");

                //((ListView) parent).performItemClick(v, position, 0);
                ValueEventListener userListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnap) {
                        final List<String> userGroups = userSnap.getValue() == null ? new ArrayList<String>(): (List<String>) userSnap.getValue();

                        final DatabaseReference clickedGroupRef = dtb.child("groups").child(groupID);

                        clickedGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot clickedGroupSnap) {
                                Log.v("CLICKED ", "CLICKED");
                                Group clickedGroup = clickedGroupSnap.getValue(Group.class);

                                if (clickedGroup.guests == null) {
                                    clickedGroup.guests = new ArrayList<>();
                                }
                                if (userGroups.contains(groupID)) {
                                    userGroups.remove(groupID);
                                    clickedGroup.guests.remove(userID);
                                    clickedGroup.partySize = Double.valueOf(clickedGroup.guests.size());
                                } else {
                                    userGroups.add(groupID);
                                    clickedGroup.guests.add(userID);
                                    clickedGroup.partySize = Double.valueOf(clickedGroup.guests.size());
                                }
                                userRef.setValue(userGroups);
                                clickedGroupRef.setValue(clickedGroup);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                    }
                };
                userRef.addListenerForSingleValueEvent(userListener);
            }
        });

        return convertView;
    }
}
