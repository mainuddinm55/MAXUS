package uk.maxusint.maxus.fragment;


import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements TextWatcher {
    public static final String TAG = "AddUserFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private Context context;
    private ApiService apiService;
    private FragmentLoader fragmentLoader;

    @BindView(R.id.name_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.username_edit_text)
    TextInputEditText usernameEditText;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.email_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.email_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.mobile_edit_text)
    TextInputEditText mobileEditText;
    @BindView(R.id.mobile_layout)
    TextInputLayout mobileLayout;
    @BindView(R.id.reference_edit_text)
    TextInputEditText referenceEditText;
    @BindView(R.id.reference_layout)
    TextInputLayout referenceLayout;
    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.confirm_password_edit_text)
    TextInputEditText confirmPasswordEditText;
    @BindView(R.id.confirm_password_layout)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.pin_edit_text)
    TextInputEditText pinEditText;
    @BindView(R.id.pin_layout)
    TextInputLayout pinLayout;
    @BindView(R.id.district_edit_text)
    TextInputEditText districtEditText;
    @BindView(R.id.district_layout)
    TextInputLayout districtLayout;
    @BindView(R.id.user_type_spinner)
    Spinner userTypeSpinner;
    @BindView(R.id.err_text_view)
    TextView errorTextView;
    @BindView(R.id.register_btn)
    Button registerBtn;

    Unbinder unbinder;

    private int userTypeId = 0;
    private String userType;
    private String[] userTypes = new String[]{"Select user type", "Royal", "Classic", "Premium"};
    private int agentId;
    private int pinId;

    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);

        nameEditText.addTextChangedListener(this);
        usernameEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);
        mobileEditText.addTextChangedListener(this);
        referenceEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        confirmPasswordEditText.addTextChangedListener(this);
        pinEditText.addTextChangedListener(this);
        districtEditText.addTextChangedListener(this);

        apiService = ApiClient.getInstance().getApi();

        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(context, R.layout.dropdown_spinner_item, R.id.text_view_list_item, userTypes);
        userTypeSpinner.setAdapter(userTypeAdapter);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = (String) parent.getItemAtPosition(position);
                if (userType.equals("Royal") || userType.equals("Classic")) {
                    pinLayout.setVisibility(View.VISIBLE);
                } else {
                    pinLayout.setVisibility(View.GONE);
                }

                switch (userType) {
                    case "Royal":
                        userTypeId = 1;
                        break;
                    case "Classic":
                        userTypeId = 2;
                        break;
                    case "Premium":
                        userTypeId = 3;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        nameLayout.setError(null);
        usernameLayout.setError(null);
        emailLayout.setError(null);
        mobileLayout.setError(null);
        referenceLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        pinLayout.setError(null);
        districtLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        fragmentLoader = (FragmentLoader) context;
    }

    @OnClick(R.id.register_btn)
    void userRegister() {
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
        if (TextUtils.isEmpty(emailEditText.getText().toString())) {
            emailLayout.setError("Email required");
            emailLayout.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailLayout.setError("Email is invalid");
            emailLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mobileEditText.getText())) {
            mobileLayout.setError("Mobile required");
            mobileLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(referenceEditText.getText())) {
            referenceLayout.setError("Reference required");
            referenceLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordLayout.setError("Password required");
            passwordLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPasswordEditText.getText()) ||
                !(passwordEditText.getText().toString().trim().equals(passwordEditText.getText().toString().trim()))) {
            confirmPasswordLayout.setError("Password does not match");
            confirmPasswordLayout.requestFocus();
            return;
        }

        if (userType.equals("Royal") || userType.equals("Classic")) {
            if (TextUtils.isEmpty(pinEditText.getText())) {
                pinLayout.setError("Pin required");
                pinLayout.requestFocus();
                return;
            }

        }
        if (TextUtils.isEmpty(pinEditText.getText())) {
            districtLayout.setError("District required");
            districtLayout.requestFocus();
            return;
        }
        if (userTypeId == 0) {
            Toast.makeText(context, "Select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        registerNewUser();


    }

    private void registerNewUser() {
        disposable.add(
                apiService.getAgentIdByReference(
                        referenceEditText.getText().toString()
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
                                        agentId = jsonObject.getInt("agent_id");
                                        if (userTypeId == 1 || userTypeId == 2) {
                                            boolean isValid = checkSecurityPin(pinEditText.getText().toString());
                                            if (isValid) {
                                                registerUser();
                                            } else {
                                                pinLayout.setError("Pin is invalid");
                                            }
                                        } else {
                                            registerUser();
                                        }
                                    } else {
                                        referenceLayout.setError(jsonObject.getString("message"));
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

    private void registerUser() {
        float tradeBalance = 0;
        if (userTypeId == 1) {
            tradeBalance = 30;
        } else if (userTypeId == 2) {
            tradeBalance = 100;
        }
        if (userTypeId != 3) {
            disposable.add(
                    apiService.registerNewUser(
                            nameEditText.getText().toString(),
                            usernameEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            mobileEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            referenceEditText.getText().toString(),
                            agentId,
                            districtEditText.getText().toString(),
                            "",
                            "",
                            userTypeId,
                            pinId,
                            tradeBalance
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                                @Override
                                public void onSuccess(ResponseBody responseBody) {
                                    try {
                                        String response = responseBody.string();
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean error = jsonObject.getBoolean("error");
                                        if (!error) {
                                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            errorTextView.setText(jsonObject.getString("message"));
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
            disposable.add(
                    apiService.registerNewPremiumUser(
                            nameEditText.getText().toString(),
                            usernameEditText.getText().toString(),
                            emailEditText.getText().toString(),
                            mobileEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            referenceEditText.getText().toString(),
                            agentId,
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
                                        JSONObject jsonObject = new JSONObject(response);
                                        boolean error = jsonObject.getBoolean("error");
                                        if (!error) {
                                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            errorTextView.setText(jsonObject.getString("message"));
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

    private boolean checkSecurityPin(String pin) {
        final boolean[] isValid = {false};
        disposable.add(
                apiService.isPinValid(
                        pin,
                        userTypeId
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                try {
                                    String response = responseBody.string();
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean error = jsonObject.getBoolean("error");
                                    pinId = jsonObject.getInt("pin");
                                    isValid[0] = error;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
        return isValid[0];
    }
}
