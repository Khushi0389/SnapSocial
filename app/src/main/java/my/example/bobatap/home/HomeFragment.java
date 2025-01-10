package my.example.bobatap.home;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import my.example.bobatap.Aboutapp;
import my.example.bobatap.R;
import my.example.bobatap.Stories.StoryAdapter;
import my.example.bobatap.Utils.HomeFragmentPostViewListAdapter;
import my.example.bobatap.Utils.UniversalImageLoader;
import my.example.bobatap.models.Comments;
import my.example.bobatap.models.Photo;
import my.example.bobatap.models.Story;


public class HomeFragment extends Fragment {
    private CircleImageView profileImageView;

    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private HomeFragmentPostViewListAdapter mAdapter;
    private int mResults;
    ImageView message,icon;

    TextView bobatap, dailyTip;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private String[] tips = {
            "Take a deep breath and relax. It's okay not to be perfect.",
            "Challenge of the day: Reach out to a friend you haven't spoken to in a while.",
            "Don't forget to practice self-care. Take time for yourself today.",
            "Positive thinking leads to positive outcomes. Keep your thoughts optimistic.",
            "A journey of a thousand miles begins with a single step. Start small, but start.",
            "You are capable of achieving great things. Believe in yourself!",
            "Today's challenge: Try something new or outside your comfort zone.",
            "Motivation for today: 'The only way to do great work is to love what you do.' - Steve Jobs",
            "Your mental health matters. Don't hesitate to seek support when needed.",
            "Remember, setbacks are a part of the journey. Keep moving forward.",
            "Challenge of the day: Express gratitude for three things in your life.",
            "Motivation for today: 'Success is not in what you have, but who you are.'",
            "Be Kinder To Yourself.",
            "What we achieve inwardly will change outer reality.",
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,null);
        mListView = v.findViewById(R.id.FragmentHome_postListView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
        icon = v.findViewById(R.id.bobaicon);
        dailyTip=v.findViewById(R.id.dailyTip);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Aboutapp.class);
                startActivity(intent);

            }
        });

        bobatap = v.findViewById(R.id.bobas);
        bobatap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Aboutapp.class);
                startActivity(intent);

            }
        });


        message = v.findViewById(R.id.FragmentHome_msg);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRandomTip();
            }
        });


        recyclerView_story = v.findViewById(R.id.FragmentHome_story_recyclerView);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearlayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);


        getFollowing();
        initImageLoader();
        displayMorePhotos();
        loadUserProfilePicture();

        profileImageView = v.findViewById(R.id.profileImg);

        return v;
    }
    // Function to display a random tip
    private void showRandomTip() {
        if (tips.length > 0) {
            int randomIndex = new Random().nextInt(tips.length);
            String randomTip = tips[randomIndex];

            // Inflate the custom tip layout
            View tipView = getLayoutInflater().inflate(R.layout.daily_tip_layout, null);

            // Find the TextView in the custom layout
            TextView dailyTipText = tipView.findViewById(R.id.dailyTipText);
            dailyTipText.setText(randomTip);

            // Create an AlertDialog to display the tip
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(tipView);

            AlertDialog dialog = builder.create();

            // Show the dialog
            dialog.show();

            // Delay the tip disappearance
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            }, 10000); // Delay for 10 seconds (10000 milliseconds)
        } else {
            dailyTip.setText("Get Some Rest !");
        }
    }
    private void loadUserProfilePicture() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && isAdded()) {
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("photos/users/" + user.getUid() + "/profilephoto"); // Adjust the path as needed

            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                String profileImageUrl = uri.toString();

                // Load the profile image into the CircularImageView using Glide
                if (getActivity() != null) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.nodp) // Placeholder image while loading
                            .error(R.drawable.day) // Error image if loading fails
                            .into(profileImageView);
                }
            }).addOnFailureListener(e -> {
                Log.e("ProfileImage", "Error getting profile image: " + e.getMessage());
            });
        }
    }


    private void getFollowing(){
        Log.d(TAG, "getFollowing: searching for following");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child("Following")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.child("user_id").getValue());

                    mFollowing.add(singleSnapshot.child("user_id").getValue().toString());
                }
                mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                //get the photos
                readStory();
                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = reference
                    .child("User_Photo")
                    .child(mFollowing.get(i))
                    .orderByChild("user_id")
                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get("caption").toString());
                        photo.setTags(objectMap.get("tags").toString());
                        photo.setPhoto_id(objectMap.get("photo_id").toString());
                        photo.setUser_id(objectMap.get("user_id").toString());
                        photo.setDate_Created(objectMap.get("date_Created").toString());
                        photo.setImage_Path(objectMap.get("image_Path").toString());


                        ArrayList<Comments> comments = new ArrayList<Comments>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child("comments").getChildren()){
                            Comments comment = new Comments();
                            comment.setUser_id(dSnapshot.getValue(Comments.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comments.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comments.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    if(count >= mFollowing.size() -1){
                        //display our photos
                        displayPhotos();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void displayPhotos(){
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_Created().compareTo(o1.getDate_Created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new HomeFragmentPostViewListAdapter(getActivity(), R.layout.fragment_home_post_viewer, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > mResults && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid(), 0,0));
                for (String id : mFollowing) {
                    int countStory = 0;
                    Story story = null;
                    if(id != FirebaseAuth.getInstance().getCurrentUser().getUid()){

                        for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                            story = snapshot.getValue(Story.class);
                            if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                                countStory++;
                            }
                        }
                        if (countStory > 0){
                            storyList.add(story);
                        }

                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
