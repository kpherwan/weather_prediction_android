package android.demo.marco.papa.com.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONArray;
import org.json.JSONException;

public class DayDetailsPagerAdapter extends FragmentStateAdapter {
    JSONArray intervals;
    JSONArray hourlyData;

    public DayDetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, JSONArray intervals, JSONArray hourlyData) {
        super(fragmentActivity);
        this.intervals = intervals;
        this.hourlyData = hourlyData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            switch (position) {
                case 0:
                case 2:
                    return new DayDetailsFragment(intervals.getJSONObject(0).getJSONObject("values"), hourlyData.getJSONObject(0).getJSONObject("values").getString("uvIndex"));
                case 1:
                    return new HighChartFragment(intervals);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
