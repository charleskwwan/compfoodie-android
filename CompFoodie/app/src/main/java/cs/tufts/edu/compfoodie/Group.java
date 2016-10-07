package cs.tufts.edu.compfoodie;

import android.location.Location;
import android.os.CountDownTimer;

import java.util.List;

/**
 * Created by charlw on 9/27/16.
 */
public class Group {
    private String cuisine; // enumize later for one value per cuisine
    private Location meetingLocation; // convert with Google Maps json for lat/long
    private Integer partyCap;
    private Integer partySize;
    private CountDownTimer waitTime;
    private String msg;
    private List<User> party;
    private User creator;
    private List<String> restaurantSuggestions;

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public Location getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(Location meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    public Integer getPartyCap() {
        return partyCap;
    }

    public void setPartyCap(Integer partyCap) {
        this.partyCap = partyCap;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public CountDownTimer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(CountDownTimer waitTime) {
        this.waitTime = waitTime;
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

    public List<String> getRestaurantSuggestions() {
        return restaurantSuggestions;
    }

    public void setRestaurantSuggestions(List<String> restaurantSuggestions) {
        this.restaurantSuggestions = restaurantSuggestions;
    }
}
