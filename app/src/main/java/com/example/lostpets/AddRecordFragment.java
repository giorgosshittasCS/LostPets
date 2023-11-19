package com.example.lostpets;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class AddRecordFragment extends Fragment {


    EditText petname;
    EditText owner;
    EditText age;
    EditText breed;
    EditText color;
    EditText dateofloss;
    EditText award;
    EditText city;
    EditText phone;
    EditText description;

    GeoPoint geoPoint;
    private String base64String;

    Button upload;

    GoogleMap map;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore db;
    private CollectionReference recordsCollection;
    private BottomNavigationView bottomNavigationView;

    private FusedLocationProviderClient fusedLocationClient;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Get the location.
                    getCurrentLocation();
                } else {
                    notPermissionGrantedLocation();
                }
            });
    private void notPermissionGrantedLocation(){
        LatLng Cyprus = new LatLng(35.05466266197189, 33.22446841746569);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Cyprus,8));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {


            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                geoPoint = new GeoPoint(latLng.latitude,latLng.longitude);
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude+" KG "+latLng.longitude);
                map.clear();
//                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,6));
                map.addMarker(markerOptions);


                Circle circle = map.addCircle(new CircleOptions()
                        .center(new LatLng(latLng.latitude, latLng.longitude))
                        .radius(1000)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(48, 0, 0, 255))); // Set blue color with 20% transparency
            }
        });

    }

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
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myplace,10));
                                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(@NonNull LatLng latLng) {
                                        geoPoint = new GeoPoint(latLng.latitude,latLng.longitude);
                                        MarkerOptions markerOptions=new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title(latLng.latitude+" KG "+latLng.longitude);

                                        map.clear();
                                        map.addMarker(new MarkerOptions()
                                                        .position(myplace)
                                                        .title("My Location")
                                                        .icon(resizedIcon))
                                                .setTag("Home Marker");
                                        map.addMarker(markerOptions);

                                        Circle circle = map.addCircle(new CircleOptions()
                                                .center(new LatLng(latLng.latitude, latLng.longitude))
                                                .radius(1000)
                                                .strokeColor(Color.RED)
                                                .fillColor(Color.argb(48, 0, 0, 255))); // Set blue color with 20% transparency
                                    }
                                });

                            }
                        }
                    });
        }
    }
    public AddRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recordsCollection = db.collection("LostRecords");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_record, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        petname = view.findViewById(R.id.petNameText);
        owner = view.findViewById(R.id.ownerText);
        age = view.findViewById(R.id.ageText);
        breed = view.findViewById(R.id.breedText);
        color = view.findViewById(R.id.colorText);
        dateofloss = view.findViewById(R.id.dateText);
        award = view.findViewById(R.id.awardText);
        city = view.findViewById(R.id.cityText);
        description = view.findViewById(R.id.descriptionText);
        phone = view.findViewById(R.id.phoneText);
        upload = view.findViewById(R.id.upload_button);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(
                   owner.getText().toString().equals("") ||
                   age.getText().toString().equals("") ||
                   award.getText().toString().equals("") ||
                   breed.getText().toString().equals("") ||
                   color.getText().toString().equals("") ||
                   dateofloss.getText().toString().equals("") ||
                   description.getText().toString().equals("") ||
                   petname.getText().toString().equals("") ||
                   geoPoint == null ||
                   city.getText().toString().equals("") ||
                   phone.getText().toString().equals("") ||
                   base64String ==null || base64String.equals("")
                ){
                    Toast toast = Toast.makeText(getContext().getApplicationContext(), "Info missing", Toast.LENGTH_SHORT);
                    toast.show();
                }else{

                    LostRecord lostRecord = new LostRecord(
                            owner.getText().toString(),
                            age.getText().toString(),
                            award.getText().toString(),
                            breed.getText().toString(),
                            color.getText().toString(),
                            dateofloss.getText().toString(),
                            description.getText().toString(),
                            petname.getText().toString(),
                            geoPoint,
                            city.getText().toString(),
                            phone.getText().toString(),
                            base64String
                    );

                    recordsCollection.add(lostRecord)
                            .addOnSuccessListener(documentReference -> {
                                // Show a success message
                                Toast.makeText(requireContext(), "Info Saved Successfully", Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(AddRecordFragment.this)
                                        .navigate(R.id.action_This_to_Home);
                            });
                }

                sendFCMNotification();

            }
        });

        //the map:
        SupportMapFragment supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                map = googleMap;
                map.getUiSettings().setZoomControlsEnabled(true);
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
                    NavHostFragment.findNavController(AddRecordFragment.this).navigate(R.id.action_This_to_Add);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_home) {
                    NavHostFragment.findNavController(AddRecordFragment.this).navigate(R.id.action_This_to_Home);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_settings) {
                    NavHostFragment.findNavController(AddRecordFragment.this).navigate(R.id.action_This_to_Settings);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_user) {
                    NavHostFragment.findNavController(AddRecordFragment.this).navigate(R.id.action_This_to_User);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_like) {
                    NavHostFragment.findNavController(AddRecordFragment.this).navigate(R.id.action_This_to_Favourites);
                    return true;
                }


                return false;
            }
        });
        FrameLayout imageFrameLayout = view.findViewById(R.id.imageFrameLayout);
        imageFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

    }
    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();
            ImageView imageFrame=getView().findViewById(R.id.imageView2);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                imageFrame.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                // Convert the image URI to a byte array
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = readBytes(inputStream);

                // Convert the byte array to a Base64 string
                base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT);

//                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
//
//                // Convert the byte array to a Bitmap
//                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//                imageFrame.setImageBitmap(decodedBitmap);
                // Now you can use base64String as needed (store it, send it to a server, etc.)
                // Note: You might want to handle permission checks and other error scenarios here
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        private void sendFCMNotification() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.drawable.dog)
                        .setContentTitle("Another dog \uD83D\uDC36 is missing \uD83D\uDE14!!")
                        .setContentText("Can you help finding it? ")
                        .setAutoCancel(true)
                        .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.dog))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}