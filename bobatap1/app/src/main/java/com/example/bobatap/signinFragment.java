package com.example.bobatap;

import static com.example.bobatap.signupFragment.EMAIL_REGEX;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class signinFragment extends Fragment {

    private EditText emails, pass;
    private TextView already, forgotpass;
    private Button signinbutton, googleSignInBtn;
    private ProgressBar progressBar;

    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth auth;


    public signinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        clickListener();

    }

    private void init(View view) {

        emails = view.findViewById(R.id.emails);
        pass = view.findViewById(R.id.pass);
        signinbutton = view.findViewById(R.id.signinbutton);
        googleSignInBtn = view.findViewById(R.id.googleSignInBtn);
        already = view.findViewById(R.id.already);
        forgotpass= view.findViewById(R.id.forgotpass);
        progressBar = view.findViewById(R.id.progressBar);


        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    private void clickListener() {

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivity) getActivity()).setFragment(new ForgotPassword());
            }
        });


        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emails.getText().toString();
                String password = pass.getText().toString();

                if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                    emails.setError("Input valid email");
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    pass.setError("Input 6 digit valid password ");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    FirebaseUser user = auth.getCurrentUser();

                                    if (!user.isEmailVerified()) {
                                        Toast.makeText(getContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                                    }

                                    sendUserToMainActivity();

                                } else {
                                    String exception = "Error: " + task.getException().getMessage();
                                    Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });

            }
        });

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReplacerActivity) getActivity()).setFragment(new signupFragment());
            }
        });

    }

    private void sendUserToMainActivity() {

        if (getActivity() == null)
            return;

        progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        getActivity().finish();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = auth.getCurrentUser();
                            updateUi(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }

                    }
                });

    }

    private void updateUi(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        Map<String, Object> map = new HashMap<>();

        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profileImage", String.valueOf(account.getPhotoUrl()));
        map.put("uid", user.getUid());
        map.put("following", 0);
        map.put("followers", 0);
        map.put("status", " ");

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            assert getActivity() != null;
                            progressBar.setVisibility(View.GONE);
                            sendUserToMainActivity();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


}