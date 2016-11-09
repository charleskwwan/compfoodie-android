package cs.tufts.edu.compfoodie;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by charlw on 10/31/16.
 * list adapter for users. primarily for status page
 */

class UsersAdapter extends ArrayAdapter<String> {
    private Context context;
    private DatabaseReference usersRef;
    private String userIds[] = null;

    // constructor
    public UsersAdapter(Context context, String[] userIds) {
        super(context, 0, userIds); // 0 for null item layout, all in item_user
        this.context = context;
        this.userIds = userIds;
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // get user id at position
        String userId = getItem(position);
        // check if view is reused, otherwise inflate
        if (convertView == null) { // guaranteed nonnull return
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }
        // fill fields
        final TextView userNameOutput = (TextView) convertView.findViewById(R.id.user_name);
        final ImageView userPicOutput = (ImageView) convertView.findViewById(R.id.user_pic);
        DatabaseReference userRef = usersRef.child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userNameOutput.setText((String) dataSnapshot.child("name").getValue());
                String userPicUrl = (String) dataSnapshot.child("picUrl").getValue();
                VolleyUtils.getImage(userPicUrl, context,
                        new VolleyCallback<Bitmap>() {
                            @Override
                            public void onSuccessResponse(Bitmap response) {
                                Bitmap rounded = ImageTransform.getRoundedCornerBitmap(response,
                                        response.getWidth() / 2);
                                userPicOutput.setImageBitmap(rounded);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("*** User Adapter", databaseError.toString());
            }
        });
        return convertView;
    }
}
