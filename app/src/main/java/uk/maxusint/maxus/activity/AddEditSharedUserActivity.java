package uk.maxusint.maxus.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.response.DefaultResponse;

public class AddEditSharedUserActivity extends AppCompatActivity {
    private static final String TAG = "AddEditSharedUserActivi";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.mobile_layout)
    TextInputLayout mobileLayout;
    @BindView(R.id.mobile_edit_text)
    TextInputEditText mobileEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.confirm_password_layout)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.confirm_password_edit_text)
    TextInputEditText confirmPasswordEditText;
    @BindView(R.id.district_layout)
    TextInputLayout districtLayout;
    @BindView(R.id.district_edit_text)
    TextInputEditText districtEditText;
    @BindView(R.id.upazilla_layout)
    TextInputLayout upazillaLayout;
    @BindView(R.id.upazilla_edit_text)
    TextInputEditText upazillaEditText;
    @BindView(R.id.up_layout)
    TextInputLayout upLayout;
    @BindView(R.id.up_edit_text)
    TextInputEditText upEditText;
    @BindView(R.id.shared_percent_layout)
    TextInputLayout sharedPercentLayout;
    @BindView(R.id.shared_percent_edit_text)
    TextInputEditText sharedPercentEditText;

    @BindView(R.id.err_text_view)
    TextView errTextView;

    @BindView(R.id.register_btn)
    Button registerBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_shared_user);
        ButterKnife.bind(this);

        apiService = ApiClient.getInstance().getApi();
    }

    @OnClick(R.id.register_btn)
    void register() {
        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameLayout.setError("Name required");
            nameLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(usernameEditText.getText())) {
            usernameLayout.setError("Username required");
            usernameLayout.requestFocus();
            return;
        }
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
        if (TextUtils.isEmpty(mobileEditText.getText())) {
            mobileLayout.setError("Mobile required");
            mobileLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordLayout.setError("Password required");
            passwordLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPasswordEditText.getText())) {
            confirmPasswordLayout.setError("Comfirm password required");
            confirmPasswordLayout.requestFocus();
            return;
        }
        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            passwordLayout.setError("Password does not match");
            confirmPasswordLayout.setError("Password does not match");
            passwordLayout.requestFocus();
            confirmPasswordLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(districtEditText.getText())) {
            districtLayout.setError("District required");
            districtLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(sharedPercentEditText.getText())) {
            sharedPercentLayout.setError("Shared Percent required");
            sharedPercentLayout.requestFocus();
            return;
        }
        addSharedUser(nameEditText.getText().toString(),
                usernameEditText.getText().toString(),
                emailEditText.getText().toString(),
                mobileEditText.getText().toString(),
                districtEditText.getText().toString(),
                upazillaEditText.getText().toString(),
                upEditText.getText().toString(),
                Double.parseDouble(sharedPercentEditText.getText().toString())
        );
    }

    private void addSharedUser(String name, String username, String email, String mobile, String district,
                               String upazilla, String up, double sharedPercent) {
        showSpinner();
        disposable.add(
                apiService.createSharedUser(name, username, email, mobile, district, upazilla, up, sharedPercent)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse response) {
                                dismissSpinner();
                                if (!response.isError()) {
                                    Toasty.success(AddEditSharedUserActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else {
                                    Toasty.error(AddEditSharedUserActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissSpinner();
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void dismissSpinner() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait....");
        progressDialog.show();
    }
}
