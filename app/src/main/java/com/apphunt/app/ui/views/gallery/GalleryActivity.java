package com.apphunt.app.ui.views.gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.apphunt.app.R;
import com.apphunt.app.constants.Constants;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private ViewPager imagesViewPager;
    private ArrayList<String> images = new ArrayList<>();
    private int selectedPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        imagesViewPager = (ViewPager) findViewById(R.id.images);
        images = getIntent().getStringArrayListExtra(Constants.EXTRA_IMAGES);
        selectedPosition = getIntent().getIntExtra(Constants.EXTRA_SELECTED_IMAGE, 0);

        imagesViewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        imagesViewPager.setCurrentItem(selectedPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryFragment.newInstance(images.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }
}
