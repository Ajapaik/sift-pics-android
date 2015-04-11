package pics.sift.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pics.sift.app.AlbumActivity;
import pics.sift.app.R;
import pics.sift.app.data.Album;
import pics.sift.app.data.Stats;

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
                ((ImageButton)v).setImageResource((m_details) ? R.drawable.ic_highlight_remove_white_36dp : R.drawable.ic_info_outline_white_36dp);

                if(m_details) {
                    Resources resources = context.getResources();
                    String summary = resources.getString(R.string.album_stats_header, album.getTotalCount());
                    Stats stats = album.getStats();

                    summary = summary + resources.getString(R.string.album_stats_personal, album.getDecisionsCount(), album.getTaggedCount());

                    if(stats != null) {
                        if(stats.getRank() != 0) {
                            summary = summary + " " + resources.getString(R.string.album_stats_rank, stats.getRank());
                        }
                    }

                    textView.setText(Html.fromHtml(summary));
                    textView = (TextView)container.findViewById(R.id.text_subtitle);
                    summary = resources.getString(R.string.album_stats_summary, stats.getUsersCount(), stats.getDecisionsCount(), stats.getTaggedCount());
                    textView.setText(Html.fromHtml(summary));
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
