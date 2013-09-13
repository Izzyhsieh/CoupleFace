package com.coupleface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Facebook.DialogListener;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CoupleFaceActivity extends Activity {
	private TextView textInfo;
	private ImageView photo;
	private Handler mHandler;
	private int clickSex=1;
	private JSONObject theObject;
	private int count;
	private int cof=0;
    private static final String TAG = "CoupleFaceActivity";
    private Button post;
    private String postMsg;
    
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        textInfo=(TextView) CoupleFaceActivity.this.findViewById(R.id.info);
        photo= (ImageView) CoupleFaceActivity.this.findViewById(R.id.photo);
        mHandler = new Handler();
        
        final RadioButton radio_male = (RadioButton) findViewById(R.id.male);
        final RadioButton radio_female = (RadioButton) findViewById(R.id.female);
//        final RadioButton radio_both = (RadioButton) findViewById(R.id.both);
        final Button search=(Button)findViewById(R.id.search);
        post=(Button)findViewById(R.id.post);
        
        radio_male.setOnClickListener(radio_listener);
        radio_female.setOnClickListener(radio_listener);
//        radio_both.setOnClickListener(radio_listener);
        search.setOnClickListener(button_listener);
        post.setOnClickListener(button_post_listener);
        trainData();
    }
    
    
    ////////////////widget////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.change_user:
        	backLogin();
            return true;
        case R.id.exit:
        	CoupleFaceActivity.this.finish();
            return true;
        case R.id.about:
