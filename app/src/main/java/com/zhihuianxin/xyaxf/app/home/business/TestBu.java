package com.zhihuianxin.xyaxf.app.home.business;

/**
 * Created by zcrpro on 2016/11/3.
 */
public class TestBu  {

    private String name;
    private Integer image;

    public TestBu(String name, Integer image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }
}
