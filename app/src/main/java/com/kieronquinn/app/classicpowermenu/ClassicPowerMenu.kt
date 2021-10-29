package com.kieronquinn.app.classicpowermenu

import android.app.DownloadManager
import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.android.systemui.broadcast.BroadcastDispatcher
import com.android.systemui.broadcast.logging.BroadcastDispatcherLogger
import com.android.systemui.controls.CustomIconCache
import com.android.systemui.controls.controller.ControlsBindingController
import com.android.systemui.controls.controller.ControlsBindingControllerImpl
import com.android.systemui.controls.controller.ControlsController
import com.android.systemui.controls.controller.ControlsControllerImpl
import com.android.systemui.controls.management.ControlsListingController
import com.android.systemui.controls.management.ControlsListingControllerImpl
import com.android.systemui.controls.ui.ControlActionCoordinator
import com.android.systemui.controls.ui.ControlActionCoordinatorImpl
import com.android.systemui.controls.ui.ControlsUiController
import com.android.systemui.controls.ui.ControlsUiControllerImpl
import com.android.systemui.dump.DumpManager
import com.android.systemui.globalactions.GlobalActionsComponent
import com.android.systemui.plugins.ActivityStarter
import com.android.systemui.plugins.activitystarter.WalletActivityStarter
import com.android.systemui.plugins.cardblur.WalletCardBlurProvider
import com.android.systemui.statusbar.phone.ShadeController
import com.android.systemui.statusbar.phone.ShadeControllerImpl
import com.android.systemui.statusbar.policy.KeyguardStateController
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl
import com.android.systemui.util.concurrency.ExecutorImpl
import com.android.systemui.util.extensions.ControlsActivityStarter
import com.android.systemui.util.extensions.ServiceRunner
import com.kieronquinn.app.classicpowermenu.components.blur.BlurProvider
import com.kieronquinn.app.classicpowermenu.components.controls.ControlsActivityStarterImpl
import com.kieronquinn.app.classicpowermenu.components.github.UpdateChecker
import com.kieronquinn.app.classicpowermenu.components.monet.MonetColorProvider
import com.kieronquinn.app.classicpowermenu.components.navigation.*
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.WalletActivityStarterImpl
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.WalletCardBlurProviderImpl
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.LoyaltyCardsRepository
import com.kieronquinn.app.classicpowermenu.components.quickaccesswallet.loyaltycards.LoyaltyCardsRepositoryImpl
import com.kieronquinn.app.classicpowermenu.components.settings.Settings
import com.kieronquinn.app.classicpowermenu.components.settings.SettingsImpl
import com.kieronquinn.app.classicpowermenu.components.starter.PowerMenuStarter
import com.kieronquinn.app.classicpowermenu.components.starter.PowerMenuStarterImpl
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainer
import com.kieronquinn.app.classicpowermenu.service.container.CPMServiceContainerImpl
import com.kieronquinn.app.classicpowermenu.service.runner.CPMServiceRunner
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivityViewModel
import com.kieronquinn.app.classicpowermenu.ui.activities.MainActivityViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.activities.PowerMenuActivityViewModel
import com.kieronquinn.app.classicpowermenu.ui.activities.PowerMenuActivityViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.decision.DecisionViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.decision.DecisionViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main.PowerMenuViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.main.PowerMenuViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.safemode.SafeModeTopSheetViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.powermenu.safemode.SafeModeTopSheetViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.developeroptions.SettingsDeveloperOptionsViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.developeroptions.SettingsDeveloperOptionsViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.devicecontrols.SettingsDeviceControlsViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.devicecontrols.SettingsDeviceControlsViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.main.SettingsMainViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.main.SettingsMainViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.SettingsPowerOptionsViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.SettingsPowerOptionsViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.rearrange.SettingsPowerOptionsRearrangeViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.poweroptions.rearrange.SettingsPowerOptionsRearrangeViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.SettingsQuickAccessWalletViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.SettingsQuickAccessWalletViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.rearrange.SettingsQuickAccessWalletRearrangeViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.quickaccesswallet.rearrange.SettingsQuickAccessWalletRearrangeViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck.SettingsRootCheckViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.rootcheck.SettingsRootCheckViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.update.UpdateAvailableBottomSheetViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.update.UpdateAvailableBottomSheetViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.update.UpdateDownloadBottomSheetViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.settings.update.UpdateDownloadBottomSheetViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.accessibility.SetupAccessibilityViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.accessibility.SetupAccessibilityViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.complete.SetupCompleteViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.complete.SetupCompleteViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.controls.SetupControlsViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.controls.SetupControlsViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.landing.SetupLandingViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.landing.SetupLandingViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.rootcheck.SetupRootCheckViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.rootcheck.SetupRootCheckViewModelImpl
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.wallet.SetupWalletViewModel
import com.kieronquinn.app.classicpowermenu.ui.screens.setup.wallet.SetupWalletViewModelImpl
import com.kieronquinn.app.classicpowermenu.utils.LifecycleApplication
import com.kieronquinn.monetcompat.core.MonetCompat
import com.topjohnwu.superuser.Shell
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.*
import com.android.systemui.plugins.monet.MonetColorProvider as WalletMonetColorProvider
import com.android.systemui.util.MonetColorProvider as ControlsMonetColorProvider

