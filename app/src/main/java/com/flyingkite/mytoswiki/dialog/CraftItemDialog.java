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
import com.flyingkite.mytoswiki.GlideApp;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.CraftSkill;
import com.flyingkite.mytoswiki.data.tos.CraftsArm;
import com.flyingkite.mytoswiki.data.tos.CraftsNormal;

import java.util.HashMap;
import java.util.Map;

public class CraftItemDialog extends BaseTosDialog {
    public static final String TAG = "CraftItemDialog";
    public static final String BUNDLE_CRAFT = "CraftItemDialog.Craft";

    // Views
    private ImageView craftIcon;
    private View craftLink;
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

        GlideApp.with(getActivity()).load(c.icon.iconLink).placeholder(R.drawable.unknown_craft).into(craftIcon);
        dismissWhenClick(R.id.craftTop, R.id.craftMark, R.id.craftEnd, R.id.craftMajor, R.id.craftMajorHeader);
        craftIcon.setOnClickListener((v) -> {
            shareImage(v);
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
        if (normal != null) {
            setPreCondition(R.id.craftAttributeLimit, getString(R.string.cards_attr), normal.attrLimit);
            setPreCondition(R.id.craftRaceLimit, getString(R.string.cards_race), normal.raceLimit);
            setViewVisibility(R.id.craftCardLimit, false);
        } else if (arm != null) {
            setViewVisibility(R.id.craftAttributeLimit, false);
            setViewVisibility(R.id.craftRaceLimit, false);
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
        }

        if (normal != null) {
            // Set normal skill
            setViewVisibility(R.id.craftSkillNormal, true);
            int[] ids = {R.id.craftSkill1, R.id.craftSkill2, R.id.craftSkill3};
            setVisibilities(View.GONE, ids);
            for (int i = 0; i < c.craftSkill.size(); i++) {
                CraftSkill cs = c.craftSkill.get(i);
                setEffect(ids[i], cs.level + "", cs.detail, cs.score + "");
            }
            // Set arm skill
            setViewVisibility(R.id.craftSkillArms, false);
        } else if (arm != null) {
            // Set normal skill
            setViewVisibility(R.id.craftSkillNormal, false);
            // Set arm skill
            setViewVisibility(R.id.craftSkillArms, true);
            int[] ids = {R.id.craftSkillArm1, R.id.craftSkillArm2, R.id.craftSkillArm3};
            setVisibilities(View.GONE, ids);
            for (int i = 0; i < c.craftSkill.size(); i++) {
                CraftSkill cs = c.craftSkill.get(i);
                setEffectArm(ids[i], cs.level + "", cs.detail);
            }
            // Set Arm bonus up
            setBonusUp(R.id.craftArmUp, arm.upHp, arm.upAttack, arm.upRecovery);

        }



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
