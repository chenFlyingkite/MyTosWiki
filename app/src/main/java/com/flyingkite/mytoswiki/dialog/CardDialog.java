package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.mytoswiki.GlideApp;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.tos.Skill;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.share.ShareHelper;

import java.text.NumberFormat;

@SuppressLint("SetTextI18n")
public class CardDialog extends BaseTosDialog {
    public static final String TAG = "CardDialog";
    public static final String BUNDLE_CARD = "CardDialog.TosCard";

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_card;
    }

    private TosCard card;
    private ImageView cardIcon;
    private ImageView cardLink;
    private ImageView cardImage;
    private TextView cardIdNorm;

    private TextView cardAttr;
    private TextView cardName;
    private TextView cardRace;
    private TextView cardSpace;
    private TextView cardRarity;
    private TextView cardSeries;
    private ImageView cardIcon2;

    private TextView cardMu;
    private TextView cardTu;
    private TextView cardLvMax;
    private TextView cardExpMax;
    private TextView cardExpCurve;
    private ViewGroup cardSkill1;
    private ViewGroup cardSkill2;
    private ViewGroup cardSkillLeader;
    private ViewGroup cardAmeTable;
    private ViewGroup cardAwkTable;
    private ViewGroup cardPowTable;
    private ViewGroup cardVirTable;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseBundle(getArguments());
        initCard();
    }

    private void parseBundle(Bundle b) {
        boolean hasCard = b != null && b.containsKey(BUNDLE_CARD);
        if (!hasCard) return;

        card = b.getParcelable(BUNDLE_CARD);
    }

    private void initCard() {
        cardIcon = findViewById(R.id.cardIcon);
        cardLink = findViewById(R.id.cardLink);
        cardImage = findViewById(R.id.cardImage);

        cardIdNorm = findViewById(R.id.cardIdNorm);
        cardAttr = findViewById(R.id.cardAttr);
        cardName = findViewById(R.id.cardName);
        cardRace = findViewById(R.id.cardRace);
        cardSpace = findViewById(R.id.cardSpace);
        cardRarity = findViewById(R.id.cardRarity);
        cardSeries = findViewById(R.id.cardSeries);
        cardIcon2 = findViewById(R.id.cardIcon2);

        cardMu = findViewById(R.id.cardMu);
        cardTu = findViewById(R.id.cardTu);
        cardLvMax = findViewById(R.id.cardLvMax);
        cardExpMax = findViewById(R.id.cardExpMax);
        cardExpCurve = findViewById(R.id.cardExpCurve);
        cardSkill1 = findViewById(R.id.cardSkill_1);
        cardSkill2 = findViewById(R.id.cardSkill_2);
        cardSkillLeader = findViewById(R.id.cardSkill_leader);
        cardAmeTable = findViewById(R.id.cardAmeliorationTable);
        cardAwkTable = findViewById(R.id.cardAwakenRecallTable);
        cardPowTable = findViewById(R.id.cardPowerReleaseTable);
        cardVirTable = findViewById(R.id.cardVirtualRebirthTable);

        if (card == null) return;

        dismissWhenClick(R.id.cardImages, R.id.cardDetails, R.id.cardMark, R.id.cardEnd);
        GlideApp.with(getActivity()).load(card.icon).placeholder(R.drawable.unknown_card).into(cardIcon);
        GlideApp.with(getActivity()).load(card.icon).placeholder(R.drawable.unknown_card).into(cardIcon2);
        GlideApp.with(getActivity()).load(card.bigImage).placeholder(R.drawable.card_background).into(cardImage);
        cardIcon.setOnClickListener(this::shareImage);
        cardLink.setOnClickListener((v) -> {
            viewLinkAsWebDialog(card.wikiLink);
        });
        findViewById(R.id.cardShare).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.cardContent));
        });
        setOnClickListeners(this::showMonsterEatDialog, R.id.cardMu, R.id.cardTu, R.id.cardMuLv, R.id.cardTuLv);

        cardIdNorm.setText(card.idNorm + "");
        cardAttr.setText(card.attribute + "");
        cardName.setText(card.name + "");
        cardRace.setText(card.race + "");
        cardSpace.setText(card.cost + "");
        cardRarity.setText(card.rarity + "★");
        cardSeries.setText(card.series);

        cardMu.setText(card.maxMUPerLevel + "");
        cardTu.setText(card.maxTUAllLevel + "");
        cardLvMax.setText(card.LvMax + "");
        cardExpMax.setText(NumberFormat.getInstance().format(card.ExpMax));
        cardExpCurve.setText(card.expCurve + "萬");
        underline(cardExpCurve);
        cardExpCurve.setOnClickListener((v) -> {
            MonsterLevelDialog d = new MonsterLevelDialog();
            Bundle b = new Bundle();
            b.putInt(MonsterLevelDialog.BUNDLE_CURVE, card.expCurve);
            d.setArguments(b);
            d.show(getActivity());
        });

        // Fill in HP
        setHp(R.id.cardHpMin, "1", card.minHP, card.minAttack, card.minRecovery);
        setHp(R.id.cardHpMax, "" + card.LvMax, card.maxHP, card.maxAttack, card.maxRecovery);

        // Fill in leader & main skills
        setSkillLeader(R.id.cardSkill_leader, card.skillLeaderName, card.skillLeaderDesc);
        setSkill(R.id.cardSkill_1, card.skillName1, card.skillCDMin1, card.skillCDMax1, card.skillDesc1);
        setSkill(R.id.cardSkill_2, card.skillName2, card.skillCDMin2, card.skillCDMax2, card.skillDesc2);

        // Fill in Amelioration, I, II, III, IV
        setImproves(card.skillAmeCost1 > 0, R.id.cardAmeliorationTable, this::setAmeLink);

        // Fill in Skill changes by Amelioration, I, II, III, IV
        setImproves(card.skillChange.size() > 0, R.id.cardAmeSkillChange, this::setSkillChange);

        // Fill in Awaken Recall
        setImproves(!TextUtils.isEmpty(card.skillAwkName), R.id.cardAwakenRecallTable, this::setAwkLink);

        // Fill in Power Release
        setImproves(!TextUtils.isEmpty(card.skillPowBattleName), R.id.cardPowerReleaseTable, this::setPowLink);

        // Fill in Virtual Rebirth
        setImproves(!TextUtils.isEmpty(card.skillVirBattleName), R.id.cardVirtualRebirthTable, this::setVirLink);

        TextView dt = findViewById(R.id.cardDetails);
        dt.setText(card.cardDetails);
    }

    private void setHp(@IdRes int id, String level, long hps, long attack, long recovery) {
        View vg = findViewById(id);
        TextView lv = vg.findViewById(R.id.cardHpLevel);
        TextView hp = vg.findViewById(R.id.cardHpHp);
        TextView ak = vg.findViewById(R.id.cardHpAttack);
        TextView rc = vg.findViewById(R.id.cardHpRecovery);
        TextView sm = vg.findViewById(R.id.cardHpSum);
        lv.setText(level);
        hp.setText("" + hps);
        ak.setText("" + attack);
        rc.setText("" + recovery);
        sm.setText("" + (hps + attack + recovery));
    }

    private void setSkillLeader(int id, String sname, String sdesc) {
        setSkillLeader(findViewById(id), sname, sdesc);
    }

    private void setSkillLeader(View vg, String sname, String sdesc) {
        TextView name = vg.findViewById(R.id.cardSkillLeaderName);
        TextView desc = vg.findViewById(R.id.cardSkillLeaderDesc);
        name.setText(sname);
        desc.setText(sdesc);
    }

    private void setSkill(View parent, String sname, int smin, int smax, String sdesc) {
        boolean exist = !TextUtils.isEmpty(sname);
        setViewVisibility(parent, exist);
        setSkill(exist, parent, sname, smin, smax, sdesc);
    }

    private void setSkill(@IdRes int id, String sname, int smin, int smax, String sdesc) {
        boolean exist = !TextUtils.isEmpty(sname);
        View vg = findViewById(id);
        setViewVisibility(vg, exist);
        setSkill(exist, vg, sname, smin, smax, sdesc);
    }

    private void setSkill(boolean exist, View vg, String sname, int smin, int smax, String sdesc) {
        if (exist) {
            TextView name = vg.findViewById(R.id.cardSkillName);
            TextView cd_m = vg.findViewById(R.id.cardSkillCDMin);
            TextView cd_M = vg.findViewById(R.id.cardSkillCDMax);
            TextView cdlv = vg.findViewById(R.id.cardSkillLv);
            TextView desc = vg.findViewById(R.id.cardSkillDesc);
            name.setText(sname);
            cd_m.setText("" + smin);
            cd_M.setText("" + smax);
            cdlv.setText("" + (smin - smax + 1));
            desc.setText(sdesc);
        }
    }

    private void setImproves(boolean has, @IdRes int tableId, Runnable runIfExist) {
        setViewVisibility(tableId, has);
        if (has) {
            runIfExist.run();
        }
    }

    private void setAmeLink() {
        setLink(R.id.cardAmeBattleName, R.id.cardAmeBattleLink, card.skillAmeBattleName, card.skillAmeBattleLink);
        setViewVisibility(R.id.cardAmeBattle, !TextUtils.isEmpty(card.skillAmeBattleName));

        setAmelioration(R.id.cardAme1, R.drawable.refine1, card.skillAmeCost1, card.skillAmeName1);
        setAmelioration(R.id.cardAme2, R.drawable.refine2, card.skillAmeCost2, card.skillAmeName2);
        setAmelioration(R.id.cardAme3, R.drawable.refine3, card.skillAmeCost3, card.skillAmeName3);
        setAmelioration(R.id.cardAme4, R.drawable.refine4, card.skillAmeCost4, card.skillAmeName4);
    }

    private void setSkillChange() {
        ViewGroup vg = findViewById(R.id.cardAmeSkillChange);
        int n = card.skillChange.size();
        for (int i = 0; i < n; i++) {
            Skill s = card.skillChange.get(i);
            View v = createSkillRow(s, vg);
            vg.addView(v);
        }
    }

    private View createSkillRow(Skill s, ViewGroup parent) {
        boolean leader = s.isLeader();
        // Inflate row view
        int childId;
        if (leader) {
            childId = R.layout.view_card_skill_leader;
        } else {
            childId = R.layout.view_card_row_skill;
        }
        View v = LayoutInflater.from(getActivity()).inflate(childId, parent, false);

        // Fill in content
        if (leader) {
            setSkillLeader(v, s.name, s.effect);
        } else {
            setSkill(v, s.name, s.cdMin, s.cdMax, s.effect);
        }
        return v;
    }

    private void setAwkLink() {
        setLink(R.id.cardAwkBattleName, R.id.cardAwkBattleLink, card.skillAwkBattleName, card.skillAwkBattleLink);
        TextView t = findViewById(R.id.cardAwkName);
        t.setText(card.skillAwkName);
    }

    private void setPowLink() {
        setLink(R.id.cardPowBattleName, R.id.cardPowBattleLink, card.skillPowBattleName, card.skillPowBattleLink);
    }

    private void setVirLink() {
        setLink(R.id.cardVirBattleName, R.id.cardVirBattleLink, card.skillVirBattleName, card.skillVirBattleLink);
    }

    private void underline(TextView t) {
        CharSequence cs = Html.fromHtml("<u>" + t.getText() + "</u>");
        t.setText(cs);
    }

    private void setLink(@IdRes int nameId, @IdRes int linkId, String bname, String blink) {
        TextView t;
        t = findViewById(nameId);
        CharSequence cs = Html.fromHtml("<u>" + bname + "</u>");
        t.setText(cs);
        View.OnClickListener clk = (v) -> {
            //viewLink(card.skillAmeBattleLink);
            viewLinkAsWebDialog(blink);
        };
        t.setOnClickListener(clk);
        findViewById(linkId).setOnClickListener(clk);
    }

    private void viewLinkAsWebDialog(String link) {
        WebDialog d = new WebDialog();
        Bundle b = new Bundle();
        b.putString(WebDialog.BUNDLE_LINK, link);
        d.setArguments(b);
        d.show(getActivity());
    }

    private void setAmelioration(@IdRes int id, @DrawableRes int ameLv, int scost, String sdesc) {
        boolean exist = scost > 0;
        View vg = findViewById(id);
        setViewVisibility(vg, exist);
        if (exist) {
            ImageView lv = vg.findViewById(R.id.cardAmeLv);
            TextView cost = vg.findViewById(R.id.cardAmeCost);
            TextView desc = vg.findViewById(R.id.cardAmeName);
            lv.setImageResource(ameLv);
            cost.setText("" + scost);
            desc.setText(sdesc);
        }
    }

    private void showMonsterEatDialog(View v) {
        MonsterEatingDialog d = new MonsterEatingDialog();

        Bundle b = new Bundle();
        b.putParcelable(MonsterEatingDialog.BUNDLE_CARD, card);
        d.setArguments(b);
        d.show(getFragmentManager(), MonsterEatingDialog.TAG);
    }

    private void shareImage(View v) {
        //ShareHelper.shareBitmap(getActivity(), card.icon);
        //ShareHelper.sendUriIntent(getActivity(), Uri.parse(card.icon), "image/png");
        String name = ShareHelper.cacheName("2.png");
        shareImage(v, name);
    }
}