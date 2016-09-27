package cs.tufts.edu.compfoodie;

/**
 * Created by charlw on 9/27/16.
 */
public class User {
    private String name;
    private String email;
    private String picture; // path to image

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
