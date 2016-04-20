package groupfiesta.fiestafinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PostDetails extends AppCompatActivity {

    private TextView post_title_details, username_details, post_text_details;
    private Bundle dataBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        post_title_details = (TextView) findViewById(R.id.post_title_details);
        username_details = (TextView) findViewById(R.id.post_username_details);
        post_text_details = (TextView) findViewById(R.id.post_text_detalis);
        dataBundle = getIntent().getExtras();
        post_title_details.setText(dataBundle.getString("post_title"));
        post_text_details.setText(dataBundle.getString("post_text"));
        username_details.setText(dataBundle.getString("username"));
    }
}
