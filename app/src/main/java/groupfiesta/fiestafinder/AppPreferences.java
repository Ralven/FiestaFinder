package groupfiesta.fiestafinder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by RafaelAguilera on 4/26/16.
 */
public class AppPreferences {
    private static final String PREFS_NAME = "MyPrefsFile";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private SharedPreferences data;
    private SharedPreferences.Editor dataEdit;


    public AppPreferences(Context context) {
        this.data = context.getSharedPreferences(PREFS_NAME, 0);
        this.dataEdit = data.edit();
    }

    public String getUserData(String text) {
        return data.getString(text, "");

    }

    public void saveUserData(String username,String password) {
        dataEdit.putString(USERNAME, username);
        dataEdit.putString(PASSWORD, password);
        dataEdit.commit();
    }

    public void eraseUserData(Context context){
        dataEdit.clear();
        dataEdit.commit();
    }

}
