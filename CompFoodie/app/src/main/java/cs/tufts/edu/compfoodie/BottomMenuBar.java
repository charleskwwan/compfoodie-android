package cs.tufts.edu.compfoodie;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

/**
 * Created by charlw on 9/30/16.
 *      Represents the Bottom Menu Bar object on all activities for navigation
 *      Tutorial credits: http://androidgifts.com/build-android-material-design-bottom-navigation/
 */
public class BottomMenuBar {
    private BottomBar bottomBar;

    public BottomMenuBar(Activity activity, Bundle savedStateInstance) {
        bottomBar = BottomBar.attach(activity, savedStateInstance);
        bottomBar.setItemsFromMenu(R.menu.bottom_bar, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int menuItemId) {
                switch (menuItemId) {
                    case R.id.browse_item:
                        Log.e("Height", String.valueOf(bottomBar.getHeight()));
                        // goto browse page
                        break;
                    case R.id.groups_item:
                        // goto groups page
                        break;
                    case R.id.settings_item:
                        // goto settings page
                        break;
                }
            }
        });
        bottomBar.setActiveTabColor(activity.getColor(R.color.colorAccent));
    }
}