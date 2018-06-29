package modellib.thrift.payment;

import java.io.Serializable;

/**
 * Created by zcrpro on 2016/12/12.
 */

public class BankLimit implements Serializable{

    public String name;// 银行名
    public String logo_url; // 银行图标地址
    public String limit_per_time;// 单笔限额
    public String limit_per_day; // 单日限额
    public String iss_ins_code; // 银行卡限额的卡

    //扩展
    public boolean local=false;
    public int local_logo;

    /**
     * 本地扩展一个存储在缴费列表是否选中的标志
     */
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
