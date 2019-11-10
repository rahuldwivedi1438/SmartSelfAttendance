package com.lpu.capstone.smartselfattendance.taskfragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lpu.capstone.smartselfattendance.R;
import com.lpu.capstone.smartselfattendance.adapters.OptionAdapter;

import java.util.ArrayList;
import java.util.List;


public class StudentFragment extends Fragment {


    public StudentFragment() {
    }

    private List<String> optionList = createList();
    private RecyclerView recyclerView;
    private OptionAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OptionAdapter(getContext(),optionList);
        recyclerView.setAdapter(adapter);

        return  view;
    }

    private List<String> createList() {
        List<String> tempList = new ArrayList<>();
        tempList.add("Add New Student");
        tempList.add("Modify Student Data");
        tempList.add("View Student's Attendance");
        tempList.add("View Student List");
        tempList.add("Delete Student's Account");
        return  tempList;
    }
}
