<template>
    <div>
      <h1 class="mt-4">Project Configuration Form</h1>
  
      <article class="descricao-ajuda">
        <p> Enter with the project configuration values</p>
      </article>
  
      <b-container>
  
        <h4 v-if="configuration.project"> Project: {{configuration.project.organization +'/'+ configuration.project.name}} </h4>

        <b-row>
           <b-form v-if="! loading" style="width: 100%;">

            <article class="descricao-ajuda">
              <p> Main Repository is usually the repository where the project's source code is located. The place where the pipeline execution occurs and where we extract the build information</p>
            </article>
              
            <b-form-group id="input-group-2" label="Main Repository URL:" label-for="input-2">
                 <b-form-input id="input-2" v-model="configuration.mainRepositoryURL" placeholder="https://gitlab.com" required> </b-form-input>
                 <help title="Api Url" text="API URl used to collect the metrics data" id="c425de9a-s23kjsad" />
            </b-form-group>

            <b-form-group id="input-group-3" label="Main Repository Token:" label-for="input-3">
                 <b-form-input id="input-3" v-model="configuration.mainRepositoryToken" placeholder="xxxxxxxxxxxxxxxxxxxxxxx" required> </b-form-input>
                 <help title="Security Token" text="Security token used by the most of the repository to give access to API" id="c425de9a-23sdfaf3" />
            </b-form-group>

            <b-form-group id="input-group-4" label="Issues Erros Labels:" label-for="input-4">
                 <b-form-input id="input-4" v-model="configuration.issuesErrosLabels" placeholder="error, bug, issue, mistake"> </b-form-input>
                 <help title="Prodution Branch" text="The name of the branch that contains the production code" id="c425de9a" />
            </b-form-group>

            <b-form-group id="input-group-1" label="Prodution Branch:" label-for="input-1">
                 <b-form-input id="input-1" v-model="configuration.produtionBranch" placeholder="master" required> </b-form-input>
                 <help title="Production Branch" text="The name of the branch that contains the production code" id="c425de9a" />
            </b-form-group>
            
            <hr>

            <article class="descricao-ajuda">
              <p> Secondary Repository is a secondary tool used to monitor project metrics, like <a href="https://www.sonarsource.com/products/sonarqube/" target="_blank"> sonarqube </a>, used to monitor test coverage, Code Smells, etc. <br>
              This tool use these two repositories to join all project's metric in one simple visualization <br>
              Some metrics, like coverage, use this second repository to be collected.</p>
            </article>

            <b-form-group id="input-group-5" label="Secondary Repository URL:" label-for="input-5">
                 <b-form-input id="input-5" v-model="configuration.secondaryRepositoryURL" placeholder="https://codecov.io/api/v2/"> </b-form-input>
                 <help title="Api Url" text="API URl of secondary repository (often used to collect coverage data)" id="2323-c425de9a-s23kjsad" />
            </b-form-group>
            
            <b-form-group id="input-group-6" label="Secondary Repository Project Organization:" label-for="input-6">
                 <b-form-input id="input-6" v-model="configuration.secondaryRepositoryOrganization" placeholder="br.com"> </b-form-input>
                 <help title="Security Token" text="Organization for the secondary repository" id="ke2wer23-c425de9a-23sdfaf3" />     
            </b-form-group>

            <b-form-group id="input-group-6" label="Secondary Repository Project Name:" label-for="input-6">
                 <b-form-input id="input-6" v-model="configuration.secondaryRepositoryName" placeholder="my-project"> </b-form-input>
                 <help title="Security Token" text="Name of the project in the secondary repository" id="ke2wer23-c425de9a-94i9sddd" />     
            </b-form-group>

            <b-form-group id="input-group-6" label="Secondary Repository Token:" label-for="input-6">
                 <b-form-input id="input-6" v-model="configuration.secondaryRepositoryToken" placeholder="xxxxxxxxxxxxxxxxxxxxxxx"> </b-form-input>
                 <help title="Security Token" text="Security token used by the most of the repository to give access to API" id="ke2wer23-c425de9a-23sdfaf3" />
            </b-form-group>

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
  import Help from '@/components/common/Help.vue'

  
  
  export default {
  
    name: 'ProjectConfigurationForm',
  
    components: {
        Loading, 
        Help
    },
  
    computed:{
      appName(){ return process.env.VUE_APP_NAME },
    }, 
  
    data() {
        return {
            
            // the object that will be created
            configuration : {},
  
            loading : false,
        }
    },
  
    methods: {
  
        /**
         * Call the back-end to save a new measure reference value
         * 
         */
        save(){
        
            this.loading = true
            
            this.$http.post('/project/configuration/save', this.configuration)
            .then( () => { 
                this.$toastw.success('Configuration of the Project '+this.configuration.project.name+' saved with success!')
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

        /** If we are creating a new  */
        loadProject(){
            this.loading = true          
            this.$http.get('/project/'+this.$route.params.id)
            .then(result => { 
                    this.project = result.data;
                    this.controlMetric.period = this.project
                }  
            ).catch(error => { this.$toastw.error(error.data.messageList) }
            ).finally(()=>{this.loading = false})

        },
  
        cancel(){
          this.$router.push("/project/list")
        },
  
    },
  
    beforeMount(){
      
      // if send a "id" in the router is because is editing the project
      if( this.$route.params.id ){
  
         this.$http.get('/project/configuration/'+this.$route.params.id)
            .then(result => { 
                this.configuration = result.data;
              }  
            )
            .catch(
                error => {
                  this.$toastw.error(error.data.messageList)
                }
            )
      }
       
    }
    
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  </style>