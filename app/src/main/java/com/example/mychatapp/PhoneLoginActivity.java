package com.example.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private Button SendVerificationCodeButton,VerifyButton;
    private EditText InputPhoneNumber,InputVerificationCodeButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String  mVerificationId;
    private PhoneAuthProvider.ForceResendingToken  mResendToken;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth=FirebaseAuth.getInstance();


        SendVerificationCodeButton=(Button)findViewById(R.id.send_ver_code_button);
        VerifyButton=(Button)findViewById(R.id.verify_button);
        InputPhoneNumber=(EditText)findViewById(R.id.phone_number_input);
        InputVerificationCodeButton=(EditText) findViewById(R.id.verification_input);

        loadingBar=new ProgressDialog(this);

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String phoneNumber=InputPhoneNumber.getText().toString();
                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Phone Number is Required...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("please wait , while we are authenticate your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
                }
            }
        });
        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                String verificationCode=InputVerificationCodeButton.getText().toString();
                if(TextUtils.isEmpty(verificationCode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter verification code first...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setTitle(" Verification Code");
                    loadingBar.setMessage("please wait , while we are verifiying verification  Code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, please enter correct phone number with country code", Toast.LENGTH_SHORT).show();
                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);
                VerifyButton.setVisibility(View.INVISIBLE);
                InputVerificationCodeButton.setVisibility(View.INVISIBLE);
            }
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                loadingBar.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "Code sent successfully to your mobile number", Toast.LENGTH_SHORT).show();


                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                VerifyButton.setVisibility(View.VISIBLE);
                InputVerificationCodeButton.setVisibility(View.VISIBLE);
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "congratulation you loged in successfully...", Toast.LENGTH_SHORT).show();
                             SendUserToMainActivity();
                        } else {
                               String message=task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                        }
                    }

    });

}

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }



}