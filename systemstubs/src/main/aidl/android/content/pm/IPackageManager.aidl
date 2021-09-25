package android.content.pm;

import android.content.pm.ServiceInfo;
import android.content.pm.ActivityInfo;

interface IPackageManager {
    ActivityInfo getActivityInfo(in ComponentName className, int flags, int userId);
    ServiceInfo getServiceInfo(in ComponentName className, int flags, int userId);
}