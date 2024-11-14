package org.koishi.launcher.h2co3.ui.fragment.terminal;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.shell.ShellUtil;
import org.koishi.launcher.h2co3.resources.component.LogWindow;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;

import java.io.File;

public class TerminalFragment extends H2CO3Fragment implements View.OnClickListener {

    private View view;
    private LogWindow logWindow;
    private TextInputEditText editText;
    private ShellUtil shellUtil;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_terminal, container, false);
        logWindow = view.findViewById(R.id.shell_log_window);
        editText = view.findViewById(R.id.shell_input);
        logWindow.appendLog("Welcome to use H2CO3Launcher!\n");
        logWindow.appendLog("Here is the shell command line!\n");
        shellUtil = new ShellUtil(new File(H2CO3Tools.FILES_DIR).getParent(), output -> logWindow.appendLog("\t" + output + "\n"));
        shellUtil.start();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String cmd = editText.getText() != null ? editText.getText().toString() : "";
                if (cmd.endsWith("\n")) {
                    logWindow.appendLog("->" + cmd);
                    if (!cmd.contains("clear")) {
                        shellUtil.append(cmd);
                    }
                    editText.setText("");
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        shellUtil.interrupt();
    }
}