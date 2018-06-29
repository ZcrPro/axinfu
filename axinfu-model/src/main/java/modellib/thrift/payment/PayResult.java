package modellib.thrift.payment;

public enum PayResult {
	Payed(),
	NotPayed(),

	;

	static{
		for(PayResult e: values()){
			if(!e.manual && e.ordinal() > 0){
				e.code = values()[e.ordinal() - 1].code + 1;
			}
		}
	}

	private int code = 0;
	private boolean manual = false;
	PayResult(int code){
		this.code = code;
		this.manual = true;
	}

	private PayResult(){
	}

	public int getCode(){
		return code;
	}
}
