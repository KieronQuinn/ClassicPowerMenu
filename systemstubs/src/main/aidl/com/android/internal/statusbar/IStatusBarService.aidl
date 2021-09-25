package com.android.internal.statusbar;

interface IStatusBarService
{
    void shutdown();
    void reboot(boolean safeMode);
}