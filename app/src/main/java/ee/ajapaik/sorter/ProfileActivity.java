package ee.ajapaik.sorter;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import ee.ajapaik.sorter.fragment.ProfileFragment;
import ee.ajapaik.sorter.util.WebActivity;

public class ProfileActivity extends WebActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ProfileFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
