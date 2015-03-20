package ee.ajapaik.sorter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ee.ajapaik.sorter.data.Album;
import ee.ajapaik.sorter.fragment.AlbumFragment;
import ee.ajapaik.sorter.util.Favorite;
import ee.ajapaik.sorter.util.WebActivity;

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
        intent.putExtra(EXTRA_TITLE, favorite.getTitle());

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
        }

        return super.onOptionsItemSelected(item);
    }
}
