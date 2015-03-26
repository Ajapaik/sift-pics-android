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
import pics.sift.android.data.Album;

public class AlbumAdapter extends ArrayAdapter<Album> {
    private static final int THUMBNAIL_SIZE = 250;

    public AlbumAdapter(Context context, List<Album> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album album = getItem(position);
        ImageView imageView;
        TextView textView;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_album_item, parent, false);
        }

        imageView = (ImageView)convertView.findViewById(R.id.image_background);
        imageView.setImageURI(album.getThumbnail(THUMBNAIL_SIZE));

        textView = (TextView)convertView.findViewById(R.id.text_title);
        textView.setText(album.getTitle());

        textView = (TextView)convertView.findViewById(R.id.text_subtitle);
        textView.setText(album.getSubtitle());

        return convertView;
    }
}