package com.sudo_code.codesprint.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sudo_code.codesprint.R;
import com.sudo_code.codesprint.holder.ChallengeHolder;
import com.sudo_code.codesprint.model.UserChallenge;
import com.sudo_code.codesprint.helper.DatabaseController;
import static com.sudo_code.codesprint.activity.LoginActivity.USER_ID;
import static com.sudo_code.codesprint.helper.DatabaseController.USER_CHALLENGE_FIELD_NAME;
import static com.sudo_code.codesprint.helper.DatabaseController.USER_DB_REF;

/**
 * A home screen that displays past challenges and allows the user to begin
 * today's challenge.
 */
public class HomeActivity extends AppCompatActivity {

    // Object fields
    private DatabaseController mDbController;
    private FirebaseRecyclerAdapter mAdapter;

    /**
     * Sets up the toolbar, defines the recycler, gets objects and populates it.
     *
     * @param savedInstanceState - the saved bundle state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_home_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.home_recycler_view);
        final LinearLayout progressLayout = (LinearLayout) findViewById(R.id.home_progress_layout);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.home_progress_bar);
        final TextView progressText = (TextView) findViewById(R.id.home_progress_text);

        // Set up indexed recycler adapter for Firebase
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userChallengeDb = db.child(USER_DB_REF).child(getCurrentUserId())
                .child(USER_CHALLENGE_FIELD_NAME);
        mAdapter = new FirebaseRecyclerAdapter<UserChallenge, ChallengeHolder>(
                UserChallenge.class, R.layout.challenge_item, ChallengeHolder.class,
                userChallengeDb) {
            @Override
            public void populateViewHolder(ChallengeHolder holder, UserChallenge userChallenge, int position) {
                holder.setComponents(userChallenge);
            }
        };

        // Adapter setup
        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        recycler.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager manager = (LinearLayoutManager) recycler.getLayoutManager();
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        // Intent definitions
        final Intent beginChallengeIntent = new Intent(this, BeginChallengeActivity.class);

        // Listeners
        Button currentChallengeButton = (Button) findViewById(R.id.home_challenge_button);
        currentChallengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(beginChallengeIntent);
            }
        });

        // Check to see if data was loaded and make changes
        userChallengeDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                progressText.setText(R.string.load_data_error);
            }
        });

        mDbController = new DatabaseController(this);
    }


    /**
     * Inflates the options menu and sets menu item colours.
     *
     * @param menu - The options menu to be inflated.
     * @return boolean - success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        Drawable drawable = menu.findItem(R.id.action_add_user).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    /**
     * Checks which option was selected and performs the approproate actions.
     *
     * @param item - the selected option MenuItem
     * @return boolean - success on normal menu processing (handled by superclass)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_users) {
            Intent followingIntent = new Intent(this, FollowingActivity.class);
            startActivity(followingIntent);
        }
        else if (id == R.id.action_quit) {
            Intent logoutIntent = new Intent(this, LoginActivity.class);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().clear().apply();
            startActivity(logoutIntent);
            finish();
        }
        else if (id == R.id.action_add_user) {
            showAddUserDialogue();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Displays an alert dialogue allowing the user to follow another user.
     */
    protected void showAddUserDialogue() {
        // Setup the view
        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View promptView = layoutInflater.inflate(R.layout.follow_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText usernameText = (EditText) promptView.findViewById(R.id.follow_username);

        // Setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.follow_user_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    mDbController.createUserFollow(usernameText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.follow_user_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // Create the alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Cleans up the recycler adapter and shuts down the connection to the database
     * after the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    /**
     * A helper method to return the id of the current user from Shared Prferences
     * just incase the session is dropped.
     *
     * @return String - the current user user id
     */
    private String getCurrentUserId() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPrefs.getString(USER_ID, null);
    }
}