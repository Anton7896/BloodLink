package com.bloodlink.ui.requests;

import android.app.Application;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;

import com.bloodlink.data.database.AppDatabase;
import com.bloodlink.data.database.entities.*;
import com.bloodlink.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestViewModel extends AndroidViewModel {

    public final MutableLiveData<String>  operationResult  = new MutableLiveData<>();
    public final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    private final MutableLiveData<FilterParams> filterParams    = new MutableLiveData<>();
    private final MutableLiveData<Integer>      selectedId      = new MutableLiveData<>();

    private final AppDatabase    db;
    private final SessionManager session;

    static class FilterParams {
        final String bloodType;
        final String city;
        FilterParams(String bt, String city) { this.bloodType = bt; this.city = city; }
    }

    public RequestViewModel(@NonNull Application app) {
        super(app);
        db      = AppDatabase.getInstance(app);
        session = new SessionManager(app);
        filterParams.setValue(new FilterParams("", ""));
    }

    public int    getCurrentUserId()    { return session.getUserId(); }
    public String getCurrentBloodType() { return session.getBloodType(); }


    public LiveData<List<BloodRequest>> getRequests() {
        return Transformations.switchMap(filterParams, p -> {
            if (!p.bloodType.isEmpty()) return db.bloodRequestDao().searchByBloodType(p.bloodType);
            if (!p.city.isEmpty())      return db.bloodRequestDao().searchByCity(p.city);
            return db.bloodRequestDao().observeOpenRequests();
        });
    }

    public void filterByBloodType(String bt) { filterParams.setValue(new FilterParams(bt, "")); }
    public void filterByCity(String city)     { filterParams.setValue(new FilterParams("", city)); }
    public void clearFilter()                 { filterParams.setValue(new FilterParams("", "")); }

    public LiveData<List<BloodRequest>> getMyRequests() {
        return db.bloodRequestDao().observeMyRequests(getCurrentUserId());
    }


    public void selectRequest(int id)  { selectedId.setValue(id); }

    public void clearOperationState() {
        operationResult.postValue(null);
        operationSuccess.postValue(false);
    }

    public LiveData<BloodRequest> getSelectedRequest() {
        return Transformations.switchMap(selectedId,
                id -> db.bloodRequestDao().observeById(id));
    }

    public LiveData<List<DonorResponse>> getResponsesForSelected() {
        return Transformations.switchMap(selectedId,
                id -> db.donorResponseDao().observeResponsesForRequest(id));
    }


    public LiveData<List<DonorResponse>> getMyResponses() {
        return db.donorResponseDao().observeMyResponses(getCurrentUserId());
    }


    public void createRequest(String bloodType, String hospital, String city,
                              String description, String urgency, int units,
                              String patientName, String contactPhone,
                              double lat, double lng) {

        if (bloodType.isEmpty() || hospital.isEmpty() || city.isEmpty()) {
            operationResult.setValue("Моля, попълнете задължителните полета (*)");
            operationSuccess.setValue(false);
            return;
        }

        AppExecutors.getInstance().diskIO(() -> {
            try {
                BloodRequest req = new BloodRequest();
                req.requesterId  = getCurrentUserId();
                req.bloodType    = bloodType;
                req.hospital     = hospital;
                req.city         = city;
                req.description  = description;
                req.urgencyLevel = urgency;
                req.unitsNeeded  = units;
                req.patientName  = patientName;
                req.contactPhone = contactPhone;
                req.lat          = lat;
                req.lng          = lng;
                db.bloodRequestDao().insert(req);


                List<User> allDonors = db.userDao()
                        .findAvailableDonorsInCity(city, getCurrentUserId());


                List<User> compatible = new ArrayList<>();
                for (User donor : allDonors) {
                    if (User.canDonateTo(donor.bloodType, bloodType)) {
                        compatible.add(donor);
                    }
                }


                if (!compatible.isEmpty()) {
                    NotificationHelper.notifyDonorNeeded(
                            getApplication(), bloodType, hospital, city);
                }

                operationResult.postValue("Заявката е публикувана! " +
                        "Намерени " + compatible.size() + " съвместими донори в " + city + ".");
                operationSuccess.postValue(true);

            } catch (Exception e) {
                operationResult.postValue("Грешка при публикуване: " + e.getMessage());
                operationSuccess.postValue(false);
            }
        });
    }


    public void respondToRequest(int requestId, String message) {
        AppExecutors.getInstance().diskIO(() -> {
            try {

                if (db.donorResponseDao().hasResponded(requestId, getCurrentUserId()) > 0) {
                    operationResult.postValue("Вече сте откликнали на тази заявка.");
                    operationSuccess.postValue(false);
                    return;
                }

                DonorResponse resp = new DonorResponse();
                resp.requestId = requestId;
                resp.donorId   = getCurrentUserId();
                resp.message   = message;
                db.donorResponseDao().insert(resp);

                NotificationHelper.notifyNewDonorResponse(
                        getApplication(), session.getUserName(), session.getBloodType());

                operationResult.postValue("Откликнахте успешно! Очаквайте потвърждение. 🩸");
                operationSuccess.postValue(true);

            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
                operationSuccess.postValue(false);
            }
        });
    }


    public void confirmDonor(int responseId) {
        AppExecutors.getInstance().diskIO(() -> {
            try {
                db.donorResponseDao().updateStatus(responseId, DonorResponse.STATUS_CONFIRMED);
                DonorResponse r   = db.donorResponseDao().getById(responseId);
                BloodRequest  req = (r != null) ? db.bloodRequestDao().getById(r.requestId) : null;
                String hospital   = (req != null) ? req.hospital : "болницата";
                NotificationHelper.notifyResponseConfirmed(getApplication(), hospital);
                operationResult.postValue("Донорът е потвърден! ✅");
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
            }
        });
    }


    public void rejectDonor(int responseId) {
        AppExecutors.getInstance().diskIO(() -> {
            try {
                db.donorResponseDao().updateStatus(responseId, DonorResponse.STATUS_REJECTED);
                operationResult.postValue("Откликът е отхвърлен.");
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
            }
        });
    }


    public void markAsDonated(int responseId, int requestId) {
        AppExecutors.getInstance().diskIO(() -> {
            try {
                DonorResponse r = db.donorResponseDao().getById(responseId);
                if (r == null) return;

                db.donorResponseDao().updateStatus(responseId, DonorResponse.STATUS_DONATED);
                db.userDao().incrementDonationCount(r.donorId);
                db.bloodRequestDao().updateStatus(requestId, BloodRequest.STATUS_FULFILLED);

                User requester = db.userDao().getById(getCurrentUserId());
                String name    = (requester != null) ? requester.getFullName() : "нуждаещия се";
                NotificationHelper.notifyDonationComplete(getApplication(), name);

                operationResult.postValue("Дарението е отбелязано. Благодарим! 💙");
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
            }
        });
    }


    public void closeRequest(int requestId) {
        AppExecutors.getInstance().diskIO(() -> {
            try {
                db.bloodRequestDao().updateStatus(requestId, BloodRequest.STATUS_CLOSED);
                operationResult.postValue("Заявката е затворена.");
                operationSuccess.postValue(true);
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
                operationSuccess.postValue(false);
            }
        });
    }


    public void submitRating(int ratedUserId, int requestId, int stars, String comment) {
        AppExecutors.getInstance().diskIO(() -> {
            try {
                if (db.ratingDao().hasRated(ratedUserId, getCurrentUserId(), requestId) > 0) {
                    operationResult.postValue("Вече сте оценили този потребител."); return;
                }
                Rating r = new Rating();
                r.ratedUserId = ratedUserId;
                r.raterUserId = getCurrentUserId();
                r.requestId   = requestId;
                r.stars       = stars;
                r.comment     = comment;
                db.ratingDao().insert(r);

                float avg   = db.ratingDao().getAverageRating(ratedUserId);
                int   count = db.ratingDao().getRatingCount(ratedUserId);
                db.userDao().updateRating(ratedUserId, avg, count);
                operationResult.postValue("Оценката е изпратена! ⭐");
            } catch (Exception e) {
                operationResult.postValue("Грешка: " + e.getMessage());
            }
        });
    }


    public void updateUserLocation(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplication(), new Locale("bg"));

            List<android.location.Address> addresses =
                    geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String city = "Неизвестно";
            if (addresses != null && !addresses.isEmpty()) {
                String c = addresses.get(0).getLocality();
                if (c != null) city = c;
            }
            final String finalCity = city;
            AppExecutors.getInstance().diskIO(() ->
                    db.userDao().updateLocation(getCurrentUserId(),
                            location.getLatitude(), location.getLongitude(), finalCity));
        } catch (Exception ignored) {}
    }
}
