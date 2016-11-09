package cs.tufts.edu.compfoodie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class GroupsAdapter extends FirebaseListAdapter<Group> {
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String userID;
    private Activity activity;
    private Boolean filterMyGroups;
    private Map<String, String> names = new HashMap<>(); // creator key -> name
    private Map<String, Bitmap> profilePics = new HashMap<>(); // creator key -> pic

    // constructor
    public GroupsAdapter(Activity activity, DatabaseReference groupRef, Boolean filterMyGroups) {
        super(activity, Group.class, R.layout.item_group, groupRef);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.activity = activity;
        this.filterMyGroups = filterMyGroups;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Group model = getItem(i);
        if (filterMyGroups && !model.guests.contains(userID)) {
            view = activity.getLayoutInflater().inflate(R.layout.item_empty_group, viewGroup, false);
        } else {
            view = activity.getLayoutInflater().inflate(R.layout.item_group, viewGroup, false);
        }
        view.setTag(i);
        populateView(view, model, i);
        return view;
    }

    @Override
    protected void populateView(final View v, final Group model, final int position) {
        // Filter out groups that the user is not in.
        if (filterMyGroups && !model.guests.contains(userID)) {
            v.setVisibility(View.GONE);
            return;
        }
        v.setVisibility(View.VISIBLE);

        // get all views
        final TextView msgOutput = (TextView)v.findViewById(R.id.group_message);
        final TextView orderTimeLocationOutput = (TextView)v.findViewById(R.id.group_order_time_location);
        final TextView partyCntOutput = (TextView)v.findViewById(R.id.group_party_cnt);
        final Button groupJoinBtn = (Button)v.findViewById(R.id.group_join_btn);
        final TextView creatorNameOutput = (TextView) v.findViewById(R.id.creator_name);
        final ImageView creatorPicOutput = (ImageView) v.findViewById(R.id.creator_pic);

        // set message
        msgOutput.setText(model.message);

        // assign party cnt
        String partyCnt = String.format(Locale.ENGLISH, "%d/%d ppl",
                model.partySize.intValue(), model.partyCap.intValue());
        partyCntOutput.setText(partyCnt);

        // assign order time and location
        String location = model.location;
        int orderHour = model.hour.intValue();
        String tformat = orderHour < 12 ? "AM" : "PM";
        String hstr = String.format(Locale.ENGLISH, "%02d", orderHour % 12);
        String mstr = String.format(Locale.ENGLISH, "%02d", model.minute.intValue());
        orderTimeLocationOutput.setText("Ordering at " + hstr + ":" + mstr + " " + tformat + " from " + location);

        // set creator image
        // if already exist in cache, use, otherwise map async request
        if (names.containsKey(model.creator) && profilePics.containsKey(model.creator)) {
            creatorNameOutput.setText(names.get(model.creator));
            creatorPicOutput.setImageBitmap(profilePics.get(model.creator));
        } else {
            DatabaseReference creatorRef = dbRef.child("users").child(model.creator);
            creatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String creatorPicURL = (String) dataSnapshot.child("picUrl").getValue();
                    if ((int) v.getTag() == position) {
                        VolleyUtils.getImage(creatorPicURL, activity,
                                new VolleyCallback<Bitmap>() {
                                    @Override
                                    public void onSuccessResponse(Bitmap response) {
                                        Bitmap rounded = ImageTransform.getRoundedCornerBitmap(response,
                                                response.getWidth() / 2);
                                        profilePics.put(model.creator, rounded);
                                        creatorPicOutput.setImageBitmap(rounded);
                                    }
                                });
                        String name = (String) dataSnapshot.child("name").getValue();
                        names.put(model.creator, name);
                        creatorNameOutput.setText(name);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("*** User Adapter", databaseError.toString());
                }
            });
        }


        // Disable button press if the user is the creator,
        // or if the group is full and the user is not in the group
        if ((model.guests.size() == model.partyCap && !model.guests.contains(userID)) || model.creator.equals(userID)) {
            groupJoinBtn.setEnabled(false);
            groupJoinBtn.setClickable(false);
        } else {
            groupJoinBtn.setEnabled(true);
            groupJoinBtn.setClickable(true);
        }

        // if the user is in the guests, press the button
        if (model.guests == null || !model.guests.contains(userID)) {
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
    }
};