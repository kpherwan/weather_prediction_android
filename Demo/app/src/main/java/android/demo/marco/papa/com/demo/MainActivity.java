package android.demo.marco.papa.com.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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
    private String locationCoord;
    private String locationName;
    Map<String, TomorrowIoData> tomorrowIoDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutTop, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    private void setup() {
        loadFragment(new ActionBarFragment());
        fetchJsonResponseForAllLocations();
    }

    private void fetchJsonResponseForAllLocations() {
        // Pass second argument as "null" for GET requests
        ApiCall.make(getBaseContext(), "https://ipinfo.io/json?token=de7857647d098b", new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                try {
                    JSONObject response = new JSONObject(resp);
                    locationCoord = response.getString("loc");
                    locationName = response.getString("city") + ", " + response.getString("region");
                    locationCoord = response.getString("loc");
                    locationName = response.getString("city") + ", " + response.getString("region");
                    makeApiCall("https://csci571-hw8-329706.wl.r.appspot.com/currentWeather?location=" + locationCoord, new VolleyCallBack() {
                        @Override
                        public void onSuccess() {
                            ViewPager2 pager = findViewById(R.id.details_viewpager);
                            DetailsPagerAdapter adapter = new DetailsPagerAdapter(MainActivity.this, tomorrowIoDataMap);
                            pager.setAdapter(adapter);

                            TabLayout tabLayout = findViewById(R.id.tab_layout);
                            new TabLayoutMediator(tabLayout, pager,
                                    (tab, position) -> tab.setText("")
                            ).attach();
                        }
                    }, "current");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
    }

    private void makeApiCall(String url, final VolleyCallBack callBack, String mapKey) {
        ApiCall.make(getBaseContext(), url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                try {
                    JSONObject response = new JSONObject(resp);
                    JSONArray timelines = response.getJSONObject("day").getJSONObject("data").getJSONArray("timelines");
                    JSONArray intervals = timelines.getJSONObject(0).getJSONArray("intervals");
                    JSONArray initializedDataHourly = response.getJSONObject("current").getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
                    tomorrowIoDataMap.put(mapKey, new TomorrowIoData(intervals, initializedDataHourly, locationName));
                    callBack.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
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
    }
}
