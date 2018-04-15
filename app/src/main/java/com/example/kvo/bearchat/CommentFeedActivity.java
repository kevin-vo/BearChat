package com.example.kvo.bearchat;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

// Displays a list of comments for a particular landmark.
public class CommentFeedActivity extends AppCompatActivity {

    private static final String TAG = CommentFeedActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Comment> mComments = new ArrayList<Comment>();

    private String username;
    private String landmarkName;

    // UI elements
    EditText commentInputBox;
    RelativeLayout layout;
    Button sendButton;
    Toolbar mToolbar;

    private FirebaseDatabase mFirebase;
    private DatabaseReference mDatabase;

    /* TODO: right now mRecyclerView is using hard coded comments.
     * You'll need to add functionality for pulling and posting comments from Firebase
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_feed);

        // TODO: replace this with the name of the landmark the user chose
        username = (String) getIntent().getExtras().get("usernameString");
        landmarkName = (String) getIntent().getExtras().get("landmarkTextString");
        Log.e(username, "YOOOOOO" );

        // sets the app bar's title
        setTitle(landmarkName + ": Posts");

        // hook up UI elements
        layout = (RelativeLayout) findViewById(R.id.comment_layout);
        commentInputBox = (EditText) layout.findViewById(R.id.comment_input_edit_text);
        sendButton = (Button) layout.findViewById(R.id.send_button);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(landmarkName + ": Posts");

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create an onclick for the send button
        setOnClickForSendButton();

        // make some test comment objects that we add to the recycler view
//        makeTestComments();


        mFirebase = FirebaseDatabase.getInstance();
        mDatabase = mFirebase.getReference(landmarkName);

        loadComments();

        // use the comments in mComments to create an adapter. This will populate mRecyclerView
        // with a custom cell (with comment_cell_layout) for each comment in mComments
//        setAdapterAndUpdateData();
    }

    private void loadComments() {
        ValueEventListener myDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                HashMap<String, Object> comments = (HashMap<String, Object>) dataSnapshot.getValue();
//                mComments = new ArrayList<Comment>();
//                for (String c : comments.keySet()) {
//                    HashMap<String, String> dat = (HashMap<String, String>) comments.get(c);
//                    String username = dat.get("username");
//                    String text = dat.get("text");
//
//                    SimpleDateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
//                    Date date = null;
//                    try {
//                        date = f.parse(c);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    mComments.add(new Comment(text, username, date));
//                }
//                setAdapterAndUpdateData();

                mComments = new ArrayList<Comment>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.e("HALLO", child.getKey().toString());
                    Log.e("HALLO", child.getValue().toString());

                    String d = child.getKey();
                    HashMap<String, String> dat = (HashMap<String, String>) child.getValue();
//                    HashMap<String, String> dat = (HashMap<String, String>) child.get;
                    String username = dat.get("username");
                    String text = dat.get("text");

                    SimpleDateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
                    Date date = null;
                    try {
                        date = f.parse(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    mComments.add(new Comment(text, username, date));
                }
                setAdapterAndUpdateData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("0", "cancelled");
            }
        };
        mDatabase.addValueEventListener(myDataListener);
    }


    // TODO: delete me
    private void makeTestComments() {
        String randomString = "hello world hello world ";
        Comment newComment = new Comment(randomString, "test_user1", new Date());
        Comment hourAgoComment = new Comment(randomString + randomString, "test_user2", new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        Comment overHourComment = new Comment(randomString, "test_user3", new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)));
        Comment dayAgoComment = new Comment(randomString, "test_user4", new Date(System.currentTimeMillis() - (25 * 60 * 60 * 1000)));
        Comment daysAgoComment = new Comment(randomString + randomString + randomString, "test_user5", new Date(System.currentTimeMillis() - (48 * 60 * 60 * 1000)));
        mComments.add(newComment);mComments.add(hourAgoComment); mComments.add(overHourComment);mComments.add(dayAgoComment); mComments.add(daysAgoComment);

    }

    private void setOnClickForSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInputBox.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    // don't do anything if nothing was added
                    commentInputBox.requestFocus();
                } else {
                    // clear edit text, post comment
                    commentInputBox.setText("");
                    postNewComment(comment);
                }
            }
        });
    }

    private void setAdapterAndUpdateData() {
        // create a new adapter with the updated mComments array
        // this will "refresh" our recycler view
        mAdapter = new CommentAdapter(this, mComments);
        mRecyclerView.setAdapter(mAdapter);

        // scroll to the last comment
        if (mComments.size() > 0) {
//            mRecyclerView.smoothScrollToPosition(mComments.size() - 1);
            mRecyclerView.scrollToPosition(mComments.size() - 1);
        }
    }

    private void postNewComment(String commentText) {
        Comment newComment = new Comment(commentText, username, new Date());
        mDatabase.child(newComment.date.toString()).setValue(newComment);
        mComments.add(newComment);
        setAdapterAndUpdateData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
