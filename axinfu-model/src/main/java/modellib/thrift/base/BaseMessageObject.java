package modellib.thrift.base;

import java.io.Serializable;

/**
 * Created by John on 2014/7/28.
 */
public class BaseMessageObject implements Serializable, Cloneable{
//	public String toString(){
//		MessageFactory converter = new MessageFactory();
//		return converter.toStringMessage(this);
//	}
//
//	@Override
//	public boolean equals(Object o){
//		if(o == null || o.getClass() != ((Object)this).getClass()){
//			return false;
//		}
//
//		BaseMessageObject otherObj = (BaseMessageObject) o;
//
//		return toString().equals(otherObj.toString());
//	}
//
//	public BaseMessageObject clone(){
//		try {
//			return (BaseMessageObject) super.clone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
