
<template>

    <!-- Sidebar -->
    <div class="bg-light border-right" id="sidebar-wrapper">
      <div class="sidebar-heading" style="text-align: center;"> <router-link to="/"> <img :src="require('@/assets/img/icon.png')" width="200px"> </router-link> </div>
      <div class="list-group list-group-flush">
        <router-link to="/" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-home"></i>  Home </router-link>
        

        <router-link to="/metric/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-tachometer-alt"></i> Metric  </router-link>
        
        <router-link to="/project/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-briefcase"></i> Projects  </router-link>
        <!-- 
        <router-link to="/configuration/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-cogs"></i> Projects Configuration</router-link>
        -->
        <router-link to="/ci/references/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-thermometer-half"></i>Metrics Reference Values</router-link>

        <!-- 
        <router-link to="/control/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-weight"></i> Project Metrics Values</router-link>
        -->
        <!--
        <router-link to="/measures/tb/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-money-check-alt"></i> Technical Debt </router-link>
       -->
        
        <router-link to="/measures/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-funnel-dollar"></i>  Collect Metrics </router-link>
        
        
        <router-link to="/metrics/evolution/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-chart-line"></i>  Monitoring Dashboard </router-link>

        <!-- 
        <router-link to="/about" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-question-circle"></i> About </router-link>
        -->
        <!--
        <router-link to="/metrics/influence/list" class="list-group-item list-group-item-action bg-light"> <i class="fas fa-object-ungroup"></i>  Measures Influence </router-link>
        -->
        
        <!-- 
        
        <router-link to="/" class="list-group-item list-group-item-action bg-light"> Overview </router-link>
        <router-link to="/" class="list-group-item list-group-item-action bg-light"> Events </router-link>
        <router-link to="/" class="list-group-item list-group-item-action bg-light"> Profile </router-link>
        <router-link to="/" class="list-group-item list-group-item-action bg-light"> Status </router-link>
        -->
      </div>
    </div>
    <!-- /#sidebar-wrapper -->

</template>
 
<script>
export default {
 
    name: 'AppSideBar',

    computed:{
        
    },

    methods:{
       
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>          