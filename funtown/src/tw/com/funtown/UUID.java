package tw.com.funtown;

import android.content.Context;
import tw.com.funtown.internal.Utility;

public class UUID {
	Context __ctx;
    private static final Object __staticLock = new Object();	
    private static UUID __uuid;	
	UUID(Context ctx) {
		__ctx=ctx;
	}
    public static final UUID getInstance(Context ctx) {
        synchronized (UUID.__staticLock) {
        	if(UUID.__uuid !=null) {
        		return UUID.__uuid;
        	}
        	else {
        		__uuid = new UUID(ctx);
        		return __uuid;
        	}
        }
    }
	public void GenerateUUID(OnUUIDGeneratedListener listener) {
        String uuid=Utility.getCurDeviceUUID(__ctx); 
        listener.onUUIDGenerated(uuid);
	}
	public interface OnUUIDGeneratedListener  {
	       public void onUUIDGenerated(String UUID);		 
	}
}
