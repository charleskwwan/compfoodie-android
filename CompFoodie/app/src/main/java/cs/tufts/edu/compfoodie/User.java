 package cs.tufts.edu.compfoodie;

import android.graphics.Bitmap;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by charlw on 9/27/16.
 *      represents a user
 */
@IgnoreExtraProperties
public class User implements Serializable {
    public String name;
    public String picUrl;
    public List<String> groups;

    public User() { // default requried for data snapshot

    }

    public User(String name, String picUrl, List<String> groups) {
        this.name = name;
        this.picUrl = picUrl;
        this.groups = groups;
    }
}
