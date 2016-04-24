package groupfiesta.fiestafinder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostCreate extends AppCompatActivity{

    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_control.php";

    private Bundle dataBundle;
    private EditText post_text, post_title;
    private TextView location_name;
    private Button post_button, cancel_button;
    private CheckBox postAs_checkBox;
    private String location_id;
    private LatLng location_coordinates;
    private String username;
    private String checkedUsername = null;



    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcreate);

        dataBundle = getIntent().getExtras();
        post_text = (EditText) findViewById(R.id.post_text_detalis);
        post_title = (EditText) findViewById(R.id.post_title_details);
        post_button = (Button) findViewById(R.id.post_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        location_name = (TextView) findViewById(R.id.location_name);
        postAs_checkBox = (CheckBox) findViewById(R.id.postAs_checkBox);
        checkedUsername ="Anonymous";
        postAs_checkBox.setText("Post as: Anonymous");
        username = dataBundle.getString("username");
        location_id = dataBundle.getString("location_id");
        location_coordinates = (LatLng) dataBundle.get("location_coordinates");
        location_name.setText(dataBundle.get("location").toString());
        requestQueue = Volley.newRequestQueue(this);

        postAs_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(postAs_checkBox.isChecked()){
                    checkedUsername = username;
                }else{
                    checkedUsername ="Anonymous";
                }
                postAs_checkBox.setText("Post as: "+checkedUsername);
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post_text.getText().toString().isEmpty() && post_title.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Must Fill all Fields", Toast.LENGTH_SHORT).show();
                }else{
                    if(post_title.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Must Fill Post Title", Toast.LENGTH_SHORT).show();
                    }
                    if(post_text.getText().toString().isEmpty()){
                      Toast.makeText(getApplicationContext(), "Must Fill Post Text", Toast.LENGTH_SHORT).show();
                    }
                }
                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
//                                    Log.i("resp", jsonObject.toString());
                                    if (jsonObject.names().get(0).equals("success")) {
                                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                                        Intent locationLaunchIntent = new Intent(getApplicationContext(), LocationList.class);
                                        locationLaunchIntent.putExtra("username", username);
                                        startActivity(locationLaunchIntent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "You exceeded the character limit", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                ) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("post_text", post_text.getText().toString());
                        hashMap.put("post_title", post_title.getText().toString());
                        hashMap.put("username", checkedUsername.toString());
                        hashMap.put("location", location_name.getText().toString());
                        hashMap.put("location_id", location_id.toString());
                        hashMap.put("location_coord", location_coordinates.toString());
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationLaunchIntent = new Intent(getApplicationContext(), LocationList.class);
                locationLaunchIntent.putExtra("username", username);
                locationLaunchIntent.putExtra("post title", post_title.getText().toString());
                locationLaunchIntent.putExtra("post text", post_text.getText().toString());
                locationLaunchIntent.putExtra("location", location_name.getText().toString());
                startActivity(locationLaunchIntent);
            }
        });
    }
}