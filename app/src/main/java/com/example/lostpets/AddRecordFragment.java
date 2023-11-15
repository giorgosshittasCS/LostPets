package com.example.lostpets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lostpets.Classes.LostRecord;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


//    private void sendFCMNotification() {
//        Intent intent = new Intent(getContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
//                PendingIntent.FLAG_IMMUTABLE);
//
//        String channelId = "fcm_default_channel";
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(getContext(), channelId)
//                        .setSmallIcon(R.drawable.dog)
//                        .setContentTitle("Another dog \uD83D\uDC36 is missing \uD83D\uDE14!!")
//                        .setContentText("Can you help finding it? ")
//                        .setAutoCancel(true)
//                        .setLargeIcon( BitmapFactory.decodeResource(getResources(), R.drawable.dog))
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
public class AddRecordFragment extends Fragment {


    private String username;

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
    private String base64String;

    Button upload;
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore db;
    private CollectionReference recordsCollection;

    public AddRecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        recordsCollection = db.collection("LostRecords");
        if (getArguments() != null) {
            username = getArguments().getString("0",null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_record, container, false);

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
//                   new GeoPoint(0,0),
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
                            new GeoPoint(0,0),
                            city.getText().toString(),
                            phone.getText().toString(),
                            base64String
                    );

                    recordsCollection.add(lostRecord)
                            .addOnSuccessListener(documentReference -> {
                                // Show a success message
                                Toast.makeText(requireContext(), "Info Saved Successfully", Toast.LENGTH_SHORT).show();
                                NavHostFragment.findNavController(AddRecordFragment.this)
                                        .navigate(R.id.action_add_to_home);
                            });
                }

            }
        });



        // Inflate the layout for this fragment
        return view;
    }
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            try {
                // Convert the image URI to a byte array
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = readBytes(inputStream);

                // Convert the byte array to a Base64 string
                base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

                // Convert the byte array to a Bitmap
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageFrame.setImageBitmap(decodedBitmap);
                // Now you can use base64String as needed (store it, send it to a server, etc.)
                // Note: You might want to handle permission checks and other error scenarios here
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}