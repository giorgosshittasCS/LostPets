package com.example.lostpets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {


    EditText editText_username;
    EditText editText_phone_number;
    EditText editText_password;

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    private FragmentRegisterBinding binding;

    private CheckBox showPasswordCheckbox;
    private EditText passwordEditText;
    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");


    }

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        editText_username = binding.editTextUsername;
        editText_phone_number = binding.editTextPhoneNumber;
        editText_password= binding.editTextPassword;

        return binding.getRoot();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    String username = editText_username.getText().toString();
                    String phone_number = editText_phone_number.getText().toString();
                    String password = editText_password.getText().toString();

                    if(username.equals("") || username==null || phone_number.equals("") || phone_number==null || password.equals("") || password==null) {
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
                                    Toast toast = Toast.makeText(getContext().getApplicationContext(), "This user is already used", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    User user = new User(username, phone_number, password);
                                    //make the api call to add the user.( calculate the max id and +1 that the new)
                                    usersCollection.add(user)
                                            .addOnSuccessListener(documentReference -> {
                                                // Show a success message
                                                Toast.makeText(requireContext(), "Info Saved Successfully", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            }
                        });


                        NavHostFragment.findNavController(RegisterFragment.this)
                                .navigate(R.id.action_RegisterFragment_to_LoginFragment);
                    }
                }
                catch(Exception e) {
                    // Show a success message
                    Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(RegisterFragment.this)
                        .navigate(R.id.action_RegisterFragment_to_LoginFragment);
            }
        });

        showPasswordCheckbox = view.findViewById(R.id.showPasswordRegisterCheckbox);
        passwordEditText = view.findViewById(R.id.editTextPassword);
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



}