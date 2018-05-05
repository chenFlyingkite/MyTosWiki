package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.TosCard;

public class CardDialog extends BaseTosDialog {
    public static final String BUNDLE_CARD = "TosCard";

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

        if (card == null) return;

        Glide.with(getActivity()).load(card.icon).apply(RequestOptions.placeholderOf(R.drawable.unknown_card)).into(cardIcon);
        Glide.with(getActivity()).load(card.bigImage).apply(RequestOptions.placeholderOf(R.drawable.card_background)).into(cardImage);

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
        cardExpMax.setText(card.ExpMax + "");
        cardExpCurve.setText(card.expCurve + "萬");

        setSkill(R.id.cardSkill_1, card.skillName, card.skillCDMin, card.skillCDMax, card.skillDesc);
        setSkill(R.id.cardSkill_2, card.skillName2, card.skillCDMin2, card.skillCDMax2, card.skillDesc2);

    }

    private void setSkill(int id, String sname, int smin, int smax, String sdesc) {
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
            cdmin.setText(smin + "");
            cdmax.setText(smax + "");
            cdlv.setText((smin - smax + 1) + "");
            desc.setText(sdesc);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_card;
    }
}