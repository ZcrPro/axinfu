package com.zhihuianxin.xyaxf.app.base;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

/**
 * Created by Vincent on 2016/12/2.
 */

@AllFavor
public interface IException {

    // Relogin异常
    @Default("false")
    boolean getReloginException();

    void setReloginException(boolean flag);
}
