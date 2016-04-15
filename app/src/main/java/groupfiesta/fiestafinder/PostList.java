package groupfiesta.fiestafinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class PostList extends AppCompatActivity {
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_list.php";
    private RequestQueue requestQueue;
    private StringRequest request;

    ListView post_ListView;
    ArrayList<String> Posts = new ArrayList<String>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        post_ListView = (ListView) findViewById(R.id.post_ListView);

        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("resp", "Getting resp");
                try {
                    JSONArray jsonOArray = new JSONArray(response);
                    Log.i("resp", jsonOArray.toString());
                    for (int i = 0; i < jsonOArray.length(); i++) {
                        Posts.add(jsonOArray.getJSONObject(i).getString("post_text"));
                    }
                    post_ListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.post_listitem,R.id.postlist_txt, Posts));
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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        assert fab != null;
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent launchPostCreate = new Intent(getApplicationContext(),PostCreate.class);
//                Bundle usernameBundle = getIntent().getExtras();
//                launchPostCreate.putExtra("username", usernameBundle.getString("username"));
//                startActivity(launchPostCreate);
//            }
//        });
    }
}