import Foundation
import Capacitor
import WebKit

@objc(LaylaWebviewDarkmode)
public class WebviewDarkmodePlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "LaylaWebviewDarkmode"
    public let jsName = "LaylaWebviewDarkmode"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getDarkMode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "setDarkMode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sync", returnType: CAPPluginReturnPromise)
    ]

    private var currentMode: String = "auto"
    private var syncHtmlClass: Bool = true
    private var htmlDarkClassName: String = "dark"

    override public func load() {
        super.load()

        currentMode = getConfig().getString("mode", "auto")
        syncHtmlClass = getConfig().getBoolean("syncHtmlClass", true)
        htmlDarkClassName = getConfig().getString("htmlDarkClassName", "dark")

        let isDark = resolveIsDark(mode: currentMode)
        applyTheme(isDark: isDark, notify: false)
    }

    @objc func getDarkMode(_ call: CAPPluginCall) {
        let isDark = resolveIsDark(mode: currentMode)
        call.resolve([
            "isDark": isDark,
            "mode": currentMode
        ])
    }

    @objc func setDarkMode(_ call: CAPPluginCall) {
        guard let mode = call.getString("mode"), ["auto", "dark", "light"].contains(mode) else {
            call.reject("Invalid mode. Expected 'auto', 'dark', or 'light'.")
            return
        }

        currentMode = mode
        let isDark = resolveIsDark(mode: currentMode)
        applyTheme(isDark: isDark, notify: true)
        call.resolve()
    }

    @objc func sync(_ call: CAPPluginCall) {
        let isDark = resolveIsDark(mode: currentMode)
        applyTheme(isDark: isDark, notify: false)
        call.resolve([
            "isDark": isDark,
            "mode": currentMode
        ])
    }

    private func resolveIsDark(mode: String) -> Bool {
        if mode == "dark" {
            return true
        }
        if mode == "light" {
            return false
        }
        // "auto" -> query trait collection
        DispatchQueue.main.sync {
            if let window = self.bridge?.viewController?.view.window {
                return window.traitCollection.userInterfaceStyle == .dark
            }
            return UIScreen.main.traitCollection.userInterfaceStyle == .dark
        }
    }

    private func applyTheme(isDark: Bool, notify: Bool) {
        DispatchQueue.main.async { [weak self] in
            guard let self = self else { return }

            if let webView = self.bridge?.webView {
                if #available(iOS 13.0, *) {
                    if self.currentMode == "dark" {
                        webView.overrideUserInterfaceStyle = .dark
                    } else if self.currentMode == "light" {
                        webView.overrideUserInterfaceStyle = .light
                    } else {
                        webView.overrideUserInterfaceStyle = .unspecified
                    }
                }

                if self.syncHtmlClass {
                    let js = "if (document.documentElement) { document.documentElement.classList.toggle('\(self.htmlDarkClassName)', \(isDark)); }"
                    webView.evaluateJavaScript(js, completionHandler: nil)
                }
            }

            if notify {
                self.notifyListeners("darkModeChanged", data: [
                    "isDark": isDark,
                    "mode": self.currentMode
                ])
            }
        }
    }
}

