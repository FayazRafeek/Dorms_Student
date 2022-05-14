package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityLoginDormBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginDormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityLoginDormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });


    }

    void startLogin(){

        String email = binding.uName.getText().toString();
        String password = binding.passwd.getText().toString();

        if(email == null | email.isEmpty()){
            binding.uName.setError("Email required");
            return;
        }
        if(password == null | password.isEmpty()){
            binding.passwd.setError("Password must be greater than 5 characters");
            return;
        }


        binding.progress.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance()
                .collection("Student")
                .whereEqualTo("email",email)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        binding.progress.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            if(task.getResult().isEmpty()){
                                showToast("No User found");
                                return;
                            }

                            Student student = task.getResult().getDocuments().get(0).toObject(Student.class);
                            if(student.getPassword().equals(password)){

                                saveStudent(student);
                                showToast("Login successfull");
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                            } else {
                                showToast("Incorrect password");
                            }

                        } else {
                            showToast(task.getException().getMessage());
                            return;
                        }
                    }
                });

    }


    void saveStudent(Student student){

        SharedPreferences preferences = getSharedPreferences("DORM_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(student);

        editor.putString("STUDENT_DATA",json);
        editor.putString("STUDENT_ID",student.getStudId());
        editor.putBoolean("IS_LOGIN",true);
        editor.commit();

    }


    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}