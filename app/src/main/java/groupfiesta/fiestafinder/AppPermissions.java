package groupfiesta.fiestafinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by RafaelAguilera on 4/14/16.
 */
public class AppPermissions {
    public static final int ACCESS_FINE_LOCATION_CODE = 1337;

    static void checkPermission(Activity activityContext, String permission,int permissionCode){
        if (ContextCompat.checkSelfPermission(activityContext, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activityContext, permission)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activityContext,new String[]{permission}, permissionCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                }
        }
    }


}
