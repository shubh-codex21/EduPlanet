package com.example.eduplanet_e_learningapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.FragmentQuizBinding;

public class QuizFragment extends Fragment {

    FragmentQuizBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater,container,false);



        return binding.getRoot();
    }
}