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

public class PostList extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_list.php";

    private RequestQueue requestQueue;
    private StringRequest request;
    private Bundle dataBundle;
    private ListView post_ListView;
    private ArrayList<String> PostTitle;
    private ArrayList<String> Id;
    private ArrayList<String> PostText;
    private ArrayList<String> Username;
    private TextView locationTitle;
    private String postTitle;
    private String postText;
    private String location;
    private String myusername;
    private String id;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);

        dataBundle = getIntent().getExtras();
        location = dataBundle.getString("location");
        myusername = dataBundle.getString("username");
        PostTitle = new ArrayList<String>();
        Id = new ArrayList<String>();
        PostText = new ArrayList<String>();
        Username = new ArrayList<String>();
        post_ListView = (ListView) findViewById(R.id.post_ListView);
        post_ListView.setOnItemClickListener(this);
        locationTitle = (TextView) findViewById(R.id.locationtitle_TextView);

        locationTitle.setText(location);
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
                            PostTitle.add(jsonOArray.getJSONObject(i).getString("post_title"));
                        }
                        for (int i = 0; i < jsonOArray.length(); i++) {
                            Id.add(jsonOArray.getJSONObject(i).getString("id"));
                        }
                        for (int i = 0; i < jsonOArray.length(); i++) {
                            PostText.add(jsonOArray.getJSONObject(i).getString("post_text"));
                        }
                        for (int i = 0; i < jsonOArray.length(); i++) {
                            Username.add(jsonOArray.getJSONObject(i).getString("username"));
                        }
                        post_ListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.post_listitem, R.id.postlistitem_txt, PostTitle));
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
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent postDetailsIntent = new Intent(getApplicationContext(), PostDetails.class);
        postTitle = PostTitle.get(position).toString();
        for(int i = 0; i < Id.size();i++){
            if(PostTitle.get(i) == postTitle){
                postDetailsIntent.putExtra("id",this.Id.get(i));
                postDetailsIntent.putExtra("post_text",this.PostText.get(i));
                postDetailsIntent.putExtra("username",this.Username.get(i));
            }
        }
        //postDetailsIntent.putExtra("location",locationTitle.getText().toString());
        postDetailsIntent.putExtra("post_title",PostTitle.get(position).toString());
        startActivity(postDetailsIntent);
    }
}