class ClassicPowerMenu: LifecycleApplication() {

    private val serviceModule by lazy {
        module {
            single<CPMServiceContainer> { CPMServiceContainerImpl(this@ClassicPowerMenu) }
            single<ServiceRunner> { CPMServiceRunner(get(), lifecycleScope) }
        }
    }

    private val singletonsModule  by lazy {
        module {
            single { BlurProvider.getBlurProvider(resources) }
            single<PowerMenuStarter> { PowerMenuStarterImpl(get(), get()) }
            single<PowerMenuNavigation> { PowerMenuNavigationImpl() }
            single<ContainerNavigation> { ContainerNavigationImpl() }
            single<AppNavigation> { AppNavigationImpl() }
            single<Settings> { SettingsImpl(get()) }
            single { UpdateChecker() }
        }
    }

    private val walletModule = module {
        single<WalletActivityStarter> { WalletActivityStarterImpl(get(), get()) }
        single<WalletCardBlurProvider> { WalletCardBlurProviderImpl(get(), get()) }
        single<LoyaltyCardsRepository> { LoyaltyCardsRepositoryImpl(get(), get(), get()) }
    }
    
    private val monetModule = module {
        val monetColorProvider = MonetColorProvider(this@ClassicPowerMenu)
        single<ControlsMonetColorProvider> { monetColorProvider }
        single<WalletMonetColorProvider> { monetColorProvider }
    }

