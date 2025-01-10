package com.example.bobatap;

import static com.example.bobatap.signupFragment.EMAIL_REGEX;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends Fragment {

    private TextView loginTV;
    private Button  resetButton;
    private EditText  emailEditText ;

    private FirebaseAuth auth;

    private ProgressBar progressBar;

    public ForgotPassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();

    }

    private void init(View view){
        loginTV = view.findViewById(R.id.loginTV);
        emailEditText = view.findViewById(R.id.editTextEmail);
        resetButton = view.findViewById(R.id.buttonReset);
        progressBar = view.findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

    }

    private void clickListener(){

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivity) getActivity()).setFragment(new signinFragment());
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEditText.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX)){
                    emailEditText.setError("Input valid email");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(), "Password reset email send successfully",
                                            Toast.LENGTH_SHORT).show();
                                    emailEditText.setText("");
                                }else {
                                    String errMsg = task.getException().getMessage();
                                    Toast.makeText(getContext(), "Error: "+errMsg, Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);

                            }
                        });


            }
        });

    }

}