package org.koishi.launcher.h2co3.ui.fragment.terminal;

import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.view.ViewGroup;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.shell.ShellUtil;
import org.koishi.launcher.h2co3.ui.fragment.H2CO3Fragment;
import org.koishi.launcher.h2co3.resources.component.LogWindow;

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
        initializeLogWindow();
        initializeShellUtil();
        setupTextWatcher();
        return view;
    }

    private void initializeLogWindow() {
        logWindow.appendLog("欢迎使用 Boat_H2CO3！\n");
        logWindow.appendLog("这是一个命令行界面！\n");
    }

    private void initializeShellUtil() {
        shellUtil = new ShellUtil(new File(H2CO3Tools.FILES_DIR).getParent(), output -> logWindow.appendLog("\t" + output + "\n"));
        shellUtil.start();
    }

    private void setupTextWatcher() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String cmd = editable.toString();
                if (cmd.endsWith("\n")) {
                    logWindow.appendLog("->" + cmd);
                    // 不执行clear命令的情况下，追加其他命令
                    if (!cmd.contains("clear")) {
                        shellUtil.append(cmd.trim()); // 去掉换行符并追加指令
                    }
                    editText.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (shellUtil != null) {
            shellUtil.interrupt();
        }
    }
}