    private val viewModelModule by lazy {
        module {
            viewModel<MainActivityViewModel> { MainActivityViewModelImpl(get(), get()) }
            viewModel<DecisionViewModel> { DecisionViewModelImpl(get(), get()) }
            viewModel<PowerMenuViewModel> { PowerMenuViewModelImpl(get(), get(), get(), get(), get(), get()) }
            viewModel<PowerMenuActivityViewModel> { PowerMenuActivityViewModelImpl(get(), get(), get()) }
            viewModel<SafeModeTopSheetViewModel> { SafeModeTopSheetViewModelImpl(get(), get()) }
            viewModel<SetupLandingViewModel> { SetupLandingViewModelImpl(get()) }
            viewModel<SetupRootCheckViewModel> { SetupRootCheckViewModelImpl(get()) }
            viewModel<SetupAccessibilityViewModel> { SetupAccessibilityViewModelImpl(get(), get()) }
            viewModel<SetupWalletViewModel> { SetupWalletViewModelImpl(get(), get()) }
            viewModel<SetupControlsViewModel> { SetupControlsViewModelImpl(get(), get()) }
            viewModel<SetupCompleteViewModel> { SetupCompleteViewModelImpl(get(), get())}
            viewModel<SettingsMainViewModel> { SettingsMainViewModelImpl(get(), get(), get()) }
            viewModel<SettingsPowerOptionsViewModel> { SettingsPowerOptionsViewModelImpl(get(), get()) }
            viewModel<SettingsPowerOptionsRearrangeViewModel> { SettingsPowerOptionsRearrangeViewModelImpl(get(), get()) }
            viewModel<SettingsQuickAccessWalletViewModel> { SettingsQuickAccessWalletViewModelImpl(get(), get(), get(), get()) }
            viewModel<SettingsDeviceControlsViewModel> { SettingsDeviceControlsViewModelImpl(get(), get()) }
            viewModel<SettingsQuickAccessWalletRearrangeViewModel> { SettingsQuickAccessWalletRearrangeViewModelImpl(get(), get(), get()) }
            viewModel<SettingsDeveloperOptionsViewModel> { SettingsDeveloperOptionsViewModelImpl(get()) }
            viewModel<SettingsRootCheckViewModel> { SettingsRootCheckViewModelImpl(get()) }
            viewModel<UpdateAvailableBottomSheetViewModel> { UpdateAvailableBottomSheetViewModelImpl(get()) }
            viewModel<UpdateDownloadBottomSheetViewModel> { UpdateDownloadBottomSheetViewModelImpl(get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER));
        HiddenApiBypass.addHiddenApiExemptions("L")
        startKoin {
            androidContext(this@ClassicPowerMenu)
            modules(singletonsModule, serviceModule, walletModule, monetModule, viewModelModule)
        }
        setupMonet()
    }

    /**
     *  Controls is quite heavy and needs to be created new when the power menu opens the or the services break.
     */
    private var controlsModule: Module? = null
    fun loadOrReloadControlsModule(themedContext: Context){
        controlsModule?.let {
            unloadKoinModules(it)
        }
        loadKoinModules(createControlsModule(themedContext).also {
            controlsModule = it
        })
    }

    private fun createControlsModule(context: Context) = module {
        val foregroundExecutor = ExecutorImpl(Looper.getMainLooper())
        val backgroundLooper = createBackgroundLooper()
        val backgroundExecutor = ExecutorImpl(backgroundLooper)
        val sharedPreferences = getSharedPreferences("controls", Context.MODE_PRIVATE)
        single<ControlsActivityStarter> { ControlsActivityStarterImpl(get(), lifecycleScope, get()) }
        single<ControlsController> { ControlsControllerImpl(context, foregroundExecutor, get(), get(), get(), get(), Optional.ofNullable(null), get()) }
        single<ControlsBindingController> { ControlsBindingControllerImpl(context, backgroundExecutor, getAsLazy(), get()) }
        single<ControlsListingController> { ControlsListingControllerImpl(context, backgroundExecutor) }
        single { BroadcastDispatcher(context, backgroundLooper, backgroundExecutor, get(), get()) }
        single { DumpManager() }
        single<ControlActionCoordinator> { ControlActionCoordinatorImpl(context, backgroundExecutor, foregroundExecutor, get(), get(), get(), get()) }
        single { ActivityStarter() }
        single<ShadeController> { ShadeControllerImpl() }
        single { CustomIconCache() }
        single { BroadcastDispatcherLogger() }
        single<KeyguardStateController> { KeyguardStateControllerImpl(get()) { get<Settings>().deviceControlsAllowWhileLocked }}
        single { GlobalActionsComponent(get()) }
        single<ControlsUiController> { ControlsUiControllerImpl(getAsLazy(), context, foregroundExecutor, backgroundExecutor, getAsLazy(), sharedPreferences, get(), get(), get(), get()) }
    }

    private inline fun <reified T> Scope.getAsLazy(): Lazy<T> {
        return lazy { get() }
    }

    private fun createBackgroundLooper(): Looper {
        return HandlerThread("controls-background").apply {
            start()
        }.looper
    }

    private fun setupMonet(){
        val settings = get<Settings>()
        MonetCompat.wallpaperColorPicker = {
            val selectedColor = settings.monetColor
            if(selectedColor != Integer.MAX_VALUE && it?.contains(selectedColor) == true) selectedColor
            else it?.firstOrNull()
        }
    }

}