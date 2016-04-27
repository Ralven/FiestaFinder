package groupfiesta.fiestafinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity implements AdapterView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String URL_LOCATIONLIST = "http://fiestafinder.azurewebsites.net/webservice/location_list.php";

    private AppPreferences myPrefs;
    private RequestQueue requestQueue;
    private StringRequest request;
    private String postTitle;
    private String postText;
    private String location;
    private String username;
    private Bundle dataBundle;
    private ListView location_ListView;
    private ArrayList<String> Locations;
    private static final int BAR = Place.TYPE_BAR;
    private LatLng location_coordinates;
    private String location_id;
    private String location_name;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location barLocation;
    private Location myLocation;
    private float distance;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myPrefs = new AppPreferences(super.getApplicationContext());
        barLocation = new Location("gps");
        Locations = new ArrayList<String>();
        location_ListView = (ListView) findViewById(R.id.location_ListView);
        location_ListView.setOnItemClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(LocationList.this, LocationList.this /* OnConnectionFailedListener */)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(AppIndex.API).build();

        dataBundle = getIntent().getExtras();
        username = dataBundle.getString("username");

        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(Request.Method.GET, URL_LOCATIONLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("resp", "Getting resp");
                try {
                    JSONArray jsonOArray = new JSONArray(response);
                    Log.i("resp", jsonOArray.toString());
                    for (int i = 0; i < jsonOArray.length(); i++) {
                        Locations.add(jsonOArray.getJSONObject(i).getString("location"));
                    }
                    location_ListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.location_listitem, R.id.locationitem_txt, Locations));
                } catch (JSONException e) {
                    Log.i("Exception", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = new Location(location);
                myLocation = location;

            }

            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            public void onProviderEnabled(String s) {
            }

            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, 10);
                        Toast.makeText(getApplicationContext(), "You must allow Location Services in order to post", Toast.LENGTH_LONG).show();
                    } else {
                        locationListener.onLocationChanged(locationManager.getLastKnownLocation("gps"));
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        try {
                            startActivityForResult(builder.build(LocationList.this), BAR);
                        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent postListIntent = new Intent(getApplicationContext(), PostList.class);
        postListIntent.putExtra("username", username);
        postListIntent.putExtra("location", Locations.get(position).toString());
        startActivity(postListIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BAR && resultCode == LocationList.RESULT_OK) {
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);
            location_id = place.getId();
            location_name = place.getName().toString();
            location_coordinates = place.getLatLng();
            barLocation.setLatitude(location_coordinates.latitude);
            barLocation.setLongitude(location_coordinates.longitude);
            distance = myLocation.distanceTo(barLocation);

            if (distance < 150) {
                Intent postCreateIntent = new Intent(getApplicationContext(), PostCreate.class);
                postCreateIntent.putExtra("username", username);
                postCreateIntent.putExtra("location", location_name);
                postCreateIntent.putExtra("location_id", location_id);
                postCreateIntent.putExtra("location_coordinates", location_coordinates);
                startActivity(postCreateIntent);
            } else {
                Toast.makeText(getApplicationContext(), "You must be at " + location_name + " to post there", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("tag", connectionResult.toString());
    }

    public void onRequestPermissionResults(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    return;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            myPrefs.eraseUserData(super.getApplicationContext());
            Intent loginIntent = new Intent(getApplicationContext(), LoginScreen.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

