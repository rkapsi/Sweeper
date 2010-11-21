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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button)findViewById(R.id.sweep);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweep();
            }
        });
    }
    
    private void sweep() {
        CheckBox externalCheckBox = (CheckBox)findViewById(R.id.external);
        CheckBox internalCheckBox = (CheckBox)findViewById(R.id.internal);
        
        boolean external = externalCheckBox.isChecked();
        boolean internal = internalCheckBox.isChecked();
        if (!external && !internal) {
            return;
        }
        
        Intent sweep = new Intent(this, SweepActivity.class);
        sweep.putExtra("external", external);
        sweep.putExtra("internal", internal);
        
        startActivity(sweep);
        finish();
    }
}