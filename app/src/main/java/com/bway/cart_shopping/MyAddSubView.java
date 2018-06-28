package com.bway.cart_shopping;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyAddSubView extends LinearLayout {

    @BindView(R.id.jian)
    TextView jian;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.jia)
    TextView jia;
    private int number = 1;


    public MyAddSubView(Context context) {
        this(context, null);

    }

    public MyAddSubView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.remove_sub_item, this);
        //初始化
        ButterKnife.bind(view);
    }

    @OnClick({R.id.jian, R.id.num, R.id.jia})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jian:
                if (number > 1) {
                    --number;
                    num.setText(number + "");
                    if (onNumberChangeListener != null) {
                        onNumberChangeListener.onNumberChange(number);
                    }
                }else{
                    Toast.makeText(getContext(), "数据一倒头，不能再少了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.jia:
                ++number;
                num.setText(number + "");
                if (onNumberChangeListener != null) {
                    onNumberChangeListener.onNumberChange(number);
                }
                break;
        }
    }

    private onNumberChangeListener onNumberChangeListener;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        num.setText(number + "");
    }

    public void setOnNumberChangeListener(MyAddSubView.onNumberChangeListener onNumberChangeListener) {
        this.onNumberChangeListener = onNumberChangeListener;
    }

    //定义接口
    public interface onNumberChangeListener {
        void onNumberChange(int num);
    }


}
