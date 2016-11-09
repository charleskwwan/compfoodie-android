package cs.tufts.edu.compfoodie;

/**
 * Created by charlw on 10/13/16.
 * allows responses data to be returned directly by caller
 */

interface VolleyCallback<T> {
    void onSuccessResponse(T response);
}
