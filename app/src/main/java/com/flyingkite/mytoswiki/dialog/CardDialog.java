package com.flyingkite.mytoswiki.dialog;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyingkite.fabric.FabricAnswers;
import com.flyingkite.library.widget.Library;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.CardFavor;
import com.flyingkite.mytoswiki.data.tos.BaseCraft;
import com.flyingkite.mytoswiki.data.tos.NameLink;
import com.flyingkite.mytoswiki.data.tos.SkillLite;
import com.flyingkite.mytoswiki.data.tos.TosCard;
import com.flyingkite.mytoswiki.library.CardCombineAdapter;
import com.flyingkite.mytoswiki.library.CardEvolveAdapter;
import com.flyingkite.mytoswiki.library.CardTileAdapter;
import com.flyingkite.mytoswiki.library.CraftTileAdapter;
import com.flyingkite.mytoswiki.tos.TosWiki;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Library<CardTileAdapter> sameSkillLibrary;
    private Library<CardCombineAdapter> combineLibrary;
    private Library<CraftTileAdapter> armCraftLibrary;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseBundle(getArguments());
        initCard();
        logShowCard();
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

        dismissWhenClick(R.id.cardImages, R.id.cardDetails, R.id.cardMark, R.id.cardEnd, R.id.cardSkill_leader);
        loadCardToImageView(cardIcon, card);
        loadCardToImageView(cardIcon2, card);
        loadLinkToImageView(cardImage, card.bigImage, getActivity(), R.drawable.card_background);
        cardIcon.setOnClickListener((v) -> {
            shareImage(v);
            logShare("icon");
        });
        cardLink.setOnClickListener((v) -> {
            viewLinkAsWebDialog(card.wikiLink);
        });
        findViewById(R.id.cardShare).setOnClickListener((v) -> {
            shareImage(findViewById(R.id.cardContent));
            logShare("table");
        });
        View favor = findViewById(R.id.cardFavor);
        favor.setOnClickListener((v) -> {
            v.setSelected(!v.isSelected());

            boolean add = v.isSelected();
            TosWiki.getCardFavor().addOrRemove(add, card.idNorm);
            TosWiki.notifyFavor();
            logFavorite(add, card.idNorm + " " + card.name);
        });
        CardFavor c = TosWiki.getCardFavor();
        boolean sel = c != null && c.exist(card.idNorm);
        favor.setSelected(sel);
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
        // Fill in same skill icons
        setSameSkills(card);
        setEvolve(card);
        setCombine(card);
        setRebirth(card);
        setArmCraft(card);
        setSwitching(card);
        setVisibilityByChild(findViewById(R.id.cardChange));

        // Fill in Amelioration, I, II, III, IV
        setImproves(card.skillAmeCost1 > 0, R.id.cardAmeliorationTable, this::setAmeLink);

        // Fill in Skill changes by Amelioration, I, II, III, IV
        setImproves(card.skillChange.size() > 0, R.id.cardAmeSkillChange, this::setSkillChange);

        // Fill in Awaken Recall
        setImproves(!TextUtils.isEmpty(card.skillAwkName), R.id.cardAwakenRecallTable, this::setAwkLink);

        // Fill in Power Release
        setImproves(card.skillPowBattle.size() > 0, R.id.cardPowerReleaseTable, this::setPowLink);

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

    private void setSameSkills(TosCard c) {
        if (c.sameSkills.isEmpty()) {
            findViewById(R.id.cardSameSkill).setVisibility(View.GONE);
            return;
        }

        // Header
        TextView h = findViewById(R.id.cardSameSkillHeader);
        h.setText(getString(R.string.cards_skillSameActive_n, "" + c.sameSkills.size()));

        // Setup recycler
        RecyclerView rv = findViewById(R.id.cardSameActiveSkills);
        // Fetch cards
        List<TosCard> same = getCardsByIdNorms(c.sameSkills);
        // Creating library
        sameSkillLibrary = new Library<>(rv);
        CardTileAdapter a = new CardTileAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return CardDialog.this.getFragmentManager();
            }
        };
        a.setDataList(same);
        sameSkillLibrary.setViewAdapter(a);
        // To allow recycler view be scrollable inside ScrollView & HorizontalScrollView
        if (same.size() > 7) { // since 8 items will directly out of the width
            rv.removeOnItemTouchListener(noIntercept);
            rv.addOnItemTouchListener(noIntercept);
        }
    }

    private void setEvolve(TosCard c) {
        if (c.evolveInfo.isEmpty()) {
            findViewById(R.id.cardEvolve).setVisibility(View.GONE);
            return;
        }

        // Setup recycler
        CardEvolveAdapter a = new CardEvolveAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return CardDialog.this.getFragmentManager();
            }
        };
        a.setDataList(c.evolveInfo);

        fillItems(findViewById(R.id.cardEvolveLinear), a);
    }

    private void setCombine(TosCard c) {
        if (c.combineFrom.isEmpty() || c.combineTo.isEmpty()) {
            findViewById(R.id.cardCombine).setVisibility(View.GONE);
            return;
        }

        // Combine to card
        TosCard tc = TosWiki.getCardByIdNorm(c.combineTo.get(0));
        setSimpleCard(findViewById(R.id.cardCombineTo), tc);

        // Setup recycler
        RecyclerView rv = findViewById(R.id.cardCombineFrom);
        combineLibrary = new Library<>(rv);
        // Fetch cards
        List<TosCard> combine = getCardsByIdNorms(c.combineFrom);
        CardCombineAdapter a = new CardCombineAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return CardDialog.this.getFragmentManager();
            }
        };
        a.setDataList(combine);
        combineLibrary.setViewAdapter(a);
    }

    private void setArmCraft(TosCard c) {
        if (c.armCrafts.isEmpty()) {
            findViewById(R.id.cardArmCrafts).setVisibility(View.GONE);
            return;
        }

        // Setup recycler
        RecyclerView rv = findViewById(R.id.cardArmCraft);
        armCraftLibrary = new Library<>(rv);
        // Fetch cards
        List<BaseCraft> arms = getCraftsByIdNorms(c.armCrafts);
        CraftTileAdapter a = new CraftTileAdapter() {
            @Override
            public FragmentManager getFragmentManager() {
                return CardDialog.this.getFragmentManager();
            }
        };
        a.setDataList(arms);
        armCraftLibrary.setViewAdapter(a);
    }

    private void setRebirth(TosCard c) {
        setCardArrowFrom(c, c.rebirthFrom, findViewById(R.id.cardRebirthFromContent));
        setCardArrowFrom(c, c.rebirthChange, findViewById(R.id.cardRebirthChangeContent));
    }

    private void setSwitching(TosCard c) {
        setCardArrowTo(c, c.switchChange, findViewById(R.id.cardSwitchContent));
    }

    private void setCardArrowFrom(TosCard c, String fromCard, View parent) {
        setCardArrow(c, fromCard, parent, false);
    }

    private void setCardArrowTo(TosCard c, String fromCard, View parent) {
        setCardArrow(c, fromCard, parent, true);
    }

    private void setCardArrow(TosCard c, String fromCard, View parent, boolean reverse) {
        if (TextUtils.isEmpty(fromCard)) {
            parent.setVisibility(View.GONE);
            return;
        }

        TosCard toC = c;
        TosCard fmC = TosWiki.getCardByIdNorm(fromCard);
        if (reverse) {
            TosCard t = toC;
            toC = fmC;
            fmC = t;
        }

        parent.setVisibility(View.VISIBLE);
        ImageView to = parent.findViewById(R.id.cardEvolveTo);
        setSimpleCard(to, toC);

        ImageView from = parent.findViewById(R.id.cardEvolveFrom);
        setSimpleCard(from, fmC);
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
            SkillLite s = card.skillChange.get(i);
            View v = createSkillRow(s, vg);
            vg.addView(v);
        }
    }

    private View createSkillRow(SkillLite s, ViewGroup parent) {
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
        int[] ids = {R.id.cardPowRel1, R.id.cardPowRel2};
        View[] vs = new View[ids.length];
        // Hide all items
        for (int i = 0; i < ids.length; i++) {
            vs[i] = findViewById(ids[i]);
            vs[i].setVisibility(View.GONE);
        }
        // Show up it
        for (int i = 0; i < card.skillPowBattle.size(); i++) {
            View v = vs[i];
            v.setVisibility(View.VISIBLE);
            NameLink ni = card.skillPowBattle.get(i);
            TextView ti = v.findViewById(R.id.cardPowBattleName);
            View li = v.findViewById(R.id.cardPowBattleLink);
            setLink(ti, li, ni.name, ni.link);
        }
    }

    private void setVirLink() {
        setLink(R.id.cardVirBattleName, R.id.cardVirBattleLink, card.skillVirBattleName, card.skillVirBattleLink);
    }

    private void underline(TextView t) {
        CharSequence cs = Html.fromHtml("<u>" + t.getText() + "</u>");
        t.setText(cs);
    }

    private void setLink(@IdRes int nameId, @IdRes int linkId, String bname, String blink) {
        setLink(findViewById(nameId), findViewById(linkId), bname, blink);
    }

    private void setLink(TextView nameView, View linkView, String bname, String blink) {
        TextView t = nameView;
        CharSequence cs = Html.fromHtml("<u>" + bname + "</u>");
        t.setText(cs);
        View.OnClickListener clk = (v) -> {
            //viewLink(card.skillAmeBattleLink);
            viewLinkAsWebDialog(blink);
        };
        t.setOnClickListener(clk);
        linkView.setOnClickListener(clk);
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

    //-- Events
    private void logShare(String type) {
        Map<String, String> m = new HashMap<>();
        m.put("share", type);
        FabricAnswers.logCard(m);
    }

    private void logShowCard() {
        Map<String, String> m = new HashMap<>();
        String id = "--";
        if (card != null) {
            id = card.idNorm + " " + card.name;
        }
        m.put("card", id);
        FabricAnswers.logCard(m);
    }

    private void logFavorite(boolean add, String value) {
        Map<String, String> m = new HashMap<>();
        String key = add ? "add" : "remove";
        m.put(key, value);
        FabricAnswers.logFavorite(m);
    }

    private void logImpression() {
        Map<String, String> m = new HashMap<>();
        m.put("impression", "1");
        FabricAnswers.logCard(m);
    }
    //-- Events
}