package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.adapter.UserAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgentUsersFragment extends Fragment implements UserAdapter.ItemClickListener {
    private static final String TAG = "AgentUsersFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private static AgentUsersFragment sInstance;
    @BindView(R.id.users_recycler_view)
    RecyclerView userRecyclerView;
    @BindView(R.id.no_user_text_view)
    TextView noUserTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private Context mContext;
    private User currentUser;


    public AgentUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static synchronized AgentUsersFragment getInstance() {
        if (sInstance == null)
            sInstance = new AgentUsersFragment();
        return sInstance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        SharedPref sharedPref = new SharedPref(mContext);
        currentUser = sharedPref.getUser();
        apiService = ApiClient.getInstance().getApi();
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setAdapter(userAdapter);
        userAdapter.setItemClickListener(this);
        if (currentUser != null) {
            getAllAgentUser();
        }
    }

    private void getAllAgentUser() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllAgentUser(currentUser.getUserId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(users);
                                userAdapter.notifyDataSetChanged();
                                toggleNoUser();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    private void toggleNoUser() {
        if (userList.size() > 0) {
            noUserTextView.setVisibility(View.GONE);
        } else {
            noUserTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(User user) {

    }
}
