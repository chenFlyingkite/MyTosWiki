package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.library.CraftAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.util.TaskMonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CraftDialog extends BaseTosDialog {
    private Library<CraftAdapter> craftLibrary;
    private TextView craftInfo;
    private View sortMenu;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_craft;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TosWiki.attendDatabaseTasks(onCraftsReady);
        initSortMenu();
        initScrollTools(R.id.craftGoTop, R.id.craftGoBottom, findViewById(R.id.craftRecycler));

    }

    private void initLibrary(List<BaseCraft> list) {
        int n = list.size();
        craftInfo = findViewById(R.id.craftInfo);
        craftInfo.setText(getString(R.string.craft_selection, n, n));
        craftLibrary = new Library<>(findViewById(R.id.craftRecycler), new GridLayoutManager(getActivity(), 5));
        CraftAdapter a = new CraftAdapter();
        a.setDataList(list);
        a.setItemListener(new CraftAdapter.ItemListener() {
            @Override
            public void onClick(BaseCraft craft, CraftAdapter.CraftVH craftVH, int position) {
                CraftItemDialog d = new CraftItemDialog();
                Bundle b = new Bundle();
                b.putParcelable(CraftItemDialog.BUNDLE_CRAFT, craft);

                d.setArguments(b);
                d.show(getActivity());
            }
        });
        craftLibrary.setViewAdapter(a);
    }

    private void initSortMenu() {
        sortMenu = findViewById(R.id.craftSortMenu);
        sortMenu.setOnClickListener((v) -> {
            // TODO : Sort menu
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private TaskMonitor.OnTaskState onCraftsReady = new TaskMonitor.OnTaskState() {
        private boolean normalOK = false;
        private boolean armOK = false;
        @Override
        public void onTaskDone(int index, String tag) {
            switch (tag) {
                case TosWiki.TAG_ARM_CRAFTS:
                    armOK = true;
                    break;
                case TosWiki.TAG_NORMAL_CRAFTS:
                    normalOK = true;
                    break;
            }
            if (armOK && normalOK) {
                runOnUiThread(() -> {
                    BaseCraft[] arm = TosWiki.allArmCrafts();
                    BaseCraft[] normal = TosWiki.allNormalCrafts();
                    List<BaseCraft> li = new ArrayList<>();
                    Collections.addAll(li, normal);
                    Collections.addAll(li, arm);
                    initLibrary(li);
                });
            }

            //log("#%s (%s) is done", index, tag);
        }

        @Override
        public void onAllTaskDone() {
            //log("All is done");
        }
    };

//    private void addCraftFragment() {
//        TosCraftFragment f = new TosCraftFragment();
//
//        FragmentManager fm = null;//getFragmentManager();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            fm = getChildFragmentManager();
//            FragmentTransaction fx = fm.beginTransaction();
//            fx.replace(R.id.craftFragment, f, TosCraftFragment.TAG);
//            fx.commitAllowingStateLoss();
//
//            fm.executePendingTransactions();
//        }
//    }

}
