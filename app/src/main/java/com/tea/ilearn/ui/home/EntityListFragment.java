package com.tea.ilearn.ui.home;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.databinding.EntityListBinding;
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.SearchHistory_;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.Entity;
import com.tea.ilearn.utils.DB_utils;
import com.tea.ilearn.utils.ObjectBox;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import io.objectbox.Box;
import io.objectbox.query.Query;
import per.goweii.actionbarex.common.AutoComplTextView;

public class EntityListFragment extends Fragment {
    private EntityListBinding binding;

    private RecyclerView mAbstractInfoRecycler;
    private AbstractInfoListAdapter mAbstractInfoAdapter;
    private CountDownLatch searchSubjectNum = new CountDownLatch(0);
    private String subject;

    public EntityListFragment setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EntityListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.sortName.setOnClickListener(view -> {
            binding.sortCategoryUp.setVisibility(View.VISIBLE);
            binding.sortCategoryDown.setVisibility(View.VISIBLE);
            if (binding.sortNameUp.getVisibility() == View.VISIBLE && binding.sortNameDown.getVisibility() == View.INVISIBLE) {
                binding.sortNameUp.setVisibility(View.INVISIBLE);
                binding.sortNameDown.setVisibility(View.VISIBLE);
                mAbstractInfoAdapter.applySortAndFilter(AbstractInfo::getName, true);
            }
            else {
                binding.sortNameUp.setVisibility(View.VISIBLE);
                binding.sortNameDown.setVisibility(View.INVISIBLE);
                mAbstractInfoAdapter.applySortAndFilter(AbstractInfo::getName, false);
            }
            mAbstractInfoRecycler.scrollToPosition(0);
        });

        binding.sortCategory.setOnClickListener(view -> {
            binding.sortNameUp.setVisibility(View.VISIBLE);
            binding.sortNameDown.setVisibility(View.VISIBLE);
            if (binding.sortCategoryUp.getVisibility() == View.VISIBLE
                    && binding.sortCategoryDown.getVisibility() == View.INVISIBLE) {
                binding.sortCategoryUp.setVisibility(View.INVISIBLE);
                binding.sortCategoryDown.setVisibility(View.VISIBLE);
                mAbstractInfoAdapter.applySortAndFilter(AbstractInfo::getCategory, true);
            }
            else {
                binding.sortCategoryUp.setVisibility(View.VISIBLE);
                binding.sortCategoryDown.setVisibility(View.INVISIBLE);
                mAbstractInfoAdapter.applySortAndFilter(AbstractInfo::getCategory, false);
            }
            mAbstractInfoRecycler.scrollToPosition(0);
        });

        mAbstractInfoRecycler = binding.entityRecycler;
        mAbstractInfoAdapter = new AbstractInfoListAdapter(root.getContext(), new ArrayList<AbstractInfo>());
        mAbstractInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mAbstractInfoRecycler.setAdapter(mAbstractInfoAdapter);

