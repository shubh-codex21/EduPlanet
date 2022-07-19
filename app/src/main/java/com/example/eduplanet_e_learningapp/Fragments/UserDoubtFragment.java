package com.example.eduplanet_e_learningapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduplanet_e_learningapp.Adapters.DoubtsAdapter;
import com.example.eduplanet_e_learningapp.Adapters.UserDoubtAdapter;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.FragmentUserDoubtBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserDoubtFragment extends Fragment {
    ArrayList<Doubt> doubts;
    UserDoubtAdapter adapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        doubts = new ArrayList<>();
        adapter = new UserDoubtAdapter(doubts, getContext());
    }

    FragmentUserDoubtBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserDoubtBinding.inflate(inflater, container, false);

        fetchDoubts();

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.doubtsRv.setLayoutManager(manager);
        binding.doubtsRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    private void fetchDoubts() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("Doubt")
                .whereEqualTo("doubtedBy", userId)
//                        .orderBy("doubtedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            binding.doubtsRv.setVisibility(View.VISIBLE);
                            doubts.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Doubt doubt = snapshot.toObject(Doubt.class);
                                doubt.setDoubtId(snapshot.getId());
                                doubt.setDoubtedBy(snapshot.getString("doubtedBy"));

                                ArrayList<DoubtAttachments> Attachments = new ArrayList<>();
                                snapshot.getReference().collection("Attachments")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                                for (DocumentSnapshot snapshot1 : value.getDocuments()) {
                                                    DoubtAttachments doubtAttachments = snapshot1.toObject(DoubtAttachments.class);
                                                    Attachments.add(doubtAttachments);
                                                }
                                            }
                                        });
                                doubt.setAttachments(Attachments);
                                doubts.add(doubt);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}