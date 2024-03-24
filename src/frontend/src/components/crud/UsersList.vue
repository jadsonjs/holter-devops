<template>
    <div>
      <h1 class="mt-4">Users</h1>
  
      <article class="descricao-ajuda">
        <p> Users can be registered to log in to the tool (if enabled) or to receive alerts via email. </p>

      </article>
  
      <b-container class="bv-example-row">
  
        <b-link  title="Create a New User" to="/users/form">
              <i class="fas fa-plus-circle"></i>  
              <button type="button" class="btn btn-link">New User</button>
        </b-link>
  
         <table class="table table-striped table-hover">
            <caption style="caption-side: top;"> List of User </caption>
            <thead>
                <tr>
                    <th>Email</th>
                    <th>Permissions</th>
                    <th>Receive Alerts?</th>
                    <th colspan="2">Actions</th>
                </tr>
            </thead>
            <tbody v-if="usersList.length > 0">
               <tr v-for="(user) in usersList" :key="user.id">
                  
                <td> {{user.email}}</td>
                  
                  <td>
                    <ul>
                        <li v-for="(per) in user.permission" :key="per.id"> {{ per.role.name }}</li>
                    </ul>
                  </td>

                  <td> <strong> {{user.alert ? 'YES' : 'NO' }} </strong> </td>

                  <td>
                      <b-link title="Edit" v-on:click="edit(user)" class="icon">
                          <i class="fas fa-edit"></i>
                      </b-link>
                  </td>
                  <td>
                    <b-button variant="danger" v-on:click="remove(user)" class="icon" v-if="user.id > 0" title="Delete the User">
                        <i class="fas fa-trash"></i> Delete User
                    </b-button>
                  </td>
               </tr>                 
            </tbody>
         </table>                  
  
         <b-row>
              <b-col>
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
  
    name: 'UsersList',
  
    components: {
          Loading
      },
  
    computed:{
      appName(){ return process.env.VUE_APP_NAME },
    }, 
  
    data() {
        return {
            
            // keep the list of users returned from back-end
            usersList : [],
  
            loading : false,
        }
    },
  
    methods: {
  
         loadUsers(){
        
            this.loading = true
            
            this.$http.get('/user')
            .then(result => { 
                this.usersList = result.data;
              }  
            )
            .catch(
                error => {
                  this.$toastw.error(error.data.messageList)
                }
            ).finally(()=>{
                this.loading = false
            })
        },
  
        edit(user){
          this.$router.push("/users/form/"+user.id)
        },
  
        remove(user){
          if(this.hasRole('ADMIN')){
            if(confirm('Confirm remove the user: '+user.email+' ? ')){
              this.$http.delete('/user/'+user.id)
                .then( (result) => {
                    this.$toastw.success(result.data.messageList)
                    this.loadUsers()
                  }  
                )
                .catch( error => {   this.$toastw.error(error.data.messageList)  })
            }
          }else{
            this.$toastw.error('User has no permission: ADMIN')
          }
        },
  
        cancel(){
          this.$router.push("/")
        },
  
    },
  
    beforeMount(){
       this.loadUsers()
    }
    
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  </style>