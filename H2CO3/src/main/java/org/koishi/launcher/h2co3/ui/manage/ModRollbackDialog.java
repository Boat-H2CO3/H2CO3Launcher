package org.koishi.launcher.h2co3.ui.manage;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.mod.LocalModFile;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

import java.util.List;

public class ModRollbackDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private ListView listView;

    private H2CO3LauncherButton negative;

    public ModRollbackDialog(@NonNull Context context, List<LocalModFile> list, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_rollback_mod);

        listView = findViewById(R.id.list);
        negative = findViewById(R.id.negative);
        negative.setOnClickListener(this);

        ModOldVersionListAdapter adapter = new ModOldVersionListAdapter(getContext(), this, list, callback);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == negative) {
            dismiss();
        }
    }

    public interface Callback {
        void onOldVersionSelect(LocalModFile localModFile);
    }
}
