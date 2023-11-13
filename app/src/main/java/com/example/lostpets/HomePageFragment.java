package com.example.lostpets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.lostpets.databinding.FragmentHomePageBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomePageFragment extends Fragment {

    private FragmentHomePageBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BottomNavigationView bottomNavigationView;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        binding=FragmentHomePageBinding.inflate(inflater,container,false);

        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Now, the view is fully created, and you can access the BottomNavigationView
        BottomNavigationView navigationView = view.findViewById(R.id.bottom_navigation);
        Menu menu = navigationView.getMenu();
        MenuItem likeIcon = menu.findItem(R.id.nav_home);
        likeIcon.setIcon(R.drawable.white_home_icon);
    }
}