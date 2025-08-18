package com.example.pomodorotimer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pomodorotimer.ui.statistics.StatisticsFragment;
import com.example.pomodorotimer.ui.settings.SettingsFragment;
import com.example.pomodorotimer.ui.home.HomeFragment;
import com.example.pomodorotimer.util.HandlerSharedPreferences;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Map<Integer, IconText> iconTextMap;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize HandlerSharedPreferences
        HandlerSharedPreferences.getInstance(this);

        // Initialize views and setup UI
        initializeViews();
        setupViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed");
    }

    private void initializeViews() {
        iconTextMap = new HashMap<>();
        viewPager2 = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    private void setupViewPager() {
        OrdersPagerAdapterFactory adapter = new OrdersPagerAdapterFactory(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            IconText iconText = getIconText(position);
            tab.setIcon(iconText.getIcon());
            tab.setText(iconText.getName());
        }).attach();
    }

    @NotNull
    private IconText getIconText(int position) {
        if (iconTextMap.containsKey(position)) {
            return Objects.requireNonNull(iconTextMap.get(position));
        } else {
            IconText iconText = null;
            switch (position) {
                case 1:
                    iconText = new IconText(R.drawable.ic_baseline_settings_24, "Settings");
                    break;
                case 2:
                    iconText = new IconText(R.drawable.ic_baseline_access_time_24, "Statistics");
                    break;
                default:
                    iconText = new IconText(R.drawable.ic_baseline_home_24, "Home");
                    break;
            }

            iconTextMap.put(position, iconText);
            return Objects.requireNonNull(iconText);
        }
    }

    private static class OrdersPagerAdapterFactory extends FragmentStateAdapter {

        private final Map<Integer, Fragment> integerFragmentMap;

        public OrdersPagerAdapterFactory(@NonNull @NotNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            integerFragmentMap = new HashMap<>();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "createFragment: " + position);
            if (integerFragmentMap.containsKey(position)) {
                return Objects.requireNonNull(integerFragmentMap.get(position));
            }

            Fragment fragment = null;
            switch (position) {
                case 2:
                    Log.d(TAG, "createFragment: StatisticsFragment");
                    fragment = new StatisticsFragment();
                    break;
                case 1:
                    Log.d(TAG, "createFragment: SettingsFragment");
                    fragment = new SettingsFragment();
                    break;
                default:
                    Log.d(TAG, "createFragment: Default");
                    fragment = new HomeFragment();
                    break;
            }

            integerFragmentMap.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private static class IconText {
        private final int icon;
        private final String name;

        public IconText(int icon, String name) {
            this.icon = icon;
            this.name = name;
        }

        public int getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }
    }
}