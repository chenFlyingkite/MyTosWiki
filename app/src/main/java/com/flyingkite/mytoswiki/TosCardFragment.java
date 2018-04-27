package com.flyingkite.mytoswiki;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingkite.library.FilesHelper;
import com.flyingkite.library.IOUtil;
import com.flyingkite.library.ListUtil;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.library.TicTac2;
import com.flyingkite.mytoswiki.data.TosCard;
import com.flyingkite.mytoswiki.library.CardLibrary;
import com.flyingkite.mytoswiki.tos.query.TosCardCondition;
import com.flyingkite.mytoswiki.tos.query.TosSelectAttribute;
import com.flyingkite.util.DialogManager;
import com.flyingkite.util.TextEditorDialog;
import com.flyingkite.util.WaitingDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class TosCardFragment extends BaseFragment {
    public static final String TAG = "TosCardFragment";
    private RecyclerView cardsRecycler;
    private CardLibrary cardLib;
    private View sortMenu;
    private PopupWindow sortWindow;
    private TextView tosInfo;
    private TosCard[] allCards;
    private View sortReset;
    private ViewGroup sortAttributes;
    private ViewGroup sortRace;
    private ViewGroup sortStar;
    private RadioGroup sortCommon;
    private RadioGroup sortCassandra;
    private RadioGroup sortSpecial;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tosInfo = findViewById(R.id.tosInfo);

        cardsRecycler = findViewById(R.id.tosRecycler);
        cardLib = new CardLibrary(cardsRecycler);
        sortMenu = findViewById(R.id.tosSortMenu);
        initSortMenu();
        initToolIcons();

        new ParseCardsTask().executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    private void initToolIcons() {
        findViewById(R.id.tosGoTop).setOnClickListener((v) -> {
            int index = 0;
            cardLib.recycler.scrollToPosition(index);
        });

        findViewById(R.id.tosGoBottom).setOnClickListener((v) -> {
            int index = cardLib.cardAdapter.getItemCount() - 1;
            cardLib.recycler.scrollToPosition(index);
        });

        findViewById(R.id.tosSave).setOnClickListener((v) -> {
            View view = cardsRecycler;
            //File folder = Environment.getExternalStoragePublicDirectory()
            File folder = App.me.getExternalCacheDir();
            String name = folder.getAbsolutePath() + File.separator + "1.png";
            LogE("Save to %s", name);

            @SuppressLint("StaticFieldLeak")
            SaveViewToBitmapTask task = new SaveViewToBitmapTask(view, name){
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    MediaScannerConnection.scanFile(getActivity(), new String[]{name}, null,
                        (path, uri) -> {
                            LogE("Scanned %s\n  as -> %s", path, uri);
                            sendUriIntent(uri, "image/png");
                        });
                }
            };
            task.executeOnExecutor(ThreadUtil.cachedThreadPool);
        });

        findViewById(R.id.tosTexts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TextEditorDialog(TosCardFragment.this::getActivity).show();
            }
        });
    }

    private void initCardLibrary() {
        int n = allCards == null ? 0 : allCards.length;
        showToast(R.string.cards_read, n);
        tosInfo.setText(getString(R.string.cards_selection, n, n));
        cardLib.setDataSet(allCards
                , (position, card) -> {
                    DialogManager.GenericViewBuilder.InflateListener onInflate = (v, dialog) -> {
                        ImageView icon = v.findViewById(R.id.cardIcon);
                        ImageView image = v.findViewById(R.id.cardImage);
                        TextView info = v.findViewById(R.id.cardInfo);

                        Glide.with(icon).load(card.icon).into(icon);
                        Glide.with(image).load(card.bigImage).into(image);
                        Gson g = new GsonBuilder().setPrettyPrinting().create();
                        String s = g.toJson(card, TosCard.class);
                        info.setText(s);
                    };

                    new DialogManager.GenericViewBuilder(getActivity(), R.layout.dialog_card, onInflate).buildAndShow();

                }, (selected, total) ->  {
                    tosInfo.setText(getString(R.string.cards_selection, selected, total));
                }
        );
        test();
    }

    private void test() {
        if (true) return;

        int cnt = 0;
        for (TosCard c : allCards) {
            String desc = c.skillDesc + " & " + c.skillDesc2;// + " & " + c.skillLeaderDesc;

            String[] names = {"可任意移動符石而不會發動消除", "任意移動符石", "不會發動消除"};

            boolean found = false;
            for (int i = 0; i < names.length && !found; i++) {
                if (desc.contains(names[i])) {
                    LogE("#%s -> @%d -> %s / %s", c.idNorm, i, c.name, desc);
                    found = true;
                    cnt++;
                }
            }
        }
        LogE("%s cards", cnt);
    }

    private void viewLink(TosCard card) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(card.wikiLink));
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendUriIntent(Uri uri, String type) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_STREAM, uri);
        it.setType(type);
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initSortMenu() {
        // Create MenuWindow
        View menu = LayoutInflater.from(getActivity()).inflate(R.layout.popup_tos_sort, (ViewGroup) getView(), false);
        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        sortWindow = new PopupWindow(menu, wrap, wrap, true);
        sortWindow.setOutsideTouchable(true);
        sortWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        sortMenu.setOnClickListener(v -> {
            sortWindow.showAsDropDown(v);
        });

        initSortReset(menu);
        initSortByAttribute(menu);
        initSortByRace(menu);
        initSortByCassandra(menu);
        initSortByStar(menu);
        initSortByCommon(menu);
        initSortBySpecial(menu);
    }

    private void initSortReset(View menu) {
        sortReset = menu.findViewById(R.id.sortReset);
        sortReset.setOnClickListener(this::clickReset);
    }

    private void initSortByAttribute(View menu) {
        sortAttributes = menu.findViewById(R.id.sortAttributes);
        ViewGroup vg = sortAttributes;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickAttr);
        }
    }

    private void initSortByRace(View menu) {
        sortRace = menu.findViewById(R.id.sortRaces);

        ViewGroup vg = sortRace;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickRace);
        }
    }

    private void initSortByCassandra(View menu) {
        sortCassandra = menu.findViewById(R.id.sortCassandraList);

        ViewGroup vg = sortCassandra;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickCassandra);
        }
    }

    private void initSortByStar(View menu) {
        sortStar = menu.findViewById(R.id.sortStar);

        ViewGroup vg = sortStar;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickStar);
        }
    }

    private void initSortByCommon(View menu) {
        sortCommon = menu.findViewById(R.id.sortCommonList);

        ViewGroup vg = sortCommon;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickCommon);
        }
    }

    private void initSortBySpecial(View menu) {
        sortSpecial = menu.findViewById(R.id.sortSpecialList);

        ViewGroup vg = sortSpecial;
        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            w.setOnClickListener(this::clickSpecial);
        }
    }

    private void clickReset(View v) {
        ViewGroup[] vgs = {sortAttributes, sortRace, sortStar};
        for (ViewGroup vg : vgs) {
            setAllChildrenSelected(vg, false);
        }
        sortCommon.check(R.id.sortCommonNo);
        sortCassandra.check(R.id.sortCassandraNo);
        sortSpecial.check(R.id.sortSpecialNo);

        applySelection();
    }

    private void clickAttr(View v) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(sortAttributes, true)) {
            setAllChildrenSelected(sortAttributes, false);
        }

        applySelection();
    }

    private void clickRace(View v) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(sortRace, true)) {
            setAllChildrenSelected(sortRace, false);
        }

        applySelection();
    }

    private void clickCassandra(View v) {
        int id = v.getId();
        sortCassandra.check(id);
        if (id != R.id.sortCassandraNo) {
            sortCommon.check(R.id.sortCommonNo);
        }
        sortCommon.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void clickStar(View v) {
        v.setSelected(!v.isSelected());
        // Deselect all if all selected
        if (isAllAsSelected(sortStar, true)) {
            setAllChildrenSelected(sortStar, false);
        }

        applySelection();
    }

    private void clickSpecial(View v) {
        sortSpecial.check(v.getId());

        applySelection();
    }

    private void clickCommon(View v) {
        int id = v.getId();
        sortCommon.check(id);
        if (id != R.id.sortCommonNo) {
            sortCassandra.check(R.id.sortCassandraNo);
        }
        sortCassandra.setEnabled(id != R.id.sortCassandraNo);

        applySelection();
    }

    private void applySelection() {
        // Attribute
        List<String> attrs = new ArrayList<>();
        getSelectTags(sortAttributes, attrs);
        // Race
        List<String> races = new ArrayList<>();
        getSelectTags(sortRace, races);
        // Star
        List<String> stars = new ArrayList<>();
        getSelectTags(sortStar, stars);

        LogE("---------");
        LogE("sel T = %s", attrs);
        LogE("sel R = %s", races);
        LogE("sel S = %s", stars);

        TosCardCondition cond = new TosCardCondition().attr(attrs).race(races).star(stars);
        cardLib.cardAdapter.setSelection(new TosSelect(Arrays.asList(allCards), cond));
    }

    private void getSelectTags(ViewGroup vg, List<String> result) {
        if (result == null) {
            result = new ArrayList<>();
        }
        int n = vg.getChildCount();

        List<String> all = new ArrayList<>();
        boolean added = false;
        for (int i = 0; i < n; i++) {
            View w = vg.getChildAt(i);
            String tag = w.getTag().toString();
            if (w.isSelected()) {
                added = true;
                result.add(tag);
            }
            all.add(tag);
        }
        // If no children is added, add all the child tags
        if (!added) {
            result.addAll(all);
        }
    }

    private void setAllChildrenSelected(ViewGroup vg, boolean sel) {
        if (vg == null) return;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            vg.getChildAt(i).setSelected(sel);
        }
    }

    private boolean isAllAsSelected(ViewGroup vg, boolean selected) {
        if (vg == null) return false;

        int n = vg.getChildCount();
        for (int i = 0; i < n; i++) {
            if (vg.getChildAt(i).isSelected() != selected) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getPageLayoutId() {
        return R.layout.fragment_tos_card;
    }

    private class ParseCardsTask extends AsyncTask<Void, Void, Void> {
        private WaitingDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new WaitingDialog.Builder(getActivity()).buildAndShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            allCards = TosWiki.parseCards(getActivity().getAssets());
            check();
            return null;
        }

        private void check() {
            HashSet<String> attr = new HashSet<>();
            for (TosCard c : allCards) {
                attr.add(c.attribute);
            }
            LogE("attr = %s", attr);

            HashSet<String> race = new HashSet<>();
            for (TosCard c : allCards) {
                race.add(c.race);
            }
            LogE("race = %s", race);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            initCardLibrary();
        }
    }

    private class SaveViewToBitmapTask extends AsyncTask<Void, Void, Void> {
        private View view;
        private String savedName;
        private int width;
        private int height;
        private WaitingDialog w;
        private TicTac2 tt = new TicTac2();

        public SaveViewToBitmapTask(View v, String filename) {
            view = v;
            savedName = filename;
            ofSize(v.getWidth(), v.getHeight());
        }

        public SaveViewToBitmapTask ofSize(int w, int h) {
            width = w;
            height = h;
            return this;
        }

        @Override
        protected void onPreExecute() {
            w = new WaitingDialog.Builder(getActivity(), true)
                    .onCancel((dialog) -> {
                        cancel(true);
                    }).buildAndShow();
            tt.tic();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (view == null || savedName == null) {
                LogE("Cannot save bitmap : %s, %s", view, savedName);
                return null;
            }

            // 1. [<10ms] Create new bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (isCancelled()) return null;

            // 2. [100ms] Let view draws to the bitmap
            Canvas c = new Canvas(bitmap);
            view.draw(c);
            if (isCancelled()) return null;

            // 3. [<10ms] Create output file
            File f = new File(savedName);
            File fp = f.getParentFile();
            if (fp != null) {
                fp.mkdirs();
            }
            FilesHelper.ensureDelete(f);
            if (isCancelled()) return null;

            // 4. [~1kms] Writing bitmap to file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                IOUtil.closeIt(fos);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tt.tac("Save done");
            if (w != null) {
                w.dismiss();
            }
        }
    }

    private class TosSelect extends TosSelectAttribute {
        private final String[] freemove = getResources().getStringArray(R.array.cards_freemove_keys);
        private final String[] commonRace = getResources().getStringArray(R.array.cards_common_keys);

        public TosSelect(List<TosCard> source, TosCardCondition condition) {
            super(source, condition);
        }

        @Override
        public boolean onSelect(TosCard c){
            List<String> attrs = select.getAttr();
            List<String> races = select.getRace();
            List<String> stars = select.getStar();

            return attrs.contains(c.attribute)
                    && races.contains(c.race)
                    && stars.contains("" + c.rarity)
                    && selectForFreeMove(c)
            ;
        }

        private boolean selectForFreeMove(TosCard c) {
            String key = c.skillDesc + " & " + c.skillDesc2;
            final int id = sortSpecial.getCheckedRadioButtonId();
            boolean result = false;
            switch (id) {
                case R.id.sortSpecialFreeMove:
                    for (int i = 0; i < freemove.length && !result; i++) {
                        if (key.contains(freemove[i])) {
                            result = true;
                        }
                    }
                    break;
                default:
                    result = true;
            }
            return result;
        }

        @NonNull
        @Override
        public List<Integer> sort(@NonNull List <Integer> result) {
            Comparator<Integer> cmp;
            cmp = getCommonComparator();
            if (cmp == null) {
                cmp = getCassandraComparator();
            }

            // Apply the comparator on result
            if (cmp != null) {
                Collections.sort(result, cmp);
            }
            return result;
        }

        @Override
        public List<String> getMessages(List<Integer> result) {
            List<String> messages;
            messages = getCommonMessages(result);
            if (messages == null) {
                messages = getCassandraMessages(result);
            }
            return messages;
        }

        private Comparator<Integer> getCassandraComparator() {
            // Create comparator
            int id = sortCassandra.getCheckedRadioButtonId();
            switch (id) {
                case R.id.sortCassandraAttack:
                    return (o1, o2) -> {
                        boolean dsc = true;
                        TosCard c1 = data.get(o1);
                        TosCard c2 = data.get(o2);
                        double atk1 = c1.maxAttack + c1.maxRecovery * 3.5;
                        double atk2 = c2.maxAttack + c2.maxRecovery * 3.5;
                        //logCard("#1", c1);
                        //logCard("#2", c2);
                        if (dsc) {
                            return Double.compare(atk2, atk1);
                        } else {
                            return Double.compare(atk1, atk2);
                        }
                    };
                case R.id.sortCassandraRatio:
                    return (o1, o2) -> {
                        boolean dsc = true;
                        TosCard c1 = data.get(o1);
                        TosCard c2 = data.get(o2);
                        double atk1 = 1 + c1.maxRecovery * 3.5 / c1.maxAttack;
                        double atk2 = 1 + c2.maxRecovery * 3.5 / c2.maxAttack;
                        if (dsc) {
                            return Double.compare(atk2, atk1);
                        } else {
                            return Double.compare(atk1, atk2);
                        }
                    };
                default:
                    break;
            }
            return null;
        }

        private Comparator<Integer> getCommonComparator() {
            // Create comparator
            int id = sortCommon.getCheckedRadioButtonId();
            if (id == RadioGroup.NO_ID || id == R.id.sortCommonNo) {
                return null;
            }
            return (o1, o2) -> {
                boolean dsc = true;
                TosCard c1 = data.get(o1);
                TosCard c2 = data.get(o2);
                long v1 = -1, v2 = -1;
                //logCard("#1", c1);
                //logCard("#2", c2);

                switch (id) {
                    case R.id.sortCommonMaxHP:
                        v1 = c1.maxHP;
                        v2 = c2.maxHP;
                        break;
                    case R.id.sortCommonMaxAttack:
                        v1 = c1.maxAttack;
                        v2 = c2.maxAttack;
                        break;
                    case R.id.sortCommonMaxRecovery:
                        v1 = c1.maxRecovery;
                        v2 = c2.maxRecovery;
                        break;
                    case R.id.sortCommonMaxSum:
                        v1 = c1.maxHP + c1.maxAttack + c1.maxRecovery;
                        v2 = c2.maxHP + c2.maxAttack + c2.maxRecovery;
                        break;
                    case R.id.sortCommonRace:
                        dsc = false;
                        v1 = ListUtil.indexOf(commonRace, c1.race);
                        v2 = ListUtil.indexOf(commonRace, c2.race);
                        break;
                }

                if (dsc) {
                    return Long.compare(v2, v1);
                } else {
                    return Long.compare(v1, v2);
                }
            };
        }

        private void logCard(String prefix, TosCard c) {
            // https://stackoverflow.com/questions/16946694/how-do-i-align-the-decimal-point-when-displaying-doubles-and-floats
            // Align float point is %(x+y+1).yf
            LogE("%s %s -> %4s + %4s * 3.5 = %7.1f => %s"
                , prefix, c.idNorm, c.maxAttack, c.maxRecovery
                , c.maxAttack + c.maxRecovery * 3.5, c.name
            );
        }

        private List<String> getCassandraMessages(List<Integer> result) {
            List<String> message = null;
            TosCard c;
            String msg;
            // Create Message
            int id = sortCassandra.getCheckedRadioButtonId();
            switch (id) {
                case R.id.sortCassandraAttack:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = c.maxAttack + c.maxRecovery * 3.5;
                        msg = String.format("%.1f", atk);
                        message.add(msg);
                    }
                    break;
                case R.id.sortCassandraRatio:
                    message = new ArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        c = data.get(result.get(i));
                        double atk = 1 + c.maxRecovery * 3.5 / c.maxAttack;
                        msg = String.format("%.2f", atk);
                        message.add(msg);
                    }
                    break;
                default:
                    break;
            }
            return message;
        }

        private List<String> getCommonMessages(List<Integer> result) {
            List<String> message = new ArrayList<>();
            TosCard c;
            String msg;
            // Create Message
            boolean added = false;
            int id = sortCommon.getCheckedRadioButtonId();

            for (int i = 0; i < result.size(); i++) {
                c = data.get(result.get(i));
                msg = null;
                switch (id) {
                    case R.id.sortCommonMaxHP:
                        msg = String.valueOf(c.maxHP);
                        break;
                    case R.id.sortCommonMaxAttack:
                        msg = String.valueOf(c.maxAttack);
                        break;
                    case R.id.sortCommonMaxRecovery:
                        msg = String.valueOf(c.maxRecovery);
                        break;
                    case R.id.sortCommonMaxSum:
                        msg = String.valueOf(c.maxHP + c.maxAttack + c.maxRecovery);
                        break;
                    case R.id.sortCommonRace:
                        msg = c.id + "\n" + c.race;
                        break;
                    default:
                }

                if (msg != null) {
                    added = true;
                    message.add(msg);
                }
            }

            if (added) {
                return message;
            } else {
                return null;
            }
        }
    }
}
