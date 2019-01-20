package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import uk.maxusint.maxus.activity.UpdateUserActivity;
import uk.maxusint.maxus.adapter.UserAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.AllUserResponse;
import uk.maxusint.maxus.utils.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUserFragment extends Fragment implements UserAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "AllUserFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    @BindView(R.id.users_recycler_view)
    RecyclerView usersRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_user_text_view)
    TextView noUserTextView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private Context mContext;
    private User currentUser;

    public AllUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_user, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        usersRecyclerView.setHasFixedSize(true);
        usersRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        apiService = ApiClient.getInstance().getApi();
        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setAdapter(userAdapter);
        currentUser = new SharedPref(mContext).getUser();
        swipeRefreshLayout.setOnRefreshListener(this);
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllUsers();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLUB) {
            getAllClubUsers(currentUser.getUserId());
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.AGENT) {
            getAllAgentUsers(currentUser.getUserId());
        }
        userAdapter.setItemClickListener(this);
    }

    private void getAllAgentUsers(int agentId) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(

                apiService.getAllAgentUser(agentId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(users);
                                userAdapter.notifyDataSetChanged();
                                toggleNoUsers();
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

    private void getAllClubUsers(int clubId) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllUsersByClub(clubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(users);
                                userAdapter.notifyDataSetChanged();
                                toggleNoUsers();
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

    private void toggleNoUsers() {
        if (userList.size() > 0) {
            noUserTextView.setVisibility(View.GONE);
        } else {
            noUserTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getAllUsers() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllUsers().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllUserResponse>() {
                            @Override
                            public void onSuccess(AllUserResponse allUserResponse) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(allUserResponse.getUsers());
                                userAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void onClick(User user) {
        Intent intent = new Intent(getContext(), UpdateUserActivity.class);
        intent.putExtra(UpdateUserActivity.USER, user);
        //intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_UPDATE_USER);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllUsers();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLUB) {
            getAllClubUsers(currentUser.getUserId());
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.AGENT) {
            getAllAgentUsers(currentUser.getUserId());
        }
    }
}
