package cs.tufts.edu.compfoodie;

import android.location.Location;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by charlw on 9/27/16.
 *      represents an order group
 *      made public for interaction with firebase
 */
@IgnoreExtraProperties
public class Group {
    public String location;
    public Double partyCap;
    public Double partySize;
    public String message;
    public String creator;
    public Double hour;
    public Double minute;
    public List<String> guests;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String location, Double partyCap, Double partySize, String message, String creator,
                 List<String> guests, Double hour, Double minute) {
        this.location = location;
        this.partyCap = partyCap;
        this.partySize = partySize;
        this.message = message;
        this.creator = creator;
        this.guests = guests;
        this.hour = hour;
        this.minute = minute;
    }
}
