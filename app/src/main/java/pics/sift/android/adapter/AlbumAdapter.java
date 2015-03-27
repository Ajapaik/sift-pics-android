package pics.sift.android.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pics.sift.android.AlbumActivity;
import pics.sift.android.R;
import pics.sift.android.data.Album;

public class AlbumAdapter extends ArrayAdapter<Album> {
    private static final int THUMBNAIL_SIZE = 250;

    public AlbumAdapter(Context context, List<Album> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Album album = getItem(position);
        ImageView imageView;
        ImageButton button;
        TextView textView;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_album_item, parent, false);
        }

        button = (ImageButton)convertView.findViewById(R.id.button_action);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumActivity.start(v.getContext(), album);
            }
        });

        button = (ImageButton)convertView.findViewById(R.id.button_details);
        button.setImageResource(R.drawable.ic_info_outline_white_36dp);
        button.setOnClickListener(new View.OnClickListener() {
            private boolean m_details = false;

            @Override
            public void onClick(View v) {
                ViewGroup container = (ViewGroup)v.getParent();
                Context context = v.getContext();
                TextView textView;

                textView = (TextView)container.findViewById(R.id.text_title);
                m_details = !m_details;

                if(m_details) {
                    textView.setText(Html.fromHtml(context.getResources().getQuantityString(R.plurals.album_stats_total, album.getTotalCount(), album.getTotalCount())));
                    textView = (TextView)container.findViewById(R.id.text_subtitle);
                    textView.setText(Html.fromHtml(context.getResources().getQuantityString(R.plurals.album_stats_tagged, album.getTaggedCount(), album.getTaggedCount())));
                } else {
                    textView.setText(album.getTitle());
                    textView = (TextView)container.findViewById(R.id.text_subtitle);
                    textView.setText(album.getSubtitle());
                }
            }
        });

        imageView = (ImageView)convertView.findViewById(R.id.image_background);
        imageView.setImageURI(album.getThumbnail(THUMBNAIL_SIZE));

        textView = (TextView)convertView.findViewById(R.id.text_title);
        textView.setText(album.getTitle());

        textView = (TextView)convertView.findViewById(R.id.text_subtitle);
        textView.setText(album.getSubtitle());

        return convertView;
    }
}
