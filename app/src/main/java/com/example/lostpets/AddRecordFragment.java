package com.example.lostpets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.lostpets.Classes.LostRecord;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


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
    EditText description;

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


        //en iparxi to koumpi na dimiourgithi je na to arxikopoiiso***
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LostRecord lostRecord = new LostRecord(
                        owner.getText().toString(),
                        age.getText().toString(),
                        award.getText().toString(),
                        breed.getText().toString(),
                        color.getText().toString(),
                        dateofloss.getText().toString(),
                        description.getText().toString(),
                        "0",
                        petname.getText().toString(),
                        "0",
                        null,
                        city.getText().toString(),
                        "999999"

                );
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
                String base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT);

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