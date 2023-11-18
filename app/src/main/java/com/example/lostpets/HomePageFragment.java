package com.example.lostpets;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Base64;
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
import android.widget.Toast;

import com.example.lostpets.Classes.Favorites;
import com.example.lostpets.Classes.LostRecord;
import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentHomePageBinding;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder(db.getFirestoreSettings())
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        db.collection("LostRecords")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }
//                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
//                            switch (change.getType()) {
//                                case ADDED:
//                                   // Log.d(TAG, "New favorite:" + change.getDocument().getData());
//                                    break;
//                                case MODIFIED:
//                                  //  Log.d(TAG, "Modified favorite:" + change.getDocument().getData());
//                                    break;
//                                case REMOVED:
//                                   // Log.d(TAG, "Removed favorite:" + change.getDocument().getData());
//                                    break;
//                            }
//
//                            String source = querySnapshot.getMetadata().isFromCache() ?
//                                    "local cache" : "server";
//                            Log.d(TAG, "Data fetched from " + source);
//                        }
                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        //View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        View view = binding.getRoot();

        petsListView = view.findViewById(R.id.petsView);
        displayPetsList();
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_add) {
                    NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_This_to_Add);
                    return true;
                } else if (item.getItemId() == R.id.nav_home) {
                    NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_This_to_Home);
                    return true;
                } else if (item.getItemId() == R.id.nav_settings) {
                    NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_This_to_Settings);
                    return true;
                } else if (item.getItemId() == R.id.nav_user) {
                    NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_This_to_User);
                    return true;
                } else if (item.getItemId() == R.id.nav_like) {
                    NavHostFragment.findNavController(HomePageFragment.this).navigate(R.id.action_This_to_Favourites);
                    return true;
                }


                return false;
            }
        });

    }

    //        Menu menu = navigationView.getMenu();
//        MenuItem likeIcon = menu.findItem(R.id.nav_home);
//        likeIcon.setIcon(R.drawable.white_home_icon);
    public void displayPetsList() {
        pets = new ArrayList<LostRecord>();
        db.collection("LostRecords") // Replace "movies" with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LostRecord pet = document.toObject(LostRecord.class);
                            pet.setId(document.getId());
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

        PetsAdapter(android.content.Context context, List<LostRecord> pets) {
            super(context, R.layout.list_item_pet, pets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pet, parent, false);

            }
            ImageView heartIcon = convertView.findViewById(R.id.like);

            LostRecord petItem = getItem(position);
            TextView ownerTextView = convertView.findViewById(R.id.ownerText);
            TextView nameTextView = convertView.findViewById(R.id.nameText);
            TextView cityTextView = convertView.findViewById(R.id.cityText);
            TextView contactTextView = convertView.findViewById(R.id.contactText);
            ImageView petimage=convertView.findViewById(R.id.homeroundedImageView);


            if (petItem != null) {
                ownerTextView.setText(petItem.getOwner());
                nameTextView.setText(petItem.getName());
                cityTextView.setText(petItem.getCity());
                contactTextView.setText(petItem.getContact());
                byte[] decodedBytes = Base64.decode(petItem.getPic(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                petimage.setImageBitmap(decodedBitmap);


                convertView.setOnClickListener(v -> {
                    Display_Pet_Fragment displayPetFragment = new Display_Pet_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", petItem.getId());
                    displayPetFragment.setArguments(bundle);
                    NavHostFragment.findNavController(HomePageFragment.this)
                            .navigate(R.id.action_This_to_Display, bundle);
                });

                // Create a query to filter the collection by the `title` attribute
                Query query = db.collection("favorites").whereEqualTo("userId", User.user).whereEqualTo("recordId",petItem.getId());
                // Execute the query and get the results
                Task<QuerySnapshot> task = query.get();
                task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            Drawable redHeartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon_red);
                            heartIcon.setImageDrawable(redHeartDrawable);
                        }else{
                            Drawable defaultHeartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon);
                            heartIcon.setImageDrawable(defaultHeartDrawable);
                        }
                    }
                });

                heartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Favorites favorite = new Favorites(User.user,petItem.getId());
                        Drawable redHeartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon_red);
                        heartIcon.setImageDrawable(redHeartDrawable);


                        Query query = db.collection("favorites").whereEqualTo("userId", User.user).whereEqualTo("recordId",petItem.getId());
                        Task<QuerySnapshot> task = query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.isEmpty()){
                                    db.collection("favorites").add(favorite).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(requireContext(), "Record added to favorites", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(requireContext(), "Already in to favorites", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }
                });


            }

            return convertView;
        }
    }
}