package com.tencent.qcloud.tuikit.tuigroup.bean;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tuicore.TUIConstants;
import com.tencent.qcloud.tuicore.TUICore;
import com.tencent.qcloud.tuicore.util.BackgroundTasks;
import com.tencent.qcloud.tuicore.util.ToastUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.Common;
import com.tencent.qcloud.tuikit.tuigroup.net.EncryptUtil;
import com.tencent.qcloud.tuikit.tuigroup.net.OkHttp3_0Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class GroupInfo extends ChatInfo {

    public static final String GROUP_TYPE_MEETING = V2TIMManager.GROUP_TYPE_MEETING;
    public static final String GROUP_TYPE_AVCHATROOM = V2TIMManager.GROUP_TYPE_AVCHATROOM;
    public static final String GROUP_TYPE_COMMUNITY = V2TIMManager.GROUP_TYPE_COMMUNITY;
    public static final String GROUP_TYPE_PUBLIC = V2TIMManager.GROUP_TYPE_PUBLIC;
    public static final String GROUP_TYPE_WORK = V2TIMManager.GROUP_TYPE_WORK;

    public static final int GROUP_MEMBER_FILTER_ALL = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL;
    public static final int GROUP_MEMBER_FILTER_OWNER = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_OWNER;
    public static final int GROUP_MEMBER_FILTER_ADMIN = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN;
    public static final int GROUP_MEMBER_FILTER_COMMON = V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_COMMON;

    private String groupType;
    private int memberCount;
    private String groupName;
    private String notice;
    private List<GroupMemberInfo> memberDetails = new ArrayList<>();
    private int joinType;
    private String owner;
    private boolean messageReceiveOption;
    private String faceUrl;
    private long mNextSeq = 0;
    private boolean canManagerGroup;
    private boolean isAllMuted;

    public GroupInfo() {
        setType(V2TIMConversation.V2TIM_GROUP);
    }

    /**
     * ???????????????
     *
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * ???????????????
     *
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public String getNotice() {
        return notice;
    }

    /**
     * ???????????????
     *
     * @param signature
     */
    public void setNotice(String signature) {
        this.notice = signature;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public int getJoinType() {
        return joinType;
    }

    /**
     * ????????????????????????
     *
     * @param joinType
     */
    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    /**
     * ??????????????????Public/Private/ChatRoom
     *
     * @return
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * ???????????????
     *
     * @param groupType
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public List<GroupMemberInfo> getMemberDetails() {
        return memberDetails;
    }

    /**
     * ????????????????????????
     *
     * @param memberDetails
     */
    public void setMemberDetails(List<GroupMemberInfo> memberDetails) {
        this.memberDetails = memberDetails;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public int getMemberCount() {
        return memberCount;
    }

    /**
     * ?????????????????????
     *
     * @param memberCount
     */
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public boolean isOwner() {
        return V2TIMManager.getInstance().getLoginUser().equals(owner);
    }

    /**
     * ?????????????????????
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner(){
        return owner;
    }


    /**
     * ????????????????????????
     *
     * @return
     */
    public boolean getMessageReceiveOption() {
        return messageReceiveOption;
    }

    /**
     * ????????????????????????
     *
     * @param messageReceiveOption, true,???????????? false???????????????
     */
    public void setMessageReceiveOption(boolean messageReceiveOption) {
        this.messageReceiveOption = messageReceiveOption;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public long getNextSeq() {
        return mNextSeq;
    }

    public void setNextSeq(long mNextSeq) {
        this.mNextSeq = mNextSeq;
    }

    public boolean isCanManagerGroup() {
        return canManagerGroup;
    }

    public boolean isAllMuted() {
        return isAllMuted;
    }

    /**
     * ???SDK?????????TUIKit????????????bean
     *
     * @param infoResult
     * @param createId
     * @return
     */
    public GroupInfo covertTIMGroupDetailInfo(V2TIMGroupInfoResult infoResult, String createId) {
        if (infoResult.getResultCode() != 0) {
            return this;
        }
        //???????????????
        setChatName(infoResult.getGroupInfo().getGroupName());
        setGroupName(infoResult.getGroupInfo().getGroupName());
        setId(infoResult.getGroupInfo().getGroupID());
        setNotice(infoResult.getGroupInfo().getNotification());
        setMemberCount(infoResult.getGroupInfo().getMemberCount());
        setGroupType(infoResult.getGroupInfo().getGroupType());
      //  setOwner(infoResult.getGroupInfo().getOwner());
        setOwner(createId);
        setJoinType(infoResult.getGroupInfo().getGroupAddOpt());
        setMessageReceiveOption(infoResult.getGroupInfo().getRecvOpt() == V2TIMMessage.V2TIM_RECEIVE_NOT_NOTIFY_MESSAGE ? true : false);
        setFaceUrl(infoResult.getGroupInfo().getFaceUrl());
        setGroupAddOpt(infoResult.getGroupInfo().getGroupAddOpt());
        int role = infoResult.getGroupInfo().getRole();
        canManagerGroup = role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_OWNER
                || role == V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN
                || groupType == V2TIMManager.GROUP_TYPE_WORK;
        isAllMuted = infoResult.getGroupInfo().isAllMuted();
        return this;
    }
}
