package com.gmail.jskapplications.hometemp.main.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.util.OnPrintTickLabel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gmail.jskapplications.hometemp.R;
import com.gmail.jskapplications.hometemp.base.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class HomeFragment extends BaseFragment {

    private SpeedView mTempSpeedView;
    private SpeedView mHumidSpeedView;

    // 그래프용 - START
    private LineChart mChart;

    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    // 그래프용 - END

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        FragmentManager fm = getChildFragmentManager();

        // 온습도 미터
        mTempSpeedView = (SpeedView) v.findViewById(R.id.tempSpeedView);
        mTempSpeedView.setUnit("적합");
        mTempSpeedView.setWithTremble(false);
        mTempSpeedView.setSpeedAt(17.5f*2.5f);

        // 번호 수정
        mTempSpeedView.setOnPrintTickLabel(new OnPrintTickLabel() {
            @Override
            public CharSequence getTickLabel(int tickPosition, float tick) {
                if(tick == 100) {
                    return "50";
                }
                return Double.toString(tick/2.5);
            }
        });


        mHumidSpeedView = (SpeedView) v.findViewById(R.id.humidSpeedView);
        mHumidSpeedView.setUnit("비적합");
        /*
        // Posts in Grid - Start
        mBestPostsFragment = fm.findFragmentById(R.id.best_posts_container);
        if(mBestPostsFragment == null) {
            mBestPostsFragment = PostRecyclerFragment.newInstance("best");
            fm.beginTransaction()
                    .add(R.id.best_posts_container, mBestPostsFragment)
                    .commit();
        }

        mNewPostsFragment = fm.findFragmentById(R.id.new_posts_container);
        if(mNewPostsFragment == null) {
            mNewPostsFragment = PostRecyclerFragment.newInstance("new");
            fm.beginTransaction()
                    .add(R.id.new_posts_container, mNewPostsFragment)
                    .commit();
        }


        // Posts in Grid - End
*/

        // 그래프 용 - START
        mTfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");


        mChart = (LineChart) v.findViewById(R.id.chart1);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        setData(100, 30);
        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(170f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        // 그래프용 - END

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    // 그래프 - START
    private void setData(int count, float range) {

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        for (float x = from; x < to; x++) {

            float y = getRandom(range, 50);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }
    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }
    // 그래프 - END
}
