<template>
    <span class="help">
        <b-link :id="'popover-target'+id" style="font-size: 1.0em; color: white;" v-bind:style="'color:'+color+';'">
            <i class="fas fa-info-circle"></i>
        </b-link>
        <b-popover :target="'popover-target'+id" triggers="hover" placement="top">
            <template v-slot:title>{{title}}</template>
            <p v-html="text"> </p>
        </b-popover>
    </span>
</template>
  
<script>
  
    export default {
    
        name: 'Help',
        
        props: {
            id: {
                type: String,
                required: true,
            },
            title: {
                type: String,
                required: true,
            },
            text: {
                type: String,
                required: true,
            },
            // color of the link
            color: {
                type: String,
                required: false,
                default: 'white',
            },
        },
        
    };
</script>
  
<style scoped>
     
     .help{
        float: right;
        padding-right: 10px;
        /* padding-bottom: 20px; */
     }
</style>
  