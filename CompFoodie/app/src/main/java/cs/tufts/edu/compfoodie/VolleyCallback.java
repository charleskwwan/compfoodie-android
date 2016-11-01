package cs.tufts.edu.compfoodie;

/**
 * Created by charlw on 10/13/16.
 *      allows repsonses data to be returned directly by caller
 */

public interface VolleyCallback<T> {
    public void onSuccessResponse(T response);
}
