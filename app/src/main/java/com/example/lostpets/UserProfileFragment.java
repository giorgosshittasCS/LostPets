package com.example.lostpets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lostpets.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


public class UserProfileFragment extends Fragment {

    private String username;
    private User user;

    private EditText username_edit_text;
    private EditText phone_edit_text;
    private EditText password_edit_text;

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");

        if (getArguments() != null) {
            username = User.user;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        // Create a query to filter the collection by the `title` attribute
        Query query = usersCollection.whereEqualTo("username", username);

        // Execute the query and get the results
        Task<QuerySnapshot> task = query.get();

        task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // Check if any documents were found
                if (!querySnapshot.isEmpty()) {
                    // Get the first document snapshot from the query results
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                    // Get the data from the document snapshot
                    user = documentSnapshot.toObject(User.class);

                    username_edit_text = view.findViewById(R.id.username_editText_userprofile);
                    phone_edit_text = view.findViewById(R.id.phonenumber_editText_userprofile);
                    password_edit_text = view.findViewById(R.id.password_editText_userprofile);

                    username_edit_text.setText(user.getUsername());
                    phone_edit_text.setText(user.getPhone_number());
                    password_edit_text.setText(user.getPassword());

                } else {
                    // No documents were found
                }
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error
            }
        });




        return view;
    }

}