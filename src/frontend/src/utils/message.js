/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 * 
 * Message util functions
 * 
 * Use:
 * import { useMessage } from '@/utils/message';
 * const { message } = useMessage();
 * 
 * @click="message.info('Test', 'Test Message')"
 */

// import { useToast } from 'primevue/usetoast';
// workaround to work outside the vue context
// https://github.com/primefaces/primevue/issues/3209
import ToastEventBus from 'primevue/toasteventbus'

export function useMessage() {
    // const toast = useToast();

    const message = {
				success: (titulo, conteudo) => {
					addMessage('success', titulo, conteudo, 5000);
        },
				info: (titulo, conteudo) => {
					addMessage('info', titulo, conteudo, 5000);
				},
				warn: (titulo, conteudo) => {
					addMessage('warn', titulo, conteudo, 5000);
				},
				error: (titulo, conteudo) => {
					addMessage('error', titulo, conteudo, 5000);
				},
    };

		/**
		 * private comum methods that add a messagem to user
		 * @param {*} severity 
		 * @param {*} summary 
		 * @param {*} detail 
		 * @param {*} life 
		 */
		const addMessage = (sev, title, content, timeToLive) => {
			if (Array.isArray(content)) {
				content.forEach((cElement) => {
					ToastEventBus.emit('add', { severity: sev, summary: title, detail: cElement , life: timeToLive})
				});
			} else {
					ToastEventBus.emit('add', { severity: sev, summary: title, detail: content  , life: timeToLive})
			}
		}

    return { message };
}