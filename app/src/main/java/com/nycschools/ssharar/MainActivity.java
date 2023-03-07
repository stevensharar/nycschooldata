package com.nycschools.ssharar;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nycschools.ssharar.helpers.HelpMethods;
import com.nycschools.ssharar.models.School;
import com.nycschools.ssharar.models.SchoolObject;
import com.nycschools.ssharar.models.SchoolSAT;
import com.nycschools.ssharar.ui.CustomListView;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* Author: Steven Sharar
   Date: 3/3/2023
 */
public class MainActivity extends AppCompatActivity {

    HelpMethods helper = null;
    private CustomListView schoolListView;
    private CustomListViewAdapter adapter;

    // IDs for the context menu actions
    private final int idInfo = 1;
    private final int idSATScores = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new HelpMethods(this);

        schoolListView = (CustomListView) findViewById(R.id.school_listview);

        // OPTIONAL: Disable scrolling when list is refreshing
        // schoolListView.setLockScrollWhileRefreshing(false);

        // OPTIONAL: Uncomment this if you want the list view header to show the 'last updated' time
        // schoolListView.setShowLastUpdatedText(true);

        // OPTIONAL: Uncomment this if you want to override the date/time format of the 'last updated' field
        // schoolListView.setLastUpdatedDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));

        // OPTIONAL: Uncomment this if you want to override the default strings
        // schoolListView.setTextPullToRefresh("Pull to Refresh");
        // schoolListView.setTextReleaseToRefresh("Release to Refresh");
        // schoolListView.setTextRefreshing("Refreshing");

        // MANDATORY: Set the onRefreshListener on the list. You could also use
        // schoolListView.setOnRefreshListener(this); and let this Activity
        // implement OnRefreshListener.

        /*
        With more time I would add network checking and better exception handling.
         */
        schoolListView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list contents goes here

                // for example:
                // If this is a webservice call, it might be asynchronous so
                // you would have to call listView.onRefreshComplete(); when
                // the webservice returns the data
                adapter.loadData();

                // Make sure you call schoolListView.onRefreshComplete()
                // when the loading is done. This can be done from here or any
                // other place, like on a broadcast receive from your loading
                // service or the onPostExecute of your AsyncTask.

