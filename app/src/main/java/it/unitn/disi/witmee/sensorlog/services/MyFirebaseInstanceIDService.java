package it.unitn.disi.witmee.sensorlog.services;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;

import it.unitn.disi.witmee.sensorlog.R;
import it.unitn.disi.witmee.sensorlog.activities.ProjectActivity;
import it.unitn.disi.witmee.sensorlog.application.iLogApplication;
import it.unitn.disi.witmee.sensorlog.utils.Utils;

/**
 * Class that extends FirebaseInstanceIdService and is used when Firebase refreshes the firebasetoken.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * @param firebaseToken The new token.
     */
    private void sendRegistrationToServer(final String firebaseToken) {

        try {
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(iLogApplication.getAppContext(), iLogApplication.gso);
            googleSignInClient.silentSignIn()
                    .addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                String idToken = account.getIdToken();

                                new HttpAsyncTask().execute(idToken, firebaseToken);
                            } catch (ApiException e) {
                                e.printStackTrace();
                                if(e.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED && !iLogApplication.sharedPreferences.getString(Utils.CONFIG_PROJECTDATA, "").equals("")) {
                                    iLogApplication.startSignInActivity();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Class that extends AsyncTask and sends the new user firebase token to the server
     */
    public class HttpAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... data) {

            ArrayList<String> returns = new ArrayList<String>();
            returns.add(data[0]);//googletoken
            returns.add(data[1]);//firebasetoken
            returns.add(REFRESHTOKEN(data[0], data[1], "refreshgcmtoken"));
            return returns;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<String> result) {
            Log.d(this.getClass().getSimpleName(), result.get(2));
        }
    }

    /**
     * Method that uses Http GET to push the new firebase token to the server
     * @param token is a String that contains the user token as generated by {@link GoogleSignInClient}
     * @param firebaseToken String with the newly generated token to be pushed
     * @param endPoint String URL of the endpoint where to push the token
     * @return String containing the response from the server if everything went fine, "error" otherwise
     */
    public static String REFRESHTOKEN(String token, String firebaseToken, String endPoint) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("token", token);
        headers.set("firebasetoken", firebaseToken);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Utils.returnServerUrl() + endPoint);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpEntity<String> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
