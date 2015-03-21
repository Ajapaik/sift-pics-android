package ee.ajapaik.sorter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ee.ajapaik.sorter.R;
import ee.ajapaik.sorter.util.Favorite;

public class FavoritesAdapter extends ArrayAdapter<Favorite> {
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
        imageView.setImageURI(favorite.getThumbnail());

        textView = (TextView)convertView.findViewById(R.id.text_title);
        textView.setText(favorite.getTitle());

        textView = (TextView)convertView.findViewById(R.id.text_subtitle);
        textView.setText(favorite.getTimestamp().toString());

        return convertView;
    }
}
