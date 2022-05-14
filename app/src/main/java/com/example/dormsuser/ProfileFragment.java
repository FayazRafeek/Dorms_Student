package com.example.dormsuser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.FragmentProfileBinding;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment{

    FragmentProfileBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    Student student;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String compId = String.valueOf(System.currentTimeMillis());
        String studentSt = getActivity().getSharedPreferences("DORM_USER", MODE_PRIVATE).getString("STUDENT_DATA","");
        student = new Gson().fromJson(studentSt,Student.class);

        if(student == null) {
            showToast("Failed to get student data");
            return;
        }

        updateUi();

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences("DORM_USER",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("STUDENT_DATA",null);
                editor.putBoolean("IS_LOGIN",false);
                editor.commit();

                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    void updateUi(){

        binding.studentName.setText(student.getFull_name());
        binding.email.setText(student.getEmail());
        binding.phone.setText(student.getPhone());
        binding.address.setText(student.getAddress());

        Glide.with(getContext())
                .load(student.getProfileUrl())
                .centerCrop()
                .into(binding.profileImg);

    }


    void showToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
