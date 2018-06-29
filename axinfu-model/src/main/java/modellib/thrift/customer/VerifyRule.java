package modellib.thrift.customer;

import java.io.Serializable;

/**
 * Created by Vincent on 2017/12/20.
 */

public class VerifyRule implements Serializable{
    public String reg_exp;
    public String error_hint;
}
