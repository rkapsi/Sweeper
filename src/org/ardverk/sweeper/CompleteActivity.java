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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CompleteActivity extends Activity {
    
    public static enum Reason {
        COMPLETE(R.string.complete_default_message),
        NOTHING_FOUND(R.string.complete_nothing_message);
        
        private final int resourceId;
        
        private Reason(int resourceId) {
            this.resourceId = resourceId;
        }
        
        public Intent putExtra(Intent intent) {
            intent.putExtra(Reason.class.getName(), name());
            return intent;
        }
        
        String stringValue(Context context) {
            return context.getString(resourceId);
        }
        
        static Reason valueOf(Intent intent) {
            String name = intent.getStringExtra(Reason.class.getName());
            return valueOf(name != null ? name : COMPLETE.name());
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete);
        
        TextView message = (TextView)findViewById(R.id.complete_message);
        
        Reason reason = Reason.valueOf(getIntent());
        message.setText(reason.stringValue(this));
        
        Button button = (Button)findViewById(R.id.complete_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
