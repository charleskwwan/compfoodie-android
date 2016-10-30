package cs.tufts.edu.compfoodie;

/**
 * Created by charlw on 10/30/16.
 *      to be thrown in the case of an incomplete create group form
 */

public class FormException extends RuntimeException {
    public FormException() { super(); }
    public FormException(String message) { super(message); }
}
