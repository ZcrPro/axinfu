package modellib.data;

import com.cocosw.favor.Default;

/**
 * Created by Vincent on 2016/11/1.
 */

public interface ISession {
    @Default("")
    String getSession();
    void setSession(String session);
}
