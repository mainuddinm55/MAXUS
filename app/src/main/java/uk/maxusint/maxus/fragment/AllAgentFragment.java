package uk.maxusint.maxus.fragment;


import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.UpdateBetActivity;
import uk.maxusint.maxus.adapter.UserAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.response.AllUserResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllAgentFragment extends Fragment {
    private static final String TAG = "AllAgentFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private UserAdapter adapter;
    @BindView(R.id.agents_recycler_view)
    RecyclerView agentsRecyclerView;
    private List<User> users = new ArrayList<>();

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        agentsRecyclerView.setHasFixedSize(true);
        agentsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        apiService = ApiClient.getInstance().getApi();
        adapter = new UserAdapter(users);
        agentsRecyclerView.setAdapter(adapter);
        getAllAgents();
        adapter.setItemClickListener(new UserAdapter.ItemClickListener() {
            @Override
            public void onClick(User user) {
                Intent intent = new Intent(getContext(), UpdateBetActivity.class);
                intent.putExtra(UpdateUserFragment.USER, user);
                intent.putExtra(UpdateBetActivity.ACTION, UpdateBetActivity.ACTION_UPDATE_USER);
                startActivity(intent);
            }
        });

    }

    private void getAllAgents() {
        disposable.add(
                apiService.getAllAgents().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllUserResponse>() {
                            @Override
                            public void onSuccess(AllUserResponse allUserResponse) {
                                users.clear();
                                users.addAll(allUserResponse.getUsers());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }
                        })
        );
    }
}
