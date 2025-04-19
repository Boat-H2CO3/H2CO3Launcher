package org.koishi.launcher.h2co3.control;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.QuickInputTexts;
import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

public class QuickInputDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final GameMenu menu;

    private H2CO3LauncherButton addText;
    private H2CO3LauncherButton positive;

    private ListView listView;

    public QuickInputDialog(@NonNull Context context, GameMenu menu) {
        super(context);
        this.menu = menu;
        setCancelable(false);
        setContentView(R.layout.dialog_quick_input);

        addText = findViewById(R.id.add_text);
        positive = findViewById(R.id.positive);
        addText.setOnClickListener(this);
        positive.setOnClickListener(this);

        listView = findViewById(R.id.list);
        refreshList(menu);
    }

    private void refreshList(GameMenu menu) {
        InputTextAdapter adapter = new InputTextAdapter(getContext(), QuickInputTexts.getInputTexts(), string -> {
            if (StringUtils.isNotBlank(string)) {
                if (menu.getCursorMode() == H2CO3LauncherBridge.CursorEnabled) {
                    for (int i = 0; i < string.length(); i++) {
                        menu.getInput().sendChar(string.charAt(i));
                    }
                } else {
                    menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_T, true);
                    menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_T, false);
                    new Handler().postDelayed(() -> {
                        for (int i = 0; i < string.length(); i++) {
                            menu.getInput().sendChar(string.charAt(i));
                        }
                        menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ENTER, true);
                        menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ENTER, false);
                    }, 50);
                }
            }
            dismiss();
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == addText) {
            AddInputTextDialog dialog = new AddInputTextDialog(getContext(), () -> refreshList(menu));
            dialog.show();
        }
        if (v == positive) {
            dismiss();
        }
    }
}
