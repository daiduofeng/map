package com.example.mapdemo;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.UIMsg;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudRgcResult;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.example.mapdemo.databinding.FragmentMapBinding;
import com.example.mapdemo.overlayutil.WalkingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

import cn.mapdatabse.mapmark;
import cn.mapdatabse.mapmarkViewModel;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.text.TextUtils.isEmpty;
import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private FragmentMapBinding binding;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private Button button;
    private Button dormitorybutton;
    private Button spotbutton;
    private Button bt_button;
    private EditText et_text;
    private ImageButton route_button;
    private AlertDialog alertDialog3; //多选框
    private RoutePlanSearch mSearch;

    List<mapmark> mapmarkList1 = null;

    private ImageView iv_img;
    private TextView tv_name;
    private TextView tv_description;
    private List<MarkerInfoUtil> teach_infos;
    private List<MarkerInfoUtil> dormitory_infos;
    private List<MarkerInfoUtil> spot_infos;
    private  Boolean show_teach_Marker = false;
    private  Boolean show_dormitory_Marker = false;
    private  Boolean show_spot_Marker = false;
    private RelativeLayout rl_marker;
    //创建自己的箭头定位
    private BitmapDescriptor bitmapDescriptor;
    private CloudManager mCloudManager;
    //mark数据model
    private mapmarkViewModel mapMarkViewModel;

    private MapMarkDate mapMarkDate;

    private NavController controller;
    private NavController controller1;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentMapBinding binding;
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_map,container,false);
        mMapView = binding.bmapView;
        button = binding.button;
        dormitorybutton = binding.dormitorybutton;
        spotbutton = binding.spotbutton;
        rl_marker = binding.rlMarker;
        iv_img = binding.ivImg;
        tv_name = binding.tvName;
        tv_description = binding.tvDescription;
        et_text = binding.etText;
        bt_button = binding.btButton;
        route_button = binding.imageButton2;

        mapMarkViewModel = new ViewModelProvider(requireActivity()).get(mapmarkViewModel.class);
        mapMarkDate = new MapMarkDate(mapMarkViewModel);

//        controller = Navigation.findNavController(getActivity());

//        controller = Navigation.findNavController(getActivity(),R.id.fragment);

//        mapMarkDate.deleteAllMarks();
        //获取数据库中的所有数据
        final LiveData<List<mapmark>> mapmarkList = mapMarkViewModel.getAllmarks();

        mapmarkList.observe(getActivity(), new Observer<List<mapmark>>() {

            @Override
            public void onChanged(List<mapmark> mapmarks) {

//                addSearchMarker(mapmarks);
                StringBuilder text = new StringBuilder();
                for(int i = 0;i<mapmarks.size();i++){
                    mapmark word = mapmarks.get(i);
                    text.append(word.getId()).append(":").append(word.getId()).append("=").append(word.getName()).append("\n");

                }
                Log.d("allmarks", "onCreateView: "+text);
                if (mapmarks.size() == 0){
                    //插入marker数据
                    mapMarkDate.insertMarks();
                }
            }
        });
        et_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    assert imm != null;
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });


        mBaiduMap = mMapView.getMap();
        mapmarkList1 = mapMarkViewModel.getAllmarksList();
        addSearchMarker(mapmarkList1);
        //显示缩放按钮
        mMapView.showZoomControls(true);
        mMapView.getMap().setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMapView.setZoomControlsPosition(new Point(950,1500));
            }
        });
        mapCenterMessage();
        //实例化UiSettings类对象
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //通过设置enable为true或false 选择是否显示指南针
        mUiSettings.setCompassEnabled(true);
        //定位初始化
        mLocationClient = new LocationClient(requireActivity());
        initLocation();
        locationMark();

        routePlan();
        cloudPOI();
        searchPOI();
        //点击其他部位隐藏软键盘
