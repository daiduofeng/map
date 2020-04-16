package com.example.mapdemo;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;

import cn.mapdatabse.mapmark;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    private WalkNaviLaunchParam walkParam;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("savedInstanceState", "onActivityCreated: "+getArguments());
        if (getArguments().getSerializable("info") != null) {
            MarkerInfoUtil infoUtil = (MarkerInfoUtil) getArguments().getSerializable("info");
            TextView textView_title = getView().findViewById(R.id.textView);
            textView_title.setText(infoUtil.getName());
            ImageView description_image = getView().findViewById(R.id.imageView);
            description_image.setImageResource(infoUtil.getImgId());
            TextView textView_description = getView().findViewById(R.id.textView2);
            textView_description.setText(infoUtil.getDescription());
        }else {
            final mapmark infoUtil = (mapmark) getArguments().getSerializable("databaseinfo");

            TextView textView_title = getView().findViewById(R.id.textView);
            textView_title.setText(infoUtil.getName());
            ImageView description_image = getView().findViewById(R.id.imageView);
            description_image.setImageResource(infoUtil.getImgId());
            Log.d("imageId", "onActivityCreated: "+infoUtil.getImgId());
            TextView textView_description = getView().findViewById(R.id.textView2);
            textView_description.setText(infoUtil.getDescription());
            ImageButton nav_button = getView().findViewById(R.id.imageButton);
            nav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
                    //info必须实现序列化接口
//                    bundle.putSerializable("navigateinfo", infoUtil);
//                    Log.d("navigateinfo", "onClick: "+bundle+"!!!!!!!!!!!!!!!"+infoUtil);
//                    NavController controller = Navigation.findNavController(v);
//                    controller.navigate(R.id.action_detailFragment_to_mapFragment,bundle);

                    //设置导航的起终点信息
                    LatLng startPt = new LatLng(38.016247, 112.449318);
                    LatLng  endPt = new LatLng(infoUtil.getLatitude(), infoUtil.getLongitude());
                    WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
                    walkStartNode.setLocation(startPt);
                    WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
                    walkEndNode.setLocation(endPt);
                    walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode).extraNaviMode(0);
                // 使用步行导航前，需要初始化引擎初。
                    WalkNavigateHelper.getInstance().initNaviEngine(requireActivity(), new IWEngineInitListener() {
                        @Override
                        public void engineInitSuccess() {
                            Log.d("chushihua", "引擎初始化成功");
                            routePlanWithParam();
                        }

                        @Override
                        public void engineInitFail() {
                            Log.d("chushihua", "引擎初始化失败");
                        }
                    });
                }
            });
        }
//        Log.d("info", "onActivityCreated: "+infoUtil);
//        TextView textView_title = getView().findViewById(R.id.textView);
//        textView_title.setText(infoUtil.getName());
//        ImageView description_image = getView().findViewById(R.id.imageView);
//        description_image.setImageResource(infoUtil.getImgId());
//        TextView textView_description = getView().findViewById(R.id.textView2);
//        textView_description.setText(infoUtil.getDescription());
    }
    /**
     * 引擎初始化成功之后，发起导航算路。算路成功后，在回调函数中设置跳转至诱导页面。
     * 开始算路
     */
    public void routePlanWithParam() {
        WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                Log.d("startplan", "开始算路");
            }

            @Override
            public void onRoutePlanSuccess() {
                Log.d("plansuccess", "算路成功,跳转至诱导页面");
                Intent intent = new Intent();
                intent.setClass(getContext(), WNaviGuideActivity.class);
                startActivity(intent);
//                NavController controller = Navigation.findNavController(requireView());
//                controller.navigate(R.id.action_detailFragment_to_guideFragment);

            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                Log.d("planlose", "算路失败");
            }

        });
    }
}
