package com.crazydev.graphbuilder.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.crazydev.graphbuilder.R;
import com.crazydev.graphbuilder.appspecific.Graph;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Identifyable;
import com.mikepenz.materialdrawer.util.UIUtils;

import java.util.ArrayList;


public class GraphItemDrawerInfo implements IDrawerItem, Identifyable<GraphItemDrawerInfo>, View.OnClickListener {

    private static int ID_HASH = 0;
    private int id_hash;

    private int identifier = -1;

    private String functionName;

    public ViewHolder viewHolder;
    private boolean enabled = false;
    private Object tag;

    private String status = "";

    private int textColor = 0;
    private int textColorRes = -1;

    private int iconColor = 0;
    private int iconColorRes = -1;

    private Drawer.Result  drawerResult;
    private OnGraphItemEvent onGraphItemEvent;

    private int color;

    private ArrayList<String> labels = new ArrayList<>();

    public GraphItemDrawerInfo(Drawer.Result  drawerResult, OnGraphItemEvent onGraphItemEvent, String functionName) {
        this.drawerResult     = drawerResult;
        this.functionName     = functionName;
        this.onGraphItemEvent = onGraphItemEvent;

        color = Color.argb(0, 0,0,0);

        this.drawerResult.addItem(this);

        if (this.drawerResult.getDrawerItems().size() != 0) {
            this.drawerResult.addItem(new DividerDrawerItem());
        }

        ID_HASH ++;
        id_hash = ID_HASH;

    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Context context;

    public void setLabels(Context context) {
        this.context = context;

        if (this.viewHolder != null && this.viewHolder.status != null) {

            Resources res = context.getResources();

            switch(this.status) {
                case Graph.STATUS_ODZ:
                    this.viewHolder.status.setText(res.getString(R.string.odz));
                    break;
                case Graph.STATUS_DERIVATIVES:
                    this.viewHolder.status.setText(res.getString(R.string.derivatives));
                    break;
                case Graph.STATUS_ASYMPTOTES:
                    this.viewHolder.status.setText(res.getString(R.string.asymptotes));
                    break;
                case Graph.STATUS_ZEROS:
                    this.viewHolder.status.setText(res.getString(R.string.zeros));
                    break;
                case Graph.STATUS_DONE:
                    this.viewHolder.status.setText(res.getString(R.string.done));
                    break;
            }

        }
    }


    public GraphItemDrawerInfo withIdentifier(int identifier) {
        this.identifier = identifier;
        return this;
    }

    public GraphItemDrawerInfo withColor(int color) {
        this.color = color;
        return this;
    }

    public GraphItemDrawerInfo setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(int identifier) {

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getType() {
        return "GRAPH_INFO_ITEM";
    }

    @Override
    public int getLayoutRes() {
        return R.layout.graph_info_drawer_item;
    }

    @Override
    public View convertView(LayoutInflater inflater, View convertView, ViewGroup parent) {

        Log.d("convertView", "CONVERT VIEW");

        Context ctx = parent.getContext();
        ViewHolder viewHolder;
        convertView = inflater.inflate(getLayoutRes(), parent, false);
        viewHolder = new ViewHolder(convertView);
        convertView.setTag(viewHolder);
        this.viewHolder = viewHolder;
        viewHolder.delete.setOnClickListener(this);
        viewHolder.table.setOnClickListener(this);

        viewHolder.name.setText(this.functionName);

        int Al = (color >> 24) & 0xff; // or color >>> 24
        int Re = (color >> 16) & 0xff;
        int Gr = (color >>  8) & 0xff;
        int Bl = (color      ) & 0xff;

        int col = Color.argb(50, Re, Gr, Bl);

        viewHolder.linearLayout.setBackgroundColor(col);

        if (this.context != null) {
            Resources res = this.context.getResources();
            switch(this.status) {

                case Graph.STATUS_ODZ:
                    this.viewHolder.status.setText(res.getString(R.string.odz));
                    break;
                case Graph.STATUS_DERIVATIVES:
                    this.viewHolder.status.setText(res.getString(R.string.derivatives));
                    break;
                case Graph.STATUS_ASYMPTOTES:
                    this.viewHolder.status.setText(res.getString(R.string.asymptotes));
                    break;
                case Graph.STATUS_ZEROS:
                    this.viewHolder.status.setText(res.getString(R.string.zeros));
                    break;
                case Graph.STATUS_DONE:
                    this.viewHolder.status.setText(res.getString(R.string.done));
                    break;
            }
        }

      //  viewHolder.status.setText(this.status);

            //  viewHolder.number.setText(String.valueOf(this.getIdentifier()));
        int color = textColor;
        if (color == 0 && textColorRes != -1) {
            color = ctx.getResources().getColor(textColorRes);
        } else if (color == 0) {
            color = UIUtils.getThemeColorFromAttrOrRes(ctx, R.attr.material_drawer_primary_text, R.color.material_drawer_primary_text);
        }
    //    viewHolder.name.setTextColor(color);
        int icon_color = iconColor;
        if (icon_color == 0 && iconColorRes != -1) {
            icon_color = ctx.getResources().getColor(iconColorRes);
        } else if (icon_color == 0) {
            icon_color = UIUtils.getThemeColorFromAttrOrRes(ctx, R.attr.material_drawer_primary_text, R.color.material_drawer_primary_text);
        }

        this.viewHolder.number.setText(String.valueOf(drawerResult.getDrawerItems().indexOf(this) / 2 + 1));

        return convertView;
    }

    public void delete() {
        ArrayList<IDrawerItem> items = drawerResult.getDrawerItems();
        int p = items.indexOf(this);

        this.drawerResult.removeItem(p);

        if (p < items.size()) {
            this.drawerResult.removeItem(p);
        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.delete:
                this.onGraphItemEvent.onDelete(this);

                break;
            case R.id.info:
                this.onGraphItemEvent.onDetails(this);
                break;
        }

    }

    public static class ViewHolder {
        private View view;
        private TextView name;
        private TextView number;
        private TextView status;
        private LinearLayout linearLayout;
        public ImageView delete;
        public ImageView table;



        private ViewHolder(View view) {
            this.view         = view;
            this.number       = (TextView) view.findViewById(R.id.number);
            this.name         = (TextView) view.findViewById(R.id.name);
            this.status       = (TextView) view.findViewById(R.id.status);
            this.delete       = (ImageView) view.findViewById(R.id.delete);
            this.table        = (ImageView) view.findViewById(R.id.info);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.linlay);

        }
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof GraphItemDrawerInfo && this.id_hash == ((GraphItemDrawerInfo) o).id_hash) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.id_hash;
    }


}
