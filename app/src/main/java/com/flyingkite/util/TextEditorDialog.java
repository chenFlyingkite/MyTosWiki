package com.flyingkite.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.flyingkite.library.GsonUtil;
import com.flyingkite.library.ThreadUtil;
import com.flyingkite.mytoswiki.App;
import com.flyingkite.mytoswiki.R;
import com.flyingkite.mytoswiki.data.Texts;
import com.flyingkite.mytoswiki.library.TextsAdapter;
import com.google.gson.Gson;

import java.io.File;

public class TextEditorDialog {
    public interface DialogOwner {
        Activity getActivity();
    }

    private DialogOwner owner;
    private Texts texts;

    // Views
    private EditText newText;
    private EditText mainText;
    private RecyclerView textRecycler;
    private LinearLayoutManager textLlm;
    private TextsAdapter textAdapter;

    // The file of text pool
    private File getTextPoolFile() {
        File folder = App.me.getExternalCacheDir();
        return new File(folder, "text.txt");
    }

    public TextEditorDialog(DialogOwner own) {
        owner = own;
    }

    public void show() {
        new DialogManager.GenericViewBuilder(owner.getActivity(), R.layout.dialog_text_editor, this::onFinishInflate).buildAndShow();
    }

    private void onFinishInflate(View view, AlertDialog dialog) {
        newText = view.findViewById(R.id.tedNewString);
        mainText = view.findViewById(R.id.tedText);
        textRecycler = view.findViewById(R.id.tedRecycler);
        textLlm = new LinearLayoutManager(getActivity());

        EditText txt = mainText;

        view.findViewById(R.id.tedSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txt.getText().toString();
                Intent it = new Intent(Intent.ACTION_SEND);
                it.putExtra(Intent.EXTRA_TEXT, msg);
                it.setType("text/plain");
                try {
                    Activity a = getActivity();
                    a.startActivity(Intent.createChooser(it, a.getString(R.string.share_to)));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        view.findViewById(R.id.tedDelete).setOnClickListener(v -> {
            txt.setText("");
        });

        view.findViewById(R.id.tedPrev).setOnClickListener(v -> {
            if (txt.getSelectionStart() > 0) {
                txt.setSelection(txt.getSelectionStart() - 1);
            }
        });

        view.findViewById(R.id.tedNext).setOnClickListener(v -> {
            if (txt.getSelectionStart() < txt.getText().length()) {
                txt.setSelection(txt.getSelectionStart() + 1);
            }
        });

        view.findViewById(R.id.tedSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GsonUtil.writeFile(getTextPoolFile(), new Gson().toJson(texts));
                App.showToast(R.string.complete);

//                new AlertDialog.Builder(getActivity())
//                        .setMessage(R.string.app_name)
//                        .setNegativeButton(null, null)
//                        .setPositiveButton(null, null)
//                        .setOnCancelListener(null)
//                        .setOnDismissListener(null)
//                        .show();
            }
        });

        view.findViewById(R.id.tedClear).setOnClickListener(v -> {
            texts.data.clear();
            textAdapter.notifyDataSetChanged();
            App.showToast(R.string.complete);
        });

        View vw = view.findViewById(R.id.tedExpand);
        View vp = view.findViewById(R.id.tedPoolString);
        vw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = vp.getVisibility() == View.VISIBLE;
                if (expanded) {
                    // Collapse it
                    vw.setRotation(90);
                    vp.setVisibility(View.GONE);
                } else {
                    // Expand it
                    vw.setRotation(270);
                    vp.setVisibility(View.VISIBLE);
                }
            }
        });

        view.findViewById(R.id.tedAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = newText.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    return;
                }

                texts.data.add(s);
                textAdapter.notifyItemInserted(texts.data.size() - 1);
            }
        });

        new LoadTextAsyncTask().executeOnExecutor(ThreadUtil.cachedThreadPool);
    }

    private void updateTextData() {
        if (textAdapter == null) {
            textAdapter = new TextsAdapter();
        }
        textAdapter.setDataList(texts.data);
        textAdapter.setItemListener(new TextsAdapter.ItemListener() {
            @Override
            public void onClick(String data, TextsAdapter.TextsVH vh, int position) {
                mainText.getText().append(data).append(" ");
            }

            @Override
            public void onDelete(String data, TextsAdapter.TextsVH vh, int position) {
                if (position < 0 || texts.data.size() - 1 < position) {
                    return;
                }

                texts.data.remove(position);
                textAdapter.notifyItemRemoved(position);
            }
        });
        textRecycler.setLayoutManager(textLlm);
        textRecycler.setAdapter(textAdapter);
        textAdapter.notifyDataSetChanged();
    }

    private Activity getActivity() {
        return owner.getActivity();
    }

    private class LoadTextAsyncTask extends AsyncTask<Void, Void, Texts> {
        @Override
        protected Texts doInBackground(Void... voids) {
            return GsonUtil.loadFile(getTextPoolFile(), Texts.class);
        }

        @Override
        protected void onPostExecute(Texts data) {
            texts = data != null ? data : new Texts();
            updateTextData();
        }
    }
}
