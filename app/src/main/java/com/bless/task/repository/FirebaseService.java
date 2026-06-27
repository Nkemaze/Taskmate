package com.bless.task.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.bless.task.data.CampusAlert;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FirebaseService {

    /**
     * Fetches campus alerts from Firestore safely and listens for real-time updates.
     * @param context Context for checking Firebase initialization.
     * @param alertsLiveData LiveData to post the results to.
     */
    public void fetchCampusAlerts(Context context, MutableLiveData<List<CampusAlert>> alertsLiveData) {
        FirebaseFirestore db = null;
        try {
            // Using check for initialized apps as required by the project's Firebase version
            if (context != null && !FirebaseApp.getApps(context).isEmpty()) {
                db = FirebaseFirestore.getInstance();
            }
        } catch (Throwable t) {
            db = null;
        }

        if (db == null) {
            alertsLiveData.postValue(new ArrayList<>());
            return;
        }

        // Use addSnapshotListener for real-time updates so the UI stays fresh automatically.
        // Ordering by timestamp descending ensures the latest alerts appear first.
        db.collection("campus_alerts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<CampusAlert> alerts = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            try {
                                CampusAlert alert = document.toObject(CampusAlert.class);
                                if (alert != null) {
                                    alert.setId(document.getId());
                                    alerts.add(alert);
                                }
                            } catch (Throwable ignored) {}
                        }
                    }
                    alertsLiveData.postValue(alerts);
                });
    }
}
