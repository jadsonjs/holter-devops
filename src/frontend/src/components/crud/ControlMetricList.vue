<template>
    <div>
      <h1 class="mt-4">Project Metrics List</h1>
  
      <article class="descricao-ajuda">
        <p> Porject Metric are basic metric about the project.</p>
        <p> They are used as a control variables</p>
      </article>
  
      <b-container class="bv-example-row">

            
            <table class="table table-striped table-hover">
                <caption style="caption-side: top;"> Select of Projects </caption>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Organization</th>
                        <th>Active</th>
                        <th colspan="3">Actions</th>
                    </tr>
                </thead>
                <tbody v-if="projectsList.length > 0">
                <tr v-for="(project) in projectsList" :key="project.id">
                                    
                    <td> {{project.name}}</td>
                    <td> {{project.organization}}</td>
                    <td> {{project.active}}</td>
                    <td>
                        <b-link title="Select" v-on:click="selectProject(project)" class="icon">
                            <i class="fas fa-arrow-circle-right"></i>
                        </b-link>
                    </td>
                </tr>                 
                </tbody>
            </table> 
            
            <!--
              == Componente de paginação
              -->
              <b-pagination v-model="pagination.page" :per-page="pagination.size" :total-rows="pagination.total" v-if="pagination.total > 0"></b-pagination>


            <table class="table table-striped table-hover texto-alto-contraste" v-if="pagination.total > 0 && selectedProject.id > 0">
                <caption style="caption-side: top;"> Select a Period of Analysis </caption>
                <thead>
                    <tr>
                    <th scope="col">Init</th>
                    <th scope="col">End</th>
                    <th style="width: 5%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="p in pagination.results" :key="p.id">
                    <td>{{ p.init }}</td>
                    <td>{{ p.end }}</td>
                    <!--
                        == IMPORTANTE:
                        == tabindex="0"   e   v-on:keydown.enter="....." 
                        == torna os links navegáveis e selecionaveis via teclado
                        -->
                    <td v-on:keydown.enter="selectPeriod(p)" tabindex="0">
                        <a title="Select a Period" v-on:click="selectPeriod(p)"  class="link" >
                            <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </td>
                    </tr>
                </tbody>
            </table>

            <div class="p-3 mb-2 bg-danger text-white" v-if="pagination.total === 0 && selectedProject.id > 0"> There are no analysis periods for the project. Please, first execute one analysis. </div>


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

        name: 'ControlMetricList',

        components: {
        Loading
        },

        computed:{
            appName(){ return process.env.VUE_APP_NAME },
        }, 

        data() {
            return {
                
                // keep the list of projects returned from back-end
                projectsList : [],

                selectedProject : {},

                loading : false,

                pagination : {
                    page: 1,
                    size: 10,
                    total: 0,
                    results: [],
                }


            }
        },

        watch: {
            pagination : {
                handler() {
                    if (!this.loading) {
                        this.selectProject(this.selectedProject );
                    }
                },
                deep: true,
            },
        },

        methods: {

        /**
         * Load all projects.
         * This method will call the ProjectController#getAll in the back-end that will return all projects salved in database.
         */
        loadProjects(){
            this.loading = true
            this.$http.get('/project')
            .then(result => { 
                this.projectsList = result.data;
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

        /**
         * Load the period of the project, the project data will be created for a period of analysis
         */
         selectProject(project){
            this.loading = true
            this.selectedProject = project
            this.$http.get("/period/list/"+project.id+"?page="+this.pagination.page+"&size="+this.pagination.size)
            .then(result => { 
                this.pagination = result.data;
            }  
            ).catch(
                error => {
                    this.$toastw.error(error.data.messageList)
                }
            ).finally(()=>{
                this.loading = false
            })
        },

        selectPeriod(period){
            this.$router.push("/control/form/"+period.id)
        },

        
        cancel(){
            this.$router.push("/")
        },

        },

        beforeMount(){
            this.loadProjects()
        }

        }
</script>
  
  
<style scoped>
  
</style>