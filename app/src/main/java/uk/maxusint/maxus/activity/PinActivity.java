package uk.maxusint.maxus.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.PinAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Pin;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.utils.SharedPref;

public class PinActivity extends AppCompatActivity {
    private static final String TAG = "PinActivity";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    @BindView(R.id.pin_recycler_view)
    RecyclerView pinRecyclerView;
    @BindView(R.id.no_pin_text_view)
    TextView noPinTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.fab_add_pin)
    FloatingActionButton addPinFab;

    private PinAdapter pinAdapter;

    int userType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        User currentUser = new SharedPref(this).getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            addPinFab.show();
        } else {
            addPinFab.hide();
        }

        apiService = ApiClient.getInstance().getApi();
        pinRecyclerView.setHasFixedSize(true);
        pinRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        pinAdapter = new PinAdapter();
        pinRecyclerView.setAdapter(pinAdapter);

        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllPins();
        } else if (currentUser != null) {
            noPinTextView.setText(R.string.no_pin_found);
            getUserPin(currentUser.getUserId());
        }
    }

    private void getUserPin(int userId) {
        disposable.add(
                apiService.getUserPins(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Pin>>() {
                            @Override
                            public void onSuccess(List<Pin> pins) {
                                pinAdapter.setPinList(pins);
                                if (pins.size() > 0) {
                                    noPinTextView.setVisibility(View.GONE);
                                } else {
                                    noPinTextView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void getAllPins() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllPins()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Pin>>() {
                            @Override
                            public void onSuccess(List<Pin> pins) {
                                progressBar.setVisibility(View.GONE);
                                pinAdapter.setPinList(pins);
                                if (pins.size() > 0) {
                                    noPinTextView.setVisibility(View.GONE);
                                } else {
                                    noPinTextView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );

    }

    @OnClick(R.id.fab_add_pin)
    void addPin() {
        showAddPinDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddPinDialog() {

        String[] userTypes = new String[]{"Select user type", "Classic", "Royal"};
        AlertDialog.Builder pinDialog = new AlertDialog.Builder(this);
        pinDialog.setTitle("Add security pin");
        View view = LayoutInflater.from(this).inflate(R.layout.add_pin_layout, null);
        pinDialog.setView(view);
        final TextInputLayout pinLayout = view.findViewById(R.id.pin_layout);
        final TextInputEditText pinEditText = view.findViewById(R.id.pin_edit_text);
        Spinner userTypeSpinner = view.findViewById(R.id.user_type_spinner);
        Button addBtn = view.findViewById(R.id.add_btn);
        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pinLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    pinLayout.setError("Enter pin");
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_spinner_item, R.id.text_view_list_item, userTypes);
        userTypeSpinner.setAdapter(adapter);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        userType = User.UserType.CLASSIC;
                        break;
                    case 2:
                        userType = User.UserType.ROYAL;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final AlertDialog dialog = pinDialog.create();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(pinEditText.getText())) {
                    pinLayout.setError("Enter pin");
                    pinLayout.requestFocus();
                    return;
                }
                if (userType == 0) {
                    Toast.makeText(PinActivity.this, "Please Select User Type", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                addSecurityPin(pinEditText.getText().toString(), userType);
            }
        });
        dialog.show();
    }

    private void addSecurityPin(String pin, int userType) {
        disposable.add(
                apiService.createSecurityPin(pin, userType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DefaultResponse>() {
                            @Override
                            public void onSuccess(DefaultResponse defaultResponse) {

                                if (!defaultResponse.isError()) {
                                    Toast.makeText(PinActivity.this, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PinActivity.this, defaultResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }
}
