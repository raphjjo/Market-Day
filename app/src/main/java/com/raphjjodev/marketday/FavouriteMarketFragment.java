package com.raphjjodev.marketday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class FavouriteMarketFragment extends Fragment {

    private MarketViewModel marketViewModel;
    private MarketRecyclerAdapter marketRecyclerAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CoordinatorLayout favCoordLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite_market, container, false);

        initiateRecyclerView(view);
        favCoordLayout = view.findViewById(R.id.favCoordLayout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        marketViewModel = new ViewModelProvider(getActivity()).get(MarketViewModel.class);
        marketViewModel.getFavouriteMarket().observe(getViewLifecycleOwner(), new Observer<List<Market>>() {
            @Override
            public void onChanged(List<Market> markets) {
                marketRecyclerAdapter.submitList(markets);
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            int id = result.getData().getIntExtra(AddEditMarket.EXTRA_ID, 1);

                            if (id == -1) {

                                MainActivity.makeSnackBar("Market cannot be updated", favCoordLayout, Snackbar.LENGTH_SHORT);
                                return;
                            }

                            String name = result.getData().getStringExtra(AddEditMarket.EXTRA_NAME);
                            String location = result.getData().getStringExtra(AddEditMarket.EXTRA_LOCATION);
                            String lastDate = result.getData().getStringExtra(AddEditMarket.EXTRA_LAST_DATE);
                            boolean favourite = result.getData().getBooleanExtra(AddEditMarket.EXTRA_FAVOURITE, false);
                            boolean dayInclusive = result.getData().getBooleanExtra(AddEditMarket.EXTRA_DAY_INCLUSIVE, true);
                            int interval = result.getData().getIntExtra(AddEditMarket.EXTRA_INTERVAL, 1);

                            Market market = new Market(name, location, lastDate, dayInclusive, favourite, interval);
                            market.setId(id);
                            marketViewModel.update(market);
                            MainActivity.makeSnackBar("Market updated successfully", favCoordLayout,
                                    Snackbar.LENGTH_SHORT);
                        } else {

                            MainActivity.makeSnackBar("Market cannot be updated", favCoordLayout, Snackbar.LENGTH_SHORT);
                        }
                    }
                }
        );
    }

    private void initiateRecyclerView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFav);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        marketRecyclerAdapter = new MarketRecyclerAdapter();
        recyclerView.setAdapter(marketRecyclerAdapter);

        marketRecyclerAdapter = new MarketRecyclerAdapter();
        recyclerView.setAdapter(marketRecyclerAdapter);

        marketRecyclerAdapter.setOnItemClickedListener(new MarketRecyclerAdapter.OnItemClicked() {
            @Override
            public void onFavouriteIconClicked(Market market) {

                Market newMarket = updateMarkets(market);
                if (newMarket != null) {
                    marketViewModel.update(newMarket);
                    MainActivity.makeSnackBar("Favourite updated", favCoordLayout, Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onEditIconClicked(Market market) {
                startActivityForResult(market);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                marketViewModel.delete(marketRecyclerAdapter.getMarketAt(viewHolder.getAdapterPosition()));
                MainActivity.makeSnackBarWithAction("Market deleted successfully", favCoordLayout,
                        Snackbar.LENGTH_LONG, "UNDO", "Deleted market restored successfully",
                        marketRecyclerAdapter.getMarketAt(viewHolder.getAdapterPosition()), marketViewModel);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void startActivityForResult(Market marketToBeEdited) {

        Intent intent = new Intent(getContext(), AddEditMarket.class);
        intent.putExtra(AddEditMarket.EXTRA_ID, marketToBeEdited.getId());
        intent.putExtra(AddEditMarket.EXTRA_NAME, marketToBeEdited.getName());
        intent.putExtra(AddEditMarket.EXTRA_LOCATION, marketToBeEdited.getLocation());
        intent.putExtra(AddEditMarket.EXTRA_LAST_DATE, marketToBeEdited.getLastDate());
        intent.putExtra(AddEditMarket.EXTRA_FAVOURITE, marketToBeEdited.isFavourite());
        intent.putExtra(AddEditMarket.EXTRA_DAY_INCLUSIVE, marketToBeEdited.isDayInclusive());
        intent.putExtra(AddEditMarket.EXTRA_INTERVAL, marketToBeEdited.getInterval());
        activityResultLauncher.launch(intent);
    }

    private Market updateMarkets(Market oldMarket) {

        if (oldMarket != null) {

            int id = oldMarket.getId();
            if (id == -1) {

                MainActivity.makeSnackBar("Favourite cannot be updated", favCoordLayout, Snackbar.LENGTH_SHORT);
                return null;
            }

            Market newMarket = new Market(oldMarket.getName(), oldMarket.getLocation(), oldMarket.getLastDate(),
                    oldMarket.isDayInclusive(), !oldMarket.isFavourite(), oldMarket.getInterval());
            newMarket.setId(id);
            return newMarket;
        }

        return null;
    }
}