/*
 * Copyright (C) 2019 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.systemui.broadcast

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.UserHandle
import android.util.ArrayMap
import android.util.ArraySet
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.util.Preconditions
import com.android.systemui.Dumpable
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger
import com.android.systemui.util.extensions.indentIfPossible
import com.android.systemui.util.extensions.registerReceiverCompat
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

private const val MSG_REGISTER_RECEIVER = 0
private const val MSG_UNREGISTER_RECEIVER = 1
private const val TAG = "UserBroadcastDispatcher"
private const val DEBUG = false

/**
 * Broadcast dispatcher for a given user registration [userId].
 *
 * Created by [BroadcastDispatcher] as needed by users. The value of [userId] can be
 * [UserHandle.USER_ALL].
 */
open class UserBroadcastDispatcher(
    private val context: Context,
    private val userId: Int,
    private val bgLooper: Looper,
    private val bgExecutor: Executor,
    private val logger: BroadcastDispatcherLogger
) : Dumpable {

    companion object {
        // Used only for debugging. If not debugging, this variable will not be accessed and all
        // received broadcasts will be tagged with 0. However, as DEBUG is false, nothing will be
        // logged
        val index = AtomicInteger(0)
    }

    private val bgHandler = object : Handler(bgLooper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_REGISTER_RECEIVER -> handleRegisterReceiver(msg.obj as ReceiverData)
                MSG_UNREGISTER_RECEIVER -> handleUnregisterReceiver(msg.obj as BroadcastReceiver)
                else -> Unit
            }
        }
    }

    // Only modify in BG thread
    @VisibleForTesting
    internal val actionsToActionsReceivers = ArrayMap<String, ActionReceiver>()
    private val receiverToActions = ArrayMap<BroadcastReceiver, MutableSet<String>>()

    @VisibleForTesting
    internal fun isReceiverReferenceHeld(receiver: BroadcastReceiver): Boolean {
        return actionsToActionsReceivers.values.any {
            it.hasReceiver(receiver)
        } || (receiver in receiverToActions)
    }

    /**
     * Register a [ReceiverData] for this user.
     */
    fun registerReceiver(receiverData: ReceiverData) {
        bgHandler.obtainMessage(MSG_REGISTER_RECEIVER, receiverData).sendToTarget()
    }

    /**
     * Unregister a given [BroadcastReceiver] for this user.
     */
    fun unregisterReceiver(receiver: BroadcastReceiver) {
        bgHandler.obtainMessage(MSG_UNREGISTER_RECEIVER, receiver).sendToTarget()
    }

    @SuppressLint("RestrictedApi")
    private fun handleRegisterReceiver(receiverData: ReceiverData) {
        Preconditions.checkState(bgHandler.looper.isCurrentThread,
            "This method should only be called from BG thread")
        if (DEBUG) Log.w(TAG, "Register receiver: ${receiverData.receiver}")
        receiverToActions
            .getOrPut(receiverData.receiver, { ArraySet() })
            .addAll(receiverData.filter.actionsIterator()?.asSequence() ?: emptySequence())
        receiverData.filter.actionsIterator().forEach {
            actionsToActionsReceivers
                .getOrPut(it, { createActionReceiver(it) })
                .addReceiverData(receiverData)
        }
        logger.logReceiverRegistered(userId, receiverData.receiver)
    }

    @VisibleForTesting
    internal open fun createActionReceiver(action: String): ActionReceiver {
        return ActionReceiver(
            action,
            userId,
            {
                context.registerReceiverCompat(this, it, null, bgHandler)
                logger.logContextReceiverRegistered(userId, it)
            },
            {
                try {
                    context.unregisterReceiver(this)
                    logger.logContextReceiverUnregistered(userId, action)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Trying to unregister unregistered receiver for user $userId, " +
                            "action $action",
                        IllegalStateException(e))
                }
            },
            bgExecutor,
            logger
        )
    }

    @SuppressLint("RestrictedApi")
    private fun handleUnregisterReceiver(receiver: BroadcastReceiver) {
        Preconditions.checkState(bgHandler.looper.isCurrentThread,
            "This method should only be called from BG thread")
        if (DEBUG) Log.w(TAG, "Unregister receiver: $receiver")
        receiverToActions.getOrDefault(receiver, mutableSetOf()).forEach {
            actionsToActionsReceivers.get(it)?.removeReceiver(receiver)
        }
        receiverToActions.remove(receiver)
        logger.logReceiverUnregistered(userId, receiver)
    }

    override fun dump(fd: FileDescriptor, pw: PrintWriter, args: Array<out String>) {
        pw.indentIfPossible {
            actionsToActionsReceivers.forEach { (action, actionReceiver) ->
                println("$action:")
                actionReceiver.dump(fd, pw, args)
            }
        }
    }
}