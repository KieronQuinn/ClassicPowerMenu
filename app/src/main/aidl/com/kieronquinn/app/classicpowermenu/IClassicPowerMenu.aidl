// IClassicPowerMenu.aidl
package com.kieronquinn.app.classicpowermenu;

import com.kieronquinn.app.classicpowermenu.model.service.ServiceBindContainer;
import com.kieronquinn.app.classicpowermenu.model.service.ServiceUnbindContainer;
import com.kieronquinn.app.classicpowermenu.model.service.BroadcastContainer;
import com.kieronquinn.app.classicpowermenu.model.service.ActivityContainer;

interface IClassicPowerMenu {

    //Used for QuickAccessWallet & Controls services
    int bindServicePrivileged(in ServiceBindContainer bindContainer, in Intent intent, int flags);
    boolean unBindServicePrivileged(in ServiceUnbindContainer unbindContainer);

    //Broadcasting ACTION_CLOSE_SYSTEM_DIALOGS (required on 12+)
    void sendBroadcastPrivileged(in BroadcastContainer broadcastContainer, in Intent intent, String intentType, String attributionTag);

    //Launching Control long press actions
    int startActivityPrivileged(in ActivityContainer activityContainer, in Intent intent);

    //Resolve a PendingIntent to a regular Intent (used in QuickAccessWallet)
    Intent getIntentForPendingIntent(in PendingIntent pendingIntent);

    //Power options
    void shutdown();
    void reboot(boolean safeMode);
    void rebootWithReason(String reason);
    void restartSystemUi();

    //Screenshot button
    void takeScreenshot();

    //Lockdown button
    void lockdown();
    int getStrongAuth();

    //Remote access to copy/load Google Pay database and images for loyalty cards
    ParcelFileDescriptor getGooglePayDatabaseForLoyalty();
    ParcelFileDescriptor getGooglePayLoyaltyImageForId(String id);

    //Requires privilaged call
    int getCurrentUser();
    void showPowerMenu();

    //Bypass 5 second rule
    void resumeAppSwitches();

}