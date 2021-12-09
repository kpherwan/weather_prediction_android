package android.demo.marco.papa.com.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetailsFragment extends Fragment {
    TomorrowIoData tomorrowIoData;
    Map<String, String> weatherMap = new HashMap<>();
    private boolean isLocal;
    private boolean isFave;

    public DetailsFragment(TomorrowIoData tomorrowIoData, boolean isLocal, boolean isFave) {
        this.isFave = isFave;
        this.tomorrowIoData = tomorrowIoData;
        this.isLocal = isLocal;
        weatherMap.put("4201", "Heavy Rain");
        weatherMap.put("1001", "Cloudy");
        weatherMap.put("4001", "Rain");
        weatherMap.put("4200", "Light Rain");
        weatherMap.put("6201", "Heavy Freezing Rain");
        weatherMap.put("6001", "Freezing Rain");
        weatherMap.put("6200", "Light Freezing Rain");
        weatherMap.put("6000", "Freezing Drizzle");
        weatherMap.put("4000", "Drizzle");
        weatherMap.put("7101", "Heavy Ice Pellets");
        weatherMap.put("7000", "Ice Pellets");
        weatherMap.put("7102", "Light Ice Pellets");
        weatherMap.put("5101", "Heavy Snow");
        weatherMap.put("5000", "Snow");
        weatherMap.put("5100", "Light Snow");
        weatherMap.put("5001", "Flurries");
        weatherMap.put("8000", "Thunderstorm");
        weatherMap.put("2100", "Light Fog");
        weatherMap.put("2000", "Fog");
        weatherMap.put("1102", "Mostly Cloudy");
        weatherMap.put("1101", "Partly Cloudy");
        weatherMap.put("1100", "Mostly Clear");
        weatherMap.put("1000", "Clear");
        weatherMap.put("3000", "Light Wind");
        weatherMap.put("3001", "Wind");
        weatherMap.put("3002", "Strong Wind");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_details_fragment, container, false);
        View button = view.findViewById(R.id.addFave);
        ImageView visual = view.findViewById(R.id.faveButton);
        if (isLocal) {
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getActivity();
                    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    if(!isFave) {
                        Toast.makeText(getContext(), tomorrowIoData.getLocationName() + " was added to favorites", Toast.LENGTH_SHORT).show();
                        visual.setImageDrawable(getResources().getDrawable(R.drawable.removefave));
                        editor.putString(tomorrowIoData.getLocationName(), tomorrowIoData.getCoord());
                        isFave = true;
                    }
                    else {
                        Toast.makeText(getContext(), tomorrowIoData.getLocationName() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                        visual.setImageDrawable(getResources().getDrawable(R.drawable.addfave));
                        editor.remove(tomorrowIoData.getLocationName());
                        isFave = false;
                    }
                    editor.apply();
                }
            });
            if (isFave) {
                visual.setImageDrawable(getResources().getDrawable(R.drawable.removefave));
            }
        }

        view.findViewById(R.id.topBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("tomorrowData", tomorrowIoData.getIntervals().toString());
                intent.putExtra("hourlyData", tomorrowIoData.getInitializedDataHourly().toString());
                intent.putExtra("location", tomorrowIoData.getLocationName());
                startActivity(intent);
            }
        });

        try {
            JSONObject values = tomorrowIoData.getIntervals().getJSONObject(0).getJSONObject("values");
            TextView c = view.findViewById(R.id.temperature);
            c.setText(values.getString("temperature") + "°F");

            ImageView i = view.findViewById(R.id.statusImage);
            String uri = "@drawable/image" + values.getString("weatherCode");
            int imageResource = getResources().getIdentifier(uri, null, "android.demo.marco.papa.com.demo");
            Drawable res = getResources().getDrawable(imageResource);
            i.setImageDrawable(res);

            c = view.findViewById(R.id.statusText);
            c.setText(weatherMap.get(values.getString("weatherCode")));

            c = view.findViewById(R.id.locationName);
            c.setText(tomorrowIoData.getLocationName());

            c = view.findViewById(R.id.humidity);
            c.setText(values.getString("humidity") + "%");

            c = view.findViewById(R.id.wind);
            c.setText(values.getString("windSpeed") + "mph");

            c = view.findViewById(R.id.visibility);
            c.setText(values.getString("visibility") + "mi");

            c = view.findViewById(R.id.pressure);
            c.setText(values.getString("pressureSeaLevel") + "inHg");

            for (int j = 0; j < tomorrowIoData.getIntervals().length(); j++) {
                LinearLayout main = view.findViewById(R.id.scrollView);
                View row = inflater.inflate(R.layout.row_detail, null);

                c = row.findViewById(R.id.date);
                c.setText(tomorrowIoData.getIntervals().getJSONObject(j).getString("startTime").substring(0, 10));

                i = row.findViewById(R.id.rowImage);
                values = tomorrowIoData.getIntervals().getJSONObject(j).getJSONObject("values");
                imageResource = getResources().getIdentifier("@drawable/image" + values.getString("weatherCode"), null, "android.demo.marco.papa.com.demo");
                res = getResources().getDrawable(imageResource);
                i.setImageDrawable(res);

                c = row.findViewById(R.id.tempMin);
                c.setText(values.getString("temperatureMin").substring(0, 2));

                c = row.findViewById(R.id.tempMax);
                c.setText(values.getString("temperatureMax").substring(0, 2));

                main.addView(row);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
