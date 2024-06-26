package com.example.lostpets;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.lostpets.databinding.FragmentFavoriteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteFragment extends Fragment {

    private FirebaseFirestore db;
    private FragmentFavoriteBinding binding;

    private ListView favoritesListView;
    private ArrayList<Favorites> favoritepets;


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
        favoritepets = new ArrayList<Favorites>();
        db.collection("favorites").whereEqualTo("userId",User.user) // Replace "movies" with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Favorites fav = document.toObject(Favorites.class);
                            fav.setId(document.getId());
                            favoritepets.add(fav);
                        }


                        //Create an adapter so i can display all the movies dynamically
                        FavoriteFragment.PetsAdapter adapter = new FavoriteFragment.PetsAdapter(requireContext(), favoritepets);

                        favoritesListView.setAdapter(adapter);
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }


    private class PetsAdapter extends ArrayAdapter<Favorites> {

        PetsAdapter(android.content.Context context, List<Favorites> favs) {
            super(context, R.layout.list_item_pet, favs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pet, parent, false);

            }
            ImageView heartIcon = convertView.findViewById(R.id.like);

            Favorites f = getItem(position);
            TextView ownerTextView = convertView.findViewById(R.id.ownerText);
            TextView nameTextView = convertView.findViewById(R.id.nameText);
            TextView cityTextView = convertView.findViewById(R.id.cityText);
            TextView contactTextView = convertView.findViewById(R.id.contactText);
            ImageView petimage = convertView.findViewById(R.id.homeroundedImageView);


            if (f != null) {

                //na pkianno ta info na gemonno ta textsviews

                Task<DocumentSnapshot> task = db.collection("LostRecords").document(f.getRecordId()).get();
                task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        LostRecord lostRecord = documentSnapshot.toObject(LostRecord.class);
                        ownerTextView.setText(lostRecord.getOwner());
                        nameTextView.setText(lostRecord.getName());
                        cityTextView.setText(lostRecord.getCity());
                        contactTextView.setText(lostRecord.getContact());
                        byte[] decodedBytes = Base64.decode(lostRecord.getPic(), Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        petimage.setImageBitmap(decodedBitmap);
                    }
                });


                convertView.setOnClickListener(v -> {
                    Display_Pet_Fragment displayPetFragment = new Display_Pet_Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", f.getRecordId());
                    displayPetFragment.setArguments(bundle);
                    NavHostFragment.findNavController(FavoriteFragment.this)
                            .navigate(R.id.action_This_to_Display, bundle);
                });

                Drawable redHeartDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.like_icon_red);
                heartIcon.setImageDrawable(redHeartDrawable);



                heartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        db.collection("favorites").document(f.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(requireContext(), "Record removed from favorites", Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(FavoriteFragment.this).navigate(R.id.action_This_to_Favourites);
                            }
                        });
                    }
                });


            }

            return convertView;
        }
    }

}



