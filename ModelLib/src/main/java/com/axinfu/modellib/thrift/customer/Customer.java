package com.axinfu.modellib.thrift.customer;

import com.axinfu.modellib.thrift.business.Business;
import com.axinfu.modellib.thrift.ecard.ECardAccount;
import com.axinfu.modellib.thrift.fee.FeeAccount;
import com.axinfu.modellib.thrift.resource.School;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class Customer extends RealmObject implements Serializable,Cloneable {
	@PrimaryKey
	public String mobile;
	public CustomerBaseInfo base_info ;  // required
	public School school ;  // optional
	public ECardAccount ecard_account ;  // optional
	public FeeAccount fee_account ;  // optional
	public RealmList<Business> businesses;
	public boolean is_could_cancel;
	@Ignore
	public boolean is_show_bind_card_guide;// 是否显示绑卡引导
	@Ignore
	public String bind_card_short_hint;		// 绑卡引导提示 短
	@Ignore
	public String bind_card_long_hint;		// 绑卡引导提示 长

	@Override
	public Object clone() {
		Customer obj = new Customer();
		try{
//			obj = (Customer)super.clone();
			obj.base_info = (CustomerBaseInfo) base_info.clone();
			obj.school = (School) school.clone();
			obj.ecard_account = (ECardAccount) ecard_account.clone();
			obj.fee_account = (FeeAccount) fee_account.clone();

			RealmList<Business> newList = new RealmList<>();
			for(Business business : businesses){
				newList.add((Business) business.clone());
			}
			obj.businesses = newList;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}