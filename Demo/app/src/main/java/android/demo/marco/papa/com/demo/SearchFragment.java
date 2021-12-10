package android.demo.marco.papa.com.demo;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private boolean isSearchCompleted = false;
    private AutoSuggestAdapter autoSuggestAdapter;
    private SharedPreferences sharedPreferencesReq;

    public static final Map<String, String> STATE_MAP = new HashMap<>();

    public SearchFragment() {
        // Required empty public constructor
        STATE_MAP.put("AL", "Alabama");
        STATE_MAP.put("AK", "Alaska");
        STATE_MAP.put("AB", "Alberta");
        STATE_MAP.put("AZ", "Arizona");
        STATE_MAP.put("AR", "Arkansas");
        STATE_MAP.put("BC", "British Columbia");
        STATE_MAP.put("CA", "California");
        STATE_MAP.put("CO", "Colorado");
        STATE_MAP.put("CT", "Connecticut");
        STATE_MAP.put("DE", "Delaware");
        STATE_MAP.put("DC", "District Of Columbia");
        STATE_MAP.put("FL", "Florida");
        STATE_MAP.put("GA", "Georgia");
        STATE_MAP.put("GU", "Guam");
        STATE_MAP.put("HI", "Hawaii");
        STATE_MAP.put("ID", "Idaho");
        STATE_MAP.put("IL", "Illinois");
        STATE_MAP.put("IN", "Indiana");
        STATE_MAP.put("IA", "Iowa");
        STATE_MAP.put("KS", "Kansas");
        STATE_MAP.put("KY", "Kentucky");
        STATE_MAP.put("LA", "Louisiana");
        STATE_MAP.put("ME", "Maine");
        STATE_MAP.put("MB", "Manitoba");
        STATE_MAP.put("MD", "Maryland");
        STATE_MAP.put("MA", "Massachusetts");
        STATE_MAP.put("MI", "Michigan");
        STATE_MAP.put("MN", "Minnesota");
        STATE_MAP.put("MS", "Mississippi");
        STATE_MAP.put("MO", "Missouri");
        STATE_MAP.put("MT", "Montana");
        STATE_MAP.put("NE", "Nebraska");
        STATE_MAP.put("NV", "Nevada");
        STATE_MAP.put("NB", "New Brunswick");
        STATE_MAP.put("NH", "New Hampshire");
        STATE_MAP.put("NJ", "New Jersey");
        STATE_MAP.put("NM", "New Mexico");
        STATE_MAP.put("NY", "New York");
        STATE_MAP.put("NF", "Newfoundland");
        STATE_MAP.put("NC", "North Carolina");
        STATE_MAP.put("ND", "North Dakota");
        STATE_MAP.put("NT", "Northwest Territories");
        STATE_MAP.put("NS", "Nova Scotia");
        STATE_MAP.put("NU", "Nunavut");
        STATE_MAP.put("OH", "Ohio");
        STATE_MAP.put("OK", "Oklahoma");
        STATE_MAP.put("ON", "Ontario");
        STATE_MAP.put("OR", "Oregon");
        STATE_MAP.put("PA", "Pennsylvania");
        STATE_MAP.put("PE", "Prince Edward Island");
        STATE_MAP.put("PR", "Puerto Rico");
        STATE_MAP.put("QC", "Quebec");
        STATE_MAP.put("RI", "Rhode Island");
        STATE_MAP.put("SK", "Saskatchewan");
        STATE_MAP.put("SC", "South Carolina");
        STATE_MAP.put("SD", "South Dakota");
        STATE_MAP.put("TN", "Tennessee");
        STATE_MAP.put("TX", "Texas");
        STATE_MAP.put("UT", "Utah");
        STATE_MAP.put("VT", "Vermont");
        STATE_MAP.put("VI", "Virgin Islands");
        STATE_MAP.put("VA", "Virginia");
        STATE_MAP.put("WA", "Washington");
        STATE_MAP.put("WV", "West Virginia");
        STATE_MAP.put("WI", "Wisconsin");
        STATE_MAP.put("WY", "Wyoming");
        STATE_MAP.put("YT", "Yukon Territory");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_search, container, false);

        Context context = getActivity();
        sharedPreferencesReq = context.getSharedPreferences(getString(R.string.req_file_key), Context.MODE_PRIVATE);

        mainView.findViewById(R.id.back2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(getContext(), InputMethodManager.class);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                if (isSearchCompleted) {
                    isSearchCompleted = false;
                    fm.popBackStack();
                }
                //getActivity().recreate();
            }
        });

        mainView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatAutoCompleteTextView autoCompleteTextView = mainView.findViewById(R.id.auto_complete_edit_text);
                autoCompleteTextView.setText("");
            }
        });

        final AppCompatAutoCompleteTextView autoCompleteTextView = mainView.findViewById(R.id.auto_complete_edit_text);
        final TextView selectedText = mainView.findViewById(R.id.selected_item);
        //Setting up the adapter for AutoSuggest
        autoSuggestAdapter = new AutoSuggestAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(autoSuggestAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                autoCompleteTextView.setText(autoSuggestAdapter.getObject(position));
                selectedText.setText("Search Result");
                autoCompleteTextView.setInputType(0);
                autoCompleteTextView.setCursorVisible(false);
                isSearchCompleted = true;
                String selectedOption = autoSuggestAdapter.getObject(position);

                getActivity().findViewById(R.id.ProgressBar01).setVisibility(View.VISIBLE);

                ApiCall.make(getContext(), "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                                selectedOption + "&key=AIzaSyAoJrD09LsoTIlgQd_SimxKZbRkJtduSHI",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject gLoc = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                    String location = gLoc.getString("lat") + "," + gLoc.getString("lng");

                                    if(sharedPreferencesReq.contains(location)) {
                                        extracted(sharedPreferencesReq.getString(location, ""), selectedOption, location);
                                        return;
                                    }
                                    makeApiCallTomorrowIo("https://csci571-hw8-329706.wl.r.appspot.com/currentWeather?location=" + location,
                                            selectedOption, location);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null);

                InputMethodManager imm = (InputMethodManager) getSystemService(getContext(), InputMethodManager.class);
                imm.hideSoftInputFromWindow(mainView.getWindowToken(), 0);
            }
        });

        autoCompleteTextView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                        handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                        handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                                AUTO_COMPLETE_DELAY);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });
        return mainView;
    }

    private void makeApiCall(String text) {
        ApiCall.make(getContext(), "https://csci571-hw8-329706.wl.r.appspot.com/autocomplete?inputCity=" + text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("city") + ", " + STATE_MAP.get(row.getString("state")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }


    private void makeApiCallTomorrowIo(String url, String locationName, String coord) {
        ApiCall.make(getContext(), url, new Response.Listener<String>() {
            @Override
            public void onResponse(String resp) {
                try {
                    extracted(resp, locationName, coord);
                    sharedPreferencesReq.edit().putString(coord, resp).commit();
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

    private void extracted(String resp, String locationName, String coord) throws JSONException {
        JSONObject response = new JSONObject(resp);
        JSONArray timelines = response.getJSONObject("day").getJSONObject("data").getJSONArray("timelines");
        JSONArray intervals = timelines.getJSONObject(0).getJSONArray("intervals");
        JSONArray initializedDataHourly = response.getJSONObject("current").getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
        DetailsFragment detailsFragment = new DetailsFragment(new TomorrowIoData(intervals, initializedDataHourly,
                locationName, coord), false, false);

        getActivity().findViewById(R.id.ProgressBar01).setVisibility(View.INVISIBLE);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutBody, detailsFragment);
        fragmentTransaction.addToBackStack("TAG2");
        fragmentTransaction.commit(); // save the changes
    }
}