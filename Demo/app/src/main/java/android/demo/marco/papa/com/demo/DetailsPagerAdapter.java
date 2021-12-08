package android.demo.marco.papa.com.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Map;

public class DetailsPagerAdapter extends FragmentStateAdapter {
    Map<String, TomorrowIoData> tomorrowIoDataMap;

    public DetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, Map<String, TomorrowIoData> tomorrowIoDataMap) {
        super(fragmentActivity);
        this.tomorrowIoDataMap = tomorrowIoDataMap;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new DetailsFragment(tomorrowIoDataMap.get("current"));
    }

    @Override
    public int getItemCount() {
        return tomorrowIoDataMap.size();
    }
}
