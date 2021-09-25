package com.android.systemui.util.annotations;

import static android.service.controls.actions.ControlAction.*;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
        RESPONSE_UNKNOWN,
        RESPONSE_OK,
        RESPONSE_FAIL,
        RESPONSE_CHALLENGE_ACK,
        RESPONSE_CHALLENGE_PIN,
        RESPONSE_CHALLENGE_PASSPHRASE
})
public @interface ResponseResult {};