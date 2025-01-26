package org.koishi.launcher.h2co3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.view.PermissionRequestCard;
import org.koishi.launcher.h2co3library.component.H2CO3RecycleAdapter;

import java.util.List;

public class PermissionRequestCardAdapter extends H2CO3RecycleAdapter<PermissionRequestCard> {

    private final LayoutInflater inflater;

    public PermissionRequestCardAdapter(Context context, List<PermissionRequestCard> permissionRequestCards) {
        super(permissionRequestCards, context);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public H2CO3RecycleAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_permission_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void bindData(BaseViewHolder holder, int position) {
        PermissionRequestCard permissionRequestCard = data.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.shapeableImageView.setImageResource(permissionRequestCard.getIconRes());
        viewHolder.title.setText(permissionRequestCard.getTitleRes());
        viewHolder.description.setText(permissionRequestCard.getDescriptionRes());
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_permission_request;
    }

    public static class ViewHolder extends BaseViewHolder {
        public ImageView shapeableImageView;
        public TextView title;
        public TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.shapeableImageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
        }
    }
}