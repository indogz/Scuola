package com.example.volcano.dewdrop;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.volcano.dewdrop.auth.User;
import com.example.volcano.dewdrop.utils.CustomAdapter;
import com.example.volcano.dewdrop.utils.DatabaseOpenHelper;
import com.example.volcano.dewdrop.utils.DownloadImageTask;
import com.example.volcano.dewdrop.utils.ProgressDialogFragment;
import com.example.volcano.dewdrop.utils.StorageHelper;
import com.example.volcano.dewdrop.utils.VideoChoice;
import com.example.volcano.dewdrop.utils.VideoParametersSelector;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_CHOOSE_FILE = 0;
    private User user;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle data = getIntent().getExtras();
        String name = data.getString("NAME");
        String email = data.getString("EMAIL");
        Uri photo = Uri.parse(data.getString("PHOTO"));

        user = new User(name, email, photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        // Setting image
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUserData();

        setAdapter();

        setFloatingButtonListener();


    }

    public void setFloatingButtonListener() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a Video to Upload"),
                            ACTIVITY_CHOOSE_FILE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(MainActivity.this, "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                Uri uri = data.getData();
                VideoParametersSelector videoParametersSelector = VideoParametersSelector.newInstance(uri);
                videoParametersSelector.show(getFragmentManager(), "TAG");
            }
        }
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    private void setAdapter() {

        final ListView listView = (ListView) findViewById(R.id.mainContent).findViewById(R.id.mainScreen).findViewById(R.id.listView);

        final ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        final ArrayList<VideoChoice> videoChoices = new ArrayList<>();

        AsyncTask continuation = new AsyncTask() {


            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.fragment_video_choice, videoChoices);
                Log.d("VIDEOCHOICES", videoChoices.toString());

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        VideoChoice videoChoice = (VideoChoice) parent.getAdapter().getItem(position);
                        String title = videoChoice.getTitle();
                        StorageHelper storageHelper = StorageHelper.getInstance();
                        storageHelper.fetchVideoUri(MainActivity.this, title);
                    }
                });
                progressDialogFragment.dismiss();

            }
        };
        DatabaseOpenHelper.getInstance().fetchAllVideos(this, progressDialogFragment, continuation, videoChoices);
    }

    private void setUserData() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        CardView cardView = (CardView) headerView.findViewById(R.id.cardView);


        ImageView src = new ImageView(this);
        System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§" + user.PHOTO_URL);
        new DownloadImageTask(src).execute(user.PHOTO_URL.toString());

        cardView.addView(src);


        //Setting name
        TextView nameHeader = (TextView) headerView.findViewById(R.id.nameHeader);
        nameHeader.setText(user.NAME);


        TextView emailHeader = (TextView) headerView.findViewById(R.id.emailHeader);
        emailHeader.setText(user.EMAIL);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            //set listview here with movies
            //set onclick action for each movie and start it
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
