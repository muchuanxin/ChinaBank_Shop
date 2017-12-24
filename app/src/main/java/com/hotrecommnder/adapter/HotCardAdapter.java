package com.hotrecommnder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.HotCardVo;
import com.xd.aselab.chinabank_shop.util.ImageLoader;

import java.util.ArrayList;

/**
 * Created by liuhaoxian on 2016/6/15.
 */
public class HotCardAdapter extends BaseAdapter {

    private ArrayList<HotCardVo> items=new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    public HotCardAdapter(ArrayList<HotCardVo> items, Context context){
        this.items=items;
        this.context=context;
        imageLoader=ImageLoader.getInstance();
    }
    @Override
    public int getCount() {

        return items.size();

    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;

        if (convertView==null){
            convertView=(View) LayoutInflater.from(context).inflate(R.layout.hotrecommend_list_item, null);
            viewHolder=new ViewHolder();
            viewHolder.im_image=(ImageView)convertView.findViewById(R.id.im_image);
            viewHolder.tv_name=(TextView)convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc=(TextView)convertView.findViewById(R.id.tv_desc);
           convertView.setTag(viewHolder);
       }
        viewHolder=(ViewHolder)convertView.getTag();
        HotCardVo vo=items.get(position);
        String imageUrl= vo.getImageUrl();
        imageLoader.loadBitmap(context, imageUrl, viewHolder.im_image, 0);
        viewHolder.tv_name.setText(vo.getCardName());
        viewHolder.tv_desc.setText(vo.getDesc());
        return convertView;
    }
    final class ViewHolder{
        public ImageView im_image;
        public TextView   tv_name;
        public TextView  tv_desc;
     }
}
