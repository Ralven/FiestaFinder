package groupfiesta.fiestafinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity{
    private static final String URLlocationlist = "http://fiestafinder.azurewebsites.net/webservice/location_list.php";

    private RequestQueue requestQueue;
    private StringRequest request;
    ListView location_ListView;

    ArrayList<String> Locations = new ArrayList<String>();


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        location_ListView = (ListView) findViewById(R.id.location_ListView);

        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(Request.Method.GET, URLlocationlist, new Response.Listener<String>() {
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
                            R.layout.list_item, R.id.item_txt, Locations));
                } catch (JSONException e) {
                    Log.i("Exception", e.getLocalizedMessage().toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(request);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PostCreate.class));
            }
        });

        location_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long rowId) {
                Toast.makeText(getApplicationContext(), "you clicked " + (position + 1), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), PostList.class));

            }
        });


    }
}