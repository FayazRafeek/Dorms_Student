package com.example.dormsuser;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dormsuser.Model.Complaint;
import com.example.dormsuser.databinding.ActivityComplaintDetailBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComplaintDetailActivity extends AppCompatActivity {


    ActivityComplaintDetailBinding binding;
    Complaint complaint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityComplaintDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        complaint = AppSingleton.getINSTANCE().getSelectedComplaint();
        setCompDetail();
        refetchComplaint();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    void setCompDetail(){

        Date date = new Date();
        date.setTime(Long.parseLong(complaint.getTimestamp()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String time = dateFormat.format(date);

        binding.timeTxt.setText(time);

        binding.compTxt.setText(complaint.getComplaint());

        binding.statusTxt.setText(complaint.getStatus());


        if(complaint.getReply() != null && complaint.getReply().length() > 0){
            binding.responseItem.setVisibility(View.VISIBLE);
            binding.replyTxt.setText(complaint.getReply());
        } else {
            binding.responseItem.setVisibility(View.GONE);
        }
    }


    void refetchComplaint(){

        FirebaseFirestore.getInstance()
                .collection("Complaints")
                .document(complaint.getComplaintId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Toast.makeText(ComplaintDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Complaint doc = value.toObject(Complaint.class);
                        if(doc != null)
                        complaint = doc;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setCompDetail();
                            }
                        },2000);


                    }
                });
    }

}
