package com.tea.ilearn;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tea.ilearn.databinding.ActivityMainBinding;
import com.tea.ilearn.ui.chatbot.ChatbotFragment;
import com.tea.ilearn.ui.exercise.ExerciseFragment;
import com.tea.ilearn.ui.home.HomeFragment;
import com.tea.ilearn.ui.link.LinkFragment;
import com.tea.ilearn.ui.me.MeFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SearchView searchView;
    private LinearLayout searchBox;
    private RadioButton searchButton;
    private RadioGroup searchGroup;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private BottomNavigationView navView;

    //View fab, bottomAppBar, frame;
    //CoordinatorLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewPager = binding.pager;
        navView = binding.navView;
        List<Fragment> fragments = new ArrayList<Fragment>() {{
            add(new ExerciseFragment());
            add(new ChatbotFragment());
            add(new HomeFragment());
            add(new LinkFragment());
            add(new MeFragment());
        }};

        pagerAdapter = new ScreenSlidePagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navView.getMenu().getItem(position).setChecked(true);
            }
        });

        navView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case(R.id.navigation_exercise):
                            viewPager.setCurrentItem(0, true);
                            break;
                        case(R.id.navigation_chatbot):
                            viewPager.setCurrentItem(1, true);
                            break;
                        case(R.id.navigation_home):
                            viewPager.setCurrentItem(2, true);
                            break;
                        case(R.id.navigation_link):
                            viewPager.setCurrentItem(3, true);
                            break;
                        case(R.id.navigation_me):
                            viewPager.setCurrentItem(4, true);
                            break;
                    }
                    return true;
                }
        );

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen)
                navView.setVisibility(View.GONE);
            else
                navView.setVisibility(View.VISIBLE);
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity,
                                       List<Fragment> _fragments) {
            super(fragmentActivity);
            fragments = _fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
        //navView.getMenu().getItem(2).setEnabled(false);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home,
//                R.id.navigation_chatbot,
//                R.id.navigation_notifications,
//                R.id.navigation_test
//        ).build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

//        fab = binding.fab;
//        bottomAppBar = binding.bottomAppBar;
//        frame = findViewById(R.id.nav_host_fragment_activity_main);
        //params = new CoordinatorLayout.LayoutParams(-1, -1);

//        final ViewTreeObserver observer= fab.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(() -> {
//            params.setMargins(0, 0, 0, fab.getHeight());
//            frame.setLayoutParams(params);
//        });

//        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
//            if (isOpen) {
//                fab.setVisibility(View.GONE);
//                bottomAppBar.setVisibility(View.GONE);
//            }
//            else {
//                fab.setVisibility(View.VISIBLE);
//                bottomAppBar.setVisibility(View.VISIBLE);
//            }
//        });

        // ==================================================================

        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = binding.searchView;
//        searchBox = binding.searchBox;
//        searchButton = binding.searchEntity;
//        searchGroup = binding.searchGroup;
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        MainActivity that = this;
//        searchGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                RadioButton btn = (RadioButton)searchGroup.findViewById(checkedId);
//            }
//        });
//        searchBox.setVisibility(View.INVISIBLE);
//        searchButton.setChecked(true);

//        int searchCloseButtonId = searchView.getContext().getResources()
//                .getIdentifier("android:id/search_close_btn", null, null);
//        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.setQuery("",false);
//                searchBox.setVisibility(View.INVISIBLE);
//                searchButton.setChecked(true);
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchBox.setVisibility(View.INVISIBLE);
//                // TODO this cannot handle history suggestion.
//                return false;
//            }
//        });
}