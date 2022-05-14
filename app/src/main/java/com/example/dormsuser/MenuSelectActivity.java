package com.example.dormsuser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dormsuser.Model.MenuSelection;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityMenuSelectBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuSelectActivity extends AppCompatActivity implements MenuAdapter.MenuAction{

    ActivityMenuSelectBinding binding;
    String type = "", date;
    Boolean IS_VIEW = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("TYPE");
        date = getIntent().getStringExtra("DATE");

        IS_VIEW = getIntent().getBooleanExtra("IS_VIEW",false);
        if(IS_VIEW){
            binding.boxHeader.setText("View Menu");
            binding.applyBtn.setVisibility(View.GONE);
            setViewUi();
        } else {
            fetchMenu();
            updateUi();
        }


        binding.applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submitData();
                } catch (ParseException e){
                    showToast("Date error");
                }

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void updateUi(){
        binding.typeHeader.setText(type);
        binding.date.setText(date);
    }

    void setViewUi(){
        MenuSelection selection = AppSingleton.getINSTANCE().getSelectedMenu();
        updateRecyler(selection.getFoodItems());
    }

    void submitData() throws ParseException{

        List<FoodItem> selected = menuAdapter.getSelected();
        if(selected == null || selected.isEmpty()){
            Toast.makeText(this, "Select atleast one item", Toast.LENGTH_SHORT).show();
            return;
        }

        String compId = String.valueOf(System.currentTimeMillis());
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        if(student == null) {
            showToast("Failed to get student data");
            return;
        }

        String selcId = System.currentTimeMillis() + student.getStudId().substring(0,3) + student.getAdminId().substring(0,3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date menuDate =  simpleDateFormat.parse(date);

        MenuSelection menuSelection = new MenuSelection(selcId,student.getStudId()
        ,student.getFull_name(),student.getProfileUrl(),student.getAdminId(),type,menuDate.getTime(),System.currentTimeMillis()
        ,new ArrayList<>(selected));

        binding.progress.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Admin")
                .document(student.getAdminId())
                .collection("Selection")
                .document(menuSelection.getSelectionId())
                .set(menuSelection)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.progress.setVisibility(View.VISIBLE);
                        if(task.isSuccessful()){

                            FirebaseFirestore.getInstance().collection("Student")
                                    .document(student.getStudId())
                                    .collection("Selection")
                                    .document(menuSelection.getSelectionId())
                                    .set(menuSelection)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showToast("Menu submitted successfully");
                                            finish();
                                        }
                                    });

                        } else {
                            showToast(task.getException().getMessage());
                        }
                    }
                });
    }

    void fetchMenu(){

        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        if(student == null) {
            showToast("Failed to get student data");
            return;
        }

        binding.progress.setVisibility(View.VISIBLE);
        binding.applyBtn.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("Admin")
                .document(student.getAdminId())
                .collection(type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        binding.progress.setVisibility(View.GONE);
                        binding.applyBtn.setVisibility(View.VISIBLE);
                        if(task.isSuccessful()){

                            List<FoodItem> foodItems = new ArrayList<>();
                            for(DocumentSnapshot doc : task.getResult()){
                                FoodItem foodItem = doc.toObject(FoodItem.class);
                                foodItems.add(foodItem);
                            }

                            updateRecyler(foodItems);

                        }
                    }
                });
    }

    MenuAdapter menuAdapter;
    void updateRecyler(List<FoodItem> list){
        if(menuAdapter == null){
            menuAdapter = new MenuAdapter(this,this,!IS_VIEW);
            binding.menuRecycler.setAdapter(menuAdapter);
            binding.menuRecycler.setLayoutManager(new LinearLayoutManager(this));

        }
        menuAdapter.updateList(list);
    }

    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(FoodItem item) {
        menuAdapter.updateSelection(item);
    }
}
