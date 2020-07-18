package com.hex.connect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.hex.connect.Fragment.EditOtherDetails;
import com.hex.connect.Fragment.EditPersonalInfo;
import com.hex.connect.Fragment.EditSocialLinks;

import java.util.ArrayList;
import java.util.List;


public class EditProfileActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    //Fragments
    EditPersonalInfo editPersonalInfo;
    EditSocialLinks editSocialLinks;
    EditOtherDetails editOtherDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        //initialize fragments
        editPersonalInfo = new EditPersonalInfo() ;
        editSocialLinks = new EditSocialLinks();
        editOtherDetails = new EditOtherDetails();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addFragment(editPersonalInfo , "Private info");
        viewPagerAdapter.addFragment(editSocialLinks , "Social Links");
        viewPagerAdapter.addFragment(editOtherDetails , "Other Details");

        viewPager.setAdapter(viewPagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment , String title){
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
