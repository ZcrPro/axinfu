package com.axinfu.modellib.thrift.message;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vincent on 2016/12/29.
 */

public class ImportantMessageWithUser extends RealmObject{
    @PrimaryKey
    public String mobile;
    public RealmList<ImportantMessage> importantMessages;
}
