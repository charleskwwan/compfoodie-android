package cs.tufts.edu.compfoodie;

import android.location.Location;
import android.os.CountDownTimer;

import java.util.List;

/**
 * Created by charlw on 9/27/16.
 */
public class Group {
    private String cuisine; // enumize later for one value per cuisine
    private Location meeting_location; // convert with Google Maps json for lat/long
    private Integer party_cap;
    private Integer party_size;
    private CountDownTimer wait_time;
    private String msg;
    private List<User> party;
    private User creator;
    private List<String> restaurant_suggestions;

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Location getMeeting_location() {
        return meeting_location;
    }

    public void setMeeting_location(Location meeting_location) {
        this.meeting_location = meeting_location;
    }

    public Integer getParty_cap() {
        return party_cap;
    }

    public void setParty_cap(Integer party_cap) {
        this.party_cap = party_cap;
    }

    public Integer getParty_size() {
        return party_size;
    }

    public void setParty_size(Integer party_size) {
        this.party_size = party_size;
    }

    public CountDownTimer getWait_time() {
        return wait_time;
    }

    public void setWait_time(CountDownTimer wait_time) {
        this.wait_time = wait_time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getParty() {
        return party;
    }

    public void setParty(List<User> party) {
        this.party = party;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<String> getRestaurant_suggestions() {
        return restaurant_suggestions;
    }

    public void setRestaurant_suggestions(List<String> restaurant_suggestions) {
        this.restaurant_suggestions = restaurant_suggestions;
    }
}
