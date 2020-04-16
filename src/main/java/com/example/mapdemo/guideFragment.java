package com.example.mapdemo;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;

import static androidx.databinding.DataBindingUtil.setContentView;


/**
 * A simple {@link Fragment} subclass.
 */
public class guideFragment extends Fragment {
    private WalkNavigateHelper mNaviHelper;

    public guideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获取WalkNavigateHelper实例
        mNaviHelper = WalkNavigateHelper.getInstance();
//获取诱导页面地图展示View
        View view = mNaviHelper.onCreate(getActivity());
//        if (view != null) {
//            setContentView(view);
//        }
        mNaviHelper.startWalkNavi(requireActivity());
        // Inflate the layout for this fragment
        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }

}
