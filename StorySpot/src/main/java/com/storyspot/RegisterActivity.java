package com.storyspot;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import static com.storyspot.utils.Constants.API_ENDPOINT;
import static com.storyspot.utils.Constants.API_REGISTER;
import com.storyspot.data.UserData;

public class RegisterActivity extends Activity {

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ctx = this;

        final EditText et_fname = (EditText) findViewById(R.id.et_fname);
        final EditText et_lname = (EditText) findViewById(R.id.et_lname);
        final EditText et_uname = (EditText) findViewById(R.id.et_uname);
        final EditText et_email = (EditText) findViewById(R.id.et_email);
        final EditText et_pass1 = (EditText) findViewById(R.id.et_password1);
        final EditText et_pass2 = (EditText) findViewById(R.id.et_password2);
        final TextView tv_status = (TextView) findViewById(R.id.tv_status);
        Button btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = et_fname.getText().toString();
                String lname = et_lname.getText().toString();
                final String uname = et_uname.getText().toString();
                String email = et_email.getText().toString();
                final String pass1 = et_pass1.getText().toString();
                String pass2 = et_pass2.getText().toString();

                // Password should be > 8 chars
                // User name shoule be greater than 4 chars

                if((fname.length() != 0) && (lname.length() != 0) && (uname.length() != 0)
                        && (email.length() != 0) && (pass1.length() >= 8) && (pass2.length() >= 8)
                        && (pass1.equals(pass2))) {

                    final ProgressDialog pDialog;
                    pDialog = new ProgressDialog(ctx);

                    pDialog.setTitle("");
                    pDialog.setMessage("Registering. Please wait.");
                    pDialog.setCancelable(false);
                    pDialog.setIndeterminate(true);
                    pDialog.show();

                    RequestParams params = new RequestParams();
                    params.put("user[first_name]",fname);
                    params.put("user[last_name]",lname);
                    params.put("user[user_name]",uname);
                    params.put("user[email]",email);
                    params.put("user[password]",pass1);
                    params.put("commit","Create User");

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(API_ENDPOINT + API_REGISTER, params, new AsyncHttpResponseHandler(){

                        @Override
                        public void onStart() {
                            tv_status.setText("");
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                            tv_status.setText("");

                            //String response = new String(responseBody);

                            //UserData userData = new UserData();
                            //userData.saveUser(ctx, uname, pass1);

                            Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(mIntent);
                            overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                            tv_status.setText("Something went wrong :(");
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
                } else if((pass1.length() < 8)) {
                    tv_status.setText("Password should be atleat 8 characters long");
                } else if(pass1 != pass2) {
                    tv_status.setText("Passwords do not match");
                } else if((fname.length() == 0) || (lname.length() == 0) ) {
                    tv_status.setText("Name cannot be blank");
                } else if(uname.length() > 4) {
                    tv_status.setText("Username should be more than 4 characters long");
                } else if(email.length() == 0) {
                    tv_status.setText("Email cannot be blank");
                } else {
                    tv_status.setText("Error");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(mIntent);
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
        finish();
    }
}
