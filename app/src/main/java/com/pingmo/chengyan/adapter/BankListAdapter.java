package com.pingmo.chengyan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.butel.easyrecyclerview.adapter.BaseViewHolder;
import com.butel.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.bean.BankItemBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BankListAdapter extends RecyclerArrayAdapter<BankItemBean> {

    private boolean isEdit = false;
    private List<String> mSelectedList = new ArrayList<>();
    private OnSelectedBank mListener;

    public BankListAdapter(Context context, OnSelectedBank listener) {
        super(context);
        mListener = listener;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        if (!isEdit) {
            mSelectedList.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedList() {
        return mSelectedList;
    }

    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BankItemHolder(inflate(parent, R.layout.view_bank_item));
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, int position) {
        super.OnBindViewHolder(holder, position);
        BankItemBean bean = getItem(position);
        BankItemHolder bankHolder = (BankItemHolder) holder;
        bankHolder.mName.setText(bean.getBackName());
        bankHolder.mNum.setText(bean.getCardNumber());
        bankHolder.mCheckBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        bankHolder.mCheckBtn.setImageResource(isSelected(bean.getBackCode()) ? R.mipmap.check : R.mipmap.uncheck);
        bankHolder.mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBean(bean, position, bankHolder);
            }
        });
        bankHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    if (mListener != null) {
                        mListener.onSelected(bean);
                    }
                } else {
                    selectedBean(bean, position, bankHolder);
                }
            }
        });
    }

    private boolean isSelected(String bankCode) {
        if (mSelectedList != null && mSelectedList.size() > 0) {
            for (String code : mSelectedList) {
                if (!TextUtils.isEmpty(code) && code.equals(bankCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void selectedBean(BankItemBean bean, int position, BankItemHolder bankHolder) {
        if (isSelected(bean.getBackCode())) {
            mSelectedList.remove(bean.getBackCode());
            bankHolder.mCheckBtn.setImageResource(R.mipmap.uncheck);
        } else {
            mSelectedList.add(bean.getBackCode());
            bankHolder.mCheckBtn.setImageResource(R.mipmap.check);
        }
    }

    public interface OnSelectedBank {
        void onSelected(BankItemBean bean);
    }
}
