<template>
	<div class="card">
		<div class="font-semibold text-xl mb-4">User</div>

		<p class="text-ajuda">
			Create a new User or edit an existing one.
		</p>

	</div>

	<div class="card min-h-screen">
		<Fluid>
			<Panel>

				<loading :loading="loading" />

				<div class="md:w-1/2" v-if="!loading">
					<div class="font-semibold text-xl">User Details</div>
				</div>

				<VForm class="flex flex-col md:flex-row gap-8" v-slot="{ validate }">

					<div class="card flex flex-col gap-4">

						<!-- Email Field -->
						<div class="flex flex-col gap-2">
							<label for="email" class="w-full">Email</label>
							<VField name="email" v-model="user.email" rules="required|email" v-slot="{ field, errors }">
								<InputText v-bind="field" type="email" placeholder="Enter the user email"
									class="w-full md:w-[30rem] mb-4" fluid />
								<Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">
									{{ errors[0] }}
								</Message>
							</VField>
						</div>

						<!-- Password Field -->
						<div class="flex flex-col gap-2">
							<label for="password" class="w-full">Password</label>
							<VField name="password" v-model="user.password" rules="required" v-slot="{ field, errors }">
								<InputText v-bind="field" type="password" placeholder="Enter the user password"
									class="w-full md:w-[30rem] mb-4" fluid />
								<Message severity="error" icon="fa-solid fa-circle-exclamation" v-if="errors.length > 0">
									{{ errors[0] }}
								</Message>
							</VField>
						</div>

						<!-- Receive Alerts Toggle -->
						<div class="flex flex-col gap-2">
							<div class="flex items-center gap-2">
								<label for="alert">Receive Alerts?</label>
								<Ajuda texto="Enable or disable if the user receives alerts by email." />
							</div>
							<ToggleSwitch id="alert" name="alert" v-model="user.alert" />
						</div>

						<!-- Role Selection Dropdown -->
						<div class="flex flex-col gap-2 mt-2">
							<label for="role" class="w-full">Select a Role</label>
							<Select id="role" class="form-control" v-model="selectedRole" :options="roles" optionLabel="name"
								placeholder="Set a user role" @change="addPermission" />
						</div>

						<!-- Permissions Table -->
						<div class="flex flex-col" v-if="user.permission && user.permission.length > 0">
							<DataTable :value="user.permission" class="p-datatable-striped p-datatable-hover">
								<!-- Permissions Column -->
								<Column header="Permissions" field="role.name"></Column>

								<!-- Action Column -->
								<Column header="Action">
									<template #body="slotProps">
										<Button icon="fa-solid fa-minus-circle" class="p-button-text"
											@click="removePermission(slotProps.data)" />
									</template>
								</Column>
							</DataTable>
						</div>

						<!-- Action Buttons -->
						<div class="flex justify-end space-x-4">
							<Button label="Cancel" icon="fa-solid fa-xmark" text @click="cancel" :style="{ width: '10em' }" />
							<Button label="Save" icon="fa-solid fa-floppy-disk" @click="save(validate)" :disabled="loading"
								:style="{ width: '15em' }" />
						</div>

					</div>
				</VForm>

			</Panel>
		</Fluid>
	</div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue'
import { useMessage } from '@/utils/message'

// Import components (adjust paths as needed)
import Loading from '@/components/base/Loading.vue';

import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

// axios
import { useHttp } from '@/utils/axiosUtils';
const { http } = useHttp();

const { message } = useMessage()

// User data: note that "permission" is an array that will hold permission objects.
const user = ref({
	email: '',
	password: '',
	alert: false,
	permission: []
})

// Roles and selected role
const roles = ref([])       // List of available roles
const selectedRole = ref(null)

// Loading state
const loading = ref(false)

// Load roles from back-end (GET /user/roles/all)
const loadRoles = async () => {
	loading.value = true
	try {
		const response = await http.get('/api/user/roles/all')
		roles.value = response.data
	} catch (error) {
		message.error('Error', error.data.messageList)
	} finally {
		loading.value = false
	}
}

// Add a new permission based on the selected role
const addPermission = () => {
	if (selectedRole.value && selectedRole.value !== '') {
		if (!containsPermission(selectedRole.value)) {
			// Push a new permission object (adjust structure as needed)
			user.value.permission.push({ id: null, role: selectedRole.value, user: { id: user.value.id } })
		} else {
			message.error('Error', 'User already has this permission')
		}
	}
}

// Remove a permission from the user
const removePermission = (per) => {
	const index = user.value.permission.findIndex(p => p.role.name === per.role.name)
	if (index !== -1) {
		user.value.permission.splice(index, 1)
	}
}

// Check if the user already has a permission for the given role
const containsPermission = (role) => {
	return user.value.permission.some(p => p.role.name === role.name)
}

// Save the user via POST /user/save
const save = async (validate) => {
	const validator = await validate()

	if (validator.valid) {
		loading.value = true
		try {
			await http.post('/api/user/save', user.value)
			message.success('Success', 'User saved with success!')
			router.back()
		} catch (error) {
			message.error('Error', error.data.messageList)
		} finally {
			loading.value = false
		}
	}
	else {
			message.error('Error', 'There are invalid fields in the form')
		}
	}

	// Cancel the operation and navigate back to the user list
	const cancel = () => {
		router.back()
	}


	// On component mount, load roles and, if editing, load the user data
	onBeforeMount(async () => {
		await loadRoles()
		if (route.params.id) {
			loading.value = true
			try {
				const response = await http.get(`/api/user/${route.params.id}`)
				user.value = response.data
			} catch (error) {
				message.error('Error', error.data.messageList)
			} finally {
				loading.value = false
			}
		}
	})
</script>

<style scoped>
/* Add any component-specific styles here */
</style>
