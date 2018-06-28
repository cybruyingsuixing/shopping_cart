package com.bway.cart_shopping;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpandViewAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ExpandViewAdapter";

    private List<CartBean.DataBean> seller;

    public ExpandViewAdapter(List<CartBean.DataBean> seller) {
        this.seller = seller;
    }

    @Override
    public int getGroupCount() {
        return seller == null ? 0 : seller.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return seller.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CartBean.DataBean dataBean = seller.get(groupPosition);
        ParentViewHolder parentViewHolder;
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.cart_item, null);
            parentViewHolder = new ParentViewHolder(convertView);
            convertView.setTag(parentViewHolder);
        } else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }
//商家名称
        parentViewHolder.sellerNameTitle.setText(dataBean.getSellerName());

        boolean allProductsSelected = productCheckStatus(groupPosition);

        parentViewHolder.sellerCb.setChecked(allProductsSelected);

        parentViewHolder.sellerCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCartListChangeListener != null) {
                    onCartListChangeListener.onSellerCheckChange(groupPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        CartBean.DataBean.ListBean listb = seller.get(groupPosition).getList().get(childPosition);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.child_item, null);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.childTitle.setText(listb.getTitle());
       // Log.d(TAG, "getChildView:+++++++++++++ " + listb.getTitle());
        childViewHolder.childPrice.setText(listb.getPrice() + "");
        String[] split = listb.getImages().split("\\|");
        ImageLoader.getInstance().displayImage(split[0], childViewHolder.childImg, MyApp.getOptions());
       // Log.d(TAG, "getChildView:_____________ "+seller.get(1).getList().get(0).getSelected()+"++++++++++++");

        childViewHolder.childCb.setChecked(listb.getSelected() == 1);

        childViewHolder.childCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCartListChangeListener != null) {
                    onCartListChangeListener.onProductCheckedChange(groupPosition, childPosition);
                }
            }
        });
        childViewHolder.myaddSub.setNumber(listb.getNum());
        childViewHolder.myaddSub.setOnNumberChangeListener(new MyAddSubView.onNumberChangeListener() {
            @Override
            public void onNumberChange(int num) {
                //拿到最新的商品数量
               // childViewHolder.myaddSub.setNumber(num);
                if(onCartListChangeListener!=null){
                    onCartListChangeListener.onProductNumberChange(groupPosition,childPosition,num);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    //所有商品是否选中
    public boolean isAllProductsSelected() {
        for (int i = 0; i < seller.size(); i++) {
                CartBean.DataBean dataBean = seller.get(i);
            List<CartBean.DataBean.ListBean> list = dataBean.getList();
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getSelected() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

//商家复选框的状态
    public boolean productCheckStatus(int groupPosition) {
        CartBean.DataBean dataBean = seller.get(groupPosition);
        List<CartBean.DataBean.ListBean> list = dataBean.getList();
        for(CartBean.DataBean.ListBean listBean: list){
              if (listBean.getSelected() == 0){
                    return false;
              }
        }
        return true;
    }

    //当子类商品点击的时候被调用，改变当前商品的状态
    public void changeCurrentProductStatus(int groupPosition, int childPosition) {
        Log.d(TAG, "changeCurrentProductStatus: ++++++++++++++++++++ "+groupPosition+"     "+childPosition);
        CartBean.DataBean dataBean = seller.get(groupPosition);
        List<CartBean.DataBean.ListBean> listBeans = dataBean.getList();
        CartBean.DataBean.ListBean listBean = listBeans.get(childPosition);
        listBean.setSelected(listBean.getSelected() == 0 ? 1:0);
}


    //设置当前商品的状态
    public void changeAllProductStatus(boolean selected) {
        for (int i = 0; i < seller.size(); i++) {
            CartBean.DataBean dataBean = seller.get(i);
            List<CartBean.DataBean.ListBean> list = dataBean.getList();
            for (int j = 0; j < list.size(); j++) {
                list.get(j).setSelected(selected ? 1 : 0);
            }
        }
    }
//点击完商家按钮，子类按钮状态跟着改变
    public void noProductCheckStatus(int groupPogrsition, boolean b) {
        List<CartBean.DataBean.ListBean> list = seller.get(groupPogrsition).getList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(b ? 1 : 0);
        }
    }
//计算总价
    public float calculateTotalPrice() {
        float totalPrice=0;
        for (int i = 0; i < seller.size(); i++) {

            List<CartBean.DataBean.ListBean> list = seller.get(i).getList();

            for (int j = 0; j < list.size(); j++) {
                //只要是选中状态
                if (list.get(j).getSelected()==1){
                    float price = list.get(j).getPrice();
                    int num = list.get(j).getNum();
                    totalPrice+=price*num;
                }
            }
        }
        return totalPrice;
    }

    //计算总数量
    public int calculateTotalNumber() {
        int totalNumber=0;
        for (int i = 0; i < seller.size(); i++) {
            List<CartBean.DataBean.ListBean> list = seller.get(i).getList();
            for (int j = 0; j <list.size() ; j++) {
                //只要是选中状态
                if(list.get(j).getSelected()==1){
                    int num = list.get(j).getNum();
                    totalNumber+=num;
                }
            }
        }
        return totalNumber;

    }

    //当加减器被点击得时候调用，改变当前商品得数量
    public void changeCurrentProductNumber(int groupPosition, int childPosition, int number) {

        List<CartBean.DataBean.ListBean> list = seller.get(groupPosition).getList();

        CartBean.DataBean.ListBean listBean = list.get(childPosition);
        listBean.setNum(number);
    }


    static class ParentViewHolder {
        @BindView(R.id.seller_cb)
        CheckBox sellerCb;
        @BindView(R.id.seller_name_title)
        TextView sellerNameTitle;

        ParentViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.child_cb)
        CheckBox childCb;
        @BindView(R.id.child_img)
        ImageView childImg;
        @BindView(R.id.child_title)
        TextView childTitle;
        @BindView(R.id.child_price)
        TextView childPrice;
        @BindView(R.id.myadd_sub)
        MyAddSubView myaddSub;
        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    //接口回调
    onCartListChangeListener onCartListChangeListener;

    public void setOnCartListChangeListener(ExpandViewAdapter.onCartListChangeListener onCartListChangeListener) {
        this.onCartListChangeListener = onCartListChangeListener;
    }

    public interface onCartListChangeListener {

        void onSellerCheckChange(int groupPosition);

        void onProductCheckedChange(int groupPosition, int childPosition);

        void onProductNumberChange(int groupPosition, int childPosition, int number);
    }
}
