package cs.tufts.edu.compfoodie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class GroupsAdapter extends FirebaseListAdapter<Group> {
    private Activity activity;
    private String groupIds[] = null;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String userID;
    private Context context;

    // constructor
    public GroupsAdapter(Activity activity, DatabaseReference groupRef) {
        super(activity, Group.class, R.layout.item_group, groupRef);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.context = activity;
    }

    @Override
    protected void populateView(View v, Group model, final int position) {
        // get all views
        final TextView msgOutput = (TextView)v.findViewById(R.id.group_message);
        final TextView orderTimeLocationOutput = (TextView)v.findViewById(R.id.group_order_time_location);
        final TextView partyCntOutput = (TextView)v.findViewById(R.id.group_party_cnt);
        final ImageView creatorPicOutput = (ImageView)v.findViewById(R.id.creator_pic);
        final TextView creatorNameOutput = (TextView)v.findViewById(R.id.creator_name);
        final Button groupJoinBtn = (Button)v.findViewById(R.id.group_join_btn);

        // set message
        msgOutput.setText(model.message);

        // assign party cnt
        String partyCnt = String.format(Locale.ENGLISH, "%d/%d",
                model.partySize.intValue(), model.partyCap.intValue());
        partyCntOutput.setText(partyCnt);

        // assign order time and location
        String location = model.location;
        int orderHour = model.hour.intValue();
        String tformat = orderHour < 12 ? "AM" : "PM";
        String hstr = String.format(Locale.ENGLISH, "%02d", orderHour % 12);
        String mstr = String.format(Locale.ENGLISH, "%02d", model.minute.intValue());
        orderTimeLocationOutput.setText(location + " at " + hstr + ":" + mstr + " " + tformat);

        DatabaseReference creatorRef = dbRef.child("users").child(model.creator);
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
        if (model.creator.equals(userID)) {
            groupJoinBtn.setPressed(true);
            groupJoinBtn.setEnabled(false);
            groupJoinBtn.setClickable(false);
        } else if (model.guests == null || !model.guests.contains(userID)) {
            groupJoinBtn.setPressed(false);
        } else {
            groupJoinBtn.setPressed(true);
        }

        groupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference groupRef = getRef(position);
                final String groupID = groupRef.getKey();
                final DatabaseReference userRef = dbRef.child("users").child(userID);
                final Group group = getItem(position);

                for (String guest: group.guests) {
                    Log.v("guest", guest);
                }

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot userSnap) {

                        final User user = userSnap.getValue(User.class);

                        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot clickedGroupSnap) {
                                if (group.guests == null) {
                                    group.guests = new ArrayList<>();
                                }
                                if (user.groups == null) {
                                    user.groups = new ArrayList<>();
                                }

                                if (group.guests.contains(userID)) {
                                    user.groups.remove(groupID);
                                    group.guests.remove(userID);
                                    group.partySize--;
                                } else {
                                    user.groups.add(groupID);
                                    group.guests.add(userID);
                                    group.partySize++;
                                }
                                userRef.setValue(user);
                                groupRef.setValue(group);
                                //notifyDataSetChanged();
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
                });
            }
        });
        //v.setVisibility(View.GONE);
    }
};