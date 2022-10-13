package com.tencent.qcloud.tuikit.tuiconversation.bean;

import java.io.Serializable;

public class SystemMessageBean implements Serializable {
    private String content;
    private String title;
    private String createTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SystemMessageBean{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
