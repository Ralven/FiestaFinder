package groupfiesta.fiestafinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String URL_LOCATIONLIST = "http://fiestafinder.azurewebsites.net/webservice/location_list.php";

    private RequestQueue requestQueue;
    private StringRequest request;
    private String postTitle;
    private String postText;
    private String location;
    private String username;
    private Bundle dataBundle;
    private ListView location_ListView;
    private ArrayList<String> Post_ID;
    private ArrayList<String> Locations;
    private ArrayList<String> Post_Title;
    private ArrayList<String> Post_Text;
    private ArrayList<String> Username;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Locations = new ArrayList<String>();
        Post_Title = new ArrayList<String>();
        Post_Text = new ArrayList<String>();
        Username = new ArrayList<String>();
        Post_ID = new ArrayList<String>();
        location_ListView = (ListView) findViewById(R.id.location_ListView);
        location_ListView.setOnItemClickListener(this);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postCreateIntent = new Intent(getApplicationContext(), PostCreate.class);
                postCreateIntent.putExtra("username",username);
                startActivity(postCreateIntent);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent postListIntent = new Intent(getApplicationContext(), PostList.class);

        //postListIntent.putExtra("username", username);
        postListIntent.putExtra("location",Locations.get(position).toString());
        startActivity(postListIntent);

    }
}

