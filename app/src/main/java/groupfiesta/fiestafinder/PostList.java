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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostList extends AppCompatActivity {
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_list.php";
    private RequestQueue requestQueue;
    private StringRequest request;
    Bundle usernameBundle;
    ListView post_ListView;
    ArrayList<String> Posts;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);
        usernameBundle = getIntent().getExtras();
        Posts = new ArrayList<String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView locationTitle = (TextView) findViewById(R.id.locationtitle_TextView);
        post_ListView = (ListView) findViewById(R.id.post_ListView);
        String title = usernameBundle.getString("location");
        locationTitle.setText(title);
        requestQueue = Volley.newRequestQueue(this);
        request = new StringRequest(
            Request.Method.POST,
            URL,
            new Response.Listener<String>() {
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
                                R.layout.post_listitem, R.id.postlist_txt, Posts));
                    } catch (JSONException e) {
                        Log.i("Exception", e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        )
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("location", locationTitle.getText().toString());
                return hashMap;
            }
        };
        requestQueue.add(request);
    }
}




