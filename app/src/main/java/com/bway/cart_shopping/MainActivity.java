package com.bway.cart_shopping;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.main_expandView)
    ExpandableListView mainExpandView;
    @BindView(R.id.checkbox_main)
    CheckBox checkboxMain;
    @BindView(R.id.zongjia)
    TextView zongjia;
    @BindView(R.id.btn_money)
    Button btnMoney;
    private ExpandViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        String url = "https://www.zhaoapi.cn/product/getCarts?uid=71";
        //联网请求
        OkHttpUtils.getInstance().doGet(url, new OkHttpUtils.onCallBack() {
            @Override
            public void onFaild(Exception e) {

            }

            @Override
            public void onResponse(String json) {

                Gson gson = new Gson();
                CartBean cartBean = gson.fromJson(json, CartBean.class);
                if ("0".equals(cartBean.getCode())) {
                    List<CartBean.DataBean> data = cartBean.getData();
                    //添加适配器
                    adapter = new ExpandViewAdapter(data);

                    adapter.setOnCartListChangeListener(new ExpandViewAdapter.onCartListChangeListener() {
                        //关于商家按钮
                        @Override
                        public void onSellerCheckChange(int groupPogrsition) {
                            //商家被点击，商家按钮状态
                            boolean b = adapter.productCheckStatus(groupPogrsition);
                            //点击完商家按钮，子类按钮状态跟着改变
                            adapter.noProductCheckStatus(groupPogrsition, !b);
                            adapter.notifyDataSetChanged();
                            reFreshSelectedAndToTalPriceAndTotalAllNumber();

                        }

                        //关于子类按钮
                        @Override
                        public void onProductCheckedChange(int groupPosition, int childPosition) {
                            adapter.changeCurrentProductStatus(groupPosition, childPosition);
                            adapter.notifyDataSetChanged();
                            reFreshSelectedAndToTalPriceAndTotalAllNumber();
                        }

                        @Override
                        public void onProductNumberChange(int groupPosition, int childPosition, int number) {

                            //设置加减按钮
                              adapter.changeCurrentProductNumber(groupPosition,childPosition,number);
                            adapter.notifyDataSetChanged();
                            reFreshSelectedAndToTalPriceAndTotalAllNumber();
                        }
                    });

                    mainExpandView.setAdapter(adapter);

                    //展开二级列表
                    Log.d(TAG, "onResponse:+++++++++++++++++++ " + data.size());
                    for (int i = 0; i < data.size(); i++) {
                        mainExpandView.expandGroup(i);
                    }
                    reFreshSelectedAndToTalPriceAndTotalAllNumber();
                }

            }
        });
    }

    //刷新
    private void reFreshSelectedAndToTalPriceAndTotalAllNumber() {
//判断商品是否全部选中
        boolean allProductsSelected = adapter.isAllProductsSelected();
        //设置全选
        checkboxMain.setChecked(allProductsSelected);
//计算总价
        float totalPrice = adapter.calculateTotalPrice();
        zongjia.setText("总价" + totalPrice);
        //计算总数量
        int totalNumber = adapter.calculateTotalNumber();
        btnMoney.setText("去结算("+totalNumber+")");
    }


    @OnClick(R.id.checkbox_main)
    public void onViewClicked() {
        //底部全选按钮
        boolean allProductsSelected = adapter.isAllProductsSelected();
        adapter.changeAllProductStatus(!allProductsSelected);
        adapter.notifyDataSetChanged();
        reFreshSelectedAndToTalPriceAndTotalAllNumber();
    }


}
