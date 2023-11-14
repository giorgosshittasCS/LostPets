package com.example.lostpets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lostpets.Classes.LostRecord;
import com.example.lostpets.databinding.FragmentHomePageBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomePageFragment extends Fragment {
    private FirebaseFirestore db;
    private FragmentHomePageBinding binding;

    private List<LostRecord> pets;

    private ListView petsListView;
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
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomePageBinding.inflate(inflater,container,false);
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        petsListView = view.findViewById(R.id.petsView);
        displayPetsList();
        return view;
    }
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Now, the view is fully created, and you can access the BottomNavigationView
        BottomNavigationView navigationView = view.findViewById(R.id.bottom_navigation);
        Menu menu = navigationView.getMenu();
        MenuItem likeIcon = menu.findItem(R.id.nav_home);
        likeIcon.setIcon(R.drawable.white_home_icon);
    }
    public void displayPetsList(){
        pets=new ArrayList<LostRecord>();
        db.collection("LostRecords") // Replace "movies" with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LostRecord pet = document.toObject(LostRecord.class);
                            pets.add(pet);
                        }


                        //Create an adapter so i can display all the movies dynamically
                       PetsAdapter adapter = new PetsAdapter(requireContext(), pets);

                        petsListView.setAdapter(adapter);
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private class PetsAdapter extends ArrayAdapter<LostRecord> {

        PetsAdapter(android.content.Context context, List<LostRecord> movies) {
            super(context, R.layout.list_item_pet, movies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pet, parent, false);

            }

            LostRecord petItem = getItem(position);
            TextView ownerTextView=convertView.findViewById(R.id.ownerText);
            TextView nameTextView=convertView.findViewById(R.id.nameText);
            TextView cityTextView=convertView.findViewById(R.id.cityText);
            TextView contactTextView=convertView.findViewById(R.id.contactText);
            if (petItem!= null) {
                ownerTextView.setText(petItem.getOwner());
                nameTextView.setText(petItem.getName());
                cityTextView.setText(petItem.getCity());
                contactTextView.setText(petItem.getContact());
            }

            return convertView;
        }
    }
}