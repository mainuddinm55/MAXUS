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
public class AllAgentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "AllAgentFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private UserAdapter adapter;
    @BindView(R.id.agents_recycler_view)
    RecyclerView agentsRecyclerView;
    @BindView(R.id.no_agent_text_view)
    TextView noAgentTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private List<User> userList = new ArrayList<>();
    private Context mContext;
    private User currentUser;

    public AllAgentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_agent, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        agentsRecyclerView.setHasFixedSize(true);
        agentsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        apiService = ApiClient.getInstance().getApi();
        adapter = new UserAdapter(userList);
        agentsRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        currentUser = new SharedPref(mContext).getUser();
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllAgents();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLUB) {
            getAllClubAgents(currentUser.getUserId());
        }
        adapter.setItemClickListener(new UserAdapter.ItemClickListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(getContext(), UpdateUserActivity.class);
                intent.putExtra(UpdateUserActivity.USER, user);
                startActivity(intent);
            }
        });

    }

    private void getAllClubAgents(int clubId) {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllAgentByClub(clubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(users);
                                adapter.notifyDataSetChanged();
                                toggleNoAgent();
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

    private void toggleNoAgent() {
        if (userList.size() > 0) {
            noAgentTextView.setVisibility(View.GONE);
        } else {
            noAgentTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getAllAgents() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add(
                apiService.getAllAgents().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllUserResponse>() {
                            @Override
                            public void onSuccess(AllUserResponse allUserResponse) {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                userList.clear();
                                userList.addAll(allUserResponse.getUsers());
                                adapter.notifyDataSetChanged();
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
    public void onRefresh() {
        if (currentUser != null && currentUser.getTypeId() == User.UserType.ADMIN) {
            getAllAgents();
        } else if (currentUser != null && currentUser.getTypeId() == User.UserType.CLUB) {
            getAllClubAgents(currentUser.getUserId());
        }
    }
}
