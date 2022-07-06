package com.example.eduplanet_e_learningapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduplanet_e_learningapp.Activities.AskDoubtActivity;
import com.example.eduplanet_e_learningapp.Adapters.DoubtsAdapter;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.FragmentDoubtsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoubtsFragment extends Fragment {
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ArrayList<Doubt> doubts;
    DoubtsAdapter adapter;
    String[] doubtOptions;
    ArrayAdapter arrayAdapter;

    int option = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        doubts = new ArrayList<>();
        adapter = new DoubtsAdapter(doubts, getContext());
    }

    FragmentDoubtsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDoubtsBinding.inflate(inflater, container, false);

        fetchDoubts();

        binding.searchView.clearFocus();

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.doubtsRv.setLayoutManager(manager);
        binding.doubtsRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.askDoubtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AskDoubtActivity.class));
            }
        });

        binding.doubtsOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        option = 0;
                        break;

                    case 1:
                        option = 1;
                        break;

                    case 2:
                        option = 2;
                        break;

                    case 3:
                        option = 3;
                        break;

                    default:
                        option = 0;
                        break;
                }
                fetchDoubts();

            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchView.setVisibility(View.VISIBLE);
                binding.searchView.requestFocus();
                binding.doubtsOption.setVisibility(View.GONE);
                binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        binding.searchView.setVisibility(View.GONE);
                        binding.searchView.clearFocus();
                        binding.searchView.setQueryHint("");
                        binding.doubtsOption.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filterList(newText);
                        return true;
                    }
                });
            }
        });

        return binding.getRoot();
    }

    private void filterList(String text) {
        List<Doubt> filteredList = new ArrayList<>();
        for (Doubt item: doubts){
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            } else if (item.getDescription().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()){
            binding.doubtsRv.setVisibility(View.GONE);
            binding.nothingTv.setVisibility(View.VISIBLE);
        } else {
            binding.nothingTv.setVisibility(View.GONE);
            binding.doubtsRv.setVisibility(View.VISIBLE);
            adapter.setFilteredList(filteredList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        doubtOptions = getResources().getStringArray(R.array.doubtsOptions);
        arrayAdapter = new ArrayAdapter(getContext(), R.layout.doubt_dropdown_itemview, doubtOptions);
        binding.doubtsOption.setAdapter(arrayAdapter);
    }

    private void fetchDoubts() {
        switch (option) {
            case 0:
                firestore.collection("Doubt")
                        .orderBy("upvotes", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                if (!value.isEmpty()) {
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
                break;

            case 1:
                firestore.collection("Doubt")
                        .orderBy("doubtedAt", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                if (!value.isEmpty()) {
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
                break;

            case 2:
                firestore.collection("Doubt")
                        .orderBy("answers", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                if (!value.isEmpty()) {
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
                break;

            case 3:
                String userId = auth.getCurrentUser().getUid();
                firestore.collection("Doubt")
                        .whereEqualTo("doubtedBy",userId)
//                        .orderBy("doubtedAt", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                if (!value.isEmpty()) {
                                    binding.doubtsRv.setVisibility(View.VISIBLE);
                                    binding.nothingTv.setVisibility(View.GONE);
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
                                } else {
                                    binding.doubtsRv.setVisibility(View.GONE);
                                    binding.nothingTv.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                break;

            default:
                firestore.collection("Doubt")
                        .orderBy("upvotes", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                if (!value.isEmpty()) {
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
                break;
        }

    }
}