package cs.tufts.edu.compfoodie;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by charlw on 9/27/16.
 *      represents a user
 */
public class User implements Serializable {
    private String name;
    private String picUrl;
    private List<String> groups;

    public User() {
        groups = new ArrayList<String>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }
    public List<String> getGroups() { return groups; }
    public void setGroups(List<String> groups) { this.groups = groups; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("picurl", picUrl);
        result.put("groups", groups);
        return result;
    }
}
