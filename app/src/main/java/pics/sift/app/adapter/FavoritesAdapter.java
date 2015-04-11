package pics.sift.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tonicartos.superslim.GridSLM;

import java.util.ArrayList;
import java.util.List;

import pics.sift.app.AlbumActivity;
import pics.sift.app.R;
import pics.sift.app.data.Favorite;
import pics.sift.app.data.Meta;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private static final int VIEW_TYPE_CONTENT = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private static final int THUMBNAIL_SIZE = 200;

    private Context m_context;
    private List<Item> m_items;

    public FavoritesAdapter(Context context, List<Favorite> favorites, Meta meta) {
        m_context = context;
        m_items = new ArrayList<Item>();

        if(favorites != null) {
            String lastHeader = "";
            int sectionFirstPosition = 0;
            int headerCount = 0;

            for(int i = 0, c = favorites.size(); i < c; i++) {
                Favorite favorite = favorites.get(i);
                String header = (meta != null) ? meta.getAlbumTitle(favorite.getAlbumIdentifier()) : null;

                if(header == null) {
                    header = "";
                }

                if(!lastHeader.equals(header)) {
                    int favoriteCount = 0;

                    sectionFirstPosition = i + headerCount;
                    headerCount += 1;
                    lastHeader = header;

                    for(Favorite f : favorites) {
                        if(f.getAlbumIdentifier().equals(favorite.getAlbumIdentifier())) {
                            favoriteCount++;
                        }
                    }

                    m_items.add(new Item(sectionFirstPosition, header + " (" + favoriteCount + ")"));
                }

                m_items.add(new Item(sectionFirstPosition, favorite));
            }
        }
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(m_context).inflate(R.layout.list_favorite_header, parent, false);
        } else {
            view = LayoutInflater.from(m_context).inflate(R.layout.list_favorite_item, parent, false);
        }

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        GridSLM.LayoutParams lp = new GridSLM.LayoutParams(holder.itemView.getLayoutParams());
        Item item = m_items.get(position);

        if(item.favorite != null) {
            if(holder.textView != null) {
                holder.textView.setText(item.favorite.getTitle());
            }

            if(holder.imageView != null) {
                holder.imageView.setImageURI(item.favorite.getThumbnail(THUMBNAIL_SIZE));
            }
        } else if(item.header != null) {
            if(holder.textView != null) {
                holder.textView.setText(item.header);
            }
        }

        lp.setSlm(GridSLM.ID);
        lp.setNumColumns(m_context.getResources().getInteger(R.integer.favorite_column_count));
        lp.setFirstPosition(item.sectionFirstPosition);
        holder.setFavorite(item.favorite);
        holder.itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return (m_items.get(position).favorite == null) ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return m_items.size();
    }

    private static class Item {
        public final String header;
        public final Favorite favorite;
        public final int sectionFirstPosition;

        public Item(int sectionFirstPosition, String header) {
            this.sectionFirstPosition = sectionFirstPosition;
            this.header = header;
            this.favorite = null;
        }

        public Item(int sectionFirstPosition, Favorite favorite) {
            this.sectionFirstPosition = sectionFirstPosition;
            this.header = null;
            this.favorite = favorite;
        }
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View itemView;
        public final ImageView imageView;
        public final TextView textView;

        private Favorite m_favorite = null;

        public FavoriteViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.imageView = (ImageView)itemView.findViewById(R.id.image_icon);
            this.textView = (TextView)itemView.findViewById(R.id.text_title);
            this.itemView.setOnClickListener(this);
        }

        public void setFavorite(Favorite favorite) {
            m_favorite = favorite;
        }

        @Override
        public void onClick(View view) {
            if(m_favorite != null) {
                AlbumActivity.start(view.getContext(), m_favorite);
            }
        }
    }
}
