package com.coupleface;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.coupleface.CoupleFaceActivity.UserPicListener;
import com.coupleface.SessionEvents.AuthListener;
import com.coupleface.SessionEvents.LogoutListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.R;
import com.facebook.android.Util;

public class LoginActivity extends Activity{
	  public static final String APP_ID = "304916812868029";
	  private static final String TAG = "LoginActivity";
	    private LoginButton mLoginButton;
	    private TextView mText;
	    private ImageView mUserPic;
	    private Handler mHandler;
		ProgressDialog dialog;
		private Button BackButton;
		Intent CoupleFaceIntent; 
		private boolean isInit;
		FbAPIsAuthListener fbAPIsAuthListener;
		FbAPIsLogoutListener fbAPIsLogoutListener;
		final int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
		final int PICK_EXISTING_PHOTO_RESULT_CODE = 1;

	    String[] permissions = {"offline_access", "publish_stream", "user_photos", "publish_checkins", "photo_upload"};
		
	    @Override
	    public void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        
	        if (APP_ID == null) {
	            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
	                    "specified before running this example: see FbAPIs.java");
	            return;
	        }

	        setContentView(R.layout.login);
	        mHandler = new Handler();
	        
	        mText = (TextView) LoginActivity.this.findViewById(R.id.txt);
	        mUserPic = (ImageView)LoginActivity.this.findViewById(R.id.user_pic);
	        CoupleFaceIntent = new Intent().setClass(LoginActivity.this, CoupleFaceActivity.class);
	        
	        //Create the Facebook Object using the app id.
	       	Utility.mFacebook = new Facebook(APP_ID);
	       	//Instantiate the asynrunner object for asynchronous api calls.
	       	Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

	       	mLoginButton = (LoginButton) findViewById(R.id.login);
	       	BackButton = (Button)findViewById(R.id.back);
	       	BackButton.setOnClickListener(back_listener);
	       	//restore session if one exists
	       	
	       	fbAPIsAuthListener = new FbAPIsAuthListener();
	       	fbAPIsLogoutListener = new FbAPIsLogoutListener();
	        SessionStore.restore(Utility.mFacebook, this);
	        SessionEvents.addAuthListener(fbAPIsAuthListener);
	        SessionEvents.addLogoutListener(fbAPIsLogoutListener);
	        
	        /*
			 * Source Tag: login_tag
			 */
	        mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook, permissions);
	        
	    	Bundle bundle = this.getIntent().getExtras();
	    	if (bundle.getString("isInit").equals("true")) {
	    		isInit=true;
	    	}else{
	    		isInit=false;
	    	}
	        
	       	if(Utility.mFacebook.isSessionValid()) {
	       		requestUserData();
	       	}
	        
	    }
	    
	    
	    /*
	     * The Callback for notifying the application when authorization
	     *  succeeds or fails.
	     */
	    
	    public class FbAPIsAuthListener implements AuthListener {

	        public void onAuthSucceed() {
	        	requestUserData();
	        }

	        public void onAuthFail(String error) {
	            mText.setText("Login Failed: " + error);
	        }
	    }

	    /*
	     * The Callback for notifying the application when log out
	     *  starts and finishes.
	     */
	    public class FbAPIsLogoutListener implements LogoutListener {
	        public void onLogoutBegin() {
	            mText.setText("Logging out...");
	        }

	        public void onLogoutFinish() {
		    	setButtonEnable(1);
	            mText.setText("You have logged out! ");
	            mUserPic.setImageBitmap(null);
	        }
	    }
	    
	    public void initCoupleFace() {
	    	if (isInit) {
	    		setButtonEnable(0);
	    		LoginActivity.this.finish();
	    		startActivity(CoupleFaceIntent);
	    	}else{
	    		setButtonEnable(1);
	    	}
	    }
	    
	    public void initCoupleFaceNochcek() {
	    	setButtonEnable(0);
    		LoginActivity.this.finish();
	    	startActivity(CoupleFaceIntent);
	    }
	    
	    private OnClickListener back_listener = new OnClickListener() {
	        public void onClick(View v) {
	        	initCoupleFaceNochcek();
	        }
	    };
	    
	    public void setButtonEnable(final int i) {
	    	// 0 is enable ; 1 is unable
	    	mHandler.post(new Runnable() {
	            public void run() {
	            	BackButton.setVisibility(i);
	            }
	    	});
	    }
	    
	    /*
	     * Request user name, and picture to show on the main screen.
	     */
	    public void requestUserData() {
	    	mText.setText("Fetching user name, profile pic...");
	    	Bundle params = new Bundle();
	    	params.putString("fields", "name, picture");
	    	Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	    }
	    
	    /*
	     * Callback for fetching current user's name, picture, uid.
	     */
	    public class UserRequestListener extends BaseRequestListener {

	        public void onComplete(final String response, final Object state) {
	        	JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					
		        	final String picURL = jsonObject.getString("picture");
		        	final String name = jsonObject.getString("name");
		        	Utility.userUID = jsonObject.getString("id");
		        	
		        	mHandler.post(new Runnable() {
		                public void run() {
		                	mText.setText("Welcome " + name + "!");
		    	        	mUserPic.setImageBitmap(Utility.getBitmap(picURL));
		                }
		            });

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(Exception ee){
					
				}
	       		initCoupleFace();
	        }

	    }
	    
	    @Override
	    public void onDestroy()
	    {
	        super.onDestroy();
	        SessionEvents.removeLogoutListener(fbAPIsLogoutListener);
	        SessionEvents.removeAuthListener(fbAPIsAuthListener);
	        Log.v(TAG,"onDestroy");
	    }
	    
}
