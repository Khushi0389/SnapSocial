package my.example.bobatap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    private TextView loginTextView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonReset);
        loginTextView = findViewById(R.id.loginTV);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                } else {
                    resetPassword(email);
                }
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetPassword(String email) {
        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setEnabled(false);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        resetPasswordButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            // Display a success message
                            Toast.makeText(ForgotPasswordActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();

                            // Optionally, navigate to the login screen
                            finish();
                        } else {
                            // Failed to send reset email
                            // Display an error message
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
