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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

class Playlist {
    
    private static final String[] ALL = { "*" };
    
    /**
     * Returns a {@link List} of {@link Playlist}s.
     */
    public static List<Playlist> list(
            Activity activity, Uri contentUri) throws SQLiteException {
        
        List<Playlist> list = new ArrayList<Playlist>();
        Cursor cursor = activity.managedQuery(contentUri, ALL, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(0);
                    String path = cursor.getString(1);
                    String name = cursor.getString(2);
                    
                    list.add(new Playlist(contentUri, id, path, name));
                } while (cursor.moveToNext());
            }
        } finally {
            CursorUtils.close(cursor);
        }
        
        return list;
    }
    
    /**
     * Returns a {@link List} of {@link Playlist}s that have been deleted.
     */
    public static List<Playlist> listDeleted(
            Activity activity, Uri contentUri) throws SQLiteException {
        List<Playlist> playlists = list(activity, contentUri);
        
        for (Iterator<Playlist> it = playlists.iterator(); it.hasNext(); ) {
            Playlist playlist = it.next();
            if (!playlist.isDeleted()) {
                it.remove();
            }
        }
        
        return playlists;
    }
    
    private final Uri databaseUri;
    
    private final long id;
    
    private final String path;
    
    private final String name;
    
    public Playlist(Uri databaseUri, long id, String path, String name) {
        this.databaseUri = databaseUri;
        this.id = id;
        this.path = path;
        this.name = name;
    }
    
    public Uri getContentUri() {
        return ContentUris.withAppendedId(databaseUri, id);
    }
    
    public Uri getDatabaseUri() {
        return databaseUri;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isDeleted() {
        return StringUtils.isEmpty(path);
    }
    
    @Override
    public String toString() {
        return name + ", " + path + ", " + id;
    }
}