//        hideInputManager(getContext(),et_text);
//        binding.getRoot().setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//
//                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus().getWindowToken()!=null){
//                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    }
//                }
//                return false;
//            }
//        });


        return binding.getRoot();
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();


    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    //设置地图中心
    public void mapCenterMessage(){
        mBaiduMap.setMyLocationEnabled(true);
        LatLng GEO_ZHONGBEI = new LatLng(38.016247, 112.449318);
        MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                .target(GEO_ZHONGBEI)
                .zoom(18)
                .build();//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态
        //中北大学为地图中心，logo在左上角
    }
    /**
     * 配置定位参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
//        Log.d("ASDFG", "onReceiveLocation: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    //构造地图数据
    public class MyLocationListener extends BDAbstractLocationListener {
        private double lat;
        private double lon;
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            //        Receive Location;
            Log.d("ASDFG", "onReceiveLocation: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            lat = location.getLatitude();
            lon = location.getLongitude();
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
//运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.v("pcw","lat : " + lat+" lon : " + lon);
            Log.i("BaiduLocationApiDem", sb.toString());

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
        }
    }
    //自定义定位圈和开启定位
    public void locationMark(){

        //自定义精度圈填充颜色
        int accuracyCircleFillColor = 0xAAFFFF88;
        //自定义精度圈边框颜色
        int accuracyCircleStrokeColor = 0xAA00FF00;
        //NORMAL（普通态）, FOLLOWING（跟随态）, COMPASS（罗盘态）
        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null,accuracyCircleFillColor,accuracyCircleStrokeColor);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
//注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        Log.d("ASDFG", "onReceiveLocation: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mLocationClient.registerLocationListener(myLocationListener);
        Log.d("ASDF", "onReceiveLocation: 11111111111111111111111111111111");
//开启地图定位图层
        mLocationClient.start();
    }

    private void initView(){
//        mapMarkViewModel.getAllmarks().observe(requireActivity(), new Observer<List<mapmark>>() {
//            @Override
//            public void onChanged(List<mapmark> mapmarks) {
//                addSearchMarker(mapmarks);
//            }
//        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){
                    case R.id.button:
                        if(!show_teach_Marker){
                            //显示marker
//                            set_teach_MarkerInfo();
                            controller = null;
                            controller = Navigation.findNavController(v);
                            showInfo("显示覆盖物");
//                            addOverlay(teach_infos,R.drawable.teach);
                            mapMarkViewModel.getTypeMapMarksLive("教学楼").observe(getActivity(), new Observer<List<mapmark>>() {
                                @Override
                                public void onChanged(List<mapmark> mapmarks) {
                                    addSearchMarker(mapmarks);
                                }
                            });
                            show_teach_Marker = true;
                            show_dormitory_Marker = false;
                            show_spot_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu4);
                        }else{
                            //关闭显示marker
                            showInfo("关闭覆盖物");
                            mBaiduMap.clear();
                            show_teach_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu3);
                        }
                        break;
                    case R.id.dormitorybutton:
                        if(!show_dormitory_Marker){
//                            set_dormitory_MarkerInfo();
                            controller = null;
                            controller = Navigation.findNavController(v);
                            //显示marker
                            showInfo("显示覆盖物");
//                            addOverlay(dormitory_infos,R.drawable.dormitory);
                            mapMarkViewModel.getTypeMapMarksLive("宿舍").observe(getActivity(), new Observer<List<mapmark>>() {
                                @Override
                                public void onChanged(List<mapmark> mapmarks) {
                                    addSearchMarker(mapmarks);
                                }
                            });
                            show_dormitory_Marker = true;
                            show_spot_Marker = false;
                            show_teach_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu4);
                        }else{
                            //关闭显示marker
                            showInfo("关闭覆盖物");
                            mBaiduMap.clear();
                            show_dormitory_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu3);
                        }
                        break;
                    case R.id.spotbutton:
                        if(!show_spot_Marker){
//                            set_spot_MarkerInfo();
                            controller = null;
                            controller = Navigation.findNavController(v);
                            //显示marker
                            showInfo("显示覆盖物");
//                            addOverlay(spot_infos,R.drawable.spot);
                            mapMarkViewModel.getTypeMapMarksLive("景点").observe(getActivity(), new Observer<List<mapmark>>() {
                                @Override
                                public void onChanged(List<mapmark> mapmarks) {
                                    addSearchMarker(mapmarks);
                                }
                            });
                            show_spot_Marker = true;
                            show_dormitory_Marker = false;
                            show_teach_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu4);
                        }else{
                            //关闭显示marker
                            showInfo("关闭覆盖物");
                            mBaiduMap.clear();
                            show_spot_Marker = false;
                            //ib_marker.setBackgroundResource(R.drawable.ditu3);
                        }
                        break;
                }

            }
        };

//        button = binding.button;
        button.setOnClickListener(listener);
//        dormitorybutton = binding.dormitorybutton;
        dormitorybutton.setOnClickListener(listener);
//        spotbutton = binding.spotbutton;
        spotbutton.setOnClickListener(listener);
//        rl_marker = binding.rlMarker;
    }
    //显示消息
    private void showInfo(String str){
        Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show();
    }
    private void set_dormitory_MarkerInfo() {
        dormitory_infos = new ArrayList<MarkerInfoUtil>();
        dormitory_infos.add(new MarkerInfoUtil(38.020959, 112.44833,"文瀛菀1号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.02076, 112.448676,"文瀛菀2号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020526, 112.448703,"文瀛菀3号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020092, 112.44957,"文瀛菀4号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020021, 112.449264,"文瀛菀5号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.01946, 112.449543,"文瀛菀6号公寓",R.drawable.teaching_building,""));
//        infos.add(new MarkerInfoUtil(37.810476, 112.592269,"文瀛菀7号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.019212, 112.449605,"文瀛菀8号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020815, 112.450266,"文瀛菀9号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020528, 112.4503,"文瀛菀10号公寓",R.drawable.teaching_building,""));
//        infos.add(new MarkerInfoUtil(38.020959, 112.44833,"文瀛菀11号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020042, 112.450336,"文瀛菀12号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.019571, 112.450559,"文瀛菀13号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.01926, 112.450134,"文瀛菀14号公寓",R.drawable.teaching_building,""));
//        infos.add(new MarkerInfoUtil(37.804251, 112.595776,"文瀛菀15号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.019632, 112.451474,"文瀛菀16号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.019642, 112.452391,"文瀛菀17号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.020875, 112.452352,"文瀛菀18号公寓",R.drawable.teaching_building,""));
        dormitory_infos.add(new MarkerInfoUtil(38.02041, 112.452618,"文瀛菀19号公寓",R.drawable.teaching_building,""));
//        infos.add(new MarkerInfoUtil(117.176955,39.111345,"南开大学",R.drawable.teaching_building,"正式成立于1919年，是由严修、张伯苓秉承教育救国理念创办的综合性大学。"));
//        infos.add(new MarkerInfoUtil(117.174081,39.094994,"天津水上公园",R.drawable.teaching_building,"天津水上公园原称青龙潭，1951年7月1日正式对游客开放，有北方的小西子之称。"));
    }
    //设置教学楼标记物信息
    private void set_teach_MarkerInfo(){
        teach_infos = new ArrayList<MarkerInfoUtil>();
        teach_infos.add(new MarkerInfoUtil(38.021138,112.455709,"中北大学主楼",R.drawable.teach,""));
    }
    //设置景点标记物信息
    private void  set_spot_MarkerInfo(){
        spot_infos = new ArrayList<MarkerInfoUtil>();
        spot_infos.add(new MarkerInfoUtil(38.018725,112.452061,"铁路主题公园",R.drawable.spot_train,"2018年4月，中北大学铁路主题公园景观开始建设。4个月之后，中北大学铁路主题公园终于基本建好！中北大学是全国唯一一所学校里有火车站的大学，虽然通往中北的小火车已退出历史舞台，但火车道却一直通行着。从中北大学正门（南门）进入，沿右手边大路直走，快到二道门时。就能看到一条悠长的铁道。尽头处还停放着两列废弃的火车，一列锈迹斑斑红白相间的双层硬座车，另一辆是普通绿皮小火车。公园内，铁道旁修建的木站台。11111111111111111111111111111111111111111111111111111111111111111"));
    }
    //显示marker
    private void addOverlay(List<MarkerInfoUtil> infos2,int img) {
        //清空地图
        mBaiduMap.clear();
        mBaiduMap.showMapPoi(false);
        //创建marker的显示图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(img);
        LatLng latLng = null;
        Marker marker;
        OverlayOptions options;
        for(MarkerInfoUtil info:infos2){
            //获取经纬度
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            String TAG = "tag";
            Log.d(TAG, "markMarker: "+info.getLatitude()+"!!!"+info.getLongitude());
            //设置marker
            options = new MarkerOptions()
                    .position(latLng)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            //添加marker
            marker = (Marker) mBaiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);

            final TextView tv = new TextView(requireContext());
            tv.setBackgroundResource(R.drawable.infowindow1);
            tv.setPadding(20, 10, 20, 20);
            tv.setTextColor(Color.WHITE);
            tv.setText(info.getName());
            tv.setGravity(Gravity.CENTER);
            bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
            //infowindow位置
            LatLng info_latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //infowindow点击事件
//                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//                    @Override
//                    public void onInfoWindowClick() {
//                        //隐藏infowindow
//                        mBaiduMap.hideInfoWindow();
//
//                    }
//                };
            //显示infowindow
            InfoWindow infoWindow = new InfoWindow(tv,info_latLng,-50);
            mBaiduMap.showInfoWindow(infoWindow);
        }
        //将地图显示在最后一个marker的位置
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
//        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                Log.d("bundle", "onMarkerClick: "+bundle);
                MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
                //将信息显示在界面上
//                ImageView iv_img = binding.ivImg;
//                iv_img.setBackgroundResource(infoUtil.getImgId());
//                TextView tv_name = binding.tvName;
//                tv_name.setText(infoUtil.getName());
//                TextView tv_description = binding.tvDescription;
//                tv_description.setText(infoUtil.getDescription());
                //将布局显示出来
//                rl_marker.setVisibility(View.VISIBLE);

                //infowindow中的布局
//                NavController controller = Navigation.findNavController(requireView());
                controller.navigate(R.id.action_mapFragment_to_detailFragment,bundle);
                return true;
            }
        });

    }
    private void cloudPOI(){


        mCloudManager = CloudManager.getInstance();
        CloudListener listener = new CloudListener() {
            @Override
            public void onGetSearchResult(CloudSearchResult result, int error) {
                Log.d("cloud", "onGetSearchResult: "+result+"!!!!!!!"+error);
                //获取云检索结果
                if (result != null && result.poiList != null && result.poiList.size() > 0) {
                    Log.d("result", "onGetSearchResult, result length: " + result.poiList.size());
                }

            }

            @Override
            public void onGetDetailSearchResult(DetailSearchResult result, int error) {

            }

            @Override
            public void onGetCloudRgcResult(CloudRgcResult result, int error) {

            }
        };
        mCloudManager.init();
        mCloudManager.registerListener(listener);
        bt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("edit", "onClick: "+et_text.getText().length());
                if (et_text.getText().toString() == ""){
                    return;
                }else {
                    Log.d("text", "onClick: 11111111");
                    LocalSearchInfo info = new LocalSearchInfo();
                    //下述ak和id为BaiduMapsApiDemo所用，用作示例，开发者需要替换为自己的
                    //该info.ak是服务端ak，非Android SDK ak，两者属于同一账户
                    info.ak = "rSYGn15q1BamLtyenSpQVrdHiBhvSGiC";
                    info.geoTableId = 209604;
                    info.tags = "";
                    info.q = et_text.getText().toString();
                    info.region = "太原市";
                    mCloudManager.localSearch(info);
//                    mCloudManager.destroy();

                }
            }
        });

    }
    private void searchPOI(){
        bt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty(et_text.getText())){
                    Log.d("edit", "onClick: "+et_text.getText().toString());
                    return;
                }else {
                    controller = Navigation.findNavController(v);
                    mapMarkDate.getSearchMapMarksLive(et_text.getText().toString()).observe(getActivity(), new Observer<List<mapmark>>() {
                        @Override
                        public void onChanged(List<mapmark> mapmarks) {
                            StringBuilder text = new StringBuilder();
                            for (int i = 0; i < mapmarks.size(); i++) {
                                mapmark word = mapmarks.get(i);
                                text.append(word.getId()).append(":").append(word.getId()).append("=").append(word.getName()).append("\n");

                            }
                            addSearchMarker(mapmarks);
                            Log.d("searchmarks", "onCreateView: " + text);
//                        View view = requireActivity().getCurrentFocus();
//                        if (view != null) {
//                            InputMethodManager imm = (InputMethodManager)getSystemService(getContext(),Context.INPUT_METHOD_SERVICE.getClass());
//                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//                        }

                        }
                    });
                }
            }
        });

    }

    //显示marker
    private void addSearchMarker(List<mapmark> infos2) {
        //清空地图
        mBaiduMap.clear();
        mBaiduMap.showMapPoi(false);
        //创建marker的显示图标
        BitmapDescriptor bitmap;
        LatLng latLng = null;
        Marker marker,marker1;
        OverlayOptions options;
        for(mapmark info:infos2){
            //获取经纬度
            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.teach);
            switch (info.getType()){
                case "景点":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.spot);
                    break;
                case "教学楼":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.teach);
                    break;
                case "宿舍":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dormitory);
                    break;

            }
//            bitmap = BitmapDescriptorFactory.fromResource(R.drawable.spot);
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            String TAG = "tag";
            Log.d(TAG, "markMarker: "+info.getLatitude()+"!!!"+info.getLongitude());
            //设置marker



            options = new MarkerOptions()
                    .position(latLng)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true);// 设置手势拖拽;

            OverlayOptions textOptions = new TextOptions()
                    .bgColor(0xAAFFFF00)  //設置文字覆盖物背景颜色
                    .fontSize(40)  //设置字体大小
                    .fontColor(0xFFFF00FF)// 设置字体颜色
                    .text(info.getName())  //文字内容
                    .rotate(0)  //设置文字的旋转角度
                    .position(latLng);
            //添加marker
            marker = (Marker) mBaiduMap.addOverlay(options);
            //在地图上显示文字覆盖物
            Overlay mText = mBaiduMap.addOverlay(textOptions);

            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("databaseinfo", info);
            marker.setExtraInfo(bundle);



            TextView tv = new TextView(requireActivity());
            tv.setBackgroundResource(R.drawable.infowindow1);
            tv.setPadding(20, 10, 20, 20);
            tv.setTextColor(Color.WHITE);
            tv.setText(info.getName());
            tv.setGravity(Gravity.CENTER);
            bitmapDescriptor = BitmapDescriptorFactory.fromView(tv);
            //infowindow位置
            LatLng info_latLng = new LatLng(info.getLatitude(), info.getLongitude());
            //infowindow点击事件
//                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//                    @Override
//                    public void onInfoWindowClick() {
//                        //隐藏infowindow
//                        mBaiduMap.hideInfoWindow();
//
//                    }
//                };

        }
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                Log.d("bundle", "onMarkerClick: "+bundle);
//                MarkerInfoUtil infoUtil = (MarkerInfoUtil) bundle.getSerializable("info");
                //将信息显示在界面上
//                ImageView iv_img = binding.ivImg;
//                iv_img.setBackgroundResource(infoUtil.getImgId());
//                TextView tv_name = binding.tvName;
//                tv_name.setText(infoUtil.getName());
//                TextView tv_description = binding.tvDescription;
//                tv_description.setText(infoUtil.getDescription());
                //将布局显示出来
//                rl_marker.setVisibility(View.VISIBLE);

                //infowindow中的布局
                controller1 = Navigation.findNavController(requireView());
//                if (controller == null){
//                    controller1.navigate(R.id.action_mapFragment_to_detailFragment,bundle);
//                }
                controller1.navigate(R.id.action_mapFragment_to_detailFragment,bundle);
                return true;
            }
        });

    }

    /**
     * 隐藏键盘
     */
