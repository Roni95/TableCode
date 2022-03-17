package com.example.trafic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import com.example.trafic.Chat.ChatingPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WarrentList extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ProgressDialog loading;
    String getMemberUrl = Config.url+"message/get_members";
    String getUnitUrl = Config.url+"document/get_unit";
    ArrayList<String> students;
    ArrayList<String> bit;
    ListView listView;
    Spinner spinnerUnit, spinnerBit;
    String selectedItem;
    String searchString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warrent);
        getSupportActionBar().setTitle("Warrant List");
        students = new ArrayList<String>();
        listView = findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        spinnerUnit = findViewById(R.id.unitList);



        //getUnit();
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = spinnerUnit.getSelectedItem().toString();
                if(!selectedItem.equals("Select an Unit")){
                    //getItems();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_chat, menu);
        MenuItem menuItem = menu.findItem(R.id.item2);
        /*
        SearchView search = (SearchView) menuItem.getActionView();
        search.setQueryHint("Type here ..");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                getItems();

                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                searchString = s;
//                adapter.getFilter().filter(s);
                return false;
            }
        });

         */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void getUnit (){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUnitUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setUnit(response);
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
                            Toast.makeText(WarrentList.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LogInPage.class));
                            SharedPrefManager.getInstance(getApplicationContext()).logout();
                        }
                        else
                            errorMsg = ""+error;
                        Toast.makeText(WarrentList.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String, String> parmas = new HashMap<>();
                parmas.put("Content-Type","application/json; charset=UTF-8");
                parmas.put("Authorization","Bearer "+ SharedPrefManager.getInstance(getApplicationContext()).getKeyApiKey());
                return parmas;
            }
        };
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                queue.getCache().clear();
            }
        });
    }


    public void setUnit(String jsonResposnce){
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");
            students.add("Select an Unit");
            for (int i= 0 ; i<jarray.length();i++){
                JSONObject jo = jarray.getJSONObject(i);
                students.add(jo.getString("name"));
            }
            spinnerUnit.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,students));
       }catch (JSONException e){

        }
    }

    private void getItems() {
        loading =  ProgressDialog.show(this,"Loading..","Please wait..",false,true);
        JSONObject jsonobject = new JSONObject();
        try {

            jsonobject.put("search", searchString);
            if(selectedItem != null &&  !selectedItem.equals("Select an Unit")) {
                jsonobject.put("unit_name", selectedItem);
            }
            System.out.println(jsonobject.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, getMemberUrl, jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        parseItems(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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
//                    System.err.println(error.getMessage());
                    Toast.makeText(WarrentList.this, "Authentication Failure", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LogInPage.class));
                    SharedPrefManager.getInstance(getApplicationContext()).logout();
                }
                else
                    errorMsg = ""+error;
                Toast.makeText(WarrentList.this, errorMsg, Toast.LENGTH_SHORT).show();
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
    private void parseItems(JSONObject jsonResposnce) {
        listView.setAdapter(null);
        SimpleAdapter adapter;
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        try {
            JSONArray jarray = jsonResposnce.getJSONArray("items");

            for (int i = 0; i <jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String id = jo.getString("brush_no");
                String name = jo.getString("name");
                String bp_no = jo.getString("bp_no");
                String phone = jo.getString("phone");

                HashMap<String, String> item = new HashMap<>();

                item.put("id",id);
                item.put("name",name);
                item.put("bp_no", bp_no);
                item.put("phone", phone);

                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new SimpleAdapter(this,list,R.layout.warrent_list_card,
                new String[]{"name","id","bp_no","phone"},new int[]{R.id.wname,R.id.wserialno,R.id.wfather,R.id.waddress});
        listView.setAdapter(adapter);
        loading.dismiss();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        HashMap<String,String> map =(HashMap)adapterView.getItemAtPosition(i);
        final String phoneNo = map.get("phone").toString();
        final String id = map.get("id").toString();
        final String name = map.get("name").toString();
        final String bp_no = map.get("bp_no").toString();
        final  LinearLayout linearLayout = view.findViewById(R.id.constraintLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WarrentList.this, ChatingPage.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("bp_no",bp_no);
                startActivity(intent);
            }
        });

        //

        final LinearLayout more = view.findViewById(R.id.more);

        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),more);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.call:
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:"+phoneNo));
                                    startActivity(callIntent);
                                }catch (Exception e)
                                {
                                    Toast.makeText(WarrentList.this, ""+e, Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case R.id.message:
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO,
                                        Uri.parse("sms:"+phoneNo));
                                startActivity(smsIntent);
                                break;

                            case R.id.addContract:
                                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNo)
                                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    /*
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.findItem(R.id.item2).expandActionView();
        return true;
    }
     */
}