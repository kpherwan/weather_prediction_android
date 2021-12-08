package android.demo.marco.papa.com.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DayDetailsFragment extends Fragment {
    JSONObject values;
    String uv;
    Map<String, String> weatherMap = new HashMap<>();

    public DayDetailsFragment(JSONObject values, String uv) {
        this.uv = uv;
        this.values = values;
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
        View view = inflater.inflate(R.layout.day_deets, container, false);

        TextView c = view.findViewById(R.id.wind_speed);
        try {
            c.setText(values.getString("windSpeed") + " mph");

            c = view.findViewById(R.id.pressure_value);
            c.setText(values.getString("pressureSeaLevel") + " inHg");

            c = view.findViewById(R.id.precipitation);
            c.setText(values.getString("precipitationProbability") + " %");

            c = view.findViewById(R.id.temperature_value);
            c.setText(values.getInt("temperature") + " °F");

            ImageView i = view.findViewById(R.id.statusImage_value);
            String uri = "@drawable/image" + values.getString("weatherCode");
            int imageResource = getResources().getIdentifier(uri, null, "android.demo.marco.papa.com.demo");
            Drawable res = getResources().getDrawable(imageResource);
            i.setImageDrawable(res);

            c = view.findViewById(R.id.statusText_value);
            c.setText(weatherMap.get(values.getString("weatherCode")));

            c = view.findViewById(R.id.humidity_value);
            c.setText(values.getInt("humidity") + " %");

            c = view.findViewById(R.id.visibility_value);
            c.setText(values.getString("visibility") + " mi");

            c = view.findViewById(R.id.cloud_cover);
            c.setText(values.getString("cloudCover") + " %");

            c = view.findViewById(R.id.ozone);
            c.setText(uv);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
