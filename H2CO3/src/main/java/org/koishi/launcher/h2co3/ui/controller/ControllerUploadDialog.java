package org.koishi.launcher.h2co3.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.util.ArrayList;

public class ControllerUploadDialog extends H2CO3LauncherDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private final Activity activity;
    private final Callback callback;
    private final ArrayList<Integer> devices = new ArrayList<>();
    private final ArrayList<String> screenshots = new ArrayList<>();
    private H2CO3LauncherEditText name;
    private H2CO3LauncherEditText author;
    private H2CO3LauncherEditText intro;
    private H2CO3LauncherEditText description;
    private AppCompatSpinner lang;
    private H2CO3LauncherCheckBox phone;
    private H2CO3LauncherCheckBox pad;
    private H2CO3LauncherCheckBox other;
    private H2CO3LauncherTextView iconText;
    private H2CO3LauncherImageButton icon;
    private H2CO3LauncherImageButton screenshot;
    private LinearLayoutCompat screenshotLayout;
    private H2CO3LauncherButton share;
    private H2CO3LauncherButton negative;
    private String language = "all";

    public ControllerUploadDialog(@NonNull Context context, Activity activity, Controller controller, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_controller_upload);
        this.activity = activity;
        this.callback = callback;

        name = findViewById(R.id.name);
        author = findViewById(R.id.author);
        intro = findViewById(R.id.intro);
        description = findViewById(R.id.description);

        name.setText(controller.getName());
        author.setText(controller.getAuthor());
        intro.setText(controller.getDescription());
        description.setText(controller.getDescription());

        lang = findViewById(R.id.lang);
        phone = findViewById(R.id.phone);
        pad = findViewById(R.id.pad);
        other = findViewById(R.id.other);
        iconText = findViewById(R.id.icon_text);
        icon = findViewById(R.id.icon);
        screenshot = findViewById(R.id.screenshot);
        screenshotLayout = findViewById(R.id.screenshot_layout);

        ArrayList<String> langs = new ArrayList<>();
        langs.add(getContext().getString(R.string.curse_category_0));
        langs.add(getContext().getString(R.string.settings_launcher_language_english));
        langs.add(getContext().getString(R.string.settings_launcher_language_simplified_chinese));
        langs.add(getContext().getString(R.string.settings_launcher_language_russian));
        langs.add(getContext().getString(R.string.settings_launcher_language_brazilian_portuguese));
        langs.add(getContext().getString(R.string.settings_launcher_language_persian));
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_auto_tint, langs);
        langAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        lang.setAdapter(langAdapter);
        lang.setSelection(0);
        lang.setOnItemSelectedListener(this);

        phone.setOnCheckedChangeListener(this);
        pad.setOnCheckedChangeListener(this);
        other.setOnCheckedChangeListener(this);
        phone.setChecked(true);

        iconText.setText("");
        icon.setOnClickListener(this);

        screenshot.setOnClickListener(this);
        screenshotLayout.removeAllViews();

        share = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        share.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == icon) {
            ArrayList<String> suffix = new ArrayList<>();
            suffix.add("png");
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setExternalSelection(false);
            builder.setLibMode(LibMode.FILE_CHOOSER);
            builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
            builder.setSuffix(suffix);
            builder.create().browse(activity, RequestCodes.SELECT_CONTROLLER_ICON_CODE, ((requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_CONTROLLER_ICON_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> results = FileBrowser.getSelectedFiles(data);
                    if (results.size() == 1) {
                        iconText.setText(results.get(0));
                    }
                }
            }));
        }
        if (view == screenshot) {
            if (screenshots.size() < 16) {
                ArrayList<String> suffix = new ArrayList<>();
                suffix.add("png");
                FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
                builder.setExternalSelection(false);
                builder.setLibMode(LibMode.FILE_CHOOSER);
                builder.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
                builder.setSuffix(suffix);
                builder.create().browse(activity, RequestCodes.SELECT_CONTROLLER_SCREENSHOT_CODE, ((requestCode, resultCode, data) -> {
                    if (requestCode == RequestCodes.SELECT_CONTROLLER_SCREENSHOT_CODE && resultCode == Activity.RESULT_OK && data != null) {
                        ArrayList<String> results = FileBrowser.getSelectedFiles(data);
                        if (!results.isEmpty()) {
                            results.forEach(r -> {
                                if (!screenshots.contains(r) && screenshots.size() < 16) {
                                    screenshots.add(r);
                                    Item item = new Item(getContext(), r, screenshots::remove);
                                    screenshotLayout.addView(item.createView(), new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                }
                            });
                        }
                    }
                }));
            } else {
                Toast.makeText(getContext(), getContext().getString(R.string.control_info_screenshot_max), Toast.LENGTH_SHORT).show();
            }
        }

        if (view == share) {
            if (StringUtils.isBlank(name.getText().toString())
                    || StringUtils.isBlank(author.getText().toString())
                    || StringUtils.isBlank(intro.getText().toString())
                    || StringUtils.isBlank(description.getText().toString())
                    || devices.isEmpty()
                    || screenshots.isEmpty()
                    || StringUtils.isBlank(iconText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
            } else {
                callback.onPositive(name.getText().toString(),
                        author.getText().toString(),
                        intro.getText().toString(),
                        description.getText().toString(),
                        language,
                        devices,
                        screenshots,
                        iconText.getText().toString());
            }
        }
        if (view == negative) {
            dismiss();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                language = "all";
                break;
            case 1:
                language = "en";
                break;
            case 2:
                language = "zh_CN";
                break;
            case 3:
                language = "ru";
                break;
            case 4:
                language = "pt";
                break;
            case 5:
                language = "fa";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = -1;
        if (compoundButton == phone) {
            id = 0;
        }
        if (compoundButton == pad) {
            id = 1;
        }
        if (compoundButton == other) {
            id = 2;
        }
        if (id != -1) {
            if (b) {
                devices.add(id);
            } else {
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i) == id) {
                        devices.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public interface Callback {
        void onPositive(String name, String author, String intro, String description, String lang, ArrayList<Integer> devices, ArrayList<String> screenshots, String iconPath);
    }

    static class Item implements View.OnClickListener {

        private final Context context;
        private final String text;
        private final RemoveListener listener;

        private View root;
        private H2CO3LauncherTextView textView;
        private H2CO3LauncherImageButton imageButton;

        public Item(Context context, String text, RemoveListener listener) {
            this.context = context;
            this.text = text;
            this.listener = listener;
        }

        public View createView() {
            if (root == null) {
                root = LayoutInflater.from(context).inflate(R.layout.item_screenshot_path, null);
                textView = root.findViewById(R.id.text);
                imageButton = root.findViewById(R.id.delete);
                textView.setText(text);
                imageButton.setOnClickListener(this);
            }
            return root;
        }

        @Override
        public void onClick(View view) {
            if (view == imageButton) {
                listener.remove(text);
                ((LinearLayoutCompat) root.getParent()).removeView(root);
            }
        }

        public interface RemoveListener {
            void remove(String text);
        }
    }
}
