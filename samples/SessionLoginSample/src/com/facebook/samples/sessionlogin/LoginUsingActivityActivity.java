/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.sessionlogin;

import tw.com.funtown.UUID;
import tw.com.funtown.internal.Utility;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;

public class LoginUsingActivityActivity extends Activity {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private Button buttonUUIDBinding;   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        buttonLoginLogout = (Button)findViewById(R.id.buttonLoginLogout);
        buttonUUIDBinding  = (Button)findViewById(R.id.buttonUUIDBinding);           
        textInstructionsOrLink = (TextView)findViewById(R.id.instructionsOrLink);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken() + "&session_key=" + session.getValues().get("session_key") + "&uuid=" + session.getValues().get("uuid"));        	
//            textInstructionsOrLink.setText(URL_PREFIX_FRIENDS + session.getAccessToken());
            buttonLoginLogout.setText(R.string.logout);
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
            buttonUUIDBinding.setClickable(false);              
        } else {
            textInstructionsOrLink.setText(R.string.instructions);
            buttonLoginLogout.setText(R.string.login);
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
            buttonUUIDBinding.setText(R.string.facebookUUIDBinding);
            buttonUUIDBinding.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickUUIDBinding(); }
            });                
        }
    }

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    
    private void onClickUUIDBinding() {
    	UUID uuid= UUID.getInstance(this);
    	final Activity ac = this;
    	uuid.generateUUID(new UUID.OnUUIDGeneratedListener() {			
			@Override
			public void onUUIDGenerated(String UUID) {
	            textInstructionsOrLink.setText("UUID : "+UUID);
	            String facebookBindingRedirectUri="";
	            String facebookApplicationId="";
	            try {
	                ApplicationInfo ai = ac.getPackageManager().getApplicationInfo(
	                        ac.getPackageName(), PackageManager.GET_META_DATA);
	                if (ai.metaData != null) {
	                	facebookBindingRedirectUri= ai.metaData.getString("com.facebook.sdk.BindingRedirectUri");
	                	facebookApplicationId= ai.metaData.getString("com.facebook.sdk.ApplicationId");
	                }
	            } catch (PackageManager.NameNotFoundException e) {
	            }	            
	            
	            Session session = Session.getActiveSession();
	            Uri redirectUri = Uri.parse(facebookBindingRedirectUri);
	            Bundle parameters= new Bundle();
	            
	            parameters.putString("provider", "facebook");
	            parameters.putString("client_id", facebookApplicationId);		            
	            parameters.putString("uuid", UUID);
	            
	            Uri redirectUriWithUUID=Utility.buildUri(redirectUri.getAuthority(), redirectUri.getPath(), parameters);

	            
	            if (!session.isOpened() && !session.isClosed()) {
	                session.openForReadWithRedirectUri(new Session.OpenRequest(ac).setCallback(statusCallback), redirectUriWithUUID.toString());
	            } else {
	                Session.openActiveSessionWithRedirectUri(ac,  redirectUriWithUUID.toString() , true, statusCallback);
	            }	            
			}     		
    	});
    }
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }
}
