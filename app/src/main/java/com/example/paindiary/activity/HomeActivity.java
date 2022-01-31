package com.example.paindiary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.example.paindiary.R;
import com.example.paindiary.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding hBinding;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = hBinding.getRoot();
        setContentView(view);

        setSupportActionBar(hBinding.appBar.toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_fragment,
                R.id.nav_pain_data_entry_fragment,
                R.id.nav_daily_record_fragment,
                R.id.nav_report_fragment,
                R.id.nav_map_fragment,
                R.id.nav_work_manager_fragment,
                R.id.nav_signout_fragment)
                .setOpenableLayout(hBinding.drawerLayout)
                .build();

        FragmentManager fragmentManager= getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(hBinding.navView, navController);
        NavigationUI.setupWithNavController(hBinding.appBar.toolbar,navController, mAppBarConfiguration);

    }
}