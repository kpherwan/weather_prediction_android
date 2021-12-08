package android.demo.marco.papa.com.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArearange;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIDateTimeLabelFormats;
import com.highsoft.highcharts.common.hichartsclasses.HIDay;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIScrollablePlotArea;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HighChartFragment extends Fragment {

    public HighChartFragment(JSONArray intervals) {
        this.intervals = intervals;
    }

    JSONArray intervals;

    public HighChartFragment() {
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
        View view = inflater.inflate(R.layout.fragment_high_chart, container, false);
        HIChartView chartView = view.findViewById(R.id.hc);
        HIOptions options = new HIOptions();
        HIChart chart = new HIChart();
        JSONObject values = null;
        try {
            Object[][] data = new Object[intervals.length()][3];
            long min = LocalDate.parse(intervals.getJSONObject(0).getString("startTime")
                    .substring(0, 10)).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            for (int i = 0; i < intervals.length(); i++) {
                values = intervals.getJSONObject(i).getJSONObject("values");
                data[i][0] = LocalDate.parse(intervals.getJSONObject(i).getString("startTime")
                        .substring(0, 10)).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                data[i][1] = Double.parseDouble(values.getString("temperatureMin"));
                data[i][2] = Double.parseDouble(values.getString("temperatureMax"));
            }

            chart.setType("arearange");
            chart.setZoomType("x");
            HIScrollablePlotArea area = new HIScrollablePlotArea();
            area.setScrollPositionX(1);
            chart.setScrollablePlotArea(area);
            options.setChart(chart);

            HIArearange series = new HIArearange();
            series.setData(new ArrayList<>(Arrays.asList(data)));
            series.setType("arearange");
            series.setName("Temperatures");
            LinkedList<HIStop> hiStops = new LinkedList<>();
            hiStops.add(new HIStop(0, HIColor.initWithHexValue("fcb97e")));
            hiStops.add(new HIStop(1, HIColor.initWithHexValue("82b8fa")));
            HIColor hiColor = HIColor.initWithLinearGradient(new HIGradient(0, 0.5f, 0, 0.8f), hiStops);
            series.setColor(hiColor);

            HIPlotOptions plotOptions = new HIPlotOptions();
            HIArearange series2 = new HIArearange();
            HIMarker marker = new HIMarker();
            marker.setFillColor(hiColor);
            marker.setRadius(2.5);
            series2.setMarker(marker);

            plotOptions.setSeries(series2);

            HITitle title = new HITitle();
            title.setText("Temperature variation by day");
            options.setTitle(title);

            HITooltip tt = new HITooltip();
            tt.setShared(true);
            tt.setValueSuffix("°F");
            options.setTooltip(tt);

            HILegend hiLegend = new HILegend();
            hiLegend.setEnabled(true);
            options.setLegend(hiLegend);

            ArrayList<HIXAxis> hixAxes = new ArrayList<>();
            HIXAxis hixAxis = new HIXAxis();
            hixAxis.setType("datetime");
            hixAxis.setTickInterval(86400000*2);
            hixAxes.add(hixAxis);
            options.setXAxis(hixAxes);

            ArrayList<HIYAxis> hiyAxes = new ArrayList<>();
            HIYAxis hiyAxis = new HIYAxis();
            hiyAxis.setTickInterval(5);
            hiyAxes.add(hiyAxis);
            options.setYAxis(hiyAxes);

            options.setSeries(new ArrayList(Arrays.asList(series)));
            options.setPlotOptions(plotOptions);
            chartView.setOptions(options);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}