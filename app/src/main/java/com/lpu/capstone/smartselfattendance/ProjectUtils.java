package com.lpu.capstone.smartselfattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class ProjectUtils {

    public static void setLevel(String resourceLevel, Context context){
        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("level",resourceLevel).apply();
    }
    public static String getLevel( Context context){
        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(context);
        return  sharedPreferences.getString("level","0");
    }
    public static ArrayList<String> setGenderList() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Select Gender");
        tempList.add("Male");
        tempList.add("Female");
        tempList.add("Transgender");
        return tempList;
    }
    public static ArrayList<String> setSubjectList() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Select Subject");
        tempList.add("Cloud Compluting");
        tempList.add("C Programming");
        tempList.add("C++ Programming");
        tempList.add("Core JAVA");
        tempList.add("Python");
        tempList.add("Android Basics");
        tempList.add("Android Advance");
        return tempList;
    }
    public static ArrayList<String> setSectionList() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Select Section");
        tempList.add("K1601");
        tempList.add("K1602");
        tempList.add("K1603");
        tempList.add("K1604");
        tempList.add("K1605");
        tempList.add("K1606");
        return tempList;
    }
    public static ArrayList<String> setLectureTimingList() {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.add("Select Lecture Time");
        tempList.add("7:00 AM - 8:00 AM");
        tempList.add("8:00 AM - 9:00 AM");
        tempList.add("9:00 AM - 10:00 AM");
        tempList.add("10:00 AM - 11:00 AM");
        tempList.add("11:00 AM - 12:00 PM");
        tempList.add("12:00 PM - 1:00 PM");
        tempList.add("1:00 PM - 2:00 PM");
        tempList.add("2:00 PM - 3:00 PM");
        tempList.add("3:00 PM - 4:00 PM");
        tempList.add("4:00 PM - 5:00 AM");
        tempList.add("5:00 PM - 6:00 PM");
        tempList.add("6:00 PM - 7:00 PM");
        tempList.add("7:00 PM - 8:00 PM");
        tempList.add("8:00 PM - 9:00 PM");

        return tempList;
    }
}
