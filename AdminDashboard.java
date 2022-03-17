package com.example.trafic.commonPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.trafic.Chat.RecentChat;
import com.example.trafic.Config;
import com.example.trafic.LogInPage;
import com.example.trafic.MainActivity;
import com.example.trafic.R;
import com.example.trafic.SharedPrefManager;
import com.example.trafic.WarrentList;
import com.example.trafic.leaveApplication.LeaveApplication;
import com.example.trafic.search.Search;
import com.example.trafic.userTypePage.AdminUser;
import com.example.trafic.userTypePage.GeneralUser;
import com.example.trafic.userTypePage.SearchUser;
import com.example.trafic.userTypePage.UnitUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboard extends AppCompatActivity {

    int backpress = 0;
    TextView tv_personName,unSeenNotice;
    String userType;
    String url = Config.url+"document/get_notice";
    int currentNotice, oldNotice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        unSeenNotice = findViewById(R.id.unSeenNotice);
        tv_personName = findViewById(R.id.personName);
        tv_personName.setText(SharedPrefManager.getInstance(this).getKeyName());
        userType = SharedPrefManager.getInstance(this).getKeyUserType();
        oldNotice = SharedPrefManager.getInstance(this).getCurrentNotice();
        getItems();
        unSeenNotice.setVisibility(View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_about) {
            Intent ic = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(ic);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void getItems() {
        JSONObject jsonobject = new JSONObject();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, url, jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jarray = response.getJSONArray("items");
                            currentNotice = jarray.length();
                            if (currentNotice>oldNotice){
                                unSeenNotice.setVisibility(View.VISIBLE);
                                int s = currentNotice-oldNotice;
                                unSeenNotice.setText(""+s);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMsg = " ";
                if (error instanceof NoConnectionError){
                    errorMsg = "No Connection";
                }
                else  if (error instanceof TimeoutError){
                    errorMsg = "Slow Connection";
                }
                else  if (error instanceof AuthFailureError){
                    errorMsg = "Authentication Failure";
                }
                else  if (error instanceof ServerError){
                    errorMsg = "Server Failure";
                }
                else  if (error instanceof Network){
                    errorMsg = "Network Failure";
                }
                else  if (error instanceof ParseError){
                    System.out.println(error.getMessage());
                    Toast.makeText(AdminDashboard.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LogInPage.class));
                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                }
                else
                    errorMsg = ""+error;
                Toast.makeText(AdminDashboard.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("Content-Type", "application/json; charset=UTF-8");
                parmas.put("Authorization", "Bearer " + SharedPrefManager.getInstance(getApplicationContext()).getKeyApiKey());
                return parmas;
            }
        };
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                queue.getCache().clear();
            }
        });
    }
    @Override
    public void onBackPressed(){
        backpress = (backpress + 1);
    Toast.makeText(this, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
    if (backpress>1)
        finishAffinity();
}
    public void profile(View view){
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }
    public void notice(View view){
        if (userType.equalsIgnoreCase("General User"))
        startActivity(new Intent(getApplicationContext(), GeneralUser.class));
        else if (userType.equalsIgnoreCase("Admin"))
        startActivity(new Intent(getApplicationContext(), AdminUser.class));
        else if (userType.equalsIgnoreCase("Unit User"))
        startActivity(new Intent(getApplicationContext(), UnitUser.class));
        else if (userType.equalsIgnoreCase("Search User"))
        startActivity(new Intent(getApplicationContext(), SearchUser.class));
    }
    public void search(View view){
        if (userType.equals("General User")){
            Toast.makeText(this, "You Have No Permission", Toast.LENGTH_LONG).show();
        }
        else
        startActivity(new Intent(getApplicationContext(), Search.class));
    }
    public void chat(View view){
        startActivity(new Intent(getApplicationContext(), RecentChat.class));
    }

    public void warrent(View view){
        startActivity(new Intent(getApplicationContext(), WarrentList.class));
    }
    public void leaveapplication(View view){
       // Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
       startActivity(new Intent(getApplicationContext(), LeaveApplication.class));

//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//       // builder.setTitle("Message");
//        builder.setTitle("Coming Soon");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        builder.create().show();

    }
    public void logout(View view){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.url+"logout",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AdminDashboard.this, ""+ response, Toast.LENGTH_LONG).show();
                        if (response.equalsIgnoreCase("Logged out")){
                            startActivity(new Intent(getApplicationContext(), LogInPage.class));
                            SharedPrefManager.getInstance(getApplicationContext()).logout();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = " ";
                        if (error instanceof NoConnectionError){
                            errorMsg = "No Connection";
                        }
                        else  if (error instanceof TimeoutError){
                            errorMsg = "Slow Connection";
                        }
                        else  if (error instanceof AuthFailureError){
                            errorMsg = "Authentication Failure";
                        }
                        else  if (error instanceof ServerError){
                            errorMsg = "Server Failure";
                        }
                        else  if (error instanceof Network){
                            errorMsg = "Network Failure";
                        }
                        else  if (error instanceof ParseError){
                            errorMsg = "Server response could not be parsed";
                        }
                        else
                            errorMsg = ""+error;
                        Toast.makeText(AdminDashboard.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("Content-Type","application/json; charset=UTF-8");
                parmas.put("Authorization","Bearer "+SharedPrefManager.getInstance(AdminDashboard.this).getKeyApiKey());
                //parmas.put("token",SharedPrefManager.getInstance(AdminDashboard.this).getKeyApiKey());
                return parmas;

            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();
            return parmas;
            }
        };
        //stringRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                queue.getCache().clear();
            }
        });

    }
}