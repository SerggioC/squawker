/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.example.com.squawker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.example.com.squawker.following.FollowingPreferenceActivity;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupWindow;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.example.com.squawker.provider.SquawkContract.INSTRUCTOR_KEYS;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener{

    private static String LOG_TAG = "Sergio> " + MainActivity.class.getSimpleName();
    private static final int LOADER_ID_MESSAGES = 0;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SquawkAdapter mAdapter;

    static final String[] MESSAGES_PROJECTION = {
            SquawkContract.COLUMN_AUTHOR,
            SquawkContract.COLUMN_MESSAGE,
            SquawkContract.COLUMN_DATE,
            SquawkContract.COLUMN_AUTHOR_KEY
    };

    static final int COL_NUM_AUTHOR = 0;
    static final int COL_NUM_MESSAGE = 1;
    static final int COL_NUM_DATE = 2;
    static final int COL_NUM_AUTHOR_KEY = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.squawks_recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Specify an adapter
        mAdapter = new SquawkAdapter();
        mRecyclerView.setAdapter(mAdapter);

        // Start the loader
        getSupportLoaderManager().initLoader(LOADER_ID_MESSAGES, null, this);

        // get notification directly form firebase console
        // select GROW -> Notifications
        // https://console.firebase.google.com/project/squawker-b2eb2/notification/compose?campaignId=7364157308717098897&dupe=true
        // it will create automatic notifications on the device from the Firebase lib itself.
        // it will only display notification if the app is on the background

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey("testkey")) {
            PopupWindow window = new PopupWindow(this);
            window.showAsDropDown(mRecyclerView);
            Log.i("Sergio>", this + " onCreate\nextras= " + extras.getString("testkey"));
        }


        String token = FirebaseInstanceId.getInstance().getToken();

        // You'll need to implement the onTokenRefresh method. Simply have it print out	the new token
        String msg = getString(R.string.message_token_format, token);

        Log.d(LOG_TAG, msg);
        // this Firebase token changes everytime the app is reinstalled
        // eAbZ8L3lnz8:APA91bF5Xt03b09QMU4oBwAdpI-74tTzm2uF_1fo2q0CRr_-VmKB_nh28zB90TyrYfdHW7HXppN6S8KZJDWqL-DvKCxINm7nPy8Dr_qZ8wtMyo2zhwU0VLpXGeS6uqd_jRvKgK3P3JWX


        // Subscribe to topics
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean defaultFollow = getResources().getBoolean(R.bool.follow_default_message_subscription);
        for (int i = 0; i < INSTRUCTOR_KEYS.length; i++) {
            if (prefs.getBoolean(INSTRUCTOR_KEYS[i], defaultFollow)) {
                FirebaseMessaging.getInstance().subscribeToTopic(INSTRUCTOR_KEYS[i]);
            }
        }

        // Register the listener
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_following_preferences) {
            // Opens the following activity when the menu icon is pressed
            Intent startFollowingActivity = new Intent(this, FollowingPreferenceActivity.class);
            startActivity(startFollowingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Loader callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This method generates a selection off of only the current followers
        String selection = SquawkContract.createSelectionForCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this));
        Log.d(LOG_TAG, "Selection is " + selection);
        return new CursorLoader(this, SquawkProvider.SquawkMessages.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, SquawkContract.COLUMN_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // ReStart the loader if the preferences has changed
        getSupportLoaderManager().restartLoader(LOADER_ID_MESSAGES, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
