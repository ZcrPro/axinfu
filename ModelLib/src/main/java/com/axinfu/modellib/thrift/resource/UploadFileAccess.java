package com.axinfu.modellib.thrift.resource;

import io.realm.RealmObject;

import java.io.Serializable;

/**
 * Created by Vincent on 2016/11/12.
 */

public class UploadFileAccess extends RealmObject implements Serializable {
    public String uptoken;
    public String key;
    public String access_url;
}
