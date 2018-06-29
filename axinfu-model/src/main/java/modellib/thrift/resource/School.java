package modellib.thrift.resource;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by zcrpro on 16/10/9.
 */

public class School extends RealmObject implements Serializable {
    @PrimaryKey
    public String code;             // 学校编号
    public String name;             // 学校名称
    public String logo;             // logo
    public String landmark_icon;
    public String pinyin_header;
    public String land_mark_image;
    public String quanpin;          // 全拼
    public String city_code;        // 城市编号
    //标志建筑图标

    @Override
    public Object clone() {
        School obj = null;
        try{
            obj = (School)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
