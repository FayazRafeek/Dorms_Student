package com.example.dormsuser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.dormsuser.Model.MenuSelection;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityDinnerBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DinnerActivity extends AppCompatActivity {

    ActivityDinnerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setUpRange();

    }

    void setUpRange(){

        Calendar calendar = Calendar.getInstance();;
        calendar.add(Calendar.DAY_OF_MONTH,7);

        binding.calendarView.setMinimumDate(Calendar.getInstance());
        binding.calendarView.setMaximumDate(calendar);

        binding.calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                if(IS_FETCHED){
                    Calendar c = eventDay.getCalendar();
                    int month = c.get(Calendar.MONTH) + 1;
                    String date = c.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + c.get(Calendar.YEAR);

                    Intent intent = new Intent(DinnerActivity.this,MenuSelectActivity.class);
                    intent.putExtra("TYPE","Dinner");
                    intent.putExtra("DATE",date);

                    if(selectedDate != null && !selectedDate.isEmpty()){
                        for (MenuSelection s : selectedDate){
                            Calendar selCal = Calendar.getInstance();
                            selCal.setTimeInMillis(s.getMenuDate());
                            int m = selCal.get(Calendar.MONTH) + 1;
                            String selDate = selCal.get(Calendar.DAY_OF_MONTH) + "/" + m + "/" + selCal.get(Calendar.YEAR);
                            if (selDate.equals(date)){
                                intent.putExtra("IS_VIEW",true);
                                AppSingleton.getINSTANCE().setSelectedMenu(s);
                                break;
                            }
                        }
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(DinnerActivity.this, "Wait a moment..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fetchEvents();
    }



    Boolean IS_FETCHED = false;
    void fetchEvents(){
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH,-2);
        Query query = FirebaseFirestore.getInstance().collection("Student").document(student.getStudId())
                .collection("Selection")
                .whereEqualTo("foodType","Dinner")
                .whereGreaterThanOrEqualTo("menuDate",startDate.getTimeInMillis())
                .limit(7);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        IS_FETCHED = true;
                        if(task.isSuccessful()){

                            Log.d("333", "onComplete: Selection " + task.getResult().size());
                            List<MenuSelection> list = new ArrayList<>();
                            if(!task.getResult().isEmpty()){
                                for (DocumentSnapshot doc : task.getResult()){
                                    list.add(doc.toObject(MenuSelection.class));
                                }
                            }

                            updateEvents(list);
                        } else Log.e("333", "onComplete: Error" + task.getException());
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEvents();
    }

    List<MenuSelection> selectedDate = new ArrayList<>();
    void updateEvents(List<MenuSelection> list){

        List<EventDay> events = new ArrayList<>();
        selectedDate = list;
        for (MenuSelection m : list){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(m.getMenuDate());
            int month = cal.get(Calendar.MONTH) + 1;
            events.add(new EventDay(cal, R.drawable.ic_done, getColor(R.color.green)));
        }
        binding.calendarView.setEvents(events);

    }

}
