package com.axinfu.modellib.thrift.customer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Vincent on 2017/12/20.
 */

public class VerifyField implements Serializable{
    public String name;
    public String field_name;
    public boolean trim;
    public String hint;
    public ArrayList<VerifyRule> verify_rule;
    public int max_length;
}