                // For the sake of this sample, the code will pause here to
                // force a delay when invoking the refresh
                schoolListView.postDelayed(new Runnable() {


                    @Override
                    public void run() {
                        schoolListView.onRefreshComplete();
                    }
                }, 2000);
            }
        });

        adapter = new CustomListViewAdapter() {};
        schoolListView.setAdapter(adapter);
        adapter.loadData();

        // click listener
        schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                CustomListViewAdapter.ViewHolder viewHolder = (CustomListViewAdapter.ViewHolder) arg1.getTag();
                if (viewHolder.name != null){
                    Log.d("NYCSchools", "NYCSchools click " + viewHolder.id);
                    if (viewHolder.phone.getVisibility() == View.VISIBLE){
                        viewHolder.phone.setVisibility(View.GONE);
                        viewHolder.email.setVisibility(View.GONE);
                        viewHolder.numSat.setVisibility(View.GONE);
                        viewHolder.satReading.setVisibility(View.GONE);
                        viewHolder.satMath.setVisibility(View.GONE);
                        viewHolder.satWriting.setVisibility(View.GONE);
                    }
                    else {
                        try {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpClient httpClient = new OkHttpClient();

                                    Request request = new Request.Builder()
                                            .addHeader("X-App-Token", "3Pa8ifSQxontAko0jzgHURgSd")
                                            .header("Accept", "application/json")
                                            .url("https://data.cityofnewyork.us/resource/f9bf-2cp4.json?dbn=" + viewHolder.id)
                                            .get()
                                            .build();

                                    try {
                                        Response respone = httpClient.newCall(request).execute();
                                        if (respone.isSuccessful()){
                                            SchoolSAT[] schoolSats = new Gson().fromJson(respone.body().string(), SchoolSAT[].class);
                                            if (schoolSats != null && schoolSats.length > 0){
                                                SchoolSAT schoolSAT = schoolSats[0];
                                                adapter.setItemSAT(viewHolder.id, schoolSAT, true);
                                                viewHolder.numSat.setText("Number of SAT takers: " + schoolSAT.num_of_sat_test_takers);
                                                viewHolder.satReading.setText("Avg. Reading Score: " + schoolSAT.sat_critical_reading_avg_score);
                                                viewHolder.satMath.setText("Avg. Math Score: " + schoolSAT.sat_math_avg_score);
                                                viewHolder.satWriting.setText("Avg. Writing Score: " + schoolSAT.sat_writing_avg_score);
                                            }
                                            else {
                                                adapter.setItemSAT(viewHolder.id, null, false);
                                                viewHolder.numSat.setText("Number of SAT takers: Data does not exist");
                                                viewHolder.satReading.setText("Avg. Reading Score: Data does not exist");
                                                viewHolder.satMath.setText("Avg. Math Score: Data does not exist");
                                                viewHolder.satWriting.setText("Avg. Writing Score: Data does not exist");
                                            }
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                            thread.join();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        viewHolder.phone.setVisibility(View.VISIBLE);
                        viewHolder.email.setVisibility(View.VISIBLE);
                        viewHolder.numSat.setVisibility(View.VISIBLE);
                        viewHolder.satReading.setVisibility(View.VISIBLE);
                        viewHolder.satMath.setVisibility(View.VISIBLE);
                        viewHolder.satWriting.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Register the context menu for actions
        registerForContextMenu(schoolListView);
    }

    /**
     * Create the context menu with the Info and SAT Score options
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Add any actions you need. Implement the logic in onContextItemSelected
        //menu.add(Menu.NONE, idInfo, Menu.NONE, R.string.school_info);
        //menu.add(Menu.NONE, idSATScores, Menu.NONE, R.string.sat_scores);
    }

    /**
     * Event called after an option from the context menu is selected
     * I would implement the context menu items for more info on the school with more time.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case idInfo:

                // Put your code here for School Info action
                // just as an example a toast message
                Toast.makeText(this, getString(R.string.school_info) + " " + adapter.getItem(info.position-1), Toast.LENGTH_SHORT).show();
                return true;
            case idSATScores:

                // Put your code here for SAT Scores action
                // just as an example a toast message
                Toast.makeText(this, getString(R.string.sat_scores) + " " + adapter.getItem(info.position-1), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    /**
     * The adapter used to display the results in the list
     *
     */
    public abstract class CustomListViewAdapter extends android.widget.BaseAdapter {

        private ArrayList<SchoolObject> items = new ArrayList<SchoolObject>();;

        public class ViewHolder {
            public String id;
            public TextView name;
            public TextView phone;
            public TextView email;
            public TextView numSat;
            public TextView satReading;
            public TextView satMath;
            public TextView satWriting;
        }

        public void setItemSAT(String dbn, SchoolSAT satItem, boolean show){
            for (SchoolObject i : items) {
                if (i.school.dbn.equals(dbn)) {
                    i.showInfo = show;
                    if (satItem != null){
                        i.sat = satItem;
                    }
                }
            }
        }

        /**
         * Loads the data.
         * With more time I would have made the Thread with the api call a wrapper in a background service.
         * I would also have more error handling.
         */
        public void loadData() {
            // Here add your code to load the data for example from a webservice or DB
            try {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient httpClient = new OkHttpClient();

                        Request request = new Request.Builder()
                                .addHeader("X-App-Token", "3Pa8ifSQxontAko0jzgHURgSd")
                                .header("Accept", "application/json")
                                .url("https://data.cityofnewyork.us/resource/s3k6-pzi2.json?$select=dbn, school_name, phone_number, school_email")
                                .get()
                                .build();

                        try {
                            Response respone = httpClient.newCall(request).execute();
                            if (respone.isSuccessful()){
                                items = new ArrayList<SchoolObject>();
                                School[] nycSchools = new Gson().fromJson(respone.body().string(), School[].class);
                                for (School s : nycSchools){
                                    SchoolObject sObject = new SchoolObject();
                                    sObject.setSchool(s);
                                    SchoolSAT sat = new SchoolSAT();
                                    sObject.sat = sat;
                                    items.add(sObject);
                                    sObject.showInfo = false;
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                thread.join();
            } catch (Exception e){
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            SchoolObject record = (SchoolObject) getItem(position);

            LayoutInflater inflater = MainActivity.this.getLayoutInflater();

            CustomListViewAdapter.ViewHolder viewHolder = new CustomListViewAdapter.ViewHolder();

            if (convertView == null){
                rowView = inflater.inflate(R.layout.school_item, null);

                viewHolder.name = (TextView) rowView.findViewById(R.id.schoolTextView);
                viewHolder.phone = (TextView) rowView.findViewById(R.id.schoolPhone);
                viewHolder.email = (TextView) rowView.findViewById(R.id.schoolEmail);
                viewHolder.numSat = (TextView) rowView.findViewById(R.id.satNumber);
                viewHolder.satReading = (TextView) rowView.findViewById(R.id.satReading);
                viewHolder.satMath = (TextView) rowView.findViewById(R.id.satMath);
                viewHolder.satWriting = (TextView) rowView.findViewById(R.id.satWriting);
                rowView.setTag(viewHolder);
            }

            final CustomListViewAdapter.ViewHolder holder = (CustomListViewAdapter.ViewHolder) rowView.getTag();

            holder.name.setText(record.school.school_name);
            holder.phone.setText("Phone: " + record.school.phone_number);
            holder.email.setText("Email: " + record.school.school_email);
            holder.id = record.school.dbn;
            if (record.showInfo){
                if (record.sat == null || record.sat.dbn == null) {
                    holder.numSat.setText("Number of SAT takers: Data does not exist");
                    holder.satReading.setText("Avg. Reading Score: Data does not exist");
                    holder.satMath.setText("Avg. Math Score: Data does not exist");
                    holder.satWriting.setText("Avg. Writing Score: Data does not exist");
                }
                else {
                    holder.numSat.setText("Number of SAT takers: " + record.sat.num_of_sat_test_takers);
                    holder.satReading.setText("Avg. Reading Score: " + record.sat.sat_critical_reading_avg_score);
                    holder.satMath.setText("Avg. Math Score: " + record.sat.sat_math_avg_score);
                    holder.satWriting.setText("Avg. Writing Score: " + record.sat.sat_writing_avg_score);
                }
                holder.phone.setVisibility(View.VISIBLE);
                holder.email.setVisibility(View.VISIBLE);
                holder.numSat.setVisibility(View.VISIBLE);
                holder.satReading.setVisibility(View.VISIBLE);
                holder.satMath.setVisibility(View.VISIBLE);
                holder.satWriting.setVisibility(View.VISIBLE);
            } else {
                holder.phone.setVisibility(View.GONE);
                holder.email.setVisibility(View.GONE);
                holder.numSat.setVisibility(View.GONE);
                holder.satReading.setVisibility(View.GONE);
                holder.satMath.setVisibility(View.GONE);
                holder.satWriting.setVisibility(View.GONE);
            }
            return rowView;
        }
    }
}