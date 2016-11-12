package cs.tufts.edu.compfoodie;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by charlw on 9/27/16.
 * represents a user
 */
@IgnoreExtraProperties
class User implements Serializable {
    public String name;
    public String picUrl;
    public List<String> groups;

    public User() { // default required for data snapshot

    }

    public User(String name, String picUrl, List<String> groups) {
        this.name = name;
        this.picUrl = picUrl;
        this.groups = groups;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("/name", name);
        map.put("/picUrl", picUrl);
        map.put("/groups", groups);
        return map;
    }
}
