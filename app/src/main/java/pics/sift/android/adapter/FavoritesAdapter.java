package pics.sift.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pics.sift.android.R;
import pics.sift.android.data.Favorite;

public class FavoritesAdapter extends ArrayAdapter<Favorite> {
    private static final int THUMBNAIL_SIZE = 64;

    public FavoritesAdapter(Context context, List<Favorite> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favorite favorite = getItem(position);
        ImageView imageView;
        TextView textView;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_profile_item, parent, false);
        }

        imageView = (ImageView)convertView.findViewById(R.id.image_icon);
        imageView.setImageURI(favorite.getThumbnail(THUMBNAIL_SIZE));

        textView = (TextView)convertView.findViewById(R.id.text_title);
        textView.setText(favorite.getTitle());

        return convertView;
    }
}
