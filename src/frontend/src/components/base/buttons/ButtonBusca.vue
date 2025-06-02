<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação
  --
  --
  --  Pesquisa por operacoes do sistema
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 28/08/2024
-->
<template>

		<!-- busca no sistema -->
		<Button type="button" class="layout-topbar-action" text raised @click="toggleBusca">
			<i class="fa-solid fa-magnifying-glass"></i>
			<span>Buscar</span>
		</Button>

		<!-- Menu pop over the busca no sistema -->
		<Popover ref="opBusca">
			<div class="flex flex-col gap-4 w-[25rem]">
					<div>
							<span class="font-semibold text-l mb-2">Pesquise uma funcionalidade no sistema</span>
					</div>
					<div>
						<InputGroup>
								<InputText placeholder="Digite a nome da funcionaldade ..." v-model="query"/>
								<Button icon="fa-solid fa-magnifying-glass" @click="pesquisarRota()"/>
						</InputGroup>
					</div>
					<div>
						<loading :loading="loading"/>
					</div>
					<div>
						<Listbox v-model="selectedRouter" :options="queryRoutersResult" v-if="queryRoutersResult?.length > 0"
								optionLabel="meta.searchQuery.titulo"  @click="entrarRotaSelecionada()" class="w-full">
								<template #option="slotProps">
									<div class="flex items-center">
										<i class="fa-solid fa-chevron-right mr-4"></i>
										<div>
											{{ slotProps.option.meta.searchQuery.titulo }} 
											<Tag :value="slotProps.option.meta.papeis" icon="fa-solid fa-person-circle-check" severity="secondary" v-if="slotProps.option.meta?.papeis?.length > 0"></Tag> 
										</div>
									</div>
							</template>
						</Listbox>	
					</div>
			</div>
	</Popover>

</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { hasRoles } from '@/utils/permissoes'
import { removeAcentuacao } from '@/utils/funcoes'

	
import Loading from '@/components/base/Loading.vue';

// rotas
import { useRouter } from 'vue-router'
const router = useRouter()


/**
 * A Parte de mostrar o pop over the busca do sistema
 */
const opBusca = ref();

const toggleBusca = (event) => {
	opBusca.value.toggle(event);
}

/*******************************************************/


// Função computada para filtrar rotas com ''searchQuery'' em seu meta
// O(n²) é um pouquino pesada essa funcao, retirar se nao for necessária
const filteredRoutes = computed(() => {
    let result = [];

    // Loop through the main routes
    router.options.routes.forEach(route => {
        // Check if the route has children
        if (route.children) {
            // Loop through the children to find those with searchQuery in meta
            route.children.forEach(child => {

							// // apenas as rotas que tem keywords e que: ou 1) é uma rota sem papeis ou 2) o uusário tem o papel para acessar a rota
							if ( child?.meta && child?.meta?.searchQuery 
									&&  ( ! child?.meta?.papeis || hasRoles(child?.meta?.papeis) ) ) {
									result.push(child);
							}
            });
        }
    });

    return result;
});


const query = ref('') // a string de busca
const queryRoutersResult = ref() // os resultados da busca

const selectedRouter = ref(); // roda selecionada no componente pelo usuario

const loading = ref(false);


// Chama a busca quando o usuario digitar 5 caracteres
watch(query, (newValue) => {
  if (newValue.trim().length >= 5) {
    pesquisarRota();
  }
});


/**
 * Funcação de pesquisa nas rotas do sistema.
 * 
 * Para funciona as rotas precisam definr as informações no campo meta
 * 
 * meta: { titulo: "Caso de uso", keywords: "cadastro", }, },
 * 
 * @param event 
 */
const pesquisarRota = () => {
	try {
		loading.value = true;
		
		if ( query.value.trim().length > 3 ) {

			queryRoutersResult.value = filteredRoutes.value.filter(({ meta }) => {
				let queryTemp = removeAcentuacao(query.value)

				let regex = new RegExp(`\\b${queryTemp}`, 'gi')

				// Caso encontre no titulo
				let matchTitulo = !!removeAcentuacao(meta.searchQuery.titulo).match(regex)

				// Caso encontre nas palavras-chave
				let matchKeywords = !!removeAcentuacao(meta.searchQuery.keywords).match(regex)

				return (matchTitulo || matchKeywords) && meta.titulo !== null
			})
		}
	} finally {
    loading.value = false;
  }
}

/**
 * para para a rota selecioanda
 */
const entrarRotaSelecionada = () => {
	router.push(selectedRouter.value.path)
	selectedRouter.value = []
	queryRoutersResult.value = []
	query.value = ''
	if (opBusca.value) {
		opBusca.value.hide();
	} 
}



</script>

<style lang="scss" scoped>

</style>