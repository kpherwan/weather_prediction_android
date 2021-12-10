package android.demo.marco.papa.com.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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
    private String coord;

    public TomorrowIoData(JSONArray intervals, JSONArray initializedDataHourly, String locationName, String coord) {
        this.intervals = intervals;
        this.initializedDataHourly = initializedDataHourly;
        this.locationName = locationName;
        this.coord = coord;
    }

    public String getCoord() {
        return coord;
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
    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    boolean isRefreshNeeded = false;
    SharedPreferences sharedPreferencesReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferencesReq = getBaseContext().getSharedPreferences(getString(R.string.req_file_key), Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onSharedPreferenceChangeListener = (sharedPreferences, key) -> {
            fetchJsonResponseForAllLocations();
        };

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
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
        tomorrowIoDataMap = new HashMap<>();
        findViewById(R.id.ProgressBar01).setVisibility(View.VISIBLE);
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> allFaves = (Map<String, String>) sharedPref.getAll();
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

                    for (Map.Entry<String, String> entry : allFaves.entrySet()) {
                        makeApiCallOuter(entry.getValue(), allFaves.size() + 1, entry.getKey());
                    }
                    makeApiCallOuter(locationCoord, allFaves.size() + 1, "current");

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

    private void makeApiCallOuter(String locationCoord, int size, String mapKey) throws JSONException {
        makeApiCall("https://csci571-hw8-329706.wl.r.appspot.com/currentWeather?location=" + locationCoord, new VolleyCallBack() {
            @Override
            public void onSuccess() {
                if (tomorrowIoDataMap.size() == size) {
                    findViewById(R.id.ProgressBar01).setVisibility(View.INVISIBLE);
                    ViewPager2 pager = findViewById(R.id.details_viewpager);
                    DetailsPagerAdapter adapter = new DetailsPagerAdapter(MainActivity.this, tomorrowIoDataMap);
                    pager.setAdapter(adapter);

                    TabLayout tabLayout = findViewById(R.id.tab_layout);
                    new TabLayoutMediator(tabLayout, pager,
                            (tab, position) -> tab.setText("")
                    ).attach();
                }
            }
        }, mapKey, locationCoord);

    }

    private void makeApiCall(String url, final VolleyCallBack callBack, String mapKey, String coord) throws JSONException {
        if (sharedPreferencesReq.contains(coord)) {
            addToMap(sharedPreferencesReq.getString(coord, ""), mapKey, coord, callBack);
            return;
        }
        ApiCall.make(getBaseContext(), url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                try {
                    sharedPreferencesReq.edit().putString(coord, resp).commit();
                    addToMap(resp, mapKey, coord, callBack);
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

    public void addToMap(String resp, String mapKey, String coord, VolleyCallBack callBack) throws JSONException {
        JSONObject response = new JSONObject(resp);
        JSONArray timelines = response.getJSONObject("day").getJSONObject("data").getJSONArray("timelines");
        JSONArray intervals = timelines.getJSONObject(0).getJSONArray("intervals");
        JSONArray initializedDataHourly = response.getJSONObject("current").getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
        tomorrowIoDataMap.put(mapKey, new TomorrowIoData(intervals, initializedDataHourly,
                mapKey.equals("current") ? locationName : mapKey, coord));
        callBack.onSuccess();
    }
}
