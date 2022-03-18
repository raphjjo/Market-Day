package com.raphjjodev.marketday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddEditMarket extends AppCompatActivity {

    public static final String EXTRA_ID = "com.raphjjodev.marketday.EXTRA_ID";
    public static final String EXTRA_NAME = "com.raphjjodev.marketday.EXTRA_NAME";
    public static final String EXTRA_LOCATION = "com.raphjjodev.marketday.EXTRA_LOCATION";
    public static final String EXTRA_LAST_DATE = "com.raphjjodev.marketday.EXTRA_LAST_DATE";
    public static final String EXTRA_DAY_INCLUSIVE = "com.raphjjodev.marketday.EXTRA_DAY_INCLUSIVE";
    public static final String EXTRA_FAVOURITE = "com.raphjjodev.marketday.EXTRA_FAVOURITE";
    public static final String EXTRA_INTERVAL = "com.raphjjodev.marketday.EXTRA_INTERVAL";
    public static final String EXTRA_REQUEST_CODE = "com.raphjjodev.marketday.EXTRA_REQUEST_CODE";

    private EditText nameEditText, lgaEditText, stateEditText;
    private Button setLastDateButton;
    private SwitchCompat mdInclusive;
    private NumberPicker intervalNumberPicker;
    private ImageView favouriteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_market);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        nameEditText = findViewById(R.id.name_editView);
        lgaEditText = findViewById(R.id.lga_editView);
        stateEditText = findViewById(R.id.state_editView);
        setLastDateButton = findViewById(R.id.set_last_date_button);
        mdInclusive = findViewById(R.id.mdinclusive_switch);
        intervalNumberPicker = findViewById(R.id.mdinterval_number_picker);
        favouriteIcon = findViewById(R.id.favourite_setter_imageView);

        intervalNumberPicker.setMinValue(1);
        intervalNumberPicker.setMaxValue(31);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {

            setTitle(R.string.edit_market);
            nameEditText.setText(intent.getStringExtra(EXTRA_NAME));
            String[] locationStringArray = intent.getStringExtra(EXTRA_LOCATION).split(",", 2);
            lgaEditText.setText(locationStringArray[0]);
            stateEditText.setText(locationStringArray[1]);
            setLastDateButton.setText(intent.getStringExtra(EXTRA_LAST_DATE));
            mdInclusive.setChecked(intent.getBooleanExtra(EXTRA_DAY_INCLUSIVE, false));
            intervalNumberPicker.setValue(intent.getIntExtra(EXTRA_INTERVAL, 1));
            favouriteIcon.setImageResource(changeNotificationIcon(intent.getBooleanExtra(EXTRA_FAVOURITE, false)));
            favouriteIcon.setTag(changeNotificationIconTag(intent.getBooleanExtra(EXTRA_FAVOURITE, false)));
        } else {

            setTitle(getString(R.string.add_market));
        }

        setLastDateButton.setOnClickListener(v -> {

            Calendar myCalender = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "dd/MM/yyyy";
                SimpleDateFormat smp = new SimpleDateFormat(format, Locale.getDefault());
                setLastDateButton.setText(smp.format(myCalender.getTime()));

            };

            new DatePickerDialog(this, date, myCalender.get(Calendar.YEAR),
                    myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)).show();

            });

        favouriteIcon.setOnClickListener(v -> {
            if (favouriteIcon.getTag().toString().equals("1")) {

                favouriteIcon.setImageResource(R.drawable.ic_notifications_off);
                favouriteIcon.setTag("2");
            } else {

                favouriteIcon.setImageResource(R.drawable.ic_notifications_active);
                favouriteIcon.setTag("1");
            }

        });



    }

    private int changeNotificationIcon(boolean favoriteBellActive) {

        if (favoriteBellActive)
            return R.drawable.ic_notifications_active;
        else
            return R.drawable.ic_notifications_off;
    }

    private int changeNotificationIconTag(boolean favoriteBellActive) {

        if (favoriteBellActive)
            return 1;
        else
            return 2;
    }

    private boolean setFavourite (ImageView imageView) {

        return Integer.parseInt(imageView.getTag().toString()) == 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_market:
                saveMarket();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveMarket() {

        String name = nameEditText.getText().toString();
        String lga = lgaEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String location = lgaEditText.getText().toString() +
                ", " + stateEditText.getText().toString();
        String lastDate = setLastDateButton.getText().toString();
        int interval = intervalNumberPicker.getValue();
        boolean isFavourite = setFavourite(favouriteIcon);
        boolean isMarketDayInclusive = mdInclusive.isChecked();

        if (name.trim().isEmpty() || lga.trim().isEmpty() || state.trim().isEmpty() ||
                lastDate.trim().isEmpty()) {

            Toast.makeText(AddEditMarket.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(AddEditMarket.EXTRA_NAME, name);
        intent.putExtra(AddEditMarket.EXTRA_LOCATION, location);
        intent.putExtra(AddEditMarket.EXTRA_LAST_DATE, lastDate);
        intent.putExtra(AddEditMarket.EXTRA_FAVOURITE, isFavourite);
        intent.putExtra(AddEditMarket.EXTRA_DAY_INCLUSIVE, isMarketDayInclusive);
        intent.putExtra(AddEditMarket.EXTRA_INTERVAL, interval);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, intent);
        finish();

    }
}