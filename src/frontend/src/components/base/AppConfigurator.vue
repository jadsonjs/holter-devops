<!--
  --  Universidade Federal do Rio Grande do Norte
  --  Instituto Metrópole Digital
  --  Diretoria de Tecnologia da Informação - DTI
  --
  --  Componente para as configurações do sistema
  --
  --  @author Jadson Santos - jadson.santos@ufrn.br
  --  @since 02/08/2024
-->

<template>
	<!-- System configuration side bar -->
 <Drawer v-model:visible="isVisible" header="Configurações" position="right" @hide="hideConfigPanel()">
	<div class="flex flex-col gap-4">
			<div>
					<span class="text-sm text-muted-color font-semibold">Cor Primária</span>
					<div class="pt-2 flex gap-2 flex-wrap justify-between">
							<button
									v-for="primaryColor of primaryColors"
									:key="primaryColor.name"
									type="button"
									:title="primaryColor.name"
									@click="updateColors('primary', primaryColor)"
									:class="['border-none w-5 h-5 rounded-full p-0 cursor-pointer outline-none outline-offset-1', { 'outline-primary': layoutConfig.primary === primaryColor.name }]"
									:style="{ backgroundColor: `${primaryColor.name === 'noir' ? 'var(--text-color)' : primaryColor.palette['500']}` }"
							></button>
					</div>
			</div>
			
			<div>
					<span class="text-sm text-muted-color font-semibold">Cor de Background</span>
					<div class="pt-2 flex gap-2 flex-wrap justify-between">
							<button
									v-for="surface of surfaces"
									:key="surface.name"
									type="button"
									:title="surface.name"
									@click="updateColors('surface', surface)"
									:class="[
											'border-none w-5 h-5 rounded-full p-0 cursor-pointer outline-none outline-offset-1',
											{ 'outline-primary': layoutConfig.surface ? layoutConfig.surface === surface.name : isDarkTheme ? surface.name === 'zinc' : surface.name === 'slate' }
									]"
									:style="{ backgroundColor: `${surface.palette['500']}` }"
							></button>
					</div>
			</div>
			<!--
			<div class="flex flex-col gap-2">
					<span class="text-sm text-muted-color font-semibold">Temas</span>
					<SelectButton v-model="preset" @change="onPresetChange" :options="presetOptions" :allowEmpty="false" />
			</div>
			-->
			<div class="flex flex-col gap-2 mt-8">
					<span class="text-sm text-muted-color font-semibold">Modo do Menu</span>
					<SelectButton v-model="menuMode" @change="onMenuModeChange" :options="menuModeOptions" :allowEmpty="false" optionLabel="label" optionValue="value" />
			</div>
	</div>
 </Drawer>
</template>


<script setup>
import { useLayout } from '@/layout/composables/layout';
import { $t, updatePreset, updateSurfacePalette } from '@primevue/themes';
import Aura from '@primevue/themes/aura';
import Lara from '@primevue/themes/lara';
import { ref, toRefs, watch } from 'vue';


import { primaryColors, surfaces, getPresetExt, updateColors, applyTheme, onPresetChange } from '@/layout/composables/useColors';


const { layoutConfig, setPrimary, setSurface, setPreset, isDarkTheme, setMenuMode } = useLayout();

const presets = { Aura, Lara};

const preset = ref(layoutConfig.preset);
const presetOptions = ref(Object.keys(presets));

const menuMode = ref(layoutConfig.menuMode);
const menuModeOptions = ref([
    { label: 'Fixo', value: 'static' },
    { label: 'Escondido', value: 'overlay' }
]);





// muda o modo do menu lateral
function onMenuModeChange() {
    setMenuMode(menuMode.value);
}

/*
 * Controls the configuration painel
 */

// Define the prop for v-model binding
const props = defineProps(['visible'])
// defineEmits is used to define the update:visible event that will be emitted to update the prop.
const emit = defineEmits(['update:visible']);

const { visible } = toRefs(props)
const isVisible = ref(false)

watch(visible, () => {
  isVisible.value = visible.value
})

//  method to hide the config panel
 // emit the update:visible event with the new value.
const hideConfigPanel = () => {
  emit('update:visible', false);
};


</script>

