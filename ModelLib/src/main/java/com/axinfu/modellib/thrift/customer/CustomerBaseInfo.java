package com.axinfu.modellib.thrift.customer;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class CustomerBaseInfo extends RealmObject implements Serializable ,Cloneable{
	@PrimaryKey
	public String mobile ;  // required
	public String nickname ;  // optional
	public String gender;  // optional
	public String avatar ;  // optional
	public String name ;  // optional
	public String regist_serial ;  // optional
	public RealmList<Protocol> protocol;

	@Override
	public Object clone() {
		CustomerBaseInfo obj = null;
		try{
			obj = (CustomerBaseInfo)super.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}