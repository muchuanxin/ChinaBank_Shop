package com.xd.aselab.chinabank_shop.activity.publicChinaBankShop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.xd.aselab.chinabank_shop.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.xd.aselab.chinabank_shop.util.AMapUtil;
import com.xd.aselab.chinabank_shop.util.ConnectUtil;
import com.xd.aselab.chinabank_shop.util.DialogFactory;
import com.xd.aselab.chinabank_shop.util.PostParameter;
import com.xd.aselab.chinabank_shop.util.SharePreferenceUtil;
import com.xd.aselab.chinabank_shop.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class CBNetwork extends AppCompatActivity implements LocationSource,AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener,RouteSearch.OnRouteSearchListener {

    private RelativeLayout back;
    private TextView where_to_go;
    private MapView mapView;
    private AMap aMap;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private OnLocationChangedListener mListener;
    private GeocodeSearch geocoderSearch;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private AMapLocation amapLocation;

    private Map<String, String> map;
    private String[] names=null;
    private String[] addrs=null;
    private String action;
    private int size;
    private int count=0;

    private final int ROUTE_TYPE_WALK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbnetwork);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //showProgressDialog("正在定位，请稍后");
        //Log.e("SAH1_MCX", sHA1(CBNetwork.this));

        back = (RelativeLayout) findViewById(R.id.act_cb_network_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        where_to_go = (TextView) findViewById(R.id.act_cb_network_where_to_go);
        where_to_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(CBNetwork.this, WhereToGo.class);
                intent.putExtra("names", names);
                intent.putExtra("addrs", addrs);
                startActivityForResult(intent, 127);
            }
        });

        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        //MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.act_cb_network_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        /**
         * 初始化AMap对象
         */
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置点击marker事件监听器
            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
            UiSettings mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);//设置地图默认的缩放按钮是否显示
            mUiSettings.setCompassEnabled(false);//设置地图默认的指南针是否显示
            mUiSettings.setMyLocationButtonEnabled(true);//设置地图默认的定位按钮是否显示
        }

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("您选择的位置：")
                .snippet(snippet)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(false);
        aMap.addMarker(markerOptions);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(this);
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为低功耗模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置为单次定位
            locationOption.setOnceLocation(true);
            //设置定位监听
            locationClient.setLocationListener(this);
            // 设置是否需要显示地址信息
            locationOption.setNeedAddress(true);
            // 设置是否开启缓存
            locationOption.setLocationCacheEnable(true);
            //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
            locationOption.setOnceLocationLatest(true);
            /**
            *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
             *  只有持续定位设置定位间隔才有效，单次定位无效
             */
            locationOption.setInterval(1000);
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

            AMapLocation location = locationClient.getLastKnownLocation();//获取最后一次定位
            if (location!=null){
                this.amapLocation = location;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                //requestDialog.showProgressDialog(MarketingGuide.this,"正在搜索，请稍后");
                //showProgressDialog("正在搜索，请稍后");
            }
            //定位与添加marker为异步，顺序为 调用最后一次定位->加marker->新定位
            sendMsg();

            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        mapView.onDestroy();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            //dissmissProgressDialog();
            if ( amapLocation.getErrorCode() == 0) {
                this.amapLocation = amapLocation;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                //showProgressDialog("正在搜索，请稍后");
                //sendMsg();
            } else {
                Toast.makeText(CBNetwork.this, "定位失败，请点击右上角的定位按钮重新定位", Toast.LENGTH_SHORT).show();
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    private void sendMsg(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                SharePreferenceUtil spu = new SharePreferenceUtil(CBNetwork.this, "user");
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("cookie", spu.getCookie());
                String reCode = ConnectUtil.httpRequest(ConnectUtil.CHINA_BANK_NETWORK, params, ConnectUtil.POST);
                try {
                    if (reCode!=null){
                        //Log.e("CBNetwork：reCode", reCode);
                        JSONObject json = new JSONObject(reCode);
                        String status = json.getString("status");
                        if ("false".equals(status)) {
                            //Toast.makeText(CBNetwork.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        } else if ("true".equals(status)) {
                            JSONArray jsonArray = json.getJSONArray("list");
                            if (jsonArray.length()>0){
                                size = jsonArray.length();
                                Log.e("size",""+size);
                                map = new HashMap<>();
                                names = new String[jsonArray.length()];
                                addrs = new String[jsonArray.length()];
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject temp = (JSONObject) jsonArray.get(i);
                                    map.put(temp.getString("address"), temp.getString("name"));
                                    names[i] = temp.getString("name");
                                    addrs[i] = temp.getString("address");
                                    GeocodeQuery query = new GeocodeQuery(temp.getString("address"), "西安");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
                                    geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
                                    action = "mark";
                                }
                            }
                            else {
                                Log.e("CBNetwork_Activity", "list长度为0");
                            }
                        } else {
                            Log.e("CBNetwork_Activity", CBNetwork.this.getResources().getString(R.string.status_exception));
                        }
                    }
                    else {
                        Toast.makeText(CBNetwork.this, CBNetwork.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                        Log.e("CBNetwork_Activity", "reCode为空");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        //dissmissProgressDialog();
        /*count++;
        Log.e("count",""+count);
        if (count==size){
            dissmissProgressDialog();
        }*/
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                if ("mark".equals(action)){
                    addMarkersToMap(AMapUtil.convertToLatLng(address.getLatLonPoint()), address.getFormatAddress());
                }
                else if ("route".equals(action)){
                    mRouteSearch = new RouteSearch(CBNetwork.this);
                    mRouteSearch.setRouteSearchListener(CBNetwork.this);
                    searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault,
                            new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude()), address.getLatLonPoint());
                }
            } else {
                //Toast.makeText(CBNetwork.this, "没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        } else {
            //ToastUtil.showerror(CBNetwork.this, rCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (172==resultCode){
            where_to_go.setText(data.getStringExtra("addr"));
            GeocodeQuery query = new GeocodeQuery(data.getStringExtra("addr"), "西安");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
            geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
            action = "route";
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode, LatLonPoint startPoint, LatLonPoint endPoint) {
        if (startPoint == null) {
            ToastUtil.show(CBNetwork.this, "定位中，稍后再试...");
            return;
        }
        if (endPoint == null) {
            ToastUtil.show(CBNetwork.this, "终点未设置");
        }
        //showProgressDialog("正在导航，请稍后");
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        //dissmissProgressDialog();
        //aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    /*WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();*/
                    Intent intent = new Intent(CBNetwork.this, WalkingRoute.class);
                    intent.putExtra("walk_path", walkPath);
                    startActivity(intent);
                }
                else if (result.getPaths() == null) {
                    Toast.makeText(CBNetwork.this, "没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CBNetwork.this, "没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        } else {
            //ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(String msg) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(msg);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
