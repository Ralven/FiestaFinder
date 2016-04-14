package groupfiesta.fiestafinder;

import android.content.Intent;
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

public class PostScreen extends AppCompatActivity {
    private EditText post_text, post_title;
    private Button post_button,cancel_button;
    private static final String URL = "http://fiestafinder.azurewebsites.net/webservice/post_control.php";
    private RequestQueue requestQueue;
    private StringRequest request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_screen);

        post_title = (EditText) findViewById(R.id.post_title);
        post_text = (EditText) findViewById(R.id.post_text);
        post_button = (Button) findViewById(R.id.post_button);
        cancel_button = (Button) findViewById(R.id.cancel_button);

        requestQueue = Volley.newRequestQueue(this);

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.names().get(0).equals("success")) {
                                Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), PostScreen.class));
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
                        hashMap.put("post_title",post_title.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(request);

            }

        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });



    }
}
