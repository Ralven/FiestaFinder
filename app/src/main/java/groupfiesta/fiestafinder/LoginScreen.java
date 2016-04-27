package groupfiesta.fiestafinder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static groupfiesta.fiestafinder.R.layout.activity_login;

public class LoginScreen extends AppCompatActivity {
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/user_control.php";
    private static final String PREFS_NAME = "MyPrefsFile";

    private AppPreferences myPrefs;
    private EditText username,password;
    private Button login_button,register_button;
    private RequestQueue requestQueue;
    private StringRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myPrefs = new AppPreferences(getApplicationContext());
        username = (EditText) findViewById(R.id.username_input);
        password = (EditText) findViewById(R.id.password_input);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);
        username.setText(myPrefs.getUserData(myPrefs.USERNAME));
        password.setText(myPrefs.getUserData(myPrefs.PASSWORD));
        requestQueue = Volley.newRequestQueue(this);

        if(myPrefs.getUserData(myPrefs.USERNAME).equals("")) {
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.names().get(0).equals("success")) {
                                    Intent locationLaunchIntent = new Intent(getApplicationContext(), LocationList.class);
                                    locationLaunchIntent.putExtra("username", username.getText().toString());
                                    startActivity(locationLaunchIntent);
                                } else {
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            return hashMap;
                        }
                    };
                    requestQueue.add(request);

                }
            });
        }else{
            Intent locationLaunchIntent = new Intent(getApplicationContext(), LocationList.class);
            locationLaunchIntent.putExtra("username", username.getText().toString());
            startActivity(locationLaunchIntent);
        }

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerLaunchIntent = new Intent(getApplicationContext(),Register.class);
                startActivity(registerLaunchIntent);
            }
        });

    }
    @Override
    protected void onStop(){
        super.onStop();
        myPrefs.saveUserData(username.getText().toString(),password.getText().toString());
    }



}
