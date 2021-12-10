package android.demo.marco.papa.com.demo;

import android.app.Activity;
import android.content.Intent;
import android.demo.marco.papa.com.demo.databinding.TabDeetBinding;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class DetailsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private TabDeetBinding binding;
    JSONArray intervals;
    JSONArray hourlyData;

    public DetailsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent(); // gets the previously created intent
        try {
            intervals = new JSONArray(myIntent.getStringExtra("tomorrowData"));
            hourlyData = new JSONArray(myIntent.getStringExtra("hourlyData"));

            binding = TabDeetBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            JSONObject values = intervals.getJSONObject(0).getJSONObject("values");
            TextView c = findViewById(R.id.locationNameD);
            c.setText(myIntent.getStringExtra("location"));

            findViewById(R.id.tweet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String twitterText = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode("Check out " +
                                myIntent.getStringExtra("location") + "'s weather! It is " + values.getString("temperatureApparent") +
                                "°F! #CSCI571WeatherSearch");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterText));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
            });

            ViewPager2 pager = findViewById(R.id.details_viewpager_2);
            DayDetailsPagerAdapter adapter = new DayDetailsPagerAdapter(DetailsActivity.this, intervals, hourlyData);
            pager.setAdapter(adapter);

            String tabLabel[] = new String[]{"TODAY", "WEEKLY", "WEATHER DATA"};
            int tabIcon[] = new int[]{R.drawable.calendar, R.drawable.weekly, R.drawable.therm};

            TabLayout tabLayout = findViewById(R.id.dtab_layout);
            new TabLayoutMediator(tabLayout, pager,
                    (tab, position) -> {
                        tab.setText(String.valueOf(tabLabel[position]));
                        tab.setIcon(tabIcon[position]);
                    }
            ).attach();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}