import { WebPlugin } from '@capacitor/core';

import type {
  DarkModePreference,
  DarkModeStatus,
  SetDarkModeOptions,
  WebviewDarkmodePlugin,
} from './definitions';

export class LaylaWebviewDarkmodeWeb extends WebPlugin implements WebviewDarkmodePlugin {
  private currentMode: DarkModePreference = 'auto';
  private syncHtmlClass = true;
  private htmlDarkClassName = 'dark';
  private mediaQueryList: MediaQueryList | null = null;

  constructor() {
    super();
    if (typeof window !== 'undefined' && window.matchMedia) {
      this.mediaQueryList = window.matchMedia('(prefers-color-scheme: dark)');
      this.mediaQueryList.addEventListener('change', (e) => {
        if (this.currentMode === 'auto') {
          this.applyTheme(e.matches);
        }
      });
    }
  }

  async getDarkMode(): Promise<DarkModeStatus> {
    const isDark = this.resolveIsDark();
    return {
      isDark,
      mode: this.currentMode,
    };
  }

  async setDarkMode(options: SetDarkModeOptions): Promise<void> {
    this.currentMode = options.mode;
    const isDark = this.resolveIsDark();
    this.applyTheme(isDark);
  }

  async sync(): Promise<DarkModeStatus> {
    const isDark = this.resolveIsDark();
    this.applyTheme(isDark);
    return {
      isDark,
      mode: this.currentMode,
    };
  }

  private resolveIsDark(): boolean {
    if (this.currentMode === 'dark') return true;
    if (this.currentMode === 'light') return false;
    return this.mediaQueryList ? this.mediaQueryList.matches : false;
  }

  private applyTheme(isDark: boolean): void {
    if (this.syncHtmlClass && typeof document !== 'undefined' && document.documentElement) {
      document.documentElement.classList.toggle(this.htmlDarkClassName, isDark);
    }
    this.notifyListeners('darkModeChanged', {
      isDark,
      mode: this.currentMode,
    });
  }
}
