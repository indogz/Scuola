package com.example.volcano.dewdrop;

import android.content.Intent;
import android.net.Uri;
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
import com.example.volcano.dewdrop.utils.DownloadImageTask;
import com.example.volcano.dewdrop.utils.StorageHelper;
import com.example.volcano.dewdrop.utils.VideoChoice;

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
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("file/*");
                intent = Intent.createChooser(chooseFile, "Choose a .mp4 file");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path = "";
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                Uri uri = data.getData();
                if (uri.toString().contains(".mp4")) {
                    //upload
                } else {
                    Toast.makeText(this, "Only .mp4 files can be uploaded", Toast.LENGTH_LONG).show();
                }
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

        ListView listView = (ListView) findViewById(R.id.mainContent).findViewById(R.id.mainScreen).findViewById(R.id.listView);

        ArrayList<VideoChoice> list = new ArrayList<>();
        ImageView i = new ImageView(this);
        i.setImageResource(R.mipmap.drop);
        for (int j = 0; j < 1; j++) {
            list.add(VideoChoice.getInstance(i, "video", "video", 30));
        }
        CustomAdapter adapter = new CustomAdapter(this, R.layout.fragment_video_choice, list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              VideoChoice videoChoice= (VideoChoice) parent.getAdapter().getItem(position);
               String title= videoChoice.getTitle();
                StorageHelper storageHelper = StorageHelper.getInstance();
               storageHelper.fetchVideoUri(MainActivity.this,title);
            }
        });


    }

    private void setUserData() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        CardView cardView = (CardView) headerView.findViewById(R.id.cardView);


        ImageView src = new ImageView(this);
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
