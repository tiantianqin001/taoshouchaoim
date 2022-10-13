package com.pingmo.chengyan.activity.shop.adapter;

import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.shop.model.entity.AddressEntity;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AddressRVAdapter extends BaseQuickAdapter<AddressEntity, BaseViewHolder> {
    public AddressRVAdapter(@Nullable List<AddressEntity> data) {
        super(R.layout.item_address_list, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, AddressEntity entity) {
        if (entity == null) {
            return;
        }
        holder.setText(R.id.tv_name_address, entity.addressee);
        holder.setText(R.id.tv_phone_address, entity.phone);
        holder.setText(R.id.tv_address, entity.address);
        CheckBox checkBox = holder.getView(R.id.cb_default);
        if (entity.getIsDefaultBoolean()) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }
}
