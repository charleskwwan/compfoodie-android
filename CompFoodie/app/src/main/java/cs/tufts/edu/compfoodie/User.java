package cs.tufts.edu.compfoodie;

import java.io.Serializable;

/**
 * Created by charlw on 9/27/16.
 */
public class User implements Serializable {

    private String name;
    private String email;
    private String picture; // path to image

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
