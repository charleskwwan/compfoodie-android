package cs.tufts.edu.compfoodie;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("/location", location);
        map.put("/partyCap", partyCap);
        map.put("/partySize", partySize);
        map.put("/message", message);
        map.put("/creator", creator);
        map.put("/unixTime", unixTime);
        map.put("/guests", guests);
        return map;
    }
}
