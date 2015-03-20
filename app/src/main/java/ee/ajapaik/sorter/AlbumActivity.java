package ee.ajapaik.sorter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import ee.ajapaik.sorter.fragment.AlbumFragment;
import ee.ajapaik.sorter.util.WebActivity;

public class AlbumActivity extends WebActivity {
    private static final String EXTRA_ALBUM_ID = "album_id";

    public static void start(Context context, String albumIdentifier) {
        Intent intent = new Intent(context, AlbumActivity.class);

        intent.putExtra(EXTRA_ALBUM_ID, albumIdentifier);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            AlbumFragment fragment = new AlbumFragment();

            fragment.setAlbumIdentifier(getIntent().getStringExtra(EXTRA_ALBUM_ID));
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
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
