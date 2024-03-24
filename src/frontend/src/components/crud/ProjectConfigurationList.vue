<!-- <template>
    <div>
      <h1 class="mt-4">Project Configuration</h1>
  
      <article class="descricao-ajuda">
        <p> Configuration to Collect Metric for a Project</p>
        <p> Please, select a project</p>
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

    name: 'ProjectConfiguraionList',

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
        ).catch(
            error => {
            this.$toastw.error(error.data.messageList)
            }
        ).finally(()=>{ this.loading = false })
    },

    /**
     * load the configuration of this project
     */
    selectProject(project){
        this.$router.push("/configuration/form/"+project.id)
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

-->