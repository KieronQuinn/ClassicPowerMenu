/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.os;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * An adapter {@link Executor} that posts all executed tasks onto the given
 * {@link Handler}.
 */
public class HandlerExecutor implements Executor {
    private final Handler mHandler;

    public HandlerExecutor(@NonNull Handler handler) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void execute(Runnable command) {
        throw new RuntimeException("Stub!");
    }
}