package pics.sift.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pics.sift.app.fragment.AlbumsFragment;
import pics.sift.app.util.WebActivity;

public class AlbumsActivity extends WebActivity {
    private static final String TAG = "AlbumsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_albums);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new AlbumsFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_albums, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
