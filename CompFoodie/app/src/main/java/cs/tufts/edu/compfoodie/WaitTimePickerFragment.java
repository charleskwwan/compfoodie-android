package cs.tufts.edu.compfoodie;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by charlw on 10/4/16.
 *      represents dialog fragment for picking wait time
 */

public class WaitTimePickerFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    public static WaitTimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener) {
        WaitTimePickerFragment wtpicker = new WaitTimePickerFragment();
        wtpicker.timeSetListener = listener;
        return wtpicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR);
        int minOfDay = cal.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), timeSetListener, hourOfDay, minOfDay,
                                                    DateFormat.is24HourFormat(getActivity()));
        return tpd;
    }
}
