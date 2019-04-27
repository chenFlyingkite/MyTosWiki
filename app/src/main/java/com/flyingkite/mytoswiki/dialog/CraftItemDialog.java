package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftSkill;
import com.flyingkite.mytoswiki.data.tos.CraftsArm;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftItemDialog extends BaseTosDialog {
    public static final String TAG = "CraftItemDialog";
    public static final String BUNDLE_CRAFT = "CraftItemDialog.Craft";

    // Views
    private ImageView craftIcon;
    private View craftLink;
    private View craftShare;
    private TextView craftIdNorm;
    private TextView craftMode;
    private TextView craftName;
    private TextView craftRarity;
    private TextView craftLevel;
    private TextView craftCharge;

    // Major content
    private BaseCraft craft;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_craft_item;
    }

    @Override
    protected void onFinishInflate(View view, Dialog dialog) {
        super.onFinishInflate(view, dialog);
        parseBundle(getArguments());
        initCraft(craft);
        logCraft();
    }

    private void parseBundle(Bundle b) {
        boolean hasCurve = b != null && b.containsKey(BUNDLE_CRAFT);
        if (hasCurve) {
            craft = b.getParcelable(BUNDLE_CRAFT);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initCraft(BaseCraft c) {
        craftIcon = findViewById(R.id.craftIcon);
        craftLink = findViewById(R.id.craftLink);
        craftShare = findViewById(R.id.craftShare);
        craftIdNorm = findViewById(R.id.craftIdNorm);
        craftMode = findViewById(R.id.craftMode);
        craftName = findViewById(R.id.craftName);
        craftRarity = findViewById(R.id.craftRarity);
        craftLevel = findViewById(R.id.craftLevel);
        craftCharge = findViewById(R.id.craftCharge);

        if (c == null) return;
        CraftsNormal normal = null;
        CraftsArm arm = null;
        if (c instanceof CraftsNormal) {
            normal = (CraftsNormal) c;
        } else if (c instanceof CraftsArm) {
            arm = (CraftsArm) c;
        }

        loadCraftToImageView(craftIcon, c.icon.iconLink);
        dismissWhenClick(R.id.craftTop, R.id.craftMark, R.id.craftEnd, R.id.craftMajor, R.id.craftMajorHeader);
        craftIcon.setOnClickListener((v) -> {
            shareImage(v);
            logShare("icon");
        });
        craftShare.setOnClickListener((v) -> {
            shareImage(findViewById(R.id.craftInfo));
            logShare("table");
        });
        craftLink.setOnClickListener((v) -> {
            viewLinkAsWebDialog(craft.link);
        });

        // Id, mode, name
        craftIdNorm.setText(c.idNorm);
        craftMode.setText(c.mode);
        craftName.setText(c.name);
        craftRarity.setText(c.rarity + "★");
        craftLevel.setText(c.level + "");
        craftCharge.setText(c.charge);

        setPreCondition(R.id.craftAttributeLimit, getString(R.string.cards_attr), c.attrLimit);
        setPreCondition(R.id.craftRaceLimit, getString(R.string.cards_race), c.raceLimit);
        if (normal != null) {
            setViewVisibility(R.id.craftCardLimit, false);
        } else if (arm != null) {
            // Join card limit as #ID name
            int n = Math.min(arm.cardLimit.size(), arm.cardLimitName.size());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (i != 0) {
                    sb.append("\n");
                }
                sb.append(arm.cardLimit.get(i)).append(" ").append(arm.cardLimitName.get(i));
            }
            setPreCondition(R.id.craftCardLimit, getString(R.string.cards_normId), sb.toString());

            boolean allGone = isAllVisibilitiesOf(View.GONE, R.id.craftAttributeLimit, R.id.craftRaceLimit, R.id.craftCardLimit);
            if (allGone) {
                setViewVisibility(findViewById(R.id.craftPrecond), false);
            }
        }

        if (normal != null) {
            // Set normal skill
            setViewVisibility(R.id.craftSkillNormal, true);
            int[] ids = {R.id.craftSkill1, R.id.craftSkill2, R.id.craftSkill3};
            setCraftEffectRow(ids, c.craftSkill);
            // Set arm skill
            setViewVisibility(R.id.craftSkillArms, false);
        } else if (arm != null) {
            int[] ids;
            // Set normal skill
            setViewVisibility(R.id.craftSkillNormal, false);
            // Set arm skill
            setViewVisibility(R.id.craftSkillArms, true);
            ids = new int[]{R.id.craftSkillArm1, R.id.craftSkillArm2, R.id.craftSkillArm3};
            setCraftEffectRow(ids, c.craftSkill);
            // Set extra skill
            ids = new int[]{R.id.craftSkillExtra1, R.id.craftSkillExtra2, R.id.craftSkillExtra3};
            setCraftEffectRow(ids, c.extraSkill);
            setViewVisibility(R.id.craftSkillExtraTitle, c.extraSkill.size() != 0);
            // Set Arm bonus up
            setBonusUp(R.id.craftArmUp, arm.upHp, arm.upAttack, arm.upRecovery);

            if (arm.hasNoUp()) {
                setVisibilities(View.GONE, R.id.craftArmUpTitle, R.id.craftArmUp);
            }
        }
    }

    private void setCraftEffectRow(int[] rowIds, List<CraftSkill> skills) {
        int n = skills == null ? 0 : skills.size();
        setVisibilities(View.GONE, rowIds);
        for (int i = 0; i < n; i++) {
            CraftSkill cs = skills.get(i);
            setEffect(rowIds[i], cs.level + "", cs.detail, cs.score + "");
        }
    }

    @Deprecated
    private void setCraftArmRow(int headerId, int[] rowIds, List<CraftSkill> skills) {
        int n = skills == null ? 0 : skills.size();
        setVisibilities(View.GONE, rowIds);
        for (int i = 0; i < n; i++) {
            CraftSkill cs = skills.get(i);
            setEffectArm(rowIds[i], cs.level + "", cs.detail);
        }
        setViewVisibility(headerId, n != 0);
    }

    private void setPreCondition(int parent, String sLim, String sDet) {
        ViewGroup vg = findViewById(parent);
        TextView limit = vg.findViewById(R.id.craftLimit);
        TextView detail = vg.findViewById(R.id.craftLimitDetail);

        limit.setText(sLim);
        detail.setText(sDet);
        setViewVisibility(parent, !TextUtils.isEmpty(sDet));
    }

    private void setEffect(int parent, String sLv, String sFx, String sPt) {
        ViewGroup vg = findViewById(parent);

        TextView lv = vg.findViewById(R.id.craftSkillLevel);
        TextView desc = vg.findViewById(R.id.craftSkillDesc);
        TextView point = vg.findViewById(R.id.craftSkillScore);

        lv.setText(sLv);
        desc.setText(sFx);
        point.setText(sPt);
        setViewVisibility(parent, !TextUtils.isEmpty(sFx));
    }

    private void setEffectArm(int parent, String sLv, String sFx) {
        setPreCondition(parent, sLv, sFx);
    }

    private void setBonusUp(int parent, String sHp, String sAtk, String sRe) {
        ViewGroup vg = findViewById(parent);

        TextView hp = vg.findViewById(R.id.craftUpHp);
        TextView atk = vg.findViewById(R.id.craftUpAttack);
        TextView rcry = vg.findViewById(R.id.craftUpRecovery);

        hp.setText(sHp);
        atk.setText(sAtk);
        rcry.setText(sRe);
        setViewVisibility(parent, true);
    }

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCraft(m);
    }

    private void logCraft() {
        Map<String, String> m = new HashMap<>();
        String id = "--";
        if (craft != null) {
            id = craft.idNorm + " " + craft.name;
        }
        m.put("craft", id);
        FabricAnswers.logCraft(m);
    }
    //-- Events
}
