package org.ardverk.sweeper;

import android.content.ContentUris;
import android.net.Uri;

class Playlist {

    private final Uri databaseUri;
    
    private final long id;
    
    private final String name;

    private boolean checked = true;
    
    public Playlist(Uri databaseUri, long id, String name) {
        this.databaseUri = databaseUri;
        this.id = id;
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

    public String getName() {
        return name;
    }
    
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
