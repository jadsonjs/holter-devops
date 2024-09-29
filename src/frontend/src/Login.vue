<template>
  
    <div id="login">
        
        
        <!-- Main Content -->
        <div class="container-fluid">
            <div class="row main-content bg-success text-center">
                <div class="col-md-4 text-center company__info">
                    <span class="company__logo"><h2> <img :src="require('@/assets/img/icon.png')" width="200px"> </h2></span>
                    <h4 class="company_title">Holter DevOps</h4>
                </div>
                <div class="col-md-8 col-xs-12 col-sm-12 login_form ">
                    <div class="container-fluid">
                        <div class="row">
                            <h2>Log In</h2>
                        </div>
                        <div class="row">
                            <article class="form-group" style="width: 100%; padding: 0 2em;">
                                <div class="row">
                                    <input type="text" name="username" id="username" v-model="user.email"  class="form__input" placeholder="Username">
                                </div>
                                <div class="row">
                                    <!-- <span class="fa fa-lock"></span> -->
                                    <input type="password" name="password" id="password" v-model="user.password"  class="form__input" placeholder="Password">
                                </div>
                                <!-- 
                                <div class="row">
                                    <input type="checkbox" name="remember_me" id="remember_me" class="">
                                    <label for="remember_me">Remember Me!</label>
                                </div>
                                -->
                                <div class="row">
                                    <input value="Submit" class="btn" v-on:click="loggin()">
                                </div>
                            </article>
                        </div>
                        <!--
                        <div class="row">
                            <p>Don't have an account? <a href="#">Register Here</a></p>
                        </div>
                         -->
                    </div>
                </div>
            </div>
						<div v-if="showSecurityAlert" class="d-flex justify-content-center align-items-center">
						Wait, checking security... <b-spinner label="Loading..."></b-spinner>
						</div>
						<div class="d-flex justify-content-center align-items-center">
							<b-alert v-if="! showSecurityAlert && ! isSecure" variant="danger" show>
								<i class="fas fa-exclamation-triangle"></i> Connection is not secure!
							</b-alert>
						</div>
        </div>
				
        <!-- Footer -->
        <div class="container-fluid text-center footer">
            CASE - Collaborative & Automated Software Engineering - <a href="http://caseufrn.github.io" target="_black">http://caseufrn.github.io</a>
        </div>


    </div> <!-- <div id="login"> -->
  
  </template>
  
  <script>
  
  export default {
    name: 'Login',
    components: {
    },
  
    computed:{
        publicPath(){ return process.env.VUE_APP_PUBLIC_PATH },

        enableLoginPage() { return this.$store.state.login.enableLoginPage;    },

				isSecure() { return window.location.protocol === 'https:'; },
    },  


    data() {
        return {
            user : { email: "", password: ""},
						showSecurityAlert: true,
        }
    },

  
    methods: {
        /**
         * login in the application
         */
        loggin(){
            const headers = {
            'Content-Type': 'application/json',
            }

            this.$http.post('/login', this.user, { headers: headers} )
            .then(
                (result) => {
                    this.$store.commit('login/setLoggedInUser' , result.data )
                    this.$store.commit('login/setAuthenticated' , true)
                    window.location.replace(this.publicPath)
                }
            )
            .catch(
                error => {
                    if(error.data)
                        this.$toastw.error(error.data.messageList)
                    else
                        this.$toastw.error(error)
                    console.log(error)
                }
            )
        },

        // get information if login page should be shown        
        getEnableLoginPage(){
            this.$http.get('/parameter/login-enable')
            .then(
                (result) => {
                    this.$store.commit('login/setEnableLoginPage' , result.data)
                }
            )
        },

        // checks if login is disable, if true redirect do public path
        // now we need login to access security areas, even no login riquered to access dashboard
        // checkLoginDisable(){
        //     if ( ! this.enableLoginPage  ) {
        //         this.$toastw.error('No login required');
        //         setTimeout(() => {
        //             window.location.replace(this.publicPath);
        //         }, 1000);
        //     }
        // },

    },

    async beforeMount(){

        await this.getEnableLoginPage()

        setTimeout(() => {
            // this.checkLoginDisable();
			this.showSecurityAlert = false;
        }, 1000); // 1000 milliseconds (1 seconds)

    }
  
  }
  
  </script>
  
  <style scoped>

    .main-content{
        width: 50%;
        border-radius: 20px;
        box-shadow: 0 5px 5px rgba(0,0,0,.4);
        margin: 5em auto;
        display: flex;
    }
    .company__info{
        background-color: #0d6efd;
        border-top-left-radius: 20px;
        border-bottom-left-radius: 20px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        color: #fff;
    }
    .fa-android{
        font-size:3em;
    }
    @media screen and (max-width: 640px) {
        .main-content{width: 90%;}
        .company__info{
            display: none;
        }
        .login_form{
            border-top-left-radius:20px;
            border-bottom-left-radius:20px;
        }
    }
    @media screen and (min-width: 642px) and (max-width:800px){
        .main-content{width: 70%;}
    }
    .row > h2{
        color:#0d6efd;
    }
    .login_form{
        background-color: #fff;
        border-top-right-radius:20px;
        border-bottom-right-radius:20px;
        border-top:1px solid #ccc;
        border-right:1px solid #ccc;
    }
    form{
        padding: 0 2em;
    }
    .form__input{
        width: 100%;
        border:0px solid transparent;
        border-radius: 0;
        border-bottom: 1px solid #aaa;
        padding: 1em .5em .5em;
        padding-left: 2em;
        outline:none;
        margin:1.5em auto;
        transition: all .5s ease;
    }
    .form__input:focus{
        border-bottom-color: #0d6efd;
        box-shadow: 0 0 5px rgba(0,80,80,.4); 
        border-radius: 4px;
    }
    .btn{
        transition: all .5s ease;
        width: 70%;
        border-radius: 30px;
        color:#0d6efd;
        font-weight: 600;
        background-color: #fff;
        border: 1px solid #0d6efd;
        margin-top: 1.5em;
        margin-bottom: 1em;
    }
    .btn:hover, .btn:focus{
        background-color: #0d6efd;
        color:#fff;
    }
  
  </style>
  