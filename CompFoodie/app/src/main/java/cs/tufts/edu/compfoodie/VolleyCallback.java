package cs.tufts.edu.compfoodie;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by charlw on 10/13/16.
 *      allows repsonses data to be returned directly by caller
 */

public interface VolleyCallback<T> {
    public void onSuccessResponse(T response);
}
