import { registerPlugin } from '@capacitor/core';

import type { WebviewDarkmodePlugin } from './definitions';

const LaylaWebviewDarkmode = registerPlugin<WebviewDarkmodePlugin>('LaylaWebviewDarkmode', {
  web: () => import('./web').then((m) => new m.LaylaWebviewDarkmodeWeb()),
});

export * from './definitions';
export { LaylaWebviewDarkmode };
export default LaylaWebviewDarkmode;

