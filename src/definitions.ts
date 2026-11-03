import type { PluginListenerHandle } from '@capacitor/core';

export type DarkModePreference = 'auto' | 'dark' | 'light';

export interface DarkModeStatus {
  /**
   * Indicates whether dark mode is currently active in the WebView.
   */
  isDark: boolean;

  /**
   * The current dark mode preference ('auto', 'dark', or 'light').
   */
  mode: DarkModePreference;
}

export interface SetDarkModeOptions {
  /**
   * The desired dark mode preference:
   * - 'auto': follows the system theme automatically.
   * - 'dark': forces dark mode on.
   * - 'light': forces light mode on.
   */
  mode: DarkModePreference;
}

export interface WebviewDarkmodeConfig {
  /**
   * Initial mode preference.
   * @default "auto"
   */
  mode?: DarkModePreference;

  /**
   * Automatically toggles the CSS dark class on document.documentElement.
   * @default true
   */
  syncHtmlClass?: boolean;

  /**
   * Name of the CSS class applied to document.documentElement when dark mode is active.
   * @default "dark"
   */
  htmlDarkClassName?: string;
}

export interface WebviewDarkmodePlugin {
  /**
   * Returns the current dark mode status and preference.
   */
  getDarkMode(): Promise<DarkModeStatus>;

  /**
   * Sets the dark mode preference ('auto', 'dark', or 'light').
   */
  setDarkMode(options: SetDarkModeOptions): Promise<void>;

  /**
   * Manually triggers a theme synchronization between the native OS, WebView, and HTML class.
   */
  sync(): Promise<DarkModeStatus>;

  /**
   * Listens for changes in the active dark mode state (e.g. system theme switches).
   */
  addListener(
    eventName: 'darkModeChanged',
    listenerFunc: (status: DarkModeStatus) => void
  ): Promise<PluginListenerHandle>;

  /**
   * Removes all registered event listeners.
   */
  removeAllListeners(): Promise<void>;
}
