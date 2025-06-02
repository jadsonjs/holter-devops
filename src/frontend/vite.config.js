import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

import Components from 'unplugin-vue-components/vite';
import {PrimeVueResolver} from '@primevue/auto-import-resolver';

const env = loadEnv('all', process.cwd());
const publicPath =  env.VITE_APP_PUBLIC_PATH

// https://vitejs.dev/config/
export default defineConfig(() => {
	return {
			// define the public path
			base: publicPath,
			plugins: [vue(),   Components({ resolvers: [ PrimeVueResolver() ] })],

			// it sets up an alias for the @ symbol, making it a shorthand for the ./src 
			resolve: {
					alias: {
							'@': fileURLToPath(new URL('./src', import.meta.url))
					}
			},

			// fix a server port
			server: {
					port: 3000,
			},
	};
});
