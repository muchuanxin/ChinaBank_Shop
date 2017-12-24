package com.xd.aselab.chinabank_shop.util;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.Public_Item;

import java.util.List;

/**
 * Created by Dorise on 2017/9/26.
 */

public class PublicItemAdapter extends ArrayAdapter<Public_Item> {
    private int resourceId;

    public PublicItemAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        resourceId=resource;
    }

    public PublicItemAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Public_Item item=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView text=(TextView) view.findViewById(R.id.text);
        text.setText(item.get_public_head());
        TextView text2=(TextView) view.findViewById(R.id.text2);
        text2.setText(item.get_public_time());
        return view;
    }
}

