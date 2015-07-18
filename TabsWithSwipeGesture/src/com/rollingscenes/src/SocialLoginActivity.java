package com.rollingscenes.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.rollingscenes.R;
import com.rollingscenes.src.asynctasks.GetEvents;
import com.rollingscenes.src.asynctasks.StoreEvents;
import com.rollingscenes.src.db.RSAppDbHelper;
import com.rollingscenes.src.domain.RSEvent;
import com.rollingscenes.src.interfaces.GetEventsLoaderIntf;
import com.rollingscenes.src.interfaces.StoreEventsIntf;
import com.rollingscenes.src.utils.ConnectionDetectorUtils;
import com.rollingscenes.src.utils.KeyConstants;
import com.rollingscenes.src.utils.SharedPrefs;
import com.rollingscenes.src.web.entities.WeSocialLogin;
import com.todddavies.components.progressbar.ProgressWheel;

public class SocialLoginActivity extends Activity implements GetEventsLoaderIntf, StoreEventsIntf {

	private static String TAG = SocialLoginActivity.class.getSimpleName();
	private static final String FACEBOOK = "facebook";
	private static final String GOOGLE = "google";

	/**
	 * Represent the social login service name user logged into.
	 */
	private String socialLoginService;

	/**
	 * Represent the shared preference for this application
	 */
	private SharedPrefs sPrefs;

	/**
	 * Represent the URL of profile photo of user
	 */
	private String profilePhotoUrl;

	// Facebook sdk related properties
	private FacebookLogin facebookLogin;
	private UiLifecycleHelper uiHelper;
	private GraphUser user;
	private static String GRAPH_URL = "https://graph.facebook.com/";
	private static int PROFILE_PICTURE_SIZE = 200;

	// Google sdk related properties
	private GoogleLogin googleLogin;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
    
	private ProgressWheel pw;
	
	private WeSocialLogin socialLogin;
	
