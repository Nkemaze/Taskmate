package com.bless.task.repository;

import androidx.lifecycle.MutableLiveData;

import com.bless.task.data.CampusAlert;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void fetchCampusAlerts(MutableLiveData<List<CampusAlert>> alertsLiveData) {
        db.collection("campus_alerts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CampusAlert> alerts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CampusAlert alert = document.toObject(CampusAlert.class);
                            alert.setId(document.getId());
                            alerts.add(alert);
                        }
                        alertsLiveData.setValue(alerts);
                    } else {
                        alertsLiveData.setValue(null);
                    }
                });
    }
}