        return root;
    }

    public void waitForBinding(String query, AutoComplTextView acTextView) {
        new Thread(()->{
            while (binding == null) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getActivity().runOnUiThread(() -> {
                search(query, acTextView);
            });
        }).start();
    }

    public void search(String query, AutoComplTextView acTextView) {
        binding.loadingBar.setVisibility(View.VISIBLE);
        mAbstractInfoAdapter.clear();
        binding.emptyHint.setVisibility(View.GONE);
        binding.sortCategoryUp.setVisibility(View.VISIBLE);
        binding.sortCategoryDown.setVisibility(View.VISIBLE);
        binding.sortNameUp.setVisibility(View.VISIBLE);
        binding.sortNameDown.setVisibility(View.VISIBLE);
        if (query.isEmpty()) {
            int initNum = 1;
            searchSubjectNum = new CountDownLatch(initNum);
            for (int i = 0; i < initNum; ++i) {
                int j = (new Random()).nextInt(12);
                int k = Constant.EduKG.SUBJECTS_EN.indexOf(subject);
                query = Constant.EduKG.INIT_KEYS[k][j];
                StaticHandler handler = new StaticHandler(
                        binding, mAbstractInfoAdapter, subject, query, searchSubjectNum, binding.loadingBar,
                        acTextView, getActivity(), getContext(), true, false);
                EduKG.getInst().fuzzySearchEntityWithCourse(subject, query, handler);
            }
            binding.emptyHint.setText("暂无条目推荐");
            return;
        }
        if (searchSubjectNum.getCount() == 0) {
            searchSubjectNum = new CountDownLatch(1);
            StaticHandler handler = new StaticHandler(
                    binding, mAbstractInfoAdapter, subject, query, searchSubjectNum, binding.loadingBar,
                    acTextView, getActivity(), getContext(), true, true);
            EduKG.getInst().fuzzySearchEntityWithCourse(subject, query, handler);
            // add history
            String finalQuery = query;
            new Thread(() -> {
                Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
                Query<SearchHistory> historyQuery = historyBox.query()
                        .equal(SearchHistory_.keyword, finalQuery).build();
                List<SearchHistory> historiesRes = historyQuery.find();
                historyQuery.close();
                SearchHistory history;
                if (historiesRes == null || historiesRes.size() == 0)
                    history = new SearchHistory();
                else
                    history = historiesRes.get(0);
                history.setKeyword(finalQuery)
                        .setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                historyBox.put(history);
                DB_utils.updateACAdapter(getActivity(), getContext(), acTextView);
                Backend.getInst().uploadSearchHistory(history, null);
            }).start();
        }
    }

    static class StaticHandler extends Handler {
        private EntityListBinding binding;
        private AbstractInfoListAdapter mAbstractInfoAdapter;
        private View loadingBar;
        private String subject;
        private CountDownLatch expectedNum;
        private String keyword;
        private AutoComplTextView acTextView;
        private Activity activity;
        private Context context;
        private boolean empty_hint;
        private boolean addToHistory;

        StaticHandler(EntityListBinding binding, AbstractInfoListAdapter mAbstractInfoAdapter,
                      String subject, String keyword, CountDownLatch _latch,
                      View _loadingBar, AutoComplTextView acTextView, Activity activity,
                      Context context, boolean emptyHint, boolean addToHistory) {
            this.binding = binding;
            this.mAbstractInfoAdapter = mAbstractInfoAdapter;
            this.subject = subject;
            this.expectedNum = _latch;
            this.loadingBar = _loadingBar;
            this.keyword = keyword;
            this.acTextView = acTextView;
            this.activity = activity;
            this.context = context;
            this.empty_hint = emptyHint;
            this.addToHistory = addToHistory;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("HomeFragment/handleMessage", "msg.what: " + msg.what);
            this.expectedNum.countDown();
            if (this.expectedNum.getCount() == 0)
                loadingBar.setVisibility(View.INVISIBLE);

            if (msg.what == 0 && msg.obj != null) {
                List<Entity> entities = (List<Entity>) msg.obj;
                if (entities != null && entities.size() != 0) {
                    new Thread(() -> {
                        // remove duplicate entities with different categories
                        HashMap<Entity, ArrayList<String>> entityToCategories = new HashMap<>();
                        for (Entity e : entities) {
                            ArrayList<String> categories = entityToCategories.getOrDefault(
                                    e, new ArrayList<>()
                            );
                            if (e.getCategory().trim().length() > 0) {
                                categories.add(e.getCategory());
                                entityToCategories.put(e, categories);
                            }
                        }
                        // iterate processed entities and its URIs
                        Box<EduKGEntityDetail> entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);
                        entityToCategories.forEach((entity, uri) -> {
                            // query the entity from DB
                            Query<EduKGEntityDetail> query = entityBox.query()
                                    .equal(EduKGEntityDetail_.uri, entity.getUri()).build();
                            List<EduKGEntityDetail> entitiesRes = query.find();
                            query.close();
                            EduKGEntityDetail detail;
                            AbstractInfo abstractInfo = new AbstractInfo().setKd(0)
                                    .setId(entity.getUri())
                                    .setSubject(subject)
                                    .setName(entity.getLabel())
                                    .setCategories(entityToCategories.get(entity));
                            if (entitiesRes != null && entitiesRes.size() > 0) {
                                detail = entitiesRes.get(0);
                                // already exists in DB, update UI
                                abstractInfo.setStar(detail.isStarred()).setLoaded(detail.isViewed());
                            }
                            // add to adapter to update UI
                            activity.runOnUiThread(() -> mAbstractInfoAdapter.add(abstractInfo));
                        }); // end foreach
                    }).start();
                }
                else {
                    if (empty_hint)
                        binding.emptyHint.setVisibility(View.VISIBLE);
                }
            }
            else { // msg.what = 1
                if (empty_hint)
                    binding.emptyHint.setVisibility(View.VISIBLE);
            }
        }
    }
}
