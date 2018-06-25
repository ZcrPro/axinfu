package com.axinfu.modellib.thrift.business;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class AccountVerifyItem extends RealmObject implements Serializable,Cloneable {
	@PrimaryKey
	public String name ;  // required
	public Boolean trim  = false;  // optional
	public String title ;  // required
	public String hint ;  // optional
	public String type ;  // required
	public String reg_exp ;  // required
	public Integer max_length ;  // optional

	@Override
	public Object clone() {
		AccountVerifyItem obj = null;
		try{
			obj = (AccountVerifyItem)super.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}