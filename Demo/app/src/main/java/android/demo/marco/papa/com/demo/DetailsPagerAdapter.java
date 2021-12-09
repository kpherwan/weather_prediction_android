package android.demo.marco.papa.com.demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Map;

public class DetailsPagerAdapter extends FragmentStateAdapter {
    Map<String, TomorrowIoData> tomorrowIoDataMap;
    String keys[];

    public DetailsPagerAdapter(@NonNull FragmentActivity fragmentActivity, Map<String, TomorrowIoData> tomorrowIoDataMap) {
        super(fragmentActivity);
        this.tomorrowIoDataMap = tomorrowIoDataMap;
        int i = 0;
        keys = new String[tomorrowIoDataMap.size()];
        for(String key: tomorrowIoDataMap.keySet()) {
            keys[i++] = key;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position < keys.length) {
            return new DetailsFragment(tomorrowIoDataMap.get(keys[position]), keys[position].equals("current"), !keys[position].equals("current"));
        }
        return new DetailsFragment(tomorrowIoDataMap.get("current"), true, !keys[position].equals("current"));
    }

    @Override
    public int getItemCount() {
        return tomorrowIoDataMap.size();
    }
}
