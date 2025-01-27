package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3RecycleAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class ProfileListAdapter extends H2CO3RecycleAdapter<Profile> {

    private boolean isItemClickable = true;

    public ProfileListAdapter(Context context, ObservableList<Profile> list) {
        super(list, context);
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        Profile profile = data.get(position);
        MaterialCardView parent = (MaterialCardView) holder.getView(R.id.parent);
        ViewGroup.LayoutParams layoutParams = holder.getView(R.id.parent).getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.getView(R.id.parent).setLayoutParams(layoutParams);

        parent.setStrokeWidth(profile == Profiles.getSelectedProfile() ? 10 : 0);
        ((H2CO3LauncherTextView) holder.getView(R.id.name)).setText(profile.getName());
        ((H2CO3LauncherTextView) holder.getView(R.id.path)).setText(profile.getGameDir().getAbsolutePath());

        parent.setOnClickListener(view -> {
            if (isItemClickable) {
                isItemClickable = false;
                Profiles.setSelectedProfile(profile);

                for (int i = 0; i < getItemCount(); i++) {
                    notifyItemChanged(i);
                }

                parent.postDelayed(() -> isItemClickable = true, 200);
            }
        });

        holder.getView(R.id.delete).setOnClickListener(view -> {
            Profiles.getProfiles().remove(profile);
            H2CO3MainActivity.getInstance().uiManager.getMainUI().refreshProfile();
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_profile;
    }
}