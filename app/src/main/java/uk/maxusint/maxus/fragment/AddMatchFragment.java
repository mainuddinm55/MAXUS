package uk.maxusint.maxus.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.listener.FragmentLoader;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.response.InsertedMatchResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMatchFragment extends Fragment implements TextWatcher {
    public static final String TAG = "AddMatchFragment";
    private FragmentLoader fragmentLoader;
    private CompositeDisposable disposable = new CompositeDisposable();

    @BindView(R.id.team1_edit_text)
    TextInputEditText team1EditText;
    @BindView(R.id.team1_layout)
    TextInputLayout team1Layout;
    @BindView(R.id.team2_edit_text)
    TextInputEditText team2EditText;
    @BindView(R.id.team2_layout)
    TextInputLayout team2Layout;
    @BindView(R.id.tournament_edit_text)
    TextInputEditText tournamentEditText;
    @BindView(R.id.tournament_layout)
    TextInputLayout tournamentLayout;
    @BindView(R.id.date_text_view)
    TextView dateTextView;
    @BindView(R.id.time_text_view)
    TextView timeTextView;
    @BindView(R.id.match_type_spinner)
    Spinner matchTypeSpinner;
    @BindView(R.id.match_format_spinner)
    Spinner matchFormatSpinner;
    @BindView(R.id.err_text_view)
    TextView errorTextView;

    private Context mContext;

    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int hour = calendar.get(Calendar.HOUR_OF_DAY);
    private int minute = calendar.get(Calendar.MINUTE);

    private String date;// = year + "-" + (month + 1) + "-" + day;
    private String time;// = hour + ":" + minute;

    private String matchType;
    private String matchFormat;

    private String[] matchTypes = new String[]{"Select match type", "Cricket", "Football"};
    private String[] cricketMatchFormat = new String[]{"Test", "ODI", "T20", "Domestic"};
    private String[] footballMatchFormat = new String[]{"International", "League", "Friendly", "UEFA"};
    private String[] defaultMatchFormat = new String[]{"Select match format"};

    public AddMatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_match, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        dateTextView.setText("Select Date");
        timeTextView.setText("Select Time");

        team1EditText.addTextChangedListener(this);
        team2EditText.addTextChangedListener(this);
        tournamentEditText.addTextChangedListener(this);

        ArrayAdapter<String> matchTypeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, matchTypes);
        matchTypeSpinner.setAdapter(matchTypeAdapter);
        matchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> matchFormatAdapter;
                matchFormat = null;
                switch (position) {
                    case 0:
                        matchFormatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, defaultMatchFormat);
                        matchType = null;
                        break;
                    case 1:
                        matchFormatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, cricketMatchFormat);
                        matchType = matchTypes[position];
                        break;
                    case 2:
                        matchFormatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, footballMatchFormat);
                        matchType = matchTypes[position];
                        break;
                    default:
                        matchFormatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, defaultMatchFormat);
                        matchType = null;
                        break;
                }
                matchFormatSpinner.setAdapter(matchFormatAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        matchFormatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                matchFormat = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.date_text_view)
    void pickDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = year + "-" + (month + 1) + "-" + dayOfMonth;
                dateTextView.setText(date);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.time_text_view)
    void pickTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = hourOfDay + ":" + minute;
                timeTextView.setText(time);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        fragmentLoader = (FragmentLoader) context;
    }

    @OnClick(R.id.add_btn)
    void addMatch() {
        if (TextUtils.isEmpty(team1EditText.getText())) {
            team1Layout.setError("Team1 required");
            team1Layout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(team2EditText.getText())) {
            team2Layout.setError("Team2 required");
            team2Layout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(tournamentEditText.getText())) {
            tournamentLayout.setError("Tournament required");
            tournamentLayout.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(date)) {
            dateTextView.setError("Date required");
            dateTextView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(time)) {
            timeTextView.setError("Time required");
            timeTextView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(matchType)) {
            Toast.makeText(mContext, "Please select match type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(matchFormat)) {
            Toast.makeText(mContext, "Please select match format", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTime = date + " " + time;
        ApiService apiService = ApiClient.getInstance().getApi();
        disposable.add(
                apiService.addMatch(
                        team1EditText.getText().toString(),
                        team2EditText.getText().toString(),
                        dateTime,
                        tournamentEditText.getText().toString(),
                        matchType,
                        matchFormat
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<InsertedMatchResponse>() {
                            @Override
                            public void onSuccess(InsertedMatchResponse responseBody) {
                                if (!responseBody.getError()) {
                                    Toast.makeText(mContext, responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(CreateBetFragment.MATCH, responseBody.getMatch());
                                    CreateBetFragment fragment = new CreateBetFragment();
                                    fragment.setArguments(bundle);
                                    fragmentLoader.loadFragment(fragment, "create_bet");
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    if (((HttpException) e).code() == 422) {
                                        errorTextView.setText(((HttpException) e).message());
                                        Log.e(TAG, "onError: " + ((HttpException) e).code());
                                        Log.e(TAG, "onError: " + ((HttpException) e).message());
                                        Log.e(TAG, "onError: " + ((HttpException) e).getMessage());
                                        Log.e(TAG, "onError: " + ((HttpException) e).toString());
                                    } else {
                                        errorTextView.setText("Match_ can't be added");
                                    }
                                } else {
                                    errorTextView.setText("Match_ can't be added");
                                }
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
        errorTextView.setText(null);
        team1Layout.setError(null);
        team2Layout.setError(null);
        tournamentLayout.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
