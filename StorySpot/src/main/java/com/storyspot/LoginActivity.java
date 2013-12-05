package com.storyspot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.storyspot.data.UserData;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_GET_USERS;
import static com.storyspot.utils.Constants.API_REGISTER;

public class LoginActivity extends Activity {

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserData userData = new UserData();
        int userid = userData.getUserId(this);

        if(userid != 0) {
            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mIntent);
            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
            finish();
        }

        getActionBar().hide();

        ctx = this;

        TextView title = (TextView) findViewById(R.id.tv_title);

        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Black.ttf");
        title.setTypeface(font);

        final EditText et_username = (EditText) findViewById(R.id.username);
        final EditText et_password = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.btn_login);
        Button register = (Button) findViewById(R.id.btn_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pDialog;
                pDialog = new ProgressDialog(ctx);

                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();

                pDialog.setTitle("");
                pDialog.setMessage("Logging in. Please wait.");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(API_ENDPOINT + API_GET_USERS, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        pDialog.hide();

                        Boolean login = false;
                        int userid = 0;

                        try {
                            JSONArray json = new JSONArray(new String(responseBody));
                            for(int i = 0; i < json.length(); i++) {
                                JSONObject element = json.getJSONObject(i);
                                String jusername = element.getString("user_name");
                                String jpassword = element.getString("password");

                                if((jusername.equals(username)) && (jpassword.equals(password))) {
                                    login = true;
                                    userid = element.getInt(("id"));
                                    break;
                                }
                            }
                        } catch (Exception e) {
                        }

                        if(login) {
                            UserData userData = new UserData();
                            userData.saveUser(ctx, username, password, userid);

                            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mIntent);
                            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                            finish();
                        } else {
                            Toast.makeText(ctx, "Wrong username or password", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }

                    @Override
                    public void onRetry() {
                        // Request was retried
                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {
                        // Progress notification
                    }

                    @Override
                    public void onFinish() {
                        pDialog.hide();
                    }
                });


                //Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(mIntent);
                //overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                //finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
