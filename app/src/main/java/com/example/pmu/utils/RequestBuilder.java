package com.example.pmu.utils;

import android.content.Context;
import android.net.Uri;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.example.pmu.fragments.ProfileFragment_;
import com.example.pmu.R;
import com.example.pmu.interfaces.CommentsListener;
import com.example.pmu.interfaces.ConversationsListener;
import com.example.pmu.interfaces.DestinationListener;
import com.example.pmu.interfaces.ImageUploadListener;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.interfaces.MessageListener;
import com.example.pmu.interfaces.MessageSentListener;
import com.example.pmu.interfaces.PlanInfoListener;
import com.example.pmu.interfaces.SimpleCallback;
import com.example.pmu.interfaces.UserFetchListener;
import com.example.pmu.interfaces.UserJoinStatusListener;
import com.example.pmu.interfaces.PlanSearchListener;
import com.example.pmu.interfaces.PlanListener;
import com.example.pmu.interfaces.UserListener;
import com.example.pmu.models.ChatPreview;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.ConversationModel;
import com.example.pmu.models.Destination;
import com.example.pmu.models.Message;
import com.example.pmu.models.Photo;
import com.example.pmu.models.Plan;
import com.example.pmu.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.firebase.firestore.FieldValue;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RequestBuilder {

    private static final String TAG = "AuthHelper";
    private static final String FIREBASE_API_KEY = "AIzaSyD4nTPYExPe_CkkHGh-XfoSWYDbLcFT7HM";
    private static final String FIREBASE_PROJECT_ID  = "gotogether-95141";
    static String ip = "172.20.10.2";
    static RequestQueue requestQueue;

    public RequestBuilder(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface UserIdCallback {
        void onSuccess(String userId);
        void onFailure(Exception e);
    }
    
    public static void register(Context context, User user, String password, LoginAndRegisterListener listener)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

        JSONObject authBody = new JSONObject();
        try {
            authBody.put("email", user.getEmail());
            authBody.put("password", password);
            authBody.put("returnSecureToken", true);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to build auth JSON", e);
            return;
        }
        final byte[] authRequestBody = authBody.toString().getBytes(StandardCharsets.UTF_8);

        StringRequest signUpRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            String localId = resp.getString("localId");
                            Log.d(TAG, "Auth success, uid=" + localId);

                            user.setUserId(localId);

                            FirebaseAuth.getInstance().signInWithEmailAndPassword(user.getEmail(), password)
                                    .addOnSuccessListener(authResult -> {
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("userId",           user.getUserId());
                            userMap.put("email",            user.getEmail());
                            userMap.put("firstName",        user.getFirstName());
                            userMap.put("country",          user.getCountry());
                            userMap.put("bio",              user.getBio());
                            userMap.put("profileImageUrl",  user.getProfileImageUrl());
                            userMap.put("createdAt", FieldValue.serverTimestamp());

                            db.collection("users")
                                    .document(localId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User document created for " + localId);
                                        listener.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to create user doc", e);
                                        listener.onFailure("Failed to save user in database.");
                                    });

                        });
                        }
                        catch (JSONException e) {
                            Log.e(TAG, "Failed to parse Auth response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errMsg = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errMsg = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            try {
                                JSONObject errJson = new JSONObject(errMsg);
                                JSONArray errors = errJson.getJSONObject("error").getJSONArray("errors");
                                String reason = errors.getJSONObject(0).getString("message");

                                if ("EMAIL_EXISTS".equals(reason)) {
                                    listener.onErrorResponse(context.getString(R.string.error_name));
                                    return;
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to parse error JSON", e);
                            }
                        }
                        Log.e(TAG, "Auth failed: " + errMsg);
                        listener.onFailure("Authentication failed.");
                    }

                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() {
                return authRequestBody;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        requestQueue.add(signUpRequest);
    }

    public static void login(String email, String password, LoginAndRegisterListener listener) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        listener.onFailure("Login failed: user is null.");
                        return;
                    }

                    String userId = firebaseUser.getUid();

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (!documentSnapshot.exists()) {
                                    listener.onFailure("User document not found.");
                                    return;
                                }

                                User.getInstance().setUserId(userId);
                                User.getInstance().setFirstName(documentSnapshot.getString("firstName"));
                                User.getInstance().setEmail(documentSnapshot.getString("email"));
                                User.getInstance().setProfileImageUrl(documentSnapshot.getString("profileImageUrl"));
                                User.getInstance().setCountry(documentSnapshot.getString("country"));
                                User.getInstance().setBio(documentSnapshot.getString("bio"));
                                User.getInstance().setCreatedAt(documentSnapshot.getTimestamp("createdAt"));

                                listener.onSuccess();
                            })
                            .addOnFailureListener(e -> listener.onErrorResponse("Firestore error: " + e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onFailure("Authentication failed: " + e.getMessage()));
    }

    public static void createTrip(Plan plan, PlanListener listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        String tripId = db.collection("trips").document().getId();
        plan.setTripId(tripId);

        if (plan.getParticipants() == null) {
            plan.setParticipants(new ArrayList<>());
        }
        if (!plan.getParticipants().contains(plan.getUserId())) {
            plan.getParticipants().add(plan.getUserId());
        }

        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("tripId",         plan.getTripId());
        tripMap.put("userId",         plan.getUserId());
        tripMap.put("title",          plan.getTitle());
        tripMap.put("destination",    plan.getDestination());
        tripMap.put("description",    plan.getDescription());
        tripMap.put("imageUrl",       plan.getImageUrl());
        tripMap.put("date",           plan.getDate());
        tripMap.put("participants",   plan.getParticipants());
        tripMap.put("createdAt",      FieldValue.serverTimestamp());

        db.collection("trips")
                .document(plan.getTripId())
                .set(tripMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Trip document created for " + plan.getTripId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create trip doc", e);
                    listener.onFailure("Failed to save trip in database.");
                });

    }
    public static void uploadTripImage(Uri imageUri, ImageUploadListener listener) {
        if (imageUri == null) {
            listener.onSuccess("");
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference();

        String filename = UUID.randomUUID().toString();
        StorageReference imgRef = rootRef.child("trip_images/" + filename);

        imgRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imgRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                listener.onSuccess(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                listener.onFailure("Failed to get image URL: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onFailure("Image upload failed: " + e.getMessage());
                });
    }
    public static void searchPlans(String destination, String arrival, String departure, PlanSearchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("trips")
                .whereEqualTo("destination", destination)
                .whereGreaterThanOrEqualTo("date", arrival)
                .whereLessThanOrEqualTo("date", departure)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Plan> result = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Plan plan = doc.toObject(Plan.class);
                        result.add(plan);
                    }
                    listener.onSuccess(result);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void getPlansForSuggestedDestination(String keyword, PlanSearchListener listener) {
        FirebaseFirestore.getInstance()
                .collection("trips")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Plan> matched = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Plan plan = doc.toObject(Plan.class);
                        if (plan != null &&
                                plan.getDestination() != null &&
                                plan.getDestination().toLowerCase().contains(keyword.toLowerCase())) {
                            matched.add(plan);
                        }
                    }

                    listener.onSuccess(matched);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void getPlanById(String tripId, PlanInfoListener listener) {
        FirebaseFirestore.getInstance()
                .collection("trips")
                .document(tripId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Plan plan = documentSnapshot.toObject(Plan.class);
                        listener.onSuccess(plan);
                    } else {
                        listener.onFailure("Plan not found.");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }
    public static void getUserById(String userId, UserFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void checkUserParticipation(String userId, String tripId, UserJoinStatusListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(tripId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> participants = (List<String>) documentSnapshot.get("participants");
                        listener.onResult(participants != null && participants.contains(userId));
                    } else {
                        listener.onFailure("Trip not found");
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void joinPlan(String userId, String tripId, PlanListener listener) {
        FirebaseFirestore.getInstance()
                .collection("trips")
                .document(tripId)
                .update("participants", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void leavePlan(String userId, String tripId, PlanListener listener) {
        FirebaseFirestore.getInstance()
                .collection("trips")
                .document(tripId)
                .update("participants", FieldValue.arrayRemove(userId))
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void deleteTrip(String tripId, PlanListener listener) {
        FirebaseFirestore.getInstance()
                .collection("trips")
                .document(tripId)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void getUpcomingTrips(String userId, PlanSearchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Plan> trips = new ArrayList<>();

        db.collection("trips")
                .whereArrayContains("participants", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Plan plan = doc.toObject(Plan.class);
                        trips.add(plan);
                    }
                    listener.onSuccess(trips);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading upcoming trips", e);
                    listener.onFailure("Unable to load upcoming trips.");
                });
    }

    public static void sendMessage(String receiverId, String text, Timestamp timestamp, MessageSentListener listener) {
        String senderId = User.getInstance().getUserId();
        String chatId = getChatId(senderId, receiverId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Message message = new Message(senderId, receiverId, text, timestamp);
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(doc -> {

                    ChatPreview preview = new ChatPreview(
                            chatId,
                            text,
                            senderId,
                            receiverId,
                            timestamp
                    );

                    db.collection("chatPreview")
                            .document(chatId)
                            .set(preview)
                            .addOnSuccessListener(unused -> listener.onSuccess())
                            .addOnFailureListener(e -> listener.onFailure("Preview update failed: " + e.getMessage()));

                })
                .addOnFailureListener(e -> listener.onFailure("Message send failed: " + e.getMessage()));
    }

    public static ListenerRegistration listenForMessages(String senderId, String receiverId, MessageListener listener) {
        String chatId = getChatId(senderId, receiverId);

        return FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        listener.onFailure("Failed to load chat");
                        return;
                    }

                    List<Message> messageList = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Message message = doc.toObject(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }

                    listener.onSuccess(messageList);
                });
    }

    public static String getChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    public static void getAllMessages(
            String myUserId,
            ConversationsListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query msgs = db.collectionGroup("messages");

        Task<QuerySnapshot> sentTask = msgs
                .whereEqualTo("senderId", myUserId)
                .orderBy("timestamp")
                .get();

        Task<QuerySnapshot> recvTask = msgs
                .whereEqualTo("receiverId", myUserId)
                .orderBy("timestamp")
                .get();

        Tasks.whenAllSuccess(sentTask, recvTask)
                .addOnSuccessListener(results -> {
                    List<Message> allMsgs = new ArrayList<>();
                    for (Object r : results) {
                        QuerySnapshot snap = (QuerySnapshot) r;
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            Message m = doc.toObject(Message.class);
                            if (m != null) allMsgs.add(m);
                        }
                    }

                    Map<String, Message> lastMsgPerPartner = new HashMap<>();
                    for (Message m : allMsgs) {
                        String partnerId = m.getSenderId().equals(myUserId)
                                ? m.getReceiverId()
                                : m.getSenderId();

                        Message prev = lastMsgPerPartner.get(partnerId);
                        if (prev == null || m.getTimestamp().toDate().after(prev.getTimestamp().toDate())) {
                            lastMsgPerPartner.put(partnerId, m);
                        }
                    }

                    if (lastMsgPerPartner.isEmpty()) {
                        listener.onSuccess(Collections.emptyList());
                        return;
                    }

                    List<ConversationModel> convos = new ArrayList<>();
                    final int total = lastMsgPerPartner.size();
                    final int[] done = {0};

                    for (Map.Entry<String, Message> entry : lastMsgPerPartner.entrySet()) {
                        String partnerId = entry.getKey();
                        Message lastMsg = entry.getValue();

                        getUserById(partnerId, new UserFetchListener() {
                            @Override
                            public void onSuccess(User user) {
                                convos.add(new ConversationModel(user, lastMsg));

                                if (++done[0] == total) {
                                    Collections.sort(convos, (a, b) ->
                                            b.getLastMessage().getTimestamp().toDate()
                                                    .compareTo(a.getLastMessage().getTimestamp().toDate()));
                                    listener.onSuccess(convos);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                listener.onFailure(error);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }


    public static void getUsersByIds(List<String> userIds, UserListener listener) {
        if (userIds == null || userIds.isEmpty()) {
            listener.onSuccess(Collections.emptyList());
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot ds : snapshot.getDocuments()) {
                        users.add(ds.toObject(User.class));
                    }
                    listener.onSuccess(users);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public static void uploadProfileImage(String userId, Uri imageUri, Context context, ImageUploadListener listener) {
        if (imageUri == null) {
            listener.onFailure("No image selected");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("profile_images/" + userId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(userId)
                                .update("profileImageUrl", imageUrl)
                                .addOnSuccessListener(unused -> listener.onSuccess(imageUrl))
                                .addOnFailureListener(e -> listener.onFailure("Failed to update Firestore"));
                    });
                })
                .addOnFailureListener(e -> listener.onFailure("Failed to upload image: " + e.getMessage()));
    }

    public static void updateUserBio(String userId, String newBio, OnCompleteListener<Void> listener) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("bio", newBio)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("BIO_UPDATE", "Update successful");
                    } else {
                        Log.e("BIO_UPDATE", "Update failed", task.getException());
                    }
                    listener.onComplete(task);
                });
    }

    public static void loadUserPhotos(String userId, OnSuccessListener<List<Photo>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("photos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Photo> photos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Photo photo = doc.toObject(Photo.class);
                        if (photo != null) {
                            photos.add(photo);
                        }
                    }
                    onSuccess.onSuccess(photos);
                })
                .addOnFailureListener(onFailure);
    }

    public static void uploadGalleryPhoto(String userId, Uri imageUri, Context context, ImageUploadListener listener) {
        if (imageUri == null) {
            listener.onFailure("No image selected");
            return;
        }

        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("user_photos/" + userId + "/" + fileName);

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            Map<String, Object> photoData = new HashMap<>();
                            photoData.put("imageUrl", imageUrl);
                            photoData.put("timestamp", FieldValue.serverTimestamp());

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .collection("photos")
                                    .add(photoData)
                                    .addOnSuccessListener(doc -> listener.onSuccess(imageUrl))
                                    .addOnFailureListener(e -> listener.onFailure("Failed to save photo to Firestore"));
                        }))
                .addOnFailureListener(e -> listener.onFailure("Failed to upload to Storage: " + e.getMessage()));
    }

    public static void getCommentsForTrip(String tripId, EventListener<QuerySnapshot> listener) {
        FirebaseFirestore.getInstance().collection("comments")
                .whereEqualTo("tripId", tripId)
                .orderBy("createdAt")
                .addSnapshotListener(listener);
    }

    public static void postComment(CommentModel comment, OnSuccessListener<DocumentReference> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore.getInstance().collection("comments")
                .add(comment)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    public static void uploadCommentImage(Uri imageUri, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
        String imageName = "comments/" + UUID.randomUUID() + ".jpg";

        FirebaseStorage.getInstance().getReference(imageName)
                .putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(onSuccess))
                .addOnFailureListener(onFailure);
    }

    public static void loadCommentsForPhoto(
            String photoUrl,
            CommentsListener listener) {

        FirebaseFirestore.getInstance()
                .collection("comments")
                .whereEqualTo("imageUrl", photoUrl)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    ArrayList<CommentModel> comments = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CommentModel c = doc.toObject(CommentModel.class);
                        if (c != null) {
                            c.setId(doc.getId());
                            comments.add(c);
                        }
                    }
                    listener.onSuccess(comments);
                })
                .addOnFailureListener(e ->
                        listener.onFailure(e.getMessage())
                );
    }

    public static void addComment(
            String photoUrl,
            String text,
            SimpleCallback callback) {

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        Map<String,Object> data = new HashMap<>();
        data.put("userId",   uid);
        data.put("imageUrl", photoUrl);
        data.put("text",     text);
        data.put("createdAt", FieldValue.serverTimestamp());

        FirebaseFirestore.getInstance()
                .collection("comments")
                .add(data)
                .addOnSuccessListener(ref -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("ADD_COMMENT", "Failed to add comment", e);
                });
    }

    public static void deletePhoto(String photoUrl, String userId, SimpleCallback callback) {

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("photos")
                .whereEqualTo("imageUrl", photoUrl)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        doc.getReference().delete();
                    }

                    FirebaseStorage.getInstance()
                            .getReferenceFromUrl(photoUrl)
                            .delete()
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> Log.e("DELETE_PHOTO", "Storage delete failed: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("DELETE_PHOTO", "Firestore query failed: " + e.getMessage()));
    }

    public static void deleteComment(String commentId, SimpleCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("comments")
                .document(commentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DELETE_COMMENT", "Comment deleted");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DELETE_COMMENT", "Error deleting comment: " + e.getMessage());
                });
    }

    public static void getDestinations(DestinationListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String lang = Locale.getDefault().getLanguage();

        db.collection("destinations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Destination> destinationList = new ArrayList<>();
                    List<Task<Void>> translationTasks = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Destination destination = doc.toObject(Destination.class);

                        if (!lang.equals("en")) {
                            Task<Void> task = doc.getReference()
                                    .collection("translations")
                                    .document(lang)
                                    .get()
                                    .continueWith(t -> {
                                        if (t.isSuccessful() && t.getResult() != null && t.getResult().exists()) {
                                            DocumentSnapshot transDoc = t.getResult();
                                            if (transDoc.contains("name")) {
                                                destination.setName(transDoc.getString("name"));
                                            }
                                            if (transDoc.contains("description")) {
                                                destination.setDescription(transDoc.getString("description"));
                                            }
                                        }
                                        return null;
                                    });

                            translationTasks.add(task);
                        }

                        destinationList.add(destination);
                    }

                    if (translationTasks.isEmpty()) {
                        listener.onSuccess(destinationList);
                    } else {
                        Tasks.whenAllComplete(translationTasks)
                                .addOnSuccessListener(tasks -> listener.onSuccess(destinationList))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

}