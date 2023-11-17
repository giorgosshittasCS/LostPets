package com.example.lostpets;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lostpets.Classes.LostRecord;
import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentFavoriteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteFragment extends Fragment {

    private FirebaseFirestore db;
    private FragmentFavoriteBinding binding;

    private ListView favoritesListView;
    private List<LostRecord> favoritepets;


    public FavoriteFragment() {
        // Required empty public constructor
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
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        //View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        View view = binding.getRoot();

        favoritesListView = view.findViewById(R.id.favoritePetsView);
        displayFavoriteList();
        return view;
    }

    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.nav_add) {
                    NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_Add);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_home) {
                    NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_Home);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_settings) {
                    NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_Settings);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_user) {
                    NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_User);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_like) {
                    NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_Favourites);
                    return true;
                }


                return false;
            }
        });

    }


    private void displayFavoriteList() {
        favoritepets = new ArrayList<>();
        System.out.println("dis favorites");

        db.collection("favorites")
                .whereEqualTo("username", User.user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalPets = task.getResult().getDocuments().size();  // Corrected line
                        System.out.println("total " + totalPets + " ");
                        int petsProcessed = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LostRecord favorite = document.toObject(LostRecord.class);
                            // Fetch additional details for each favorite
                            String doc = document.getId();
                            fetchPetDetails(doc, totalPets);
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchPetDetails(String doc, int totalPets) {
        db.collection("LostRecords")
                .document(doc)  // Use the correct method to get the document ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            LostRecord petDetails = document.toObject(LostRecord.class);
                            favoritepets.add(petDetails);

                            // Check if this is the last pet to fetch details for
                            if (favoritepets.size() == totalPets) {
                                // Create an adapter to display the list
                                FavoritesAdapter adapter = new FavoritesAdapter(requireContext(), favoritepets);
                                favoritesListView.setAdapter(adapter);
                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "Error getting document: ", task.getException());
                    }
                });
    }

    private interface OnFavoriteCheckListener {
        void onCheckComplete(boolean isFavorite);
    }
    private class FavoritesAdapter extends ArrayAdapter<LostRecord> {
        FavoritesAdapter(Context context, List<LostRecord> favorites) {
            super(context, R.layout.list_item_favorite, favorites);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_favorite, parent, false);
            }
            ImageView heartIcon = convertView.findViewById(R.id.like);

            LostRecord favoriteItem = getItem(position);
            TextView ownerTextView = convertView.findViewById(R.id.ownerText);
            TextView nameTextView = convertView.findViewById(R.id.nameText);
            TextView cityTextView = convertView.findViewById(R.id.cityText);
            TextView contactTextView = convertView.findViewById(R.id.contactText);

//            System.out.println(favoriteItem.getOwner());
//            System.out.println(favoriteItem.getName());
//            System.out.println(favoriteItem.getCity());
//            System.out.println(favoriteItem.getContact());

            if (favoriteItem != null) {
                ownerTextView.setText(favoriteItem.getOwner());
                nameTextView.setText(favoriteItem.getName());
                cityTextView.setText(favoriteItem.getCity());
                contactTextView.setText(favoriteItem.getContact());

                // Set the initial drawable based on the favorite status
                Drawable heartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon_red);
                heartIcon.setImageDrawable(heartDrawable);
                // Set a click listener for the heart icon
                heartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LostRecord clickedPet = getItem(position);
                        System.out.println("clicked"  + clickedPet.toString());

                        Log.d(TAG, "Heart icon clicked for pet ID: " + clickedPet.getId());

                        if (clickedPet != null) {
                            String petId = clickedPet.getId();
                            isFavorite(petId, User.user, new OnFavoriteCheckListener() {
                                @Override
                                public void onCheckComplete(boolean isFavorite) {
                                    if (isFavorite) {
                                        // Pet is a favorite, so remove it
                                        removeFavorite(petId, User.user);
                                        Drawable defaultHeartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon);
                                        heartIcon.setImageDrawable(defaultHeartDrawable);
                                    }
                                }
                            });
                        }
                    }
                });

                convertView.setOnClickListener(v -> {

                    Display_Pet_Fragment displayPetFragment = new Display_Pet_Fragment();
                    Bundle bundle = new Bundle();

                    bundle.putString("id", favoriteItem.getId());

                    displayPetFragment.setArguments(bundle);
                    //Put the index of the item in the helper Class so i can use it in the FavoriteFragment to retrieve the data.
                    NavHostFragment.findNavController(FavoriteFragment.this)
                            .navigate(R.id.action_This_to_Display, bundle);
                });
            }

            return convertView;
        }


        private void isFavorite(String petId, String username, OnFavoriteCheckListener listener) {
            db.collection("favorites")
                    .whereEqualTo("dogId", petId)
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                // Document exists, pet is a favorite
                                listener.onCheckComplete(true);
                            } else {
                                // Document does not exist, pet is not a favorite
                                listener.onCheckComplete(false);
                            }
                        } else {
                            // Error occurred, consider it not a favorite
                            listener.onCheckComplete(false);
                            Log.d(TAG, "Error checking favorite document: ", task.getException());
                        }
                    });
        }


        private void removeFavorite(String petId, String username) {
            Log.d(TAG, "Removing favorite: " + petId);

            db.collection("favorites")
                    .whereEqualTo("dogId", petId)
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Favorite document successfully removed!"))
                                        .addOnFailureListener(e -> Log.e(TAG, "Error removing favorite document", e));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
    }



