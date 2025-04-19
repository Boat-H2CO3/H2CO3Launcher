package org.koishi.launcher.h2co3.control;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.setting.Controllers;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

public class SelectControllerDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final Callback callback;
    private final ObjectProperty<Controller> selectedController = new SimpleObjectProperty<Controller>() {
        {
            Controllers.getControllers().addListener(onInvalidating(this::invalidated));
        }

        @Override
        protected void invalidated() {
            if (!Controllers.isInitialized()) return;

            Controller controller = get();
            if (Controllers.getControllers().isEmpty()) {
                if (controller != null) {
                    set(null);
                }
            } else {
                if (!Controllers.getControllers().contains(controller)) {
                    set(Controllers.getControllers().get(0));
                }
            }
        }
    };
    private ListView listView;
    private H2CO3LauncherButton positive;

    public SelectControllerDialog(@NonNull Context context, String id, Callback callback) {
        super(context);
        this.callback = callback;
        setContentView(R.layout.dialog_select_controller);
        setCancelable(false);
        listView = findViewById(R.id.list);
        positive = findViewById(R.id.positive);
        positive.setOnClickListener(this);

        boolean set = true;
        for (Controller controller : Controllers.getControllers()) {
            if (controller.getId().equals(id)) {
                setSelectedController(controller);
                set = false;
            }
        }
        if (set) {
            setSelectedController(null);
        }

        refreshList();
    }

    public Controller getSelectedController() {
        return selectedController.get();
    }

    public void setSelectedController(Controller selectedController) {
        this.selectedController.set(selectedController);
    }

    public void refreshList() {
        SelectableControllerListAdapter adapter = new SelectableControllerListAdapter(getContext(), Controllers.controllersProperty(), this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            callback.onControllerSelected(selectedController.get());
            dismiss();
        }
    }

    public interface Callback {
        void onControllerSelected(Controller controller);
    }
}
