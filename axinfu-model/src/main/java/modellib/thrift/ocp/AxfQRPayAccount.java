package modellib.thrift.ocp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zcrpro on 2017/11/17.
 */

public class AxfQRPayAccount implements Serializable {
    public String status;
    public String name;
    public String student_no;
    public String credential_type;
    public String credential_no;
    public List<CredentialType> supported_credential_type;
    public boolean name_verified;
    public boolean id_card_verified;


    @Override
    public String toString() {
        return "AxfQRPayAccount{" +
                "status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", student_no='" + student_no + '\'' +
                ", credential_type='" + credential_type + '\'' +
                ", credential_no='" + credential_no + '\'' +
                ", supported_credential_type=" + supported_credential_type +
                ", name_verified=" + name_verified +
                '}';
    }
}
