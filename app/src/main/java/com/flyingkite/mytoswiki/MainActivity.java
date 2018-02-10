package com.flyingkite.mytoswiki;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addTosFragment();
    }

    private void addTosFragment() {
        TosCardFragment f = new TosCardFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fx = fm.beginTransaction();
        fx.add(R.id.cardFragment, f, "TosCardFragment");
        fx.commitAllowingStateLoss();

        fm.executePendingTransactions();
    }
}
