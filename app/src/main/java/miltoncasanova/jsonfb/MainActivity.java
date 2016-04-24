package miltoncasanova.jsonfb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String url = "http://api.randomuser.me/?results=1&format=jsaon";
    private static String firebaseUrl = "https://sorkerf.firebaseio.com/";
    JSONArray users = null;
    ArrayList<DataEntry> usersList;
    private ListView usersListView;
    private ProgressDialog pDialog;
    private ArrayList<String> ids;
    Firebase rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        rootRef = new Firebase(firebaseUrl);
        usersList = new ArrayList<>();
        usersListView = (ListView) findViewById(R.id.usersListView);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("data", (DataEntry) view.getTag());
                intent.putExtra("firebase", firebaseUrl);
                intent.putExtra("key",ids.get(i));
                MainActivity.this.startActivity(intent);
            }
        });

        Button addUser = (Button) findViewById(R.id.userAddBtn);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData(view);
            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<DataEntry> users = new ArrayList<DataEntry>();
                ids = new ArrayList<String>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    users.add(new DataEntry(postSnapshot.child("gender").getValue().toString(),
                            postSnapshot.child("firstName").getValue().toString(),
                            postSnapshot.child("lastName").getValue().toString(),
                            postSnapshot.child("picture").getValue().toString()
                    ));
                    ids.add(postSnapshot.getRef().getKey().toString());
                }
                CustomAdapter adapter = new CustomAdapter(MainActivity.this, users, ids, rootRef);
                usersListView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isNetworkAvaible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isNetworkAvaible = true;
            Toast.makeText(this, "Network is available ", Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this, "Network not available ", Toast.LENGTH_LONG)
                    .show();
        }
        return isNetworkAvaible;
    }

    public void requestData(View view) {
        new GetData().execute();
    }

    public void checkInternet(View view) {
        isNetworkAvailable();
    }

    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    users = jsonObj.getJSONArray("results");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject c = users.getJSONObject(i);

                        DataEntry dataEntry = new DataEntry();

                        dataEntry.setGender(c.getString("gender"));

                        JSONObject name = c.getJSONObject("name");

                        dataEntry.setFirstName(name.getString("first"));
                        dataEntry.setLastName(name.getString("last"));

                        JSONObject imageObject = c.getJSONObject("picture");

                        dataEntry.setPicture(imageObject.getString("large"));

                        rootRef.push().setValue(dataEntry);
                        rootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                ArrayList<DataEntry> users = new ArrayList<DataEntry>();
                                ArrayList<String> id = new ArrayList<String>();
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    users.add(new DataEntry(postSnapshot.child("gender").getValue().toString(),
                                            postSnapshot.child("firstName").getValue().toString(),
                                            postSnapshot.child("lastName").getValue().toString(),
                                            postSnapshot.child("picture").getValue().toString()
                                    ));
                                    id.add(postSnapshot.getRef().getKey().toString());
                                }
                                CustomAdapter adapter = new CustomAdapter(MainActivity.this, users, id, rootRef);
                                usersListView.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) { }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            rootRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    ArrayList<DataEntry> users = new ArrayList<DataEntry>();
                    ArrayList<String> id = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        users.add(new DataEntry(postSnapshot.child("gender").getValue().toString(),
                                postSnapshot.child("firstName").getValue().toString(),
                                postSnapshot.child("lastName").getValue().toString(),
                                postSnapshot.child("picture").getValue().toString()
                        ));
                        id.add(postSnapshot.getRef().getKey().toString());
                    }
                    CustomAdapter adapter = new CustomAdapter(MainActivity.this, users, id, rootRef);
                    usersListView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) { }
            });
        }

    }
}
