package com.flyingkite.mytoswiki;

@Deprecated
public class TosCraftFragment extends BaseFragment {/*
    public static final String TAG = "TosCraftFragment";

    private Library<CraftAdapter> craftLibrary;
    private TextView craftInfo;

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_craft;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        craftInfo = findViewById(R.id.craftInfo);
        TosWiki.attendDatabaseTasks(onCraftsReady);
        initScrollTools(R.id.craftGoTop, R.id.craftGoBottom, findViewById(R.id.craftRecycler));
    }

    private void initLibrary(List<BaseCraft> list) {
        int n = list.size();
        LogE("Hi init list = %s", n);
        LogE("#0 = %s", list.get(0));
        craftInfo.setText(getString(R.string.craft_selection, n, n));
        craftLibrary = new Library<>(findViewById(R.id.craftRecycler), new GridLayoutManager(getActivity(), 5));
        CraftAdapter a = new CraftAdapter();
        a.setDataList(list);
        a.setItemListener(new CraftAdapter.ItemListener() {
            @Override
            public void onClick(BaseCraft baseCraft, CraftAdapter.CraftVH craftVH, int position) {
                log("Hi CLK = %s", baseCraft);
                new CraftItemDialog().show(getActivity());
            }
        });
        craftLibrary.setViewAdapter(a);
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

            log("#%s (%s) is done", index, tag);
        }

        @Override
        public void onAllTaskDone() {
            log("All is done");
        }
    };
    */
}
