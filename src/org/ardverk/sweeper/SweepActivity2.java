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

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SweepActivity2 extends Activity {

    private static final String TAG 
        = SweepActivity2.class.getName();

    private static final String[] ALL = { "*" };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sweep);
        
        Bundle extras = getIntent().getExtras();
        
        boolean external = extras.getBoolean("external");
        boolean internal = extras.getBoolean("internal");
        
        List<Playlist> playlists = new ArrayList<Playlist>();
        
        ProgressDialog dialog = ProgressDialog.show(this, null, 
                getString(R.string.searching), true);
        try {
            if (external) {
                search(EXTERNAL_CONTENT_URI, playlists);
            }
            
            if (internal) {
                search(INTERNAL_CONTENT_URI, playlists);
            }
        } finally {
            dialog.dismiss();
        }
        
        ListView listView = (ListView)findViewById(R.id.playlists);
        ListAdapter adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_list_item_multiple_choice, 
                new String[] { "A", "B", "C", "D", "E", "F", "G", "H" });
        listView.setAdapter(adapter);
    }
    
    private void search(Uri contentUri, List<Playlist> playlists) {
        try {
            List<Playlist> list = search(contentUri);
            playlists.addAll(list);
        } catch (SQLiteException err) {
            Log.e(TAG, "SQLiteException", err);
        }
    }
    
    private List<Playlist> search(Uri contentUri) throws SQLiteException {
        List<Playlist> list = new ArrayList<Playlist>();
        Cursor cursor = managedQuery(contentUri, ALL, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long playlistId = cursor.getLong(0);
                    String playlistPath = cursor.getString(1);
                    String playlistName = cursor.getString(2);
                    
                    if (StringUtils.isEmpty(playlistPath)) {
                        list.add(new Playlist(contentUri, playlistId, playlistName));
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            CursorUtils.close(cursor);
        }
        
        return list;
    }
}
