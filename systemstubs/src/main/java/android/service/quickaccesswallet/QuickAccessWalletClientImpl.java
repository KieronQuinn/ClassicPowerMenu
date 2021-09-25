/*
 * Copyright 2020 The Android Open Source Project
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

package android.service.quickaccesswallet;

import android.annotation.CallbackExecutor;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Implements {@link QuickAccessWalletClient}. The client connects, performs requests, waits for
 * responses, and disconnects automatically one minute after the last call is performed.
 */
public class QuickAccessWalletClientImpl implements QuickAccessWalletClient, ServiceConnection {

    QuickAccessWalletClientImpl(@NonNull Context context) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean isWalletServiceAvailable() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean isWalletFeatureAvailable() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean isWalletFeatureAvailableWhenDeviceLocked() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void getWalletCards(
            @NonNull android.service.quickaccesswallet.GetWalletCardsRequest request,
            @NonNull OnWalletCardsRetrievedCallback callback) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void getWalletCards(
            @NonNull @CallbackExecutor Executor executor,
            @NonNull GetWalletCardsRequest request,
            @NonNull OnWalletCardsRetrievedCallback callback) {
        throw new RuntimeException("Stub!");

    }

    @Override
    public void selectWalletCard(@NonNull SelectWalletCardRequest request) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void notifyWalletDismissed() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void addWalletServiceEventListener(WalletServiceEventListener listener) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void addWalletServiceEventListener(
            @NonNull @CallbackExecutor Executor executor,
            @NonNull WalletServiceEventListener listener) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void removeWalletServiceEventListener(WalletServiceEventListener listener) {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void disconnect() {
        throw new RuntimeException("Stub!");
    }

    @Override
    @Nullable
    public Intent createWalletIntent() {
        throw new RuntimeException("Stub!");
    }

    @Override
    @Nullable
    public Intent createWalletSettingsIntent() {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    private Intent createIntent(@Nullable String activityName, String packageName, String action) {
        throw new RuntimeException("Stub!");
    }

    @Nullable
    private static String queryActivityForAction(PackageManager pm, String packageName,
            String action) {
        throw new RuntimeException("Stub!");
    }

    private static boolean isActivityEnabled(PackageManager pm, ComponentName component) {
        throw new RuntimeException("Stub!");
    }

    @Override
    @Nullable
    public Drawable getLogo() {
        throw new RuntimeException("Stub!");
    }

    @Override
    @Nullable
    public CharSequence getServiceLabel() {
        throw new RuntimeException("Stub!");
    }

    @Override
    @Nullable
    public CharSequence getShortcutShortLabel() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public CharSequence getShortcutLongLabel() {
        throw new RuntimeException("Stub!");
    }

    @Override // ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder binder) {
        throw new RuntimeException("Stub!");
    }

    @Override // ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        throw new RuntimeException("Stub!");
    }

    @Override // ServiceConnection
    public void onBindingDied(ComponentName name) {
        throw new RuntimeException("Stub!");
    }

    @Override // ServiceConnection
    public void onNullBinding(ComponentName name) {
        throw new RuntimeException("Stub!");
    }

}
