package com.example.dormsuser;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    CardView fees,transactions,complaint,v_comp,leave,v_leave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.fragment_home,container,false);


        fees= view.findViewById(R.id.fees);
        fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FeesActivity.class);
                startActivity(intent);
            }
        });

        complaint=view.findViewById(R.id.complaints);
        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), NewComplaintActivity.class);
                startActivity(intent);
            }
        });
        leave=view.findViewById(R.id.leave);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), NewLeaveActivity.class);
                startActivity(intent);
            }
        });

        transactions=view.findViewById(R.id.transaction);
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), TransactionActivity.class);
                startActivity(intent);
            }
        });

        v_leave=view.findViewById(R.id.v_leave);
        v_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), LeaveListActivity.class);
                startActivity(intent);
            }
        });
        v_comp=view.findViewById(R.id.v_comp);
        v_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ComplaintListActivity.class);
                startActivity(intent);
            }
        });



        return view;

    }

}
