package com.example.lostpets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostpets.Classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class UserProfileFragment extends Fragment {

    private String username;
    private User user;

    private EditText username_edit_text;
    private EditText phone_edit_text;
    private EditText password_edit_text;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private CheckBox showPasswordCheckbox;
    private EditText passwordEditText;
    private CollectionReference usersCollection;

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");


        username = User.user;


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
                    User user_sample = documentSnapshot.toObject(User.class);

                    username_edit_text = view.findViewById(R.id.username_editText_userprofile);
                    phone_edit_text = view.findViewById(R.id.phonenumber_editText_userprofile);
                    password_edit_text = view.findViewById(R.id.password_editText_userprofile);

                    username_edit_text.setText(user_sample.getUsername());
                    phone_edit_text.setText(user_sample.getPhone_number());
                    password_edit_text.setText(user_sample.getPassword());
                    docRef = db.collection("users").document(documentSnapshot.getId());
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
                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(R.id.action_This_to_Add);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_home) {
                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(R.id.action_This_to_Home);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_settings) {
                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(R.id.action_This_to_Settings);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_user) {
                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(R.id.action_This_to_User);
                    return true;
                }
                else if(item.getItemId()==R.id.nav_like) {
                    NavHostFragment.findNavController(UserProfileFragment.this).navigate(R.id.action_This_to_Favourites);
                    return true;
                }


                return false;
            }
        });
        Button updateButton=view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User temp_user;

                // Create a query to filter the collection by the `title` attribute
                Query query = usersCollection.whereEqualTo("username", username_edit_text.getText().toString());
                // Execute the query and get the results
                Task<QuerySnapshot> task = query.get();
                task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Check if any documents were found
                        if (!querySnapshot.isEmpty() && !querySnapshot.getDocuments().get(0).toObject(User.class).getUsername().equals(User.user)) {
                            Toast toast = Toast.makeText(getContext().getApplicationContext(), "This username is already used", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            User user = new User(username_edit_text.getText().toString(), password_edit_text.getText().toString(), password_edit_text.getText().toString());

                            Map<String,Object> pdt = new HashMap<>();
                            User.user = username_edit_text.getText().toString();
                            pdt.put("username", username_edit_text.getText().toString());
                            pdt.put("password", password_edit_text.getText().toString());
                            pdt.put("phone_number", phone_edit_text.getText().toString());

                            //make the api call to add the user.( calculate the max id and +1 that the new)
                            docRef.update(pdt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext().getApplicationContext(), "Account Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });


            }
        });

        showPasswordCheckbox = view.findViewById(R.id.showPasswordCheckbox);
        passwordEditText = view.findViewById(R.id.password_editText_userprofile);
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

}