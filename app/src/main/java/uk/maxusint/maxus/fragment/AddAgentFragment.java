package uk.maxusint.maxus.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAgentFragment extends Fragment {
    public static final String TAG = "AddAgentFragment";
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.mobile_edit_text)
    TextInputEditText mobileEditText;
    @BindView(R.id.club_username_edit_text)
    TextInputEditText clubUsernameEditText;
    @BindView(R.id.club_layout)
    TextInputLayout clubLayout;
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

    private ApiService apiService;

    Unbinder unbinder;
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
            clubLayout.setError(null);
            errTextView.setText(null);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public AddAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_agent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        nameEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        mobileEditText.addTextChangedListener(textWatcher);
        clubUsernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);
        districtEditText.addTextChangedListener(textWatcher);
        apiService = ApiClient.getInstance().getApi();
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
        if (TextUtils.isEmpty(clubUsernameEditText.getText())) {
            clubLayout.setError("Club username required");
            clubLayout.requestFocus();
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
        registerNewClub();
    }

    private void registerNewClub() {
        disposable.add(
                apiService.getIdByUsername(
                        clubUsernameEditText.getText().toString()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                try {
                                    String response = responseBody.string();
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean err = jsonObject.getBoolean("error");
                                    if (!err) {
                                        String msg = jsonObject.getString("message");
                                        //Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onSuccess: " + msg);
                                        Log.e(TAG, "onSuccess: " + jsonObject.getString("club_id"));
                                        int clubId = jsonObject.getInt("club_id");
                                        disposable.add(
                                                apiService.registerNewAgent(
                                                        nameEditText.getText().toString(),
                                                        usernameEditText.getText().toString(),
                                                        emailEditText.getText().toString(),
                                                        mobileEditText.getText().toString(),
                                                        passwordEditText.getText().toString(),
                                                        clubId,
                                                        districtEditText.getText().toString(),
                                                        "",
                                                        ""
                                                ).subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                                                            @Override
                                                            public void onSuccess(ResponseBody responseBody) {
                                                                try {
                                                                    String response = responseBody.string();
                                                                    JSONObject responseJson = new JSONObject(response);
                                                                    boolean err = responseJson.getBoolean("error");
                                                                    String msg = responseJson.getString("message");
                                                                    if (!err) {
                                                                        Toast.makeText(getContext(), responseJson.getString("message"), Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        errTextView.setText(responseJson.getString("message"));
                                                                        errTextView.setVisibility(View.VISIBLE);
                                                                    }
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {
                                                                Log.e(TAG, "onError: " + e.getMessage());
                                                            }
                                                        })
                                        );
                                    } else {
                                        errTextView.setText(jsonObject.getString("message"));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );

    }

}
