package android.demo.marco.papa.com.demo;

import static androidx.core.content.ContextCompat.getSystemService;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private boolean isSearchCompleted = false;
    private AutoSuggestAdapter autoSuggestAdapter;

    public SearchFragment() {
        // Required empty public constructor
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
        mainView.findViewById(R.id.back2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                ApiCall.make(getContext(), "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                                selectedOption + "&key=AIzaSyAoJrD09LsoTIlgQd_SimxKZbRkJtduSHI",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject gLoc = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                                    String location = gLoc.getString("lat") + "," + gLoc.getString("lng");

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
                        stringList.add(row.getString("city") + ", " + row.getString("state"));
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
                    JSONObject response = new JSONObject(resp);
                    JSONArray timelines = response.getJSONObject("day").getJSONObject("data").getJSONArray("timelines");
                    JSONArray intervals = timelines.getJSONObject(0).getJSONArray("intervals");
                    JSONArray initializedDataHourly = response.getJSONObject("current").getJSONObject("data").getJSONArray("timelines").getJSONObject(0).getJSONArray("intervals");
                    DetailsFragment detailsFragment = new DetailsFragment(new TomorrowIoData(intervals, initializedDataHourly,
                            locationName, coord), false, false);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayoutBody, detailsFragment);
                    fragmentTransaction.addToBackStack("TAG2");
                    fragmentTransaction.commit(); // save the changes
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
}