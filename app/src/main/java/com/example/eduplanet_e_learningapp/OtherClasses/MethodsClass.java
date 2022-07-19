package com.example.eduplanet_e_learningapp.OtherClasses;

public class MethodsClass {

    public static String shortenNumber(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp),"kMBTPE".charAt(exp-1));
    }

}
