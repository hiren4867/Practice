package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signupm extends AppCompatActivity {
    public static final String TAG = "TAG";
    private EditText name,email,ph,pass;
    private Button bt;
    private TextView txt;
    private ProgressBar pr;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    private String userid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupm);
        name=(EditText)findViewById(R.id.editT);
        email=(EditText)findViewById(R.id.editT2);
        pass=(EditText)findViewById(R.id.editT3);
        ph=(EditText)findViewById(R.id.editT4);
        bt=(Button)findViewById(R.id.butto1);
        txt=(TextView)findViewById(R.id.textV2);
        pr=(ProgressBar)findViewById(R.id.progressBar);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        if(fauth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String memail=email.getText().toString().trim();
                final String mpass=pass.getText().toString().trim();
                final String mph=ph.getText().toString().trim();
                final String mname=name.getText().toString().trim();
                if(TextUtils.isEmpty(memail))
                {
                    email.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(mpass))
                {
                    pass.setError("password is required");
                    return;
                }
                if(mpass.length()<6)
                {
                    pass.setError("Password should be grater than 6");
                    return;
                }
                pr.setVisibility(View.VISIBLE);
                fauth.createUserWithEmailAndPassword(memail,mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if(task.isSuccessful()){
                            DocumentReference doc;
                            userid=fauth.getCurrentUser().getUid();
                            doc = fstore.collection("Manager").document(userid);
                            Map<String,Object> map=new HashMap<>();
                            map.put("Fname",mname);
                            map.put("phone number",mph);
                            map.put("email",memail);
                            map.put("Password",mpass);
                            doc.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"on sucess: user profile is created for "+userid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"On failure :"+e.toString());
                                }
                            });
                            sendemail();
                  /*     }
                       else
                            Toast.makeText(signupm.this,"Register  not successfully",Toast.LENGTH_LONG).show();*/
                    }
                });


            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),loginm.class));
            }
        });



    }
    private void sendemail()
    {
        FirebaseUser firebaseUser=fauth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(signupm.this,"Register successfully ,Verification email is sent",Toast.LENGTH_LONG).show();
                        fauth.signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(),loginm.class));
                    }
                    else
                    {
                        Toast.makeText(signupm.this,"Verification email has not been sent",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }
}
