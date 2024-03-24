<template>
    <div>
      <h1 class="mt-4">User</h1>
  
      <article class="descricao-ajuda">
        <p> Create a new User</p>
      </article>
  
      <b-container class="bv-example-row">
  
        <b-row>
           <b-form v-if="! loading">
              <b-form-group id="input-group-1" label="Email:" label-for="input-1" description="Write the user email">
                 <b-form-input id="input-1" v-model="user.email" type="email" placeholder="Enter the user email" required></b-form-input>
              </b-form-group>
              <b-form-group id="input-group-2" label="Password:" label-for="input-2" description="Write the user password">
                 <b-form-input id="input-2" v-model="user.password" type="password" placeholder="Enter the user password" required></b-form-input>
              </b-form-group>
              <b-form-group id="input-group-3" label="Receive Alerts?" label-for="input-3" description="Enable or disable the user receive alerts by email">
                 <b-form-checkbox switch size="lg" id="input-3" v-model="user.alert"></b-form-checkbox>
              </b-form-group>

              <!--
                Combo box of roles to the user
                --> 
              <b-row>
                <b-col sm="12" md="12" lg="10">
                  <b-form-group id="input-group-4" label="Select a Role" label-for="input-4" description="Set a User Role">
                  <select class="form-control" v-model="selectedRole" v-on:change="addPermission">
                    <option v-for="role in roles" :key="role.name" :value="role">
                      {{ role.name }}  
                    </option>
                  </select>
                  </b-form-group>
                </b-col>
              </b-row>

               <!--
                Visualization of the user permission
                --> 
              <b-row>
                  <b-col sm="12" md="12" lg="12">
                      
                    <table class="table table-striped table-hover" v-if="user.permission && user.permission.length > 0">
                        <thead>
                            <tr>
                                <th>Permissions</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                          
                          <tr v-for="(per) in user.permission" v-bind:key="per.role.name">
                                <td> {{ per.role.name }} </td>
                                <td>
                                    <button class="btn btn-default" type="button" v-on:click="removePermission(per)">
                                        <i class="fas fa-minus-circle"></i>
                                    </button>
                                </td>
                          </tr>
                            
                        </tbody>
                    </table>
                              
                  </b-col>
              </b-row>
              
           </b-form>
        </b-row>  
  
        <b-row>
              <b-col>
                 <b-button variant="primary" v-on:click="save()"> Save </b-button>
                 <b-button variant="outline-dark" v-on:click="cancel()"> Cancel </b-button>
              </b-col>
        </b-row>
  
        <div class="w-100" style="height: 50px;"></div>
  
      </b-container>
  
      <loading :show="loading"></loading>
  
      
    </div>
  </template>
  
  <script>
  
  import Loading from '@/components/common/Loading.vue'
  
  
  export default {
  
    name: 'UserForm',
  
    components: {
          Loading
      },
  
    computed:{
      appName(){ return process.env.VUE_APP_NAME },
    }, 
  
    data() {
        return {
            
            user : {
              permission : [],
            },

            roles : [],       // list  of the role of the tool

            selectedRole: {}, // role selecto to add to the user
  
            loading : false,
        }
    },
  
    methods: {
  
        /**
         * Call the back-end to save a new user
         * 
         */
        save(){
        
            this.loading = true
            
            this.$http.post('/user/save', this.user)
            .then( () => { 
                this.$toastw.success('User saved with success!')
                this.cancel()
              }  
            ).catch(
                error => {
                  this.$toastw.error(error.data.messageList)
                }
            ).finally(()=>{
                this.loading = false
            })
          
        },

        loadRoles(){
            this.loading = true
            this.$http.get('/user/roles/all')
                .then(result => { this.roles = result.data;}  )
                    .catch(  error => {this.$toastw.error(error.data.messageList)})
                        .finally( () => {this.loading = false})
        },


        /** Add a new permission to the user*/
        addPermission(){
            if (this.selectedRole != undefined && this.selectedRole !== '') { 
                if( ! this.containsPermission(this.selectedRole) ){
                    this.user.permission.push( { id: null, role: this.selectedRole, user: { id: this.user.id } } )
                }else{
                    this.$toastw.error('User already have this permission')
                }
            }
        },

        /** remove permission of the select user */
        removePermission(per){
            const index = this.user.permission.findIndex(obj => obj.role.name === per.role.name);
            if (index !== -1) {
                this.user.permission.splice(index, 1);
            }
        },

        containsPermission(role){
            for(var i=0; i < this.user.permission.length; i++){
                if( this.user.permission[i].role.name === role.name){
                    return true
                }
            }  
            return false
        },
  
        cancel(){
          this.$router.push("/users/list")
        },
  
    },
  
    beforeMount(){

        this.loadRoles()
      
        // if send a "id" in the router is because is editing the user
        if( this.$route.params.id ){
    
            this.$http.get('/user/'+this.$route.params.id)
                .then(result => { 
                    this.user = result.data;
                }  
                )
                .catch(
                    error => {
                    this.$toastw.error(error.data.messageList)
                    }
                )
        }else{
            // creatig a new one
            this.user = { permission : [] }
        }
       
    }
    
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  </style>