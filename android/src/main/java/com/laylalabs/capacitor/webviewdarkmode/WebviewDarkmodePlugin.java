package com.laylalabs.capacitor.webviewdarkmode;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "LaylaWebviewDarkmode")
public class WebviewDarkmodePlugin extends Plugin {

    private static final String PREFS_NAME = "layla_webview_darkmode_prefs";
    private static final String KEY_MODE = "mode";

    private String currentMode = "auto";
    private boolean syncHtmlClass = true;
    private String htmlDarkClassName = "dark";

    @Override
    public void load() {
        super.load();

        // Recupera valores padrão da configuração do capacitor.config
        String configuredMode = getConfig().getString("mode", "auto");
        syncHtmlClass = getConfig().getBoolean("syncHtmlClass", true);
        htmlDarkClassName = getConfig().getString("htmlDarkClassName", "dark");

        // Preferência persistida tem prioridade sobre a configuração inicial
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentMode = prefs.getString(KEY_MODE, configuredMode);

        // Aplica o tema na inicialização
        boolean isDark = resolveIsDark(currentMode);
        applyTheme(isDark, false);
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        if ("auto".equals(currentMode)) {
            boolean isDark = resolveIsDark(currentMode);
            applyTheme(isDark, false);
        }
    }

    @Override
    protected void handleOnConfigurationChanged(Configuration newConfig) {
        super.handleOnConfigurationChanged(newConfig);
        if ("auto".equals(currentMode)) {
            int nightModeFlags = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDark = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
            applyTheme(isDark, true);
        }
    }

    @PluginMethod
    public void getDarkMode(PluginCall call) {
        boolean isDark = resolveIsDark(currentMode);
        JSObject ret = new JSObject();
        ret.put("isDark", isDark);
        ret.put("mode", currentMode);
        call.resolve(ret);
    }

    @PluginMethod
    public void setDarkMode(PluginCall call) {
        String mode = call.getString("mode");
        if (mode == null || (!"auto".equals(mode) && !"dark".equals(mode) && !"light".equals(mode))) {
            call.reject("Invalid mode. Expected 'auto', 'dark', or 'light'.");
            return;
        }

        currentMode = mode;
        saveModePreference(mode);

        boolean isDark = resolveIsDark(currentMode);
        applyTheme(isDark, true);
        call.resolve();
    }

    @PluginMethod
    public void sync(PluginCall call) {
        boolean isDark = resolveIsDark(currentMode);
        applyTheme(isDark, false);

        JSObject ret = new JSObject();
        ret.put("isDark", isDark);
        ret.put("mode", currentMode);
        call.resolve(ret);
    }

    private boolean resolveIsDark(String mode) {
        if ("dark".equals(mode)) {
            return true;
        }
        if ("light".equals(mode)) {
            return false;
        }
        // "auto" -> consulta o tema atual do sistema
        try {
            int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            return (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void saveModePreference(String mode) {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_MODE, mode).apply();
        } catch (Throwable ignored) {
        }
    }

    private void applyTheme(final boolean isDark, final boolean notify) {
        if (getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(() -> {
            try {
                if (getBridge() == null || getBridge().getWebView() == null) {
                    return;
                }

                WebView webView = getBridge().getWebView();
                WebSettings webSettings = webView.getSettings();

                // Algorithmic Darkening ou Force Dark conforme suporte da API/WebView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    webSettings.setAlgorithmicDarkeningAllowed(isDark);
                } else if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(webSettings, isDark);
                } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(
                        webSettings,
                        isDark ? WebSettingsCompat.FORCE_DARK_ON : WebSettingsCompat.FORCE_DARK_OFF
                    );
                }

                // Sincroniza a classe CSS no elemento raiz do documento HTML
                if (syncHtmlClass) {
                    String js = String.format(
                        "if (document.documentElement) { document.documentElement.classList.toggle('%s', %b); }",
                        htmlDarkClassName,
                        isDark
                    );
                    webView.evaluateJavascript(js, null);
                }

                if (notify) {
                    JSObject ret = new JSObject();
                    ret.put("isDark", isDark);
                    ret.put("mode", currentMode);
                    notifyListeners("darkModeChanged", ret);
                }
            } catch (Throwable ignored) {
            }
        });
    }
}
