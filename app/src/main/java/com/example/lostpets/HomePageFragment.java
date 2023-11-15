package com.example.lostpets;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
//        db.collection("favourites")
//                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
//                                        @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//                            Log.w(TAG, "Listen error", e);
//                            return;
//                        }
//
//                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
//                            switch (change.getType()) {
//                                case ADDED:
//                                    Log.d(TAG, "New favorite:" + change.getDocument().getData());
//                                    break;
//                                case MODIFIED:
//                                    Log.d(TAG, "Modified favorite:" + change.getDocument().getData());
//                                    break;
//                                case REMOVED:
//                                    Log.d(TAG, "Removed favorite:" + change.getDocument().getData());
//                                    break;
//                            }
//
//                            String source = querySnapshot.getMetadata().isFromCache() ?
//                                    "local cache" : "server";
//                            Log.d(TAG, "Data fetched from " + source);
//                        }
//                    }
//                });



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


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

//        Menu menu = navigationView.getMenu();
//        MenuItem likeIcon = menu.findItem(R.id.nav_home);
//        likeIcon.setIcon(R.drawable.white_home_icon);
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
                convertView.setOnClickListener(v -> {
                    //Put the index of the item in the helper Class so i can use it in the MovieInfoFragment to retrieve the data.
                    NavHostFragment.findNavController(HomePageFragment.this)
                            .navigate(R.id.action_Home_to_Add);
                });
            }

            return convertView;
        }
    }
}