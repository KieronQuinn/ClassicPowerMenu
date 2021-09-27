## Can Classic Power Menu work without root?

Unfortunately not. Root is required to access the Quick Access Wallet and Device Controls APIs, so even with ADB/`shell` permissions, it would not be possible, therefore root is required.

## Why does the Accessibility Service require permissions to view & control my screen?

The "View & Control screen" permission is automatically granted to all Accessibility Services, even when not required. Classic Power Menu only uses Accessibility Service to react to the power button being long pressed (by tracking when the original power menu opens), it does not track any other device or app usage.

## There's a delay when opening Classic Power Menu / I can see the normal power menu briefly before Classic Power Menu opens

This is normal, when using the Accessibility Service. It's because the original power menu has to fire *before* Classic Power Menu can open, and therefore there is a slight delay and a brief moment where you can see the stock power menu.

For a faster experience, the optional Xposed module does not have this issue.

## Why do I need classic Google Pay rather than the new Pay ("GPay") for Loyalty Cards to work?

Currently, only the format for reading Loyalty Cards from the classic Google Pay app is supported, due to region restrictions preventing testing on the new version. This may change in the future if/when Google roll out the new app worldwide.

## Which apps work with Quick Access Wallet?

Google Pay is the only known service that works with Quick Access Wallet. You don't actually *need* Google Pay or GPay installed for it to work (Google Play Services provides it), but it allows configuration if you do.

## Which apps work with Device Controls?

There are numerous apps that work with Device Controls. The feature is designed for controlling Smart Home devices, so apps like Google Home have support.

If you do not have Smart Home devices, or prefer to use the space for controlling your phone instead, there are some apps on Google Play that add Quick Settings-esque toggles and controls to the menu (search "Device Controls").

For power users, Tasker also supports running tasks via the feature.

## Can you add (x) button to the Power Options?

Probably not. The Power Options are intentionally minimal, use a Device Controls app (such as those mentioned above) for more options including Airplane Mode, Sound Mode etc.

## Background Blur isn't working on my device

On some devices you will have to enable the system background blurs for Classic Power Menu's background blur to work. This can be done easily in a terminal with root, or ADB shell with root. The following commands assume you are using Magisk.

```
su
resetprop ro.surface_flinger.supports_background_blur 1
pkill -TERM -f com. android.systemui
```

Your device will reboot and blurs will be enabled. Please note that this has some other side effects of where blur is enabled, such as when the notification shade is open. To disable it, run the same commands, but with `0` rather than `1`.

## How do I keep Classic Power Menu up to date, is it on the Play Store?

Classic Power Menu is not on the Play Store as it would not meet the Terms of Service, due to its use of Accessibility Service (for non-accessibility uses) and using root to bypass permissions (for Quick Access Wallet & Device Controls).

Instead, Classic Power Menu will check for updates periodically and on launch, and notify you if an update is available.

