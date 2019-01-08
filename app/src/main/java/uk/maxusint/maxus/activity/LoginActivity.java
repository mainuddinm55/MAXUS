package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.AdminResponse;
import uk.maxusint.maxus.network.response.AgentResponse;
import uk.maxusint.maxus.network.response.ClubResponse;
import uk.maxusint.maxus.network.response.UserResponse;
import uk.maxusint.maxus.utils.SharedPref;

public class LoginActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = "LoginActivity";
    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String ADMIN_TYPE = "ADMIN_TYPE";
    public static final String CLUB_TYPE = "CLUB_TYPE";
    public static final String AGENT_TYPE = "AGENT_TYPE";
    public static final String NORMAL_TYPE = "NORMAL_TYPE";

    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.login_btn)
    Button loginButton;

    Unbinder unbinder;

    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        sharedPref = new SharedPref(this);
        if (sharedPref.getUser() != null) {
            Intent intent;
            switch (sharedPref.getUser().getTypeId()) {
                case User.UserType.ROYAL:
                case User.UserType.CLASSIC:
                case User.UserType.PREMIUM:
                    intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                    break;
                case User.UserType.AGENT:
                    intent = new Intent(LoginActivity.this, AgentHomeActivity.class);
                    break;
                case User.UserType.CLUB:
                    intent = new Intent(LoginActivity.this, ClubHomeActivity.class);
                    break;
                case User.UserType.ADMIN:
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    break;
                default:
                    intent = new Intent(LoginActivity.this, LoginActivity.class);
                    break;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);

        apiService = ApiClient.getInstance().getApi();
    }

    @OnClick(R.id.register_btn)
    void gotoRegister() {

    }

    @OnClick(R.id.login_btn)
    void login() {
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailLayout.setError("Email required");
            emailLayout.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailLayout.setError("Email invalid");
            emailLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordLayout.setError("Password required");
            passwordLayout.requestFocus();
            return;
        }
        userLogin();
    }

    private void userLogin() {
        disposable.add(
                apiService.login(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.getError()) {
                                    sharedPref.putUser(userResponse.getUser());
                                    Intent intent;
                                    switch (userResponse.getUser().getTypeId()) {
                                        case User.UserType.ROYAL:
                                        case User.UserType.CLASSIC:
                                        case User.UserType.PREMIUM:
                                            intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                                            break;
                                        case User.UserType.AGENT:
                                            intent = new Intent(LoginActivity.this, AgentHomeActivity.class);
                                            break;
                                        case User.UserType.CLUB:
                                            intent = new Intent(LoginActivity.this, ClubHomeActivity.class);
                                            break;
                                        case User.UserType.ADMIN:
                                            intent = new Intent(LoginActivity.this, MainActivity.class);
                                            break;
                                        default:
                                            intent = new Intent(LoginActivity.this, LoginActivity.class);
                                            break;
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: RxJava " + e.getMessage());
                            }
                        })
        );
    }

    private void agentLogin() {
        disposable.add(
                apiService.agentLogin(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AgentResponse>() {
                            @Override
                            public void onSuccess(AgentResponse agentResponse) {
                                Log.e(TAG, "Error: " + agentResponse.getError());
                                Log.e(TAG, "Message: " + agentResponse.getMessage());
                                Log.e(TAG, "Club: " + agentResponse.getAgent());
                                if (!agentResponse.getError()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void adminLogin() {
        disposable.add(
                apiService.adminLogin(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AdminResponse>() {
                            @Override
                            public void onSuccess(AdminResponse adminResponse) {
                                Log.e(TAG, "Error: " + adminResponse.getError());
                                Log.e(TAG, "Message: " + adminResponse.getMessage());
                                Log.e(TAG, "Admin: " + adminResponse.getAdmin());
                                if (!adminResponse.getError()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void clubLogin() {
        disposable.add(
                apiService.clubLogin(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ClubResponse>() {
                            @Override
                            public void onSuccess(ClubResponse clubResponse) {
                                Log.e(TAG, "Error: " + clubResponse.getError());
                                Log.e(TAG, "Message: " + clubResponse.getMessage());
                                Log.e(TAG, "Club: " + clubResponse.getClub());
                                if (!clubResponse.getError()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        emailLayout.setError(null);
        passwordLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)) {
            emailLayout.setError(null);
            passwordLayout.setError(null);
        }
    }
}
