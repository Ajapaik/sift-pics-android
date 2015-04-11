package pics.sift.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pics.sift.app.fragment.AlbumsFragment;
import pics.sift.app.util.WebActivity;

public class AlbumsActivity extends WebActivity {
    private static final String TAG = "AlbumsActivity";

    private static final String EXTRA_ALBUM_ID = "album_id";

    public static Intent getStartIntent(Context context, String albumId) {
        Intent intent = new Intent(context, AlbumsActivity.class);

        intent.putExtra(EXTRA_ALBUM_ID, albumId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_albums);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new AlbumsFragment()).commit();
        }

        if(checkPlayServices(true)) {
            registerDevice(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albums, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));

            return true;
        } else if(id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
