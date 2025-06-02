<template>
	<Button
		:icon="isAlternateStyle ? icon + ' text-4xl' : icon"
		:severity="severity"
		:text="true"
		:raised="raised"
		:rounded="true"
		:style="buttonStyle"
		@mouseenter="handleHover"
		@mouseleave="handleHover"
		@click="handleClick"
		@keyup.tab="handleTab"
		@keydown.tab="handleTab"
	/>
	
	<Popover ref="op">
		<div class="flex flex-col gap-4 max-w-[25rem] w-auto">
			<div class="card bg-surface-100 dark:bg-surface-900 py-2 px-4 help-title">
				<span class="font-bold text-xl block break-words max-w-full">
					Help<span v-if="titulo"> - {{ titulo }}</span>
				</span>
			</div>
			<Panel>
				<div>
					<span class="font-medium block mt-[-0.7rem] gap-4">{{ texto }}</span>
					<span v-if="rotulo">
						<br>
						<i>{{ rotulo }}</i>
					</span>
				</div>
			</Panel>
		</div>
	</Popover>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
	texto: {
		type: String,
		required: true,
	},
	titulo: {
		type: String,
	},
	rotulo: {
		type: String,
	},
	isAlternateStyle: {
		type: Boolean,
		default: false,
	},
	alternateBackgroundColor: {
		type: String,
	},
	icon: {
		type: String,
		default: 'fa-solid fa-circle-info',
	},
	severity: {
		type: String,
		default: 'info',
	},
	raised: {
		type: Boolean,
		default: false,
	},
	enableHover: {
		type: Boolean,
		default: true,
	},
});

const op = ref();
const isPinned = ref(false);

/**
 * Default behavior: toggles the Popover upon clicking the button
 * Behavior when hover is enabled: pins or unpins the Popover to the page
 * @param event the click event
 */
const handleClick = (event) => {
	// If hover is enabled, then the Popover appears upon hovering the button
	// This pins or unpins the Popover upon clicking the button with the mouse only
	if (props.enableHover && event.detail >= 1) {
		isPinned.value = !isPinned.value;
	}
	// Default behavior 
	else {
		op.value.toggle(event);
	}
};

/**
 * Shows or hides the popover when the mouse hovers (enter/exit) the button
 * @param event the hover event
 */
const handleHover = (event) => {
	if (props.enableHover) {
		// If the popover was dismissed by clicking outside then it's no longer pinned
		if (isPinned.value && !op.value.visible) {
			isPinned.value = false;
		}
		// Default behavior on hover
		if (!isPinned.value) {
			op.value.toggle(event);
		}
	}
};

/**
 * When tab navigation is used, shows or hides the popover (tab enter/exit)
 * @param event the tab event
 */
const handleTab = (event) => {
	if (event.type === 'keyup' && props.enableHover) {
		op.value.show(event);
	}
	else if (event.type === 'keydown') {
		op.value.hide(event)
	}
};

const buttonStyle = computed(() => {
	if (props.isAlternateStyle && props.alternateBackgroundColor) {
		return {
			'background': props.alternateBackgroundColor,
			'color': 'var(--surface-ground)',
		};
	}
	return {};
});
</script>

<style lang="scss" scoped>
.p-button-text.p-button-info {
	background: transparent;
	border-color: transparent;
	color: var(--p-button-text-primary-color);
}

.help-title {
	margin-bottom: 0rem;
}
</style>
