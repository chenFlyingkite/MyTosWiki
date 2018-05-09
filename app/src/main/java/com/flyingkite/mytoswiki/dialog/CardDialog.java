package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.share.ShareHelper;

import java.text.NumberFormat;

public class CardDialog extends BaseTosDialog {
    public static final String TAG = "CardDialog";
    public static final String BUNDLE_CARD = "CardDialog.TosCard";

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_card;
    }

    private TosCard card;
    private ImageView cardIcon;
    private ImageView cardImage;
    private TextView cardIdNorm;

    private TextView cardAttr;
    private TextView cardName;
    private TextView cardRace;
    private TextView cardSpace;
    private TextView cardRarity;
    private TextView cardSeries;

    private TextView cardMu;
    private TextView cardTu;
    private TextView cardLvMax;
    private TextView cardExpMax;
    private TextView cardExpCurve;
    private ViewGroup cardSkill1;
    private ViewGroup cardSkill2;
    private ViewGroup cardAmeTable;
    private ViewGroup cardAwkTable;


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

    @SuppressLint("SetTextI18n")
    private void initCard() {
        cardIcon = findViewById(R.id.cardIcon);
        cardImage = findViewById(R.id.cardImage);

        cardIdNorm = findViewById(R.id.cardIdNorm);
        cardAttr = findViewById(R.id.cardAttr);
        cardName = findViewById(R.id.cardName);
        cardRace = findViewById(R.id.cardRace);
        cardSpace = findViewById(R.id.cardSpace);
        cardRarity = findViewById(R.id.cardRarity);
        cardSeries = findViewById(R.id.cardSeries);

        cardMu = findViewById(R.id.cardMu);
        cardTu = findViewById(R.id.cardTu);
        cardLvMax = findViewById(R.id.cardLvMax);
        cardExpMax = findViewById(R.id.cardExpMax);
        cardExpCurve = findViewById(R.id.cardExpCurve);
        cardSkill1 = findViewById(R.id.cardSkill_1);
        cardSkill2 = findViewById(R.id.cardSkill_2);
        cardAmeTable = findViewById(R.id.cardAmeliorationTable);
        cardAwkTable = findViewById(R.id.cardAwakenRecallTable);

        if (card == null) return;

        Glide.with(getActivity()).load(card.icon).apply(RequestOptions.placeholderOf(R.drawable.unknown_card)).into(cardIcon);
        Glide.with(getActivity()).load(card.bigImage).apply(RequestOptions.placeholderOf(R.drawable.card_background)).into(cardImage);
        cardIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ShareHelper.shareBitmap(getActivity(), card.icon);
                //ShareHelper.sendUriIntent(getActivity(), Uri.parse(card.icon), "image/png");
                String name = ShareHelper.cacheName("2.png");
                ShareHelper.shareImage(getActivity(), v, name);
            }
        });

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

        // Fill in main skills
        setSkill(R.id.cardSkill_1, card.skillName, card.skillCDMin, card.skillCDMax, card.skillDesc);
        setSkill(R.id.cardSkill_2, card.skillName2, card.skillCDMin2, card.skillCDMax2, card.skillDesc2);

        // Fill in Amelioration, I, II, III, IV
        boolean hasAme = card.skillAmeliorationCost1 > 0;
        cardAmeTable.setVisibility(hasAme ? View.VISIBLE : View.GONE);
        if (hasAme) {
            setAmeLink();

            setAmelioration(R.id.cardAme1, R.drawable.refine1, card.skillAmeliorationCost1, card.skillAmeliorationName1);
            setAmelioration(R.id.cardAme2, R.drawable.refine2, card.skillAmeliorationCost2, card.skillAmeliorationName2);
            setAmelioration(R.id.cardAme3, R.drawable.refine3, card.skillAmeliorationCost3, card.skillAmeliorationName3);
            setAmelioration(R.id.cardAme4, R.drawable.refine4, card.skillAmeliorationCost4, card.skillAmeliorationName4);
        }

        // Fill in AwakenRecall
        boolean hasAwk = !TextUtils.isEmpty(card.skillAwakenRecallName);
        cardAwkTable.setVisibility(hasAwk ? View.VISIBLE : View.GONE);
        if (hasAwk) {
            setAwkLink();
        }
    }

    private void setSkill(@IdRes int id, String sname, int smin, int smax, String sdesc) {
        boolean exist = !TextUtils.isEmpty(sname);
        View vg = findViewById(id);
        vg.setVisibility(exist ? View.VISIBLE : View.GONE);
        if (exist) {
            TextView name = vg.findViewById(R.id.cardSkillName);
            TextView cdmin = vg.findViewById(R.id.cardSkillCDMin);
            TextView cdmax = vg.findViewById(R.id.cardSkillCDMax);
            TextView cdlv = vg.findViewById(R.id.cardSkillLv);
            TextView desc = vg.findViewById(R.id.cardSkillDesc);
            name.setText(sname);
            cdmin.setText("" + smin);
            cdmax.setText("" + smax);
            cdlv.setText("" + (smin - smax + 1));
            desc.setText(sdesc);
        }
    }

    private void setAmeLink() {
        setLink(R.id.cardAmeBattleName, R.id.cardAmeBattleLink, card.skillAmeliorationBattleName, card.skillAmeliorationBattleLink);
    }

    private void setAwkLink() {
        setLink(R.id.cardAwkBattleName, R.id.cardAwkBattleLink, card.skillAwakenRecallName, card.skillAwakenRecallBattleLink);
    }

    private void setLink(@IdRes int nameId, @IdRes int linkId, String bname, String blink) {
        TextView t;
        t = findViewById(nameId);
        CharSequence cs = Html.fromHtml("<u>" + bname + "</u>");
        t.setText(cs);
        View.OnClickListener clk = (v) -> {
            //viewLink(card.skillAmeliorationBattleLink);
            WebDialog d = new WebDialog();
            Bundle b = new Bundle();
            b.putString(WebDialog.BUNDLE_LINK, blink);
            d.setArguments(b);
            d.show(getActivity());
        };
        t.setOnClickListener(clk);
        findViewById(linkId).setOnClickListener(clk);
    }

    private void setAmelioration(@IdRes int id, @DrawableRes int ameLv, int scost, String sdesc) {
        boolean exist = scost > 0;
        View vg = findViewById(id);
        vg.setVisibility(exist ? View.VISIBLE : View.GONE);
        if (exist) {
            ImageView lv = vg.findViewById(R.id.cardAmeLv);
            TextView cost = vg.findViewById(R.id.cardAmeCost);
            TextView desc = vg.findViewById(R.id.cardAmeName);
            lv.setImageResource(ameLv);
            cost.setText("" + scost);
            desc.setText(sdesc);
        }
    }
}