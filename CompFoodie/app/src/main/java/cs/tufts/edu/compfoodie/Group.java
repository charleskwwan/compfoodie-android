package cs.tufts.edu.compfoodie;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by charlw on 9/27/16.
 * represents an order group
 * made public for interaction with firebase
 */
@IgnoreExtraProperties
class Group implements Serializable {
    public String location;
    public Double partyCap;
    public Double partySize;
    public String message;
    public String creator;
    public Double unixTime;
    public List<String> guests;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String location, Double partyCap, Double partySize, String message, String creator,
                 List<String> guests, Double unixTime) {
        this.location = location;
        this.partyCap = partyCap;
        this.partySize = partySize;
        this.message = message;
        this.creator = creator;
        this.guests = guests;
        this.unixTime = unixTime;
    }
}
