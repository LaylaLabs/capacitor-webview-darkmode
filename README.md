# @laylalabs/capacitor-webview-darkmode

Native WebView dark mode manager for Capacitor with algorithmic darkening, system theme change listener, and instant HTML dark class synchronization.

## Install

```bash
pnpm add @laylalabs/capacitor-webview-darkmode
npx cap sync
```

## Configuration

In your `capacitor.config.ts`:

```typescript
import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    LaylaWebviewDarkmode: {
      mode: 'auto', // 'auto' | 'dark' | 'light'
      syncHtmlClass: true,
      htmlDarkClassName: 'dark',
    },
  },
};

export default config;
```

## API

<docgen-index>

* [`getDarkMode()`](#getdarkmode)
* [`setDarkMode(...)`](#setdarkmode)
* [`sync()`](#sync)
* [`addListener('darkModeChanged', ...)`](#addlistenerdarkmodechanged-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getDarkMode()

```typescript
getDarkMode() => any
```

Returns the current dark mode status and preference.

**Returns:** <code>any</code>

--------------------


### setDarkMode(...)

```typescript
setDarkMode(options: SetDarkModeOptions) => any
```

Sets the dark mode preference ('auto', 'dark', or 'light').

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#setdarkmodeoptions">SetDarkModeOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### sync()

```typescript
sync() => any
```

Manually triggers a theme synchronization between the native OS, WebView, and HTML class.

**Returns:** <code>any</code>

--------------------


### addListener('darkModeChanged', ...)

```typescript
addListener(eventName: 'darkModeChanged', listenerFunc: (status: DarkModeStatus) => void) => any
```

Listens for changes in the active dark mode state (e.g. system theme switches).

| Param              | Type                                                                           |
| ------------------ | ------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'darkModeChanged'</code>                                                 |
| **`listenerFunc`** | <code>(status: <a href="#darkmodestatus">DarkModeStatus</a>) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => any
```

Removes all registered event listeners.

**Returns:** <code>any</code>

--------------------


### Interfaces


#### DarkModeStatus

| Prop         | Type                                                              | Description                                                     |
| ------------ | ----------------------------------------------------------------- | --------------------------------------------------------------- |
| **`isDark`** | <code>boolean</code>                                              | Indicates whether dark mode is currently active in the WebView. |
| **`mode`**   | <code><a href="#darkmodepreference">DarkModePreference</a></code> | The current dark mode preference ('auto', 'dark', or 'light').  |


#### SetDarkModeOptions

| Prop       | Type                                                              | Description                                                                                                                                         |
| ---------- | ----------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`mode`** | <code><a href="#darkmodepreference">DarkModePreference</a></code> | The desired dark mode preference: - 'auto': follows the system theme automatically. - 'dark': forces dark mode on. - 'light': forces light mode on. |


#### PluginListenerHandle

| Prop         | Type                      |
| ------------ | ------------------------- |
| **`remove`** | <code>() =&gt; any</code> |


### Type Aliases


#### DarkModePreference

<code>'auto' | 'dark' | 'light'</code>

</docgen-api>
