package android.demo.marco.papa.com.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class TomorrowIoData {
    private JSONArray intervals;
    private JSONArray initializedDataHourly;
    private String locationName;

    public TomorrowIoData(JSONArray intervals, JSONArray initializedDataHourly, String locationName) {
        this.intervals = intervals;
        this.initializedDataHourly = initializedDataHourly;
        this.locationName = locationName;
    }

    public JSONArray getIntervals() {
        return intervals;
    }

    public String getLocationName() {
        return locationName;
    }

    public JSONArray getInitializedDataHourly() {
        return initializedDataHourly;
    }
}

interface VolleyCallBack {
    void onSuccess();
}

public class MainActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;
    private String locationCoord;
    private String locationName;
    Map<String,TomorrowIoData> tomorrowIoDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setup();
    }

    private void setup() {
        mRequestQueue = Volley.newRequestQueue(this);
        fetchJsonResponseForAllLocations();
    }

    private void fetchJsonResponseForAllLocations() {
        // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, "https://ipinfo.io/json?token=de7857647d098b", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            locationCoord = response.getString("loc");
                            locationName = response.getString("city") + "," + response.getString("region");
                            fetchJsonResponseForAllLocationsInner(locationCoord, "current", locationName,new VolleyCallBack() {
                                @Override
                                public void onSuccess() {
                                    ViewPager2 pager =  findViewById(R.id.details_viewpager);
                                    DetailsPagerAdapter adapter = new DetailsPagerAdapter(MainActivity.this, tomorrowIoDataMap);
                                    pager.setAdapter(adapter);

                                    TabLayout tabLayout = findViewById(R.id.tab_layout);
                                    new TabLayoutMediator(tabLayout, pager,
                                            (tab, position) -> tab.setText("")
                                    ).attach();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        /* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
    }

    private void fetchJsonResponseForAllLocationsInner(String locationInput, String mapKey, String locationName, final VolleyCallBack callBack) {
            // Pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                "https://csci571-hw8-329706.wl.r.appspot.com/currentWeather?location=" + locationInput, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray timelines = response.getJSONObject("day").getJSONObject("data").getJSONArray("timelines");
                            JSONArray intervals = timelines.getJSONObject(0).getJSONArray("intervals");
                            JSONArray initializedDataHourly = response.getJSONObject("current").getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
                            tomorrowIoDataMap.put(mapKey, new TomorrowIoData(intervals, initializedDataHourly, locationName));
                            callBack.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        /* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
    }
}
