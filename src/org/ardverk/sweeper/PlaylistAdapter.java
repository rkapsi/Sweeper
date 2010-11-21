package org.ardverk.sweeper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    private final Context context;
    
    private final List<Playlist> playlists;
    
    public PlaylistAdapter(Context context, int textViewResourceId,
            List<Playlist> playlists) {
        super(context, textViewResourceId, playlists);
        
        this.context = context;
        this.playlists = playlists;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.playlist, null);
        }*/
        
        Playlist playlist = playlists.get(position);
        /*if (o != null) {
            TextView tt = (TextView) v.findViewById(R.id.toptext);
            TextView bt = (TextView) v.findViewById(R.id.bottomtext);
            if (tt != null) {
                tt.setText("Name: " + o.getOrderName());
            }
            if (bt != null) {
                bt.setText("Status: " + o.getOrderStatus());
            }
        }*/
        
        return convertView;
    }
}
