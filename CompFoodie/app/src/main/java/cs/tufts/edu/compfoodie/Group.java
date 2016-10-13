package cs.tufts.edu.compfoodie;

import android.location.Location;
import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by charlw on 9/27/16.
 */
public class Group implements Serializable {
    // private Location meetingLocation; // convert with Google Maps JSON for lat/long
    private String groupId;
    private String location;
    private Integer partyCap;
    private Integer partySize;
    private Long orderTime;
    private String message;
    private List<User> party;
    private User creator;

    public Group(User creator, String location, Integer partyCap, Integer partySize, Long orderTime, String message, List<User> party) {
        this.creator = creator;
        this.location = location;
        this.partyCap = partyCap;
        this.partySize = partySize;
        this.orderTime = orderTime;
        this.message = message;
        this.party = party;
    }

    public String     getGroupId() { return groupId; }
    public String     getLocation() { return location; }
    public Integer    getPartyCap() { return partyCap; }
    public Integer    getPartySize() { return partySize; }
    public Long       getOrderTime() { return orderTime; }
    public String     getMessage() { return message; }
    public List<User> getParty() { return party; }
    public User       getCreator() { return creator; }

    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setMeetingLocation(String location) {
        this.location = location;
    }
    public void setPartyCap(Integer partyCap) {
        this.partyCap = partyCap;
    }
    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }
    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }
    public void setMsg(String message) {
        this.message = message;
    }
    public void setParty(List<User> party) {
        this.party = party;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }

    public JSONObject getJSON() {
        JSONObject groupJSON = new JSONObject();
        try {
            groupJSON.put("location", this.location);
            groupJSON.put("partyCap", this.partyCap);
            groupJSON.put("partySize", this.partySize);
            groupJSON.put("orderTime", this.orderTime);
            groupJSON.put("message", this.message);
            groupJSON.put("creator", this.creator);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groupJSON;
    }
}
