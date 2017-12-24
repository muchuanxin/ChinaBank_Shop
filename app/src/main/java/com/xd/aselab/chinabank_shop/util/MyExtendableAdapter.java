package com.xd.aselab.chinabank_shop.util;

import android.content.Context;
import android.support.annotation.StringDef;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Info_Info;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/8.
 */

public class MyExtendableAdapter extends BaseExpandableListAdapter {
    private String[] parent;
    private String[][] child;
    private LayoutInflater infalter;
    private boolean bool;
    private ImageView head_image;
    private SharePreferenceUtil sp;
    private ImageLoader imageLoader;
    private Context context;
    private List new_group_account_info;
    private CheckBox box;
    //记录checkbox勾选状态
    private boolean[][] checkbox_state;
    private String[][] head_image_resource;


    //bool用来设置左边和右边  复选框是否显示  head_image数组形式传入头像
    public MyExtendableAdapter(String[] parent, String[][] child, Context context, boolean bool, String[][] head_image) {
        this.parent = parent;
        this.child = child;
        this.infalter = LayoutInflater.from(context);
        this.bool = bool;
        this.context = context;
        new_group_account_info = new ArrayList();
        sp = new SharePreferenceUtil(context, "user");
        checkbox_state = new boolean[parent.length][child[parent.length - 1].length];
        head_image_resource = new String[][]{};
        this.head_image_resource = head_image;
    }


    @Override
    public int getGroupCount() {
        return parent.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        //  以后用list存吧   数组的length空的也算长度  还得数。，

        int temp=0;
        for(int i=0;i<child[groupPosition].length;i++){
            if(null!=child[groupPosition][i]){
                temp++;
            }
        }
        return temp;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parent[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 是否指定分组视图及其子视图的id对应的后台数据改变也会保持该id
     *
     * @return 是否相同的id总是指向同一个对象
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = infalter.inflate(R.layout.parent, null);
        TextView parent_name = (TextView) convertView.findViewById(R.id.parent_name);
        parent_name.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("Dorise",groupPosition+","+childPosition);
            convertView = infalter.inflate(R.layout.child, null);
            if (bool == false) {
                convertView.findViewById(R.id.check_box).setVisibility(View.GONE);
            } else {
                convertView.findViewById(R.id.check_box).setVisibility(View.VISIBLE);
            }
            box = (CheckBox) convertView.findViewById(R.id.check_box);
            if (checkbox_state[groupPosition][childPosition] == true) {
                box.setChecked(true);
            }
            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d("Dorise_group数：",groupPosition+"");
                    Log.d("Dorise_child数：",childPosition+"");
                    if (isChecked == true) {

                        checkbox_state[groupPosition][childPosition] = true;
                        new_group_account_info.add(sp.getShopManagerAccount());

                    } else if (isChecked == false) {

                        checkbox_state[groupPosition][childPosition] = false;
                        new_group_account_info.remove(sp.getShopManagerAccount());

                    }
//                new_group_account_info.add(sp.getShopManagerAccount());

                }
            });
            head_image = (ImageView) convertView.findViewById(R.id.head_image);
            TextView child_name = (TextView) convertView.findViewById(R.id.child_name);

            //如果是新建群拉人或者左边联系人   则头像仅显示  我的上级一个人

            imageLoader = ImageLoader.getInstance();
            if (null != head_image_resource[groupPosition][childPosition])
                imageLoader.loadBitmap(context, head_image_resource[groupPosition][childPosition], head_image, R.drawable.portrait);


            child_name.setText(child[groupPosition][childPosition]);

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setCheckBoxInvisible() {

    }

    public List getList() {
        return new_group_account_info;
    }

}
