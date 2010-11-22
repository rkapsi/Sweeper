/*
 * Copyright 2010 Roger Kapsi
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.ardverk.sweeper;

import static android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI;

import java.util.ArrayList;
import java.util.List;

import org.ardverk.sweeper.CompleteActivity.Reason;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG 
        = MainActivity.class.getName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SearchTask task = new SearchTask() {
            @Override
            protected void onPostExecute(PlaylistEntity[] entities) {
                super.onPostExecute(entities);
                initMainActivity(entities);
            }
        };
        
        task.execute();
    }
    
    private void initMainActivity(PlaylistEntity[] entities) {
        if (entities == null || entities.length == 0) {
            Intent complete = new Intent(this, CompleteActivity.class);
            Reason.NOTHING_FOUND.putExtra(complete);
            startActivity(complete);
            finish();
            return;
        }
        
        setContentView(R.layout.main);
        
        Button button = (Button)findViewById(R.id.main_delete_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        
        ListView listView = (ListView)findViewById(R.id.main_playlist_list);
        ListAdapter adapter = new PlaylistEntityAdapter(this, entities);
        listView.setAdapter(adapter);
    }
    
    private void delete() {
        
        DeleteTask task = new DeleteTask() {
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                
                Intent complete = new Intent(
                    MainActivity.this, CompleteActivity.class);
                startActivity(complete);
                finish();
            }
        };
        
        ListView listView = (ListView)findViewById(R.id.main_playlist_list);
        PlaylistEntityAdapter adapter = (PlaylistEntityAdapter)listView.getAdapter();
        task.execute(adapter.getPlaylistEntities());
    }
    
    private static class PlaylistEntity implements Checkable, 
            CheckBox.OnCheckedChangeListener {
        
        public static List<PlaylistEntity> transform(List<Playlist> playlists) {
            List<PlaylistEntity> entities 
                = new ArrayList<PlaylistEntity>(playlists.size());
            for (Playlist playlist : playlists) {
                entities.add(new PlaylistEntity(playlist));
            }
            return entities;
        }
        
        private final Playlist playlist;
        
        private boolean checked = true;
        
        private PlaylistEntity(Playlist playlist) {
            this.playlist = playlist;
        }

        public Playlist getPlaylist() {
            return playlist;
        }
        
        @Override
        public void toggle() {
            setChecked(!isChecked());
        }
        
        @Override
        public boolean isChecked() {
            return checked;
        }

        @Override
        public void setChecked(boolean checked) {
            this.checked = checked;
        }
        
        public String getName() {
            return playlist.getName();
        }
        
        public long getId() {
            return playlist.getId();
        }
        
        public boolean isExternal() {
            return playlist.getDatabaseUri().equals(EXTERNAL_CONTENT_URI);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, 
                boolean isChecked) {
            setChecked(isChecked);
        }
    }
    
    private static class PlaylistEntityAdapter extends BaseAdapter {
        
        private final View.OnClickListener listener 
                = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v.findViewById(R.id.playlist_row_checkbox);
                checkBox.toggle();
            }
        };
        
        private final Context context;
        
        private final PlaylistEntity[] entities;
        
        public PlaylistEntityAdapter(Context context, 
                PlaylistEntity[] entities) {
            this.context = context;
            this.entities = entities;
        }

        public PlaylistEntity[] getPlaylistEntities() {
            return entities;
        }
        
        @Override
        public int getCount() {
            return entities.length;
        }

        @Override
        public PlaylistEntity getItem(int position) {
            return entities[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getPlaylist().getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.playlist_row, null);
                convertView.setOnClickListener(listener);
            }
            
            PlaylistEntity entity = getItem(position);
            
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.playlist_row_checkbox);
            checkBox.setOnCheckedChangeListener(entity);
            
            TextView topText = (TextView) convertView.findViewById(R.id.playlist_row_toptext);
            TextView bottomText = (TextView) convertView.findViewById(R.id.playlist_row_bottomtext);
            
            checkBox.setChecked(entity.isChecked());
            topText.setText(entity.getName());
            bottomText.setText(createBottomString(entity));
            
            return convertView;
        }
        
        private String createBottomString(PlaylistEntity entity) {
            StringBuilder builder = new StringBuilder();
            
            boolean external = entity.isExternal();
            builder.append(context.getString(
                    external ? R.string.external : R.string.internal));
            builder.append(", ").append(entity.getId());
            
            return builder.toString();
        }
    }
    
    private class SearchTask extends AsyncTask<Void, Void, PlaylistEntity[]> {
        
        private ProgressDialog dialog = null;
        
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, null, 
                    getString(R.string.searching), true);
        }
        
        @Override
        protected PlaylistEntity[] doInBackground(Void... params) {
            
            List<PlaylistEntity> entities = new ArrayList<PlaylistEntity>();
            
            List<PlaylistEntity> external = listDeleted(EXTERNAL_CONTENT_URI);
            List<PlaylistEntity> internal = listDeleted(INTERNAL_CONTENT_URI);
            
            if (external != null) {
                entities.addAll(external);
            }
            
            if (internal != null) {
                entities.addAll(internal);
            }
            
            /*external = new ArrayList<PlaylistEntity>();
            external.add(new PlaylistEntity(new Playlist(EXTERNAL_CONTENT_URI, 0, null, "A")));
            external.add(new PlaylistEntity(new Playlist(EXTERNAL_CONTENT_URI, 1, null, "B")));
            external.add(new PlaylistEntity(new Playlist(EXTERNAL_CONTENT_URI, 2, null, "C")));
            external.add(new PlaylistEntity(new Playlist(EXTERNAL_CONTENT_URI, 3, null, "D")));
            external.add(new PlaylistEntity(new Playlist(INTERNAL_CONTENT_URI, 4, null, "E")));
            external.add(new PlaylistEntity(new Playlist(INTERNAL_CONTENT_URI, 5, null, "F")));
            external.add(new PlaylistEntity(new Playlist(INTERNAL_CONTENT_URI, 6, null, "G")));
            external.add(new PlaylistEntity(new Playlist(INTERNAL_CONTENT_URI, 7, null, "H")));*/
            
            return entities.toArray(new PlaylistEntity[0]);
        }

        private List<PlaylistEntity> listDeleted(Uri contentUri) {
            try {
                List<Playlist> playlists 
                    = Playlist.listDeleted(MainActivity.this, contentUri);
                if (playlists != null && !playlists.isEmpty()) {
                    return PlaylistEntity.transform(playlists);
                }
            } catch (SQLiteException err) {
                Log.d(TAG, "SQLiteException", err);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(PlaylistEntity[] entities) {
            dialog.dismiss();
        }
    }
    
    private class DeleteTask extends AsyncTask<PlaylistEntity, Void, Void> {
        
        private ProgressDialog dialog = null;
        
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, null, 
                    getString(R.string.sweeping), true);
        }
        
        @Override
        protected Void doInBackground(PlaylistEntity... entities) {
            deleteAll(entities);
            return null;
        }
        
        private boolean deleteAll(PlaylistEntity... entities) {
            try {
                ContentResolver contentResolver = getContentResolver();
                for (PlaylistEntity entity : entities) {
                    if (!entity.isChecked()) {
                        continue;
                    }
                    
                    Playlist playlist = entity.getPlaylist();
                    Uri uri = playlist.getContentUri();
                    contentResolver.delete(uri, null, null);
                }
                return true;
            } catch (SQLiteException err) {
                Log.d(TAG, "SQLiteException", err);
            }
            return false;
        }
    
        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
}
