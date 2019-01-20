package uk.maxusint.maxus.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import uk.maxusint.maxus.adapter.RankAdapter;
import uk.maxusint.maxus.network.ApiClient;
import uk.maxusint.maxus.network.ApiService;
import uk.maxusint.maxus.network.model.Rank;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {
    private static final String TAG = "RankFragment";
    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    private Context mContext;
    @BindView(R.id.ranks_recycler_view)
    RecyclerView ranksRecyclerView;
    @BindView(R.id.no_rank_text_view)
    TextView noRankTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private RankAdapter rankAdapter;
    private List<Rank> rankList = new ArrayList<>();

    public RankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        apiService = ApiClient.getInstance().getApi();
        ranksRecyclerView.setHasFixedSize(true);
        rankAdapter = new RankAdapter();
        ranksRecyclerView.setAdapter(rankAdapter);
        getAllRanks();
    }

    private void getAllRanks() {
        disposable.add(
                apiService.getAllRanks()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Rank>>() {
                            @Override
                            public void onSuccess(List<Rank> ranks) {
                                rankList.clear();
                                rankList.addAll(ranks);
                                rankAdapter.setRankList(rankList);
                                toggleNoRank();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void toggleNoRank() {
        if (rankList.size()>0){
            noRankTextView.setVisibility(View.GONE);
        }else {
            noRankTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