	@SuppressLint({"ShowToast", "NewApi"})
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_login);
		
		// Get the shared pref of the application
		sPrefs = SharedPrefs.getInstance(getApplicationContext());

		// Initialize toast object
		final Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);

		pw = (ProgressWheel)findViewById(R.id.pw_spinner);

		// If user is already logged in, redirect him to HomeScreenActivity
		if (sPrefs.isLoggedIn()) {
			Intent intent = new Intent(getApplicationContext(),
					EventListActivity.class);
			startActivity(intent);
			finish();
		}
		
		// If logout is clicked from other activity then, logout user from
		// Social login service
		if (sPrefs.isLogoutClicked()) {
			RSAppDbHelper.getInstance(getApplicationContext()).truncateAllTables();
			((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
			if (sPrefs.getLoggedInWith().equals(FACEBOOK)) {

				// logout user from facebook server
				Session.getActiveSession().close();
				SocialLoginActivity.this.user = null;
				new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
					}
				};
				
				sPrefs.resetSharedPrefs();
			}
		}

		// Remove the action bar
		getActionBar().hide();

		// Represent connection detector object used to find the Internet
		// connectivity of user device
		final ConnectionDetectorUtils connectionDetectorUtils = new ConnectionDetectorUtils(
				getApplicationContext());

		
		socialLogin = new WeSocialLogin();
		
		socialLogin.setUser_id(((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
		
		// Facebook related
		facebookLogin = new FacebookLogin();

		// Initialize the UILifeCycleHelper class, used to handle the facebook
		// login
		uiHelper = new UiLifecycleHelper(SocialLoginActivity.this,
				facebookLogin.callback);
		uiHelper.onCreate(savedInstanceState);
		
		socialLoginService = "";
		// Represent the facebook login button
		final LoginButton btnFacebookLogin = (LoginButton) findViewById(R.id.btn_facebook_login);
		btnFacebookLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (connectionDetectorUtils.isConnectedToInternet()) {
					showProgressWheel();
					socialLoginService = FACEBOOK;
					btnFacebookLogin.setReadPermissions(Arrays.asList("email,user_birthday,user_location"));
					btnFacebookLogin
							.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
								@Override
								public void onUserInfoFetched(GraphUser user) {
									SocialLoginActivity.this.user = user;
									facebookLogin.updateUI();
								}
							});
				} 
				else {
					toast.setText(getResources().getString(R.string.internet_connection_problem));
					toast.show();
				}
			}
		});

		// Google related
		googleLogin = new GoogleLogin();

		// Represent the google login button
		SignInButton btnGoogleLogin = (SignInButton) findViewById(R.id.btn_google_login);
		btnGoogleLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (connectionDetectorUtils.isConnectedToInternet()) {
					showProgressWheel();
					socialLoginService = GOOGLE;
					googleLogin.signInWithGplus();
				} 
				else {
					toast.setText(getResources().getString(R.string.internet_connection_problem));
					toast.show();
				}
			}
		});
		
		/*final ImageView imageView = (ImageView)findViewById(R.id.img_app_icon);
		final LinearLayout buttonLayout = (LinearLayout)findViewById(R.id.lin_buttons);
		buttonLayout.setVisibility(View.GONE);
		
		// load the animation
		Animation animZoomIn1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
		final Animation animZoomIn2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        final Animation animMoveUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_up);
		
        animZoomIn1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            	imageView.clearAnimation();
            	
            	// If user is already logged in, redirect him to HomeScreenActivity
        		if (sPrefs.isLoggedIn()) {
        			Intent intent = new Intent(getApplicationContext(),
        					EventListActivity.class);
        			startActivity(intent);
        			finish();
        		}
        		else {
	                imageView.startAnimation(animMoveUp);
        		}
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        
        animMoveUp.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
                imageView.clearAnimation();
        		buttonLayout.setVisibility(View.VISIBLE);
        		buttonLayout.startAnimation(animZoomIn2);
			}
		});
		imageView.startAnimation(animZoomIn1);*/
		
	}
	
    
	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
		hideProgressWheel();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (socialLoginService.equals(FACEBOOK)) {
				showProgressWheel();
				uiHelper.onActivityResult(requestCode, resultCode, data,
						facebookLogin.dialogCallback);
			} 
			else if (socialLoginService.equals(GOOGLE)) {
				if (resultCode != RESULT_OK) {
					mSignInClicked = false;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
			}
			if(resultCode == RESULT_CANCELED) {
				Log.i(TAG, "Result code : "+resultCode);
			}
	}

	/**
	 * This class is used to establish connection to facebook account of user
	 * and authenticate user using credentials of users facebook account and
	 * check for errors during connection and handle those errors
	 * 
	 * @author Kunal Bhavsar
	 */
	public class FacebookLogin {

		private Session.StatusCallback callback = new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				onSessionStateChange(session, state, exception);
			}
		};

		private void onSessionStateChange(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				Log.i(TAG, "Logged in...");
				if (socialLoginService == null
						|| !socialLoginService.equals(FACEBOOK)) {
					Session.getActiveSession().close();
				}
			} else if (state.isClosed()) {
				Log.i(TAG, "Logged out...");
			}
		}

		private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall,
					Exception error, Bundle data) {
				Log.i("TAG", String.format("Error: %s", error.toString()));
			}

			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall,
					Bundle data) {
				Log.i("TAG", "Success!");
			}
		};

		private void updateUI() {
			final Session session = Session.getActiveSession();
			boolean enableButtons = (session != null && session.isOpened());
			if (enableButtons && user != null) {
				profilePhotoUrl = GRAPH_URL + user.getId() + "/picture?height="
						+ PROFILE_PICTURE_SIZE + "&width="
						+ PROFILE_PICTURE_SIZE;
				
				sPrefs.setLogoutClicked(false);
				sPrefs.setLoggedIn(true);
				sPrefs.setLoggedInWith(socialLoginService);
				socialLogin.setSocial_login_id(user.getId());
				socialLogin.setUser_email_id(user.getProperty("email")+"");
				socialLogin.setSocial_login_service(socialLoginService);
				socialLogin.setUser_name(user.getName());
				socialLogin.setUser_city(user.getLocation() != null ? user.getLocation().getName() : null);
				socialLogin.setUser_dob(user.getBirthday());
/*				new Request(
					    session,
					    "/me/photos",
					    null,
					    HttpMethod.GET,
					    new Request.Callback() {
					        public void onCompleted(Response response) {
					             Gson gson = new Gson();
					             Album album = (Album) response;
					        }
					    }
					).executeAsync();
				
*/
				showProgressWheel();
				new SocialLogin().execute();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		uiHelper.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}
	
	/**
	 * This class is used to establish connection to google+ account of user and
	 * authenticate user using credentials of user google+ account and check for
	 * errors during connection and handle those errors
	 * 
	 * @author Kunal Bhavsar
	 * 
	 */
	class GoogleLogin
			implements
				ConnectionCallbacks,
				OnConnectionFailedListener {
		private ConnectionResult mConnectionResult;
		public GoogleLogin() {
			mGoogleApiClient = new GoogleApiClient.Builder(
					SocialLoginActivity.this).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).addApi(Plus.API)
					.addScope(new Scope(Scopes.PLUS_LOGIN)).build();
		}

		private void signInWithGplus() {
			if (!mGoogleApiClient.isConnecting()) {
				mSignInClicked = true;
				resolveSignInError();
			}
		}

		private void signOutFromGplus() {
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();
			}
		}

		private void resolveSignInError() {
			if (mConnectionResult == null) {
				signOutFromGplus();
			} 
			else if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					startIntentSenderForResult(mConnectionResult
							.getResolution().getIntentSender(), 0, null, 0, 0,
							0);
				} 
				catch (SendIntentException e) {
					mIntentInProgress = false;
					mGoogleApiClient.connect();
				}
			}
		}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			hideProgressWheel();
			if (!mIntentInProgress) {
				mConnectionResult = result;

				if (mSignInClicked) {
					resolveSignInError();
				}
			}
		}

		@Override
		public void onConnected(Bundle arg0) {
			hideProgressWheel();
			if (!sPrefs.isLogoutClicked()
					&& socialLoginService != null
					&& socialLoginService.equals(GOOGLE)) {
				mSignInClicked = false;
				googleLogin.updateGoogleUI();
			} 
			else {
				// logout user from google server
				googleLogin.signOutFromGplus();

				sPrefs.resetSharedPrefs();
			}
		}

		@Override
		public void onConnectionSuspended(int arg0) {
			mGoogleApiClient.connect();
		}

		private void updateGoogleUI() {
			try {
				if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
					Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

					profilePhotoUrl = currentPerson.getImage().getUrl();
					profilePhotoUrl = profilePhotoUrl.substring(0,
							profilePhotoUrl.length() - 2) + PROFILE_PICTURE_SIZE;

					sPrefs.setLogoutClicked(false);
					sPrefs.setLoggedIn(true);
					sPrefs.setLoggedInWith(socialLoginService);
					socialLogin.setSocial_login_id(currentPerson.getId());
					socialLogin.setUser_email_id(Plus.AccountApi.getAccountName(mGoogleApiClient));
					socialLogin.setSocial_login_service(socialLoginService);
					socialLogin.setUser_name(currentPerson.getDisplayName());
					socialLogin.setUser_dob(currentPerson.getBirthday());
					socialLogin.setUser_city(currentPerson.getCurrentLocation());
					
					showProgressWheel();
					new SocialLogin().execute();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				hideProgressWheel();
			} 
		}
	}
	
	private void showProgressWheel() {
		((RelativeLayout)findViewById(R.id.rel_progress_wheel)).setVisibility(View.VISIBLE);
		pw.spin();
	}
	
	private void hideProgressWheel() {
		((RelativeLayout)findViewById(R.id.rel_progress_wheel)).setVisibility(View.INVISIBLE);
		pw.stopSpinning();
	}


	@Override
	public void eventsLoaded(ArrayList<RSEvent> events) {
		
		new StoreEvents(SocialLoginActivity.this, getApplicationContext()).execute(events);
	}


	@Override
	public void errorInLoadingEvents(String error) {
		Log.e(TAG, "Error in loading events");
	}


	@Override
	public void storedSuccessfully() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(SocialLoginActivity.this, EventListActivity.class);
				startActivity(intent);
				hideProgressWheel();
				finish();
			}
		});
	}


	@Override
	public void errorInStoring() {
		Log.e(TAG, "Error in storing events");
	}
	
	private class SocialLogin extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected void onPreExecute() {
			findViewById(R.id.lin_buttons).setVisibility(View.GONE);
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			
			HttpURLConnection connection = null;
			int statusCode = 0;
			BufferedReader reader = null;
			try {
				URL socialLoginUrl = new URL(KeyConstants.BASE_URL_FOR_USER_CONTROLLER + "socialUserLogin");

				connection = (HttpURLConnection)socialLoginUrl.openConnection();
				connection.setRequestMethod(KeyConstants.POST_REQUEST_METHOD);
		        connection.setRequestProperty(KeyConstants.CONTENT_TYPE,KeyConstants.JSON_APPLICATION);
		        connection.setConnectTimeout(15000);
		        connection.setDoOutput(true);
				String requestData = new Gson().toJson(socialLogin);
				Log.i(TAG, "request (SocialLogin) :\n" + requestData);

				// setting the json String as print writer content
				PrintWriter out = new PrintWriter(connection.getOutputStream());
				out.print(requestData);
				out.flush();

				statusCode = connection.getResponseCode();
				if (statusCode == KeyConstants.POST_OK) {
					
					return statusCode;
/*					reader = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));
					String line = "";
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					String responseData = sb.toString();
					//Log.e(TAG, responseData);
					events = new Gson().fromJson(responseData, new TypeToken<ArrayList<WeEvent>>() {}.getType());*/
				}
				else {
					reader = new BufferedReader(new InputStreamReader(
							connection.getErrorStream()));
					String line = "";
					StringBuilder sb = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					Log.e(TAG, "Error occured with status code : "+statusCode+"\n"+sb.toString());
				}
			} 
			catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());
			}
			catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			
			return statusCode;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(result.intValue() == KeyConstants.POST_OK) {
				storeSocialLoginDetails();
				if (new ConnectionDetectorUtils(getApplicationContext()).isConnectedToInternet()) {
					int corePoolSize = 60;
					int maximumPoolSize = 80;
					int keepAliveTime = 10;

					BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
					Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
					new GetEvents(SocialLoginActivity.this, true).executeOnExecutor(threadPoolExecutor, KeyConstants.BASE_URL_FOR_USER_CONTROLLER+"getAllEvents");
				} 
				else {
					Toast.makeText(getApplicationContext(), KeyConstants.INTERNET_CONNECTION_PROBLEM, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	private void storeSocialLoginDetails() {
		
	}
}
