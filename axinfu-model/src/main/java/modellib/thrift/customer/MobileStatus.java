package modellib.thrift.customer;

public enum MobileStatus {
    NotRegistered(),
    NoPassword(),
    OK(),

    ;

    static{
        for(MobileStatus e: values()){
            if(!e.manual && e.ordinal() > 0){
                e.code = values()[e.ordinal() - 1].code + 1;
            }
        }
    }

    private int code = 0;
    private boolean manual = false;
    private MobileStatus(int code){
        this.code = code;
        this.manual = true;
    }

    private MobileStatus(){
    }

    public int getCode(){
        return code;
    }
}