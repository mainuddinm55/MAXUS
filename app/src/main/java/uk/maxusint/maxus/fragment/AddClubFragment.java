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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddClubFragment extends Fragment {
    public static final String TAG = "AddClubFragment";
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

    Unbinder unbinder;

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
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public AddClubFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_club, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        nameEditText.addTextChangedListener(textWatcher);
        usernameEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        mobileEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);
        districtEditText.addTextChangedListener(textWatcher);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ApiService apiService = ApiClient.getInstance().getApi();
                disposable.add(
                        apiService.createClub(
                                nameEditText.getText().toString(),
                                usernameEditText.getText().toString(),
                                emailEditText.getText().toString(),
                                mobileEditText.getText().toString(),
                                passwordEditText.getText().toString(),
                                districtEditText.getText().toString(),
                                "",
                                "")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                                    @Override
                                    public void onSuccess(ResponseBody responseBody) {
                                        try {
                                            String response = responseBody.string();
                                            JSONObject jsonObject = new JSONObject(response);
                                            String msg = jsonObject.getString("message");
                                            errTextView.setText(msg);
                                            errTextView.setVisibility(View.VISIBLE);
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
        });
    }


}
