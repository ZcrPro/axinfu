package com.xyaxf.axpay.modle;

public enum PayFor {
	RechargeECard(),
	SchoolFee(),
	ScanPay(),
	GatewayPay(),
	RechargeMobile(),
	RechargeFlow(),
	TdtcFee(),
	UPQRPay(),
	AxfQRPay(),
	ALL(),
	CANCEL(),
	;

	static{
		for(PayFor e: values()){
			if(!e.manual && e.ordinal() > 0){
				e.code = values()[e.ordinal() - 1].code + 1;
			}
		}
	}

	private int code = 0;
	private boolean manual = false;
	private PayFor(int code){
		this.code = code;
		this.manual = true;
	}

	private PayFor(){
	}

	public int getCode(){
		return code;
	}
}
