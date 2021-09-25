package com.android.systemui.util.annotations;

import static android.service.controls.DeviceTypes.*;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        TYPE_GENERIC_ON_OFF,
        TYPE_GENERIC_START_STOP,
        TYPE_GENERIC_OPEN_CLOSE,
        TYPE_GENERIC_LOCK_UNLOCK,
        TYPE_GENERIC_ARM_DISARM,
        TYPE_GENERIC_TEMP_SETTING,
        TYPE_GENERIC_VIEWSTREAM,

        TYPE_UNKNOWN,

        TYPE_AC_HEATER,
        TYPE_AC_UNIT,
        TYPE_AIR_FRESHENER,
        TYPE_AIR_PURIFIER,
        TYPE_COFFEE_MAKER,
        TYPE_DEHUMIDIFIER,
        TYPE_DISPLAY,
        TYPE_FAN,
        TYPE_HOOD,
        TYPE_HUMIDIFIER,
        TYPE_KETTLE,
        TYPE_LIGHT,
        TYPE_MICROWAVE,
        TYPE_OUTLET,
        TYPE_RADIATOR,
        TYPE_REMOTE_CONTROL,
        TYPE_SET_TOP,
        TYPE_STANDMIXER,
        TYPE_STYLER,
        TYPE_SWITCH,
        TYPE_TV,
        TYPE_WATER_HEATER,
        TYPE_DISHWASHER,
        TYPE_DRYER,
        TYPE_MOP,
        TYPE_MOWER,
        TYPE_MULTICOOKER,
        TYPE_SHOWER,
        TYPE_SPRINKLER,
        TYPE_WASHER,
        TYPE_VACUUM,
        TYPE_AWNING,
        TYPE_BLINDS,
        TYPE_CLOSET,
        TYPE_CURTAIN,
        TYPE_DOOR,
        TYPE_DRAWER,
        TYPE_GARAGE,
        TYPE_GATE,
        TYPE_PERGOLA,
        TYPE_SHUTTER,
        TYPE_WINDOW,
        TYPE_VALVE,
        TYPE_LOCK,
        TYPE_SECURITY_SYSTEM,
        TYPE_HEATER,
        TYPE_REFRIGERATOR,
        TYPE_THERMOSTAT,
        TYPE_CAMERA,
        TYPE_DOORBELL,
        TYPE_ROUTINE
})
public @interface DeviceType {}
