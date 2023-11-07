package com.example.lostpets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lostpets.Classes.User;
import com.example.lostpets.databinding.FragmentRegisterBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    }

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        editText_username = binding.editTextUsername;
        editText_phone_number = binding.editTextPhoneNumber;
        editText_password= binding.editTextPassword;

        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
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

                    Log.d("TEST_USERNAME",username.length() + " " + phone_number.length() + " " + password.length());

                    if(username.equals("") || username==null || phone_number.equals("") || phone_number==null || password.equals("") || password==null)
                        throw new Exception();
                    User user = new User(username,phone_number,password);

                    //make the api call to add the user.( calculate the max id and +1 that the new)
//                    usersCollection.add(user)
//                            .addOnSuccessListener(documentReference -> {
//                                // Show a success message
//                                Toast.makeText(requireContext(), "Info Saved Successfully", Toast.LENGTH_SHORT).show();
//                            });

                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_RegisterFragment_to_LoginFragment);
                }
                catch(Exception e) {
                    // Show a success message
                    Toast.makeText(requireContext(), "There is empty TextBox", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}