package groupfiesta.fiestafinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostCreate extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText post_text, post_title;
    private TextView location_name;
    private Button post_button,cancel_button;
    private CheckBox postAs_checkBox;
    private String checkedUsername = null;
    private String location_id;
    int REQUEST_PLACE_PICKER = 1;
    private GoogleApiClient mGoogleApiClient;
    private int clientId;
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_control.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcreate);
        post_text = (EditText) findViewById(R.id.post_text);
        post_title = (EditText) findViewById(R.id.post_title);
        post_button = (Button) findViewById(R.id.post_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);
        location_name = (TextView) findViewById(R.id.location_name);
        postAs_checkBox = (CheckBox) findViewById(R.id.postAs_checkBox);
        Bundle usernameBundle = getIntent().getExtras();
        final String username = usernameBundle.getString("username");
        postAs_checkBox.setText("Post as "+username);
        requestQueue = Volley.newRequestQueue(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(PostCreate.this,PostCreate.this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(PostCreate.this), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postAs_checkBox.isChecked())
                {
                    checkedUsername = username;
                }else{
                    checkedUsername = "Anonymous";
                }
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("resp", jsonObject.toString());
                            if (jsonObject.names().get(0).equals("success")) {
                                Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                                Intent locationLaunchIntent = new Intent(getApplicationContext(),LocationList.class);
                                locationLaunchIntent.putExtra("username",username);
                                startActivity(locationLaunchIntent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + jsonObject.getString("error"), Toast.LENGTH_LONG).show();
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
                        hashMap.put("post_text", post_text.getText().toString());
                        hashMap.put("post_title", post_title.getText().toString());
                        hashMap.put("username",checkedUsername.toString());
                        hashMap.put("location",location_name.getText().toString());
                        hashMap.put("location_id",location_id.toString());
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationLaunchIntent = new Intent(getApplicationContext(),LocationList.class);
                locationLaunchIntent.putExtra("username",username);
                startActivity(locationLaunchIntent);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == LocationList.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);
            location_id = place.getId();
            final CharSequence name = place.getName();
            location_name.setText(name);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("tag",connectionResult.toString());
    }


}
