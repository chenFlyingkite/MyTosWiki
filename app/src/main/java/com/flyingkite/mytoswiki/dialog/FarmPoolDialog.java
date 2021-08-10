package com.flyingkite.mytoswiki.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class FarmPoolDialog extends BaseTosDialog {
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_farm_pool;
    }

    @Override
    protected boolean useFloating() {
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logImpression();
        dismissWhenClick(R.id.fpdTitle);
        setDetails();
        setBattle();
    }

    private void setDetails() {
        TextView detail = findViewById(R.id.fpdDetail);
        findViewById(R.id.fpdShare).setOnClickListener((v) -> {
            shareString(detail.getText().toString());
            logShare("text");
        });
    }

    private void setBattle() {
        String[] ids;
        ids = new String[]{"0981", "0983", "0985", "0498", "0500", "1083"};
        setTeamSimple(findViewById(R.id.fpdTeam0), ids);

        ids = new String[]{"2595", "2634", "", "", "", "2595"};
        setTeamSimple(findViewById(R.id.fpdTeam1), ids);

        ids = new String[]{"1983", "2634", "", "", "", "1983"};
        setTeamSimple(findViewById(R.id.fpdTeam2), ids);
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logFarmPool(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logFarmPool(m);
    }
    //-- Events

}