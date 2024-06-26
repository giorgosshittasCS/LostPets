package com.example.lostpets;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseMessagingService firebaseMessagingService;
    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CheckBox showPasswordCheckbox;
    private EditText passwordEditText;

    private User user;

    EditText username_edittext;
    EditText password_edittext;

    private String username;
    private String password;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");


    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        username_edittext = binding.getRoot().findViewById(R.id.username_editText);
        password_edittext = binding.getRoot().findViewById(R.id.password_login_edittext);


        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_LoginFragment_to_RegisterFragment);
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = username_edittext.getText().toString();
                password = password_edittext.getText().toString();

                if(username.equals("") || password.equals("")){
                    Toast toast = Toast.makeText(getContext().getApplicationContext(), "Fill Your Credentials", Toast.LENGTH_SHORT);
                    toast.show();
                }else {

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

                                if (user.getPassword().equals( password)) {
                                    User.user = user.getUsername();
                                    HomePageFragment homepage= new HomePageFragment();
                                    NavHostFragment.findNavController(LoginFragment.this)
                                            .navigate(R.id.action_LoginFragment_to_HomePageFragment);
                                } else {
                                    Toast toast = Toast.makeText(getContext().getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            } else {
                                Toast toast = Toast.makeText(getContext().getApplicationContext(), "Wrong Credential", Toast.LENGTH_SHORT);
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
                }

            }
        });
        showPasswordCheckbox = view.findViewById(R.id.showPasswordLoginCheckbox);
        passwordEditText = view.findViewById(R.id.password_login_edittext);
        // Set a listener for checkbox clicks
        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle password visibility based on checkbox state
                togglePasswordVisibility(isChecked);
            }
        });
    }
    private void togglePasswordVisibility(boolean showPassword) {
        if (showPassword) {
            // Show the password as plain text
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            // Hide the password with asterisks
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
/*
    @Override
    public void onResume() {
        super.onResume();

        // Show or unhide the UI element in the main activity
        ((MainActivity) requireActivity()).showYourUIElement();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Hide the UI element in the main activity
        ((MainActivity) requireActivity()).hideYourUIElement();
    }*/
}