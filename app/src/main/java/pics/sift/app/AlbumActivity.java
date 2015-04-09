package pics.sift.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pics.sift.app.data.Album;
import pics.sift.app.fragment.AlbumFragment;
import pics.sift.app.data.Favorite;
import pics.sift.app.util.WebActivity;

public class AlbumActivity extends WebActivity {
    private static final String EXTRA_ALBUM_ID = "album_id";
    private static final String EXTRA_PHOTO_ID = "photo_id";
    private static final String EXTRA_TITLE = "title";

    public static void start(Context context, Album album) {
        Intent intent = new Intent(context, AlbumActivity.class);

        intent.putExtra(EXTRA_ALBUM_ID, album.getIdentifier());
        intent.putExtra(EXTRA_TITLE, album.getTitle());

        context.startActivity(intent);
    }

    public static void start(Context context, Favorite favorite) {
        Intent intent = new Intent(context, AlbumActivity.class);

        intent.putExtra(EXTRA_ALBUM_ID, favorite.getAlbumIdentifier());
        intent.putExtra(EXTRA_PHOTO_ID, favorite.getPhotoIdentifier());

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(title != null) {
            getSupportActionBar().setTitle(title);
        }

        if(savedInstanceState == null) {
            AlbumFragment fragment = new AlbumFragment();

            fragment.setAlbumIdentifier(getIntent().getStringExtra(EXTRA_ALBUM_ID));
            fragment.setPhotoIdentifier(getIntent().getStringExtra(EXTRA_PHOTO_ID));
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();

            return true;
        } else if(id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}