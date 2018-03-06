package com.xd.aselab.chinabank_shop.activity.shop.worksPerformanceList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.Vos.MyWorkersInfoVo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class WorkersPerAdapter extends BaseAdapter {

    private ArrayList<MyWorkersInfoVo> items=new ArrayList<MyWorkersInfoVo>();
    private Context context;
    public WorkersPerAdapter(ArrayList<MyWorkersInfoVo> items, Context context){
        this.items=items;
        this.context=context;
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
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.shop_my_workers_performance_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.tv_name=(TextView)convertView.findViewById(R.id.tv_worker_name);
            viewHolder.tv_cardsNumber=(TextView)convertView.findViewById(R.id.tv_worker_cards_number);
            viewHolder.tv_tel=(TextView)convertView.findViewById(R.id.tv_worker_tel);
            convertView.setTag(viewHolder);
        }

        viewHolder=(ViewHolder)convertView.getTag();
        MyWorkersInfoVo vo=items.get(position);
        if("店主".equals(vo.getWorkerName())){
            viewHolder.tv_name.setText("   "+vo.getWorkerName());
        }else{
            viewHolder.tv_name.setText("员工姓名："+vo.getWorkerName());
        }
        viewHolder.tv_cardsNumber.setText("成功办卡数:"+vo.getCardsNumber());
        viewHolder.tv_tel.setText("联系电话："+vo.getTel());

        return convertView;
    }

    public class ViewHolder{
        public TextView tv_name;
        public TextView tv_cardsNumber;
        public TextView tv_tel;
    }

}
