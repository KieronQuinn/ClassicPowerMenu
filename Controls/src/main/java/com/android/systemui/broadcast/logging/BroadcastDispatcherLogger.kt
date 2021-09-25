/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.broadcast.logging

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter

class BroadcastDispatcherLogger {

    fun logBroadcastReceived(broadcastId: Int, user: Int, intent: Intent) {
        //no-op
    }

    fun logBroadcastDispatched(broadcastId: Int, action: String?, receiver: BroadcastReceiver) {
        //no-op
    }

    fun logReceiverRegistered(user: Int, receiver: BroadcastReceiver) {
        //no-op
    }

    fun logReceiverUnregistered(user: Int, receiver: BroadcastReceiver) {
        //no-op
    }

    fun logContextReceiverRegistered(user: Int, filter: IntentFilter) {
        //no-op
    }

    fun logContextReceiverUnregistered(user: Int, action: String) {
        //no-op
    }
}