package com.coupleface;

public class Facecom {
	private String appID="f57d16358ae09a316a71464b5b911478";
	private String appSecret="faec6475e2cee135bb10dd6e793ac383";
	private String userAuth;
	private String uids;
	private String namespace;
	private String fbToken;
	private String detector;
	private String callbackUrl;
	private String detecUrl;
	
	public Facecom(){
		detector="Aggressive";
	}
	
	public Facecom(String id,String secret){
		appID=id;
		appSecret=secret;
		detector="Aggressive";
	}
	
	public String getTrainReq(){
		StringBuilder req= new StringBuilder();
		req.append("http://api.face.com/faces/train.json?api_key="+appID+"&api_secret="+appSecret);
		req.append("&uids="+uids);
		req.append("&namespace="+namespace);
		req.append("&callback_url="+callbackUrl);
		req.append("&user_auth=fb_user:"+userAuth+",fb_oauth_token:"+fbToken);
		return req.toString();
	}
	
	public String getRecognizeReq(){
		StringBuilder req= new StringBuilder();
		req.append("http://api.face.com/faces/recognize.json?api_key="+appID+"&api_secret="+appSecret);
		req.append("&urls="+detecUrl);
		req.append("&uids="+uids);
		req.append("&namespace="+namespace);
		req.append("&detecor="+detector);
		req.append("&attributes=all");
		req.append("&user_auth=fb_user:"+userAuth+",fb_oauth_token:"+fbToken);
		return req.toString();
	}

	public String getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(String userAuth) {
		this.userAuth = userAuth;
	}

	public String getUids() {
		return uids;
	}

	public void setUids(String uids) {
		this.uids = uids;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getFbToken() {
		return fbToken;
	}

	public void setFbToken(String fbToken) {
		this.fbToken = fbToken;
	}

	public String getDetector() {
		return detector;
	}

	public void setDetector(String detector) {
		this.detector = detector;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getDetecUrl() {
		return detecUrl;
	}

	public void setDetecUrl(String detecUrl) {
		this.detecUrl = detecUrl;
	}
	
	
}
