package com.example.lostpets;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostpets.Classes.LostRecord;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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


public class Display_Pet_Fragment extends Fragment {


    String id;
    LostRecord lostRecord;

    GoogleMap map;

    private FirebaseFirestore db;
    private CollectionReference lostCollection;
    private FusedLocationProviderClient fusedLocationClient;
    TextView petname;
    TextView owner;
    TextView age;
    TextView breed;
    TextView color;
    TextView dateofloss;
    TextView award;
    TextView description;
    TextView phone;

    ImageView petimage;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Get the location.
                    getCurrentLocation();
                } else {
                    notPermissionGrantedLocation();
                }
            });
    private void getCurrentLocation(){
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_home_icon);

        int width = 50;
        int height = 50;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        BitmapDescriptor resizedIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng myplace = new LatLng(location.getLatitude(), location.getLongitude());
                                map.addMarker(new MarkerOptions()
                                                .position(myplace)
                                                .title("My Location")
                                                .icon(resizedIcon))
                                        .setTag("Home Marker");
//                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myplace,10));

                                        MarkerOptions markerOptions=new MarkerOptions();
                                        markerOptions.position(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()));
                                        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude())));
                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()),13));

                                        map.addMarker(markerOptions);

                                        Circle circle = map.addCircle(new CircleOptions()
                                                .center(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()))
                                                .radius(1000)
                                                .strokeColor(Color.RED)
                                                .fillColor(Color.argb(48, 0, 0, 255))); // Set blue color with 20% transparency


                            }
                        }
                    });
        }
    }
    private void notPermissionGrantedLocation(){
//        LatLng Cyprus = new LatLng(35.05466266197189, 33.22446841746569);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Cyprus,8));



                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()));
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude())));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()),13));

                map.addMarker(markerOptions);

                Circle circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(lostRecord.getLocation().getLatitude(),lostRecord.getLocation().getLongitude()))
                        .radius(1000)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(48, 0, 0, 255))); // Set blue color with 20% transparency



    }

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        petname = view.findViewById(R.id.petname_textview);
        owner = view.findViewById(R.id.owner_TextView);
        age = view.findViewById(R.id.age_TextView);
        breed = view.findViewById(R.id.breedTextView);
        color = view.findViewById(R.id.color_textview);
        dateofloss = view.findViewById(R.id.dateOfLoss_textView);
        award = view.findViewById(R.id.award_textView);
        description = view.findViewById(R.id.description_textView);
        phone = view.findViewById(R.id.phoneTextView);
        petimage=view.findViewById(R.id.favouriteImageView);

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

                    byte[] decodedBytes = Base64.decode(lostRecord.getPic(), Base64.DEFAULT);
                    // Convert the byte array to a Bitmap
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    petimage.setImageBitmap(decodedBitmap);


                    //the map:
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_d);
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {

                            map = googleMap;
                            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted. Request it using ActivityResultLauncher.
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            } else {
                                // Permission is granted. Get the location.

                                getCurrentLocation();
                            }



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