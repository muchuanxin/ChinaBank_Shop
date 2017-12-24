package com.xd.aselab.chinabank_shop.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabank_shop.R;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_Cancel_League;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_Check_Public;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Info;
import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Info_Info;
import com.xd.aselab.chinabank_shop.activity.publicChinaBankShop.LoginActivity;
//import com.xd.aselab.chinabank_shop.my.BaseMyAllPerformanceActivity;
//import com.xd.aselab.chinabank_shop.my.PersonalInfo;
import com.xd.aselab.chinabank_shop.util.Constants;
import com.xd.aselab.chinabank_shop.util.ImageLoader;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeBasicFragment extends Fragment {


    private SharePreferenceUtil sp;
    private SharePreferenceUtil spu;
    private RelativeLayout back;
    // private CircleImageView iv_head_photo;
    private ImageView head_photo;
    private ImageView dafault_head;
    private TextView user_name;
    private TextView tv_user_name;
    private RelativeLayout rl_my_info;
    private RelativeLayout rl_my_performance;
    private RelativeLayout rl_my_Information;
    private ImageLoader imageLoader;
    private View root;

    public MeBasicFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.activity_card_div__my__info, container, false);
        dafault_head= (ImageView) root.findViewById(R.id.dafault_head);
        sp = new SharePreferenceUtil(getActivity(), "user");
        user_name = (TextView) root.findViewById(R.id.user_name);
        user_name.setText(sp.getWorkerName());
        head_photo = (ImageView) root.findViewById(R.id.dafault_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(getActivity(), sp.getHead_image(), head_photo, R.drawable.portrait);

        LinearLayout my_performance_row = (LinearLayout) root.findViewById(R.id.my_performance_row);
        my_performance_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_My_Info_Info.class);
                startActivity(intent);
            }
        });
        LinearLayout my_info_row = (LinearLayout) root.findViewById(R.id.my_info_row);
        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_Check_Public.class);
                startActivity(intent);
            }
        });
        LinearLayout terminate_row = (LinearLayout) root.findViewById(R.id.terminate_row);
        terminate_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardDiv_Cancel_League.class);
                startActivity(intent);
            }
        });
/*            Button exit= (Button) root.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });*/

        ImageView return_button = (ImageView) root.findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        return root;
    }

    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root=
        setContentView(R.layout.activity_card_div__my__info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        sp = new SharePreferenceUtil(getActivity(),"user");
        head_photo = (ImageView)root.findViewById(R.id.dafault_head);
        imageLoader = ImageLoader.getInstance();
        imageLoader.loadBitmap(getActivity(),sp.getHead_image(),head_photo,R.drawable.portrait);

        LinearLayout my_performance_row= (LinearLayout) root.findViewById(R.id.my_performance_row);
        my_performance_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),CardDiv_My_Info_Info.class);
                startActivity(intent);
            }
        });
        LinearLayout my_info_row= (LinearLayout) root.findViewById(R.id.my_info_row);
        my_info_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),CardDiv_Check_Public.class);
                startActivity(intent);
            }
        });
        LinearLayout terminate_row= (LinearLayout) root.findViewById(R.id.terminate_row);
        terminate_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),CardDiv_Cancel_League.class);
                startActivity(intent);
            }
        });
        Button exit= (Button) root.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        ImageView return_button= (ImageView) root.findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }*/

    @Override
    public void onResume() {
        super.onResume();
         imageLoader.loadBitmap(getActivity(), sp.getHead_image(), dafault_head, R.drawable.default_photo);
    }
}
