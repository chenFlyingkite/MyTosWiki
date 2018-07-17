package com.flyingkite.mytoswiki;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.flyingkite.library.logging.Loggable;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.dialog.FeedbackDialog;
import com.flyingkite.mytoswiki.dialog.MonsterLevelDialog;
import com.flyingkite.mytoswiki.dialog.SkillEatingDialog;
import com.flyingkite.mytoswiki.dialog.SummonerLevelDialog;
import com.flyingkite.mytoswiki.dialog.WebDialog;
import com.flyingkite.mytoswiki.library.IconAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;
import com.flyingkite.util.TaskMonitor;
import com.flyingkite.util.TextEditorDialog;
import com.flyingkite.util.WaitingDialog;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements TosCardFragment.ToolBarOwner, Loggable {
    // Provide alternative bitmaps
    // https://developer.android.com/training/multiscreen/screendensities
    private List<Integer> tools = Arrays.asList(R.drawable.card_0617
            , R.drawable.logo_chrome
            , R.drawable.ic_send_black_48dp
            , R.drawable.logo_stamina
            //, R.mipmap.app_icon
            , R.drawable.exp_eat
            , R.drawable.ic_description_black_48dp
    );
    private Library<IconAdapter> iconLibrary;

    @Override
    public void log(String message) {
        Log.e(LTag(), message);
    }

    private WaitingDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new CheckTosDBTask().executeOnExecutor(ThreadUtil.cachedThreadPool);
        initToolIcons();
        addTosFragment();
        showCardsLoading();
    }

    private void onCardsLoaded(TosCard[] cards) {
        Fragment f = findFragmentByTag(TosCardFragment.TAG);
        if (f instanceof TosCardFragment) {
            TosCardFragment tf = (TosCardFragment) f;
            tf.onCardsReady(cards);
        }
    }

    private void test() {
//
//
//        log("+ card = %s", TosWiki.isLoaded_allCards());
//        log("+ ame = %s", TosWiki.isLoaded_ameSkills());
//        TosWiki.registerLoaded(new TosWiki.OnLoadState() {
//            @Override
//            public void onLoaded_allCards() {
//                log("Reg : card");
//            }
//
//            @Override
//            public void onLoaded_ameSkills() {
//                log("Reg : ame");
//            }
//
//            @Override
//            public void onLoaded_All() {
//                log("Reg : all");
//                onOK();
//            }
//        });
//
//        findViewById(R.id.mainTools).postDelayed(() -> {
//            TosWiki.registerLoaded(new TosWiki.OnLoadState() {
//                @Override
//                public void onLoaded_allCards() {
//                    log("Reg1 : card");
//                }
//
//                @Override
//                public void onLoaded_ameSkills() {
//                    log("Reg1 : ame");
//                }
//
//                @Override
//                public void onLoaded_All() {
//                    log("Reg1 : all");
//                }
//            });
//        }, 1000);
//
//
//        findViewById(R.id.mainTools).postDelayed(() -> {
//            TosWiki.registerLoaded(new TosWiki.OnLoadState() {
//                @Override
//                public void onLoaded_allCards() {
//                    log("Reg2 : card");
//                }
//
//                @Override
//                public void onLoaded_ameSkills() {
//                    log("Reg2 : ame");
//                }
//
//                @Override
//                public void onLoaded_All() {
//                    log("Reg2 : all");
//                }
//            });
//        }, 2000);
//        findViewById(R.id.mainTools).postDelayed(() -> {
//            TosWiki.registerLoaded(new TosWiki.OnLoadState() {
//                @Override
//                public void onLoaded_allCards() {
//                    log("Reg7 : card");
//                }
//
//                @Override
//                public void onLoaded_ameSkills() {
//                    log("Reg7 : ame");
//                }
//
//                @Override
//                public void onLoaded_All() {
//                    log("Reg7 : all");
//                }
//            });
//        }, 7000);
//        findViewById(R.id.mainTools).postDelayed(() -> {
//            TosWiki.registerLoaded(new TosWiki.OnLoadState() {
//                @Override
//                public void onLoaded_allCards() {
//                    log("Reg12 : card");
//                }
//
//                @Override
//                public void onLoaded_ameSkills() {
//                    log("Reg12 : ame");
//                }
//
//                @Override
//                public void onLoaded_All() {
//                    log("Reg12 : all");
//                }
//            });
//        }, 12000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addTosFragment() {
        TosCardFragment f = new TosCardFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fx = fm.beginTransaction();
        fx.replace(R.id.cardFragment, f, TosCardFragment.TAG);
        fx.commitAllowingStateLoss();

        fm.executePendingTransactions();
    }

    private void initToolIcons() {
        iconLibrary = new Library<>(findViewById(R.id.mainTools));
        IconAdapter adapter = new IconAdapter();
        adapter.setDataList(tools);
        adapter.setItemListener(new IconAdapter.ItemListener() {
//            public void onHello() {
//
//            }

            @Override
            public void onClick(Integer iconId, IconAdapter.IconVH vh, int position) {
                switch (iconId) {
                    case R.drawable.card_0617:
                        new SkillEatingDialog().show(getActivity());
                        break;
                    case R.drawable.logo_stamina:
                        new SummonerLevelDialog().show(getActivity());
                        break;
                    case R.drawable.exp_eat:
                        new MonsterLevelDialog().show(getActivity());
                        break;
                    case R.drawable.ic_description_black_48dp:
                        new TextEditorDialog(MainActivity.this::getActivity).show();
                        break;
                    case R.drawable.logo_chrome:
                        new WebDialog().show(getActivity());
                        break;
                    //case R.mipmap.app_icon: // TODO
                    case R.drawable.ic_send_black_48dp:
                        new FeedbackDialog().show(getActivity());
                        break;
                }
            }
        });
        iconLibrary.setViewAdapter(adapter);
    }

    private Activity getActivity() {
        return this;
    }

    @Override
    public void setToolsVisible(boolean visible) {
        iconLibrary.recyclerView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean isToolsVisible() {
        return iconLibrary.recyclerView.getVisibility() == View.VISIBLE;
    }

//    private class CheckTosDBTask extends AsyncTask<Void, Void, Void> {
//        private WaitingDialog dialog;
//        private int n = 0;
//        private static final int time = 16_000; // 6 second
//
//        @Override
//        protected void onPreExecute() {
//            dialog = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            long tic = System.currentTimeMillis();
//            while (TosWiki.allCards() == null) {
//
//            };
//            long tac = System.currentTimeMillis();
////            do {
////                tac = System.currentTimeMillis();
////                try {
////                    Thread.sleep(5);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            } while (tac - tic < time && TosWiki.allCards() == null);
//            Say.Log(" End %s", StringUtil.toTimeMMSSF(tac - tic));
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            //onOK();
//            if (dialog != null) {
//                dialog.dismiss();
//                dialog = null;
//            }
//        }
//    }

    private void showCardsLoading() {
        waiting = new WaitingDialog.Builder(getActivity()).message(getString(R.string.cardsLoading)).buildAndShow();
        TosWiki.attendDatabaseTasks(onDatabaseState);
    }

    private TaskMonitor.OnTaskState onDatabaseState = new TaskMonitor.OnTaskState() {
        @Override
        public void onTaskDone(int index, String tag) {
            runOnUiThread(() -> {
                if (TosWiki.TAG_ALL_CARDS.equals(tag)) {

                    int n = TosWiki.getAllCardsCount();
                    showToast(R.string.cards_read, n);
                    if (waiting != null) {
                        waiting.dismiss();
                        waiting = null;
                    }
                }
                log("#%s (%s) is done", index, tag);
            });
        }

        @Override
        public void onAllTaskDone() {
            log("All done");
        }
    };

    //
//    private void onOK() {
//        TosCard[] cards = TosWiki.allCards();
//        showToast(R.string.cards_read, cards.length);
//        onCardsLoaded(cards);
//        if (waiting != null) {
//            waiting.dismiss();
//            waiting = null;
//        }
//    }

}
