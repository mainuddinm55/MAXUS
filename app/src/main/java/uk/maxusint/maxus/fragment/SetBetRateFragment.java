package uk.maxusint.maxus.fragment;


import android.app.MediaRouteButton;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.SnackBar;
import com.orhanobut.dialogplus.DialogPlus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.BetRateResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetBetRateFragment extends Fragment implements TextWatcher {
    public static final String TAG = "SetBetRateFragment";
    public static final String BET_RATE = "uk.maxusint.maxus.fragment.BET_RATE";
    public static final String BET = "uk.maxusint.maxus.fragment.BET";

    private CompositeDisposable disposable = new CompositeDisposable();
    private Context mContext;
    @BindView(R.id.question_text_view)
    TextView questionTextView;
    @BindView(R.id.option_edit_text)
    TextInputEditText optionEditText;
    @BindView(R.id.option_layout)
    TextInputLayout optionLayout;
    @BindView(R.id.rate_edit_text)
    TextInputEditText rateEditText;
    @BindView(R.id.rate_layout)
    TextInputLayout rateLayout;
    @BindView(R.id.user_type_spinner)
    Spinner userTypeSpinner;
    @BindView(R.id.user_type_layout)
    LinearLayout userTypeLayout;
    @BindView(R.id.bet_mode_text_view)
    TextView betModeTextView;
    @BindView(R.id.set_bet_rate_btn)
    Button setBetRateBtn;

    private String[] tradeModeUserTypes = new String[]{"Select user type", "Classic", "Royal"};
    private String[] advancedModeUserTypes = new String[]{"Select user type", "Classic", "Royal", "Premium"};

    private int userType;
    private int betMode;

    private ApiService apiService;
    private Bet bet;
    private BetRate betRate;


    public SetBetRateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_bet_rate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        optionEditText.addTextChangedListener(this);
        rateEditText.addTextChangedListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            apiService = ApiClient.getInstance().getApi();
            bet = bundle.getParcelable(BET);
            betRate = bundle.getParcelable(BET_RATE);

            if (bet != null) {
                questionTextView.setText(bet.getQuestion());
                if (bet.getBetMode() == Bet.BetMode.TRADE) {
                    betMode = Bet.BetMode.TRADE;
                    betModeTextView.setText("Bet Mode: Trade");
                } else if (bet.getBetMode() == Bet.BetMode.ADVANCED) {
                    betMode = Bet.BetMode.ADVANCED;
                    betModeTextView.setText("Bet Mode: Advanced");
                }

                ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(mContext, R.layout.dropdown_spinner_item, R.id.text_view_list_item);
                if (bet.getBetMode() == Bet.BetMode.TRADE) {
                    userTypeAdapter.addAll(tradeModeUserTypes);
                } else if (bet.getBetMode() == Bet.BetMode.ADVANCED) {
                    userTypeAdapter.addAll(advancedModeUserTypes);
                }
                userTypeSpinner.setAdapter(userTypeAdapter);

                userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String type = (String) parent.getItemAtPosition(position);
                        switch (type) {
                            case "Royal":
                                userType = User.UserType.ROYAL;
                                break;
                            case "Classic":
                                userType = User.UserType.CLASSIC;
                                break;
                            case "Premium":
                                userType = User.UserType.PREMIUM;
                                break;
                            case "All":
                                userType = User.UserType.BOTH;
                                break;
                            default:
                                userType = 0;
                                break;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                if (betRate != null) {
                    userType = betRate.getUserTypeId();
                    betMode = betRate.getBetModeId();
                    userTypeLayout.setVisibility(View.GONE);
                    betModeTextView.setVisibility(View.GONE);
                    optionEditText.setText(betRate.getOptions());
                    rateEditText.setText(betRate.getRate() + "");
                    setBetRateBtn.setText("Update");
                }
            }


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick(R.id.set_bet_rate_btn)
    void setBetRate() {
        if (TextUtils.isEmpty(optionEditText.getText())) {
            optionLayout.setError("Option required");
            optionLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(rateEditText.getText())) {
            rateLayout.setError("Rate required");
            rateLayout.requestFocus();
            return;
        }
        if (userType == 0) {
            Toast.makeText(mContext, "Select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (betRate != null) {
            updateBetRate();
        } else {
            createBetRate();
        }


    }

    private void createBetRate() {
        if (userType == User.UserType.BOTH) {
            disposable.add(
                    apiService.setBetRate(
                            bet.getBetId(),
                            optionEditText.getText().toString(),
                            Float.parseFloat(rateEditText.getText().toString()),
                            User.UserType.ROYAL,
                            betMode
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                                @Override
                                public void onSuccess(ResponseBody responseBody) {
                                    try {
                                        String royalResponse = responseBody.string();
                                        JSONObject royalJson = new JSONObject(royalResponse);
                                        boolean error = royalJson.getBoolean("error");
                                        if (!error) {
                                            disposable.add(
                                                    apiService.setBetRate(
                                                            bet.getBetId(),
                                                            optionEditText.getText().toString(),
                                                            Float.parseFloat(rateEditText.getText().toString()),
                                                            User.UserType.CLASSIC,
                                                            betMode
                                                    ).subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                                                                @Override
                                                                public void onSuccess(ResponseBody responseBody) {
                                                                    try {
                                                                        String classicResponse = responseBody.string();
                                                                        JSONObject classicJson = new JSONObject(classicResponse);
                                                                        boolean error = classicJson.getBoolean("error");
                                                                        if (!error) {
                                                                            disposable.add(
                                                                                    apiService.setBetRate(
                                                                                            bet.getBetId(),
                                                                                            optionEditText.getText().toString(),
                                                                                            Float.parseFloat(rateEditText.getText().toString()),
                                                                                            User.UserType.PREMIUM,
                                                                                            betMode
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
                                                                                                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                                                                        } else {
                                                                                                            Log.e(TAG, "onSuccess: " + jsonObject.getString("message"));
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
                                                                            Log.e(TAG, "onSuccess: " + classicJson.getString("message"));
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
                                            Log.e(TAG, "onSuccess: " + royalJson.getString("message"));
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
                    apiService.setBetRate(
                            bet.getBetId(),
                            optionEditText.getText().toString(),
                            Float.parseFloat(rateEditText.getText().toString()),
                            userType,
                            betMode
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
                                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                            showMoreOptionDialog();
                                        }
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
        }
    }

    private void updateBetRate() {
        disposable.add(
                apiService.updateBetRate(
                        optionEditText.getText().toString(),
                        Float.parseFloat(rateEditText.getText().toString()),
                        betRate.getId()
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<BetRateResponse>() {
                            @Override
                            public void onSuccess(BetRateResponse betRateResponse) {
                                if (!betRateResponse.getError()) {
                                    Toast.makeText(mContext, betRateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void showMoreOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add options");
        builder.setMessage("Do you want add more options");
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                optionEditText.setText(null);
                rateEditText.setText(null);
                userTypeSpinner.setSelection(0);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        optionLayout.setError(null);
        rateLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
