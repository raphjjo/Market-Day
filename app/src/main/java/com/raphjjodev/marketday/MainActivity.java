package com.raphjjodev.marketday;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BadgeDrawable badgeDrawable;
    private MarketViewModel marketViewModel;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CoordinatorLayout mainCoordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mainCoordLayout = findViewById(R.id.mainCoordLayout);

        FloatingActionButton addMarketFAB = findViewById(R.id.addFAB);
        addMarketFAB.setOnClickListener(v -> {
            startActivityForResult();
        });

        setupViewPager();

        marketViewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        marketViewModel.getFavouriteMarket().observe(this, new Observer<List<Market>>() {
            @Override
            public void onChanged(List<Market> markets) {
                badgeDrawable.setNumber(markets.size());
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            String name = result.getData().getStringExtra(AddEditMarket.EXTRA_NAME);
                            String location = result.getData().getStringExtra(AddEditMarket.EXTRA_LOCATION);
                            String lastDate = result.getData().getStringExtra(AddEditMarket.EXTRA_LAST_DATE);
                            boolean favourite = result.getData().getBooleanExtra(AddEditMarket.EXTRA_FAVOURITE, false);
                            boolean dayInclusive = result.getData().getBooleanExtra(AddEditMarket.EXTRA_DAY_INCLUSIVE, true);
                            int interval = result.getData().getIntExtra(AddEditMarket.EXTRA_INTERVAL, 1);

                            marketViewModel.insert(new Market(name, location, lastDate, dayInclusive, favourite, interval));
                            makeSnackBar("Market saved successfully", mainCoordLayout, Snackbar.LENGTH_SHORT);

                        } else if (result.getResultCode() == Activity.RESULT_CANCELED){

                            makeSnackBar("Market not saved", mainCoordLayout, Snackbar.LENGTH_SHORT);
                        } else {

                            makeSnackBar("Market not saved", mainCoordLayout, Snackbar.LENGTH_SHORT);
                        }
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.share_app:
                shareApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareApp() {

        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        // MIME of .apk is "application/vnd.android.package-archive".
        // but Bluetooth does not accept this. Let's use "*/*" instead.
        intent.setType("*/*");

        // Append file and send Intent
        File originalApk = new File(filePath);

        try {
            //Make new directory in new location=
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            //If directory doesn't exists create new
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            //Get application's name and convert to lowercase
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            //If file doesn't exists create new
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            //Copy file to new location
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
            //Open share dialog
            Uri appURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
            intent.putExtra(Intent.EXTRA_STREAM, appURI);
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startActivityForResult() {

        Intent intent = new Intent(this, AddEditMarket.class);
        activityResultLauncher.launch(intent);
    }

    public static void makeSnackBar(String message, View view, int duration) {

        //USe SnackBar constant duration for duration
        if (view == null) {

            Log.i("makeSnackBar", "Something went wrong");
            return;
        }
        Snackbar.make(view, message, duration).show();
    }

    public static void makeSnackBarWithAction(String message, View view, int duration,
                                              String actionMessage, String returnActionMessage,
                                              Market market, MarketViewModel marketViewModel) {

        if (view == null || marketViewModel == null || market == null) {

            Log.i("makeSnackBarWithAction", "Something went wrong");
            return;
        }

        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction(actionMessage, v -> {


            marketViewModel.insert(market);
            Snackbar.make(view, returnActionMessage, Snackbar.LENGTH_SHORT).show();

        });
        snackbar.show();
    }

    private void setupViewPager() {

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        viewPager2.setAdapter(new MarketsPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position) {

                    case 0: {

                        tab.setText(R.string.all_market);
                        tab.setIcon(R.drawable.ic_market_list);
                        break;
                    }
                    case 1: {

                        tab.setText(R.string.favourite);
                        tab.setIcon(R.drawable.ic_favourite);
                        badgeDrawable = tab.getOrCreateBadge();
                        badgeDrawable.setBackgroundColor(
                                ContextCompat.getColor(getApplicationContext(), R.color.secondary_light)
                        );
                        badgeDrawable.setVisible(true);
                        badgeDrawable.setMaxCharacterCount(2);
                        break;
                    }
                }
            }
        }
        );

        tabLayoutMediator.attach();
    }



}