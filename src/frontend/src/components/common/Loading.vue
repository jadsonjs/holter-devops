
<template>
    <div class="loading">
        <div v-if="show" class="text-center"> <b-spinner style="width: 1rem; height: 1rem;" label="Spinning" variant="primary"> </b-spinner> Wait ... </div>
    </div>
</template>

<script>


export default {
    
    name: "Loading",

    props:{
        show:{
            type: Boolean,
            required: true,
            default: false
        }
    },
}

</script>

<style scoped>

    .loading{
        padding-top: 20px;
        padding-bottom: 20px;
        color: #5c6473;
        width: 100%;
        font-size: small;
    }

</style>