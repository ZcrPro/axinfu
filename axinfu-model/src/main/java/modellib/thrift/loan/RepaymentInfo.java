package modellib.thrift.loan;

import java.io.Serializable;

/**
 * Created by Vincent on 2018/3/12.
 */

public class RepaymentInfo implements Serializable{
    public String id_card_no;
    public String card_no;
    public String instalment_apply_no;
    public String instalment_sequence_no;
    public String repayment_type;
    public String status_change_fee;
    public String pre_payment_fee;
    public String loan_surplus_amt;
    public String loan_surplus_fee;
    public String capital_balance;
    public String this_period_no_return;
    public String repayment_part_amount;
}
