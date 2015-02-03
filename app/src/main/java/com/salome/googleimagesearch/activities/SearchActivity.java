package com.salome.googleimagesearch.activities;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.salome.googleimagesearch.R;
import com.salome.googleimagesearch.adapters.ImageResultsAdapter;
import com.salome.googleimagesearch.api.GoogleImageSearchHttpClient;
import com.salome.googleimagesearch.fragments.SettingsDialogFragment;
import com.salome.googleimagesearch.listeners.EndlessScrollListener;
import com.salome.googleimagesearch.models.ImageResult;
import com.salome.googleimagesearch.models.ImageSearchSettings;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity implements SettingsDialogFragment.OnFragmentInteractionListener {

    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;
    private ImageSearchSettings imageSearchSettings;
    private SearchView searchView;
    private String queryText;
    
    private ImageView ivPlaceHolder; 
    private TextView tvPlaceHolder;
    
    private FragmentManager fragmentManager;
    private SettingsDialogFragment mSettingsFragment;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // Creates the data source
        imageResults = new ArrayList<ImageResult>();
        // Attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to the adapterview (gridview)
        gvResults.setAdapter(aImageResults);
        // TODO read the saved user preferences from the disk?
        
        //initialize search settings with defaults
        imageSearchSettings = new ImageSearchSettings();
       
        //fragment inits
        fragmentManager= getSupportFragmentManager();
        mSettingsFragment = new SettingsDialogFragment();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.miSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Handle button click here
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                // fire the search query here
                queryText = query;
                Toast.makeText(SearchActivity.this,"user initiated search with: "+query,Toast.LENGTH_SHORT).show();
                searchView.setSubmitButtonEnabled(true);
                searchItem.collapseActionView();
                searchImages(0);
                ivPlaceHolder.setVisibility(View.GONE);
                tvPlaceHolder.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //will activate when pressed on miSettings button
        if (id == R.id.miSettings){
            Toast.makeText(this, "settings button invoked", Toast.LENGTH_SHORT).show();
            showSearchDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    
    /**
     * * private methods
     * */
    
    private void showSearchDialog() {
        Bundle bundle = new Bundle();
        //sending in default imageSearch settings to the fragment so that it can show appropriate options
        bundle.putSerializable("ImageSearchSettings", imageSearchSettings);
        mSettingsFragment.setArguments(bundle);
        mSettingsFragment.show(fragmentManager,"fragment_settings_dialog");
        mSettingsFragment.setCancelable(false);
    }

    private void setupViews() {
        ivPlaceHolder = (ImageView)findViewById(R.id.ivThumbNail);
        tvPlaceHolder = (TextView) findViewById(R.id.tvPlaceHolder);
        
        gvResults = (StaggeredGridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image detailed display activity
                // Creating an intent
                Intent i = new Intent(SearchActivity.this, ImageDetailedDisplayActivity.class);
                // Get the clicked image result to display in detailed view
                ImageResult imageResult = imageResults.get(position);
                // Pass the image result into the intent
                i.putExtra("ImageResult", imageResult); // need to either be serializable or parcelable
                // Launch the new activity
                startActivity(i);

            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                Log.d("page: "+String.valueOf(page),"total items count:"+String.valueOf(totalItemsCount));
                searchImages(totalItemsCount);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
        
    }
    
    private void searchImages(int start){
        if(!isNetworkAvailable()){
        
            Toast.makeText(this,"Network unavailable try again!",Toast.LENGTH_LONG).show();
        }
        else{
            StringBuilder searchUrl = new StringBuilder("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=")
                    .append(queryText)
                    .append("&rsz=8")
                    .append(imageSearchSettings.getURLParams());
            
            //if new request
            if(start ==0){
                aImageResults.clear();
                aImageResults.notifyDataSetChanged();
            }
            
            // if needs pagination
            if (start > 0) {
                if(start>=64){
                    Toast.makeText(this, "Max results limit reached!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    searchUrl.append("&start=").append(String.valueOf(start));
            }
            if(queryText==null || queryText.trim().equals(""))
                return;
            GoogleImageSearchHttpClient.get(searchUrl.toString(), aImageResults);

        }
            

    }

    // check if network is available 
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onSettingsSubmit(ImageSearchSettings settings) {
        //update the settings on the activity
        imageSearchSettings = settings;
        // now perform a new image search with this applied settings
        searchImages(0);
        Log.d("grabbed a click on settings button and opening up the settings fragment now + "+settings.toString(), this.toString());
        
    }

}
