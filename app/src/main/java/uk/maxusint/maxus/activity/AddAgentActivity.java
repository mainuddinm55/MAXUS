package uk.maxusint.maxus.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import uk.maxusint.maxus.utils.SharedPref;

public class AddAgentActivity extends AppCompatActivity {
    private static final String TAG = "AddAgentActivity";
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.mobile_edit_text)
    TextInputEditText mobileEditText;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.confirm_password_edit_text)
    TextInputEditText confirmPasswordEditText;
    @BindView(R.id.district_edit_text)
    TextInputEditText districtEditText;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.mobile_layout)
    TextInputLayout mobileLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.confirm_password_layout)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.err_text_view)
    TextView errTextView;
    @BindView(R.id.district_layout)
    TextInputLayout districtLayout;
    @BindView(R.id.upazilla_layout)
    TextInputLayout upazillaLayout;
    @BindView(R.id.upazilla_edit_text)
    TextInputEditText upazillaEditText;
    @BindView(R.id.up_layout)
    TextInputLayout upLayout;
    @BindView(R.id.up_edit_text)
    TextInputEditText upEditText;

    private ApiService apiService;

    User currentUser;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameLayout.setError(null);
            usernameLayout.setError(null);
            emailLayout.setError(null);
            mobileLayout.setError(null);
            passwordLayout.setError(null);
            confirmPasswordLayout.setError(null);
            districtLayout.setError(null);
            errTextView.setText(null);
            upazillaLayout.setError(null);
            upLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        mobileEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);
        districtEditText.addTextChangedListener(textWatcher);
        upazillaEditText.addTextChangedListener(textWatcher);
        upEditText.addTextChangedListener(textWatcher);
        apiService = ApiClient.getInstance().getApi();
        currentUser = new SharedPref(this).getUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.register_btn)
    void registerClub() {
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameLayout.setError("Name required");
            nameLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(usernameEditText.getText())) {
            usernameLayout.setError("username required");
            usernameLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailLayout.setError("Email required");
            emailLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileEditText.getText())) {
            mobileLayout.setError("Mobile required");
            mobileLayout.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailLayout.setError("Email is invalid");
            emailLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordLayout.setError("Password required");
            passwordLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPasswordEditText.getText())) {
            confirmPasswordLayout.setError("Confirm password required");
            confirmPasswordLayout.requestFocus();
            return;
        }
        if (!passwordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
            confirmPasswordLayout.setError("Password does not match");
            confirmPasswordLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(districtEditText.getText())) {
            districtLayout.setError("District required");
            districtLayout.requestFocus();
            return;
        }
        showSpinner();
        String upazilla = upazillaEditText.getText().toString();
        String up = upEditText.getText().toString();
        if (currentUser != null) {
            registerAgent(nameEditText.getText().toString(), usernameEditText.getText().toString(),
                    emailEditText.getText().toString(), mobileEditText.getText().toString(), passwordEditText.getText().toString(), currentUser.getUserId()
                    , districtEditText.getText().toString(), upazilla, up);
        }
    }

    private void registerAgent(String name, String username, String email, String mobile, String password, int clubId, String district, String upazilla, String up) {
        showSpinner();
        disposable.add(
                apiService.registerNewAgent(
                        name,
                        username,
                        email,
                        mobile,
                        password,
                        clubId,
                        district,
                        upazilla,
                        up
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                try {
                                    dismissSpinner();
                                    String response = responseBody.string();
                                    JSONObject responseJson = new JSONObject(response);
                                    boolean err = responseJson.getBoolean("error");
                                    String msg = responseJson.getString("message");
                                    if (!err) {
                                        Toast.makeText(AddAgentActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    } else {
                                        errTextView.setText(msg);
                                        errTextView.setVisibility(View.VISIBLE);
                                    }
                                } catch (IOException e) {
                                    dismissSpinner();
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    dismissSpinner();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissSpinner();
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );


    }

    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading......");
        progressDialog.setMessage("Please wait.......");
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void dismissSpinner() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
