package com.example.lostpets;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostpets.Classes.LostRecord;
import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentHomePageBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;


public class Display_Pet_Fragment extends Fragment {


    String id;
    LostRecord lostRecord;

    GoogleMap map;

    private FirebaseFirestore db;
    private CollectionReference lostCollection;

    TextView petname;
    TextView owner;
    TextView age;
    TextView breed;
    TextView color;
    TextView dateofloss;
    TextView award;
    TextView description;
    TextView phone;




    public Display_Pet_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            String petId = args.getString("id", ""); // Provide a default value if needed
            Log.d("PetID",petId);
            // Now you can use the petId in your fragment logic
        }
        if (getArguments() != null) {
            id = getArguments().getString("id",null);
        }

        db = FirebaseFirestore.getInstance();
        lostCollection = db.collection("LostRecords");




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display__pet_, container, false);

        petname = view.findViewById(R.id.petname_textview);
        owner = view.findViewById(R.id.owner_TextView);
        age = view.findViewById(R.id.age_TextView);
        breed = view.findViewById(R.id.breedTextView);
        color = view.findViewById(R.id.color_textview);
        dateofloss = view.findViewById(R.id.dateOfLoss_textView);
        award = view.findViewById(R.id.award_textView);
        description = view.findViewById(R.id.description_textView);
        phone = view.findViewById(R.id.phoneTextView);

        DocumentReference documentReference = lostCollection.document(id);


        // Execute the query and get the results
        Task<DocumentSnapshot> task = documentReference.get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Check if any documents were found
                if (documentSnapshot != null) {

                    // Get the data from the document snapshot
                    lostRecord = documentSnapshot.toObject(LostRecord.class);

                    petname.setText(lostRecord.getName());
                    owner.setText(lostRecord.getOwner());
                    age.setText(lostRecord.getAge());
                    breed.setText(lostRecord.getBreed());
                    color.setText(lostRecord.getColor());
                    dateofloss.setText(lostRecord.getDate());
                    award.setText(lostRecord.getAward());
                    description.setText(lostRecord.getDescription());
                    phone.setText(lostRecord.getContact());


                    //the map:
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_d);
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {

                            map = googleMap;

                            MarkerOptions markerOptions=new MarkerOptions();
                            markerOptions.position(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude())));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()),13));

                            googleMap.addMarker(markerOptions);

                            Circle circle = map.addCircle(new CircleOptions()
                                    .center(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()))
                                    .radius(1000)
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.argb(48, 0, 0, 255))); // Set blue color with 20% transparency


                        }
                    });


                } else {
                    Toast toast = Toast.makeText(getContext().getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error
            }
        });








        // Inflate the layout for this fragment
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
                    NavHostFragment.findNavController(Display_Pet_Fragment.this).navigate(R.id.action_This_to_Add);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_home) {
                    NavHostFragment.findNavController(Display_Pet_Fragment.this).navigate(R.id.action_This_to_Home);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_settings) {
                    NavHostFragment.findNavController(Display_Pet_Fragment.this).navigate(R.id.action_This_to_Settings);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_user) {
                    NavHostFragment.findNavController(Display_Pet_Fragment.this).navigate(R.id.action_This_to_User);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_like) {
                    NavHostFragment.findNavController(Display_Pet_Fragment.this).navigate(R.id.action_This_to_Favourites);
                    return true;
                }


                return false;
            }
        });

    }
}