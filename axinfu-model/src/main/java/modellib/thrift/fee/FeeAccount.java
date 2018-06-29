package modellib.thrift.fee;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import modellib.thrift.business.AccountVerifyItem;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class FeeAccount extends RealmObject implements Serializable, Cloneable {
    public String status;  // required
    public String name;  // optional
    @PrimaryKey
    public String student_no;  // optional
    public String new_student_no;  // optional
    public String id_card_no;  // optional
    public String other_no;  // optional
    public AccountVerifyItem new_stu_verify_config; //新生缴费记录
    public boolean support_required_fee;
    public boolean collect_bankcard=false;
    public boolean id_card_verified=false;
    public String open_fee_account_hint; 	// 开通缴费功能提示信息, 仅当status不为OK时有效
    public String new_student_fee_hint;	// 新生缴费提示信息, 仅当status不为OK时有效

    @Override
    public Object clone() {
        FeeAccount obj = new FeeAccount();
        try {
            //obj = (FeeAccount)super.clone();
            obj.new_stu_verify_config = (AccountVerifyItem) new_stu_verify_config.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}