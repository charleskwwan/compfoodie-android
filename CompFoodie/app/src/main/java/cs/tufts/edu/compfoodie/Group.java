package cs.tufts.edu.compfoodie;

import android.location.Location;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by charlw on 9/27/16.
 */
@IgnoreExtraProperties
public class Group {
    //public String groupId = "123456789";
    public String location;
    public Double partyCap;
    public Double partySize;
    public Long orderTime;
    public String message;
    //public List<User> party = new List<>();
    //public User creator;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String location, Double partyCap, Double partySize, Long orderTime, String message) {
        this.location = location;
        this.partyCap = partyCap;
        this.partySize = partySize;
        this.orderTime = orderTime;
        this.message = message;
        //this.party = party;
        //this.creator = creator;
    }

    /*
    public JSONObject getJSON() {
        JSONObject groupJSON = new JSONObject();
        try {
            groupJSON.put("location", this.location);
            groupJSON.put("partyCap", this.partyCap);
            groupJSON.put("partySize", this.partySize);
            groupJSON.put("orderTime", this.orderTime);
            groupJSON.put("message", this.message);
            //groupJSON.put("creator", this.creator);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupJSON;
    }*/
}