//    protected void hideInput() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        View v = getWindow().peekDecorView();
//        if (null != v) {
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        }
//    }
    public static void hideInputManager(Context context,View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏
//imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//imm.hideSoftInputFromWindow(view.getWindowToken(), 0x0002);
        }
    }
    /**
     * 路线规划
     */
    private void routePlan(){
        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMutilAlertDialog(v);
            }
        });
    }
    /**
     * 多选框
     */
    public void showMutilAlertDialog(View view){
        Log.d("routelist", "onClick: 我出来了");
        final String[] items = {"多选1", "多选2", "多选3", "多选4"};

        mapMarkViewModel.getTypeMapMarksLive("景点").observe(getActivity(), new Observer<List<mapmark>>() {
            @Override
            public void onChanged(List<mapmark> mapmarks) {
                final List<String> list = new ArrayList<String>();
                for (mapmark info:mapmarks){

                    list.add(info.getName());
                }
                final String[] items = new String[list.size()];
                final Boolean[] itemsFlag = new Boolean[list.size()];
                for (int m = 0; m<list.size();m++){
                    itemsFlag[m] = false;
                }
                list.toArray(items);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());
                alertBuilder.setTitle("路线推荐");
                /**
                 *第一个参数:弹出框的消息集合，一般为字符串集合
                 * 第二个参数：默认被选中的，布尔类数组
                 * 第三个参数：勾选事件监听
                 */
                alertBuilder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {
                        if (isChecked){
                            Toast.makeText(requireContext(), "选择" + items[i], Toast.LENGTH_SHORT).show();
                            Log.d("routelist", "onClick: 我出来了");
                            itemsFlag[i] = true;
                        }else {
                            Toast.makeText(requireContext(), "取消选择" + items[i], Toast.LENGTH_SHORT).show();
                            itemsFlag[i] = false;
                        }
                    }
                });
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       for (int n = 0; n < list.size();n++){
                           if (itemsFlag[n]){
                               Log.d("trueitems", "onClick: "+items[n]);
                                routePlanBuild();
                           }
                       }
                        alertDialog3.dismiss();
                    }
                });

                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog3.dismiss();
                    }
                });


                alertDialog3 = alertBuilder.create();
                alertDialog3.show();
            }
        });


    }
    /**
     * 路线规划创建
     */
    public void routePlanBuild(){
        //初始化路径规划
        mSearch = RoutePlanSearch.newInstance();
        OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult result) {

                //创建WalkingRouteOverlay实例
                WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
                if (null == result) {
                    return;
                }

                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    // result.getSuggestAddrInfo()
                    Toast.makeText(requireContext(),"起终点或途径点地址有歧义",Toast.LENGTH_SHORT).show();;
                    return;
                }
                if (result.error == SearchResult.ERRORNO.PERMISSION_UNFINISHED){
                    Toast.makeText(requireContext(),"权限鉴定未完成，再次尝试",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(requireContext(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR){
                    //获取路径规划数据,(以返回的第一条数据为例)
                    //为WalkingRouteOverlay实例设置路径数据
                    overlay.setData(result.getRouteLines().get(0));
                    //在地图上绘制WalkingRouteOverlay
                    overlay.addToMap();
                }
//                    `
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }

        };
        mSearch.setOnGetRoutePlanResultListener(listener);
        LatLng latLng1 = new LatLng(38.018725,112.452061);
        LatLng latLng2 = new LatLng(38.019371,112.447387);
        LatLng latLng3 = new LatLng(38.021138,112.455709);
        PlanNode stNode = PlanNode.withLocation(latLng1);
        PlanNode enNode = PlanNode.withLocation(latLng2);
        PlanNode enNode1 = PlanNode.withLocation(latLng3);
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(stNode)
                .to(enNode));
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(enNode)
                .to(enNode1));
        mSearch.destroy();
    }


}