//            showHelp();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private OnClickListener radio_listener = new OnClickListener() {
        public void onClick(View v) {
        	cof=0;
        	theObject=null;
            // Perform action on clicks
            RadioButton rb = (RadioButton) v;
            if(rb.getId()==R.id.male){
            	clickSex=1;
            }else if (rb.getId()==R.id.female){
            	clickSex=2;            	
            }
//            else if (rb.getId()==R.id.both){
//            	clickSex=3;
//            }
        }
    };
    
    private OnClickListener button_listener = new OnClickListener() {
        public void onClick(View v) {
        	String query = "SELECT pic_big FROM user WHERE uid="+Utility.userUID;
            queryFQL(query,new UserPicListener());
        }
    };
    
    private OnClickListener button_post_listener = new OnClickListener() {
        public void onClick(View v) {
        	postOnWall(postMsg);
        }
    };
    
    public void postOnWall(String msg) {
         try {
                String response = Utility.mFacebook.request("me");
                Bundle parameters = new Bundle();
                parameters.putString("message", msg);
                response = Utility.mFacebook.request("me/feed", parameters, "POST");
                if (response == null || response.equals("") || response.equals("false")) {

                }else{
        			showToast(getString(R.string.postdone));
                }
         } catch(Exception e) {
             e.printStackTrace();
         }
    }
    
    public void setInfoText(final String msg) {
    	postMsg=getString(R.string.postmsg)+msg+"\n"+getString(R.string.postmsg2);
    	mHandler.post(new Runnable() {
            public void run() {
            	textInfo.setText(msg);
            }
    	});
    }
    
    public void setPhoto(final Bitmap pic) {
    	mHandler.post(new Runnable() {
            public void run() {
            	photo.setImageBitmap(pic);
            }
    	});
    	
    	mHandler.post(new Runnable() {
            public void run() {
            	post.setVisibility(0);
            }
    	});
   
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	CoupleFaceActivity.this.finish();
			return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void backLogin(){
    	CoupleFaceActivity.this.finish();
		Intent login  = new Intent().setClass(CoupleFaceActivity.this, LoginActivity.class);
	    Bundle bundle = new Bundle();
		bundle.putString("isInit", "false");
		login.putExtras(bundle);
		startActivity(login);
    }
    ////////////////////////////////////////////////////////////
    
    /////////////////////////FQL////////////////////////////////
    private void queryFQL(String query, BaseRequestListener listen){
    	Bundle params = new Bundle();
    	params.putString("method", "fql.query");
    	params.putString("query", query);
    	Utility.mAsyncRunner.request(null, params, listen);
    }
        
	public class ObjPicListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	JSONObject jsonObject;
    		try {
    			jsonObject = new JSONArray(response).getJSONObject(0);
                String url = jsonObject.getString("pic_big");
                setPhoto(Utility.getBitmap(url));
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        }
        
	    public void onFacebookError(FacebookError error) {
	        Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	    }
    }
    
	public class UserPicListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	JSONObject jsonObject;
    		try {
    			jsonObject = new JSONArray(response).getJSONObject(0);
                String url = jsonObject.getString("pic_big");
                detect(url);
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        }
        
	    public void onFacebookError(FacebookError error) {
	        Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	    }
    }
	
	public class UserSexListener extends BaseRequestListener {
		private JSONObject obj;
		
		public UserSexListener(JSONObject jsonObject){
			obj=jsonObject;
		}

        public void onComplete(final String response, final Object state) {
        	JSONObject jsonObject;
    		try {
    			jsonObject = new JSONArray(response).getJSONObject(0);
    			filterSex(jsonObject,obj);
    			countDown();
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }        
	    public void onFacebookError(FacebookError error) {
	        Toast.makeText(getApplicationContext(), "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
	    }
    }
	/////////////////////////////////////////////////////////
	
	////////////////////work flow/////////////////////////////
	
	private void detect(String url){
    	Facecom api= new Facecom();
    	api.setNamespace("facebook.com");
    	api.setUids("friends");
    	api.setDetecUrl(url);
    	api.setUserAuth(Utility.userUID);
    	api.setFbToken(Utility.mFacebook.getAccessToken());
//    	setInfoText(api.getRecognizeReq());
    	parseRecognizeResp(useApi(api.getRecognizeReq()));
	}
    
    private void trainData(){
    	Facecom api= new Facecom();
    	api.setCallbackUrl('/'+"TW"+'/'+"Facecouple.html");
    	api.setNamespace("facebook.com");
    	api.setUids("friends");
    	api.setUserAuth(Utility.userUID);
    	api.setFbToken(Utility.mFacebook.getAccessToken());
//    	setInfoText( parseTrainResp(useApi(api.getTrainReq())));
    }
    
    private void parseRecognizeResp(String response) {
    	JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			JSONArray uids = jsonObject.getJSONArray("photos").getJSONObject(0).getJSONArray("tags").getJSONObject(0).getJSONArray("uids");
			getTheObj(uids);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	private void parseRecognizeResp2() {
		try {
			if (theObject != null) {
				String uid = this.theObject.getString("uid").split("@")[0];
				String query = "SELECT pic_big FROM user WHERE uid=" + uid;
				queryFQL(query, new ObjPicListener());
				setCof(cof);
			} else {
				setInfoText(getString(R.string.none));
				setPhoto(null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    private void setCof(int cof){
    	if(cof>=70){
    		setInfoText(getString(R.string.cof1_1)+String.valueOf(cof)+getString(R.string.cof2_1));
    	}else if (cof >40 && cof <70){
    		setInfoText(getString(R.string.cof1_2)+String.valueOf(cof)+getString(R.string.cof2_2));
    	}else{
    		setInfoText(getString(R.string.cof1_3)+String.valueOf(cof)+getString(R.string.cof2_3));
    	}
    }
    private void getTheObj(JSONArray uids){
    	try{
    	if(this.clickSex==3){
    		setTheObject(uids.getJSONObject(0));
			parseRecognizeResp2();
    	}else{
    		setCount(uids.length());
    		for(int i=uids.length()-1;i>-1;i--){
    			String uid=uids.getJSONObject(i).getString("uid").split("@")[0];
            	String query = "SELECT sex FROM user WHERE uid="+uid;
    	        Log.v(TAG, "cof="+uids.getJSONObject(i).getString("confidence"));
//            	setInfoText(uids.getJSONObject(i).getString("confidence"));
                queryFQL(query,new UserSexListener(uids.getJSONObject(i)));
    		}
    	}
    	}catch (JSONException e){
    		
    	}
    }
    
	private void filterSex(JSONObject jsonObject,JSONObject obj){
		String sex;
		try {
			sex = jsonObject.getString("sex");
		if(this.clickSex==1){
			if(sex.equals("male")){
				setTheObject(obj);
			}
		}else if(this.clickSex==2){
			if(sex.equals("female")){
				setTheObject(obj);
			}
		}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
//    private String parseTrainResp(String response) {
//    	JSONObject jsonObject;
//		try {
//			jsonObject = new JSONObject(response);
//            String status = jsonObject.getString("status");
//        	if(status.equals("success")){
//        		return "success";
//        	}else{
//        		return jsonObject.toString();
//        	}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return response;
//    }
    
    private String useApi(String url){        
        try {
        	StringBuilder sb = new StringBuilder();
            URL link = new URL(url);
            URLConnection tc = link.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    tc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null; 
    }

	private void setTheObject(JSONObject theObject) {
		int tmp;
		try {
			tmp = Integer.valueOf(theObject.getString("confidence"));
			if (tmp > cof) {
				cof=tmp;
				this.theObject = theObject;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setCount(int count) {
		this.count = count;
	}
	
	private void countDown(){
		this.count--;
		if(count==0){
			parseRecognizeResp2();
		}
	}
	private void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

}