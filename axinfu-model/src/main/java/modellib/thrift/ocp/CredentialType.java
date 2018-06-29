package modellib.thrift.ocp;

import java.io.Serializable;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class CredentialType implements Serializable {
    public String verify_type_code;
    public String name;
    public String trim;
    public String title;
    public String hint;
    public String type;
    public String reg_exp;
    public String max_length;

    @Override
    public String toString() {
        return "CredentialType{" +
                "verify_type_code='" + verify_type_code + '\'' +
                ", name='" + name + '\'' +
                ", trim='" + trim + '\'' +
                ", title='" + title + '\'' +
                ", hint='" + hint + '\'' +
                ", type='" + type + '\'' +
                ", reg_exp='" + reg_exp + '\'' +
                ", max_length='" + max_length + '\'' +
                '}';
    }
}
