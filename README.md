# holter-devops

holter-devops is a tool that collect and analyze <b>DevOps</b> metrics. To assess the maturity of DevOps adoption.

This tool is part of a doctoral thesis and is still under development
Therefore, there are no guarantees about its perfect functioning; the methodology contained in it is under evaluation, still without any scientific proof of its efficiency.


![dashboard](https://github.com/jadsonjs/holter-devops/blob/master/dashboard.png)


## Tool Settings:
Edit the file <b>src/frontend/.evn.prod</b>, it's necessary to put the production backend API address, as shown in the example below:

         VUE_APP_BACKEND_BASE_URL = https://xxxxxx.xxx.xxx.xxx/holter/api

After this step, open the file <b>src/main/resources/application.properties</b> and change the 5 properties below according to your preferences:


        /*
        * If the dashboard will remain public, or if we will require a login to access it
        *
        * In the case study we will use login to check information of specific users
        * default = false
        */
        enable.login-page=false

        /*
        * IF the tool will access each access to the dashboard, this can take a lot of space.
        * default = false
        */
        log.dashboard-access=false

        /*
        * For the study we will use just CI metric, after the study other metric will be unlocked to the company
        * default = false
        */
        lock.no-ci-metrics=false

        /*
        * We will send alert by e-mail when automatic collect execute.
        * we will make this feature optional. let's see if they ask for it.
        * default = true
        */
        send.email-alert=true


        /*
        * Disable ssl verification ONLY if the CI server do not use HTTPS ou its certificate if invalid.
        * Local Gitlab and Sonar instances
        * default = false
        */
        disable.ssl-verification=true

Where:
  * enable.login-page: enable or disable login in the system, if login the tool will have public access. default = false
  * log.dashboard-access: If the tool will register each access to the dashboard. Disable this option, which was used only for the case study. default = false
  * lock.no-ci-metrics: Limits metrics only to CI metrics. Disable this option to release all tool metrics. default = false
  * send.email-alert: If the tool will send alert emails whenever a monitoring runs. default = true
  * disable.ssl-verification: Disables SSL verification. Use only if your CI server does not use HTTPS or its certificate is invalid or for local Gitlab and Sonar instances. Otherwise, keep enabled. default = false


Also, change the property with the application address.


        /*
        * Used just to send by e-mail
        * default value: http://localhost:8080/holter
        */
        app.url = http://localhost:8080/holter/


## Tool Build
To build the tool just type the docker command below inside the main directory of the tool, where the Dockerfile is located:

          docker build . -t <institution>/holter-devops:vX

Where:
        * < institution >: replace it with the name of your institution
        * vX: The version of the image



## Tool Execution
To run the tool type the command below (in a single line):

       docker container run -d -p 8200:8080 --env DATABASE_PATH=/data --env DATABASE_PASSWORD=sa123456 --env MAIL_ACCOUNT=xxxx@ufrn.br --env MAIL_PASSWORD='xxxx xxxx xxxx xxxx' --env TOKEN_SECRET=aaaaaaaa00000 -v /data/holter:/data --name holter-devops <institution>/holter-devops:vX

Where:
  * 8200: The port where the tool will run.
  * sa123456: The password of the database embedded in the tool.
  * xxxx@ufrn.br: The google email account to send alerts
  * 'xxxx xxxx xxxx xxxx': The app password to send emails (replaces the password of the google account). https://support.google.com/accounts/answer/185833?hl=en
  * aaaaaaaa00000: Random sha512 hash (128 characters) that can be generated at this address: https://emn178.github.io/online-tools/sha512.html
  * /data/holter: The server directory where the tool database will be located (created on the first run).
  * <institution>/holter-devops:vX: The name of the docker image generated in the previous step.

## Configuring the Initial User

To perform the first login to the tool you need to register 1 initial user via SQL command, for this access the H2 database console:

        https://server.ufrn.br:8200/holter/h2-console/

In the "Password" field enter the database password defined in the docker run command (--env DATABASE_PASSWORD=sa123456) and in the JDBC URL field enter the name of the database file (holter_db) also defined in the docker run command by the parameter (--env DATABASE_PATH=/data). Now insert the initial ADMIN user with the SQL commands below:

        INSERT INTO HOLTER.USER_ (email, password) VALUES ('xxxxx.xxxx@ufrn.br', 'xxxxxx'); INSERT INTO HOLTER.PERMISSION (role, user_id) VALUES ('ADMIN', (SELECT id FROM HOLTER.USER_ WHERE email = 'xxxxx.xxxx@ufrn.br'));



## Considerations on Metrics Collection

Some metrics, especially those involving project deployment, will only be correctly collected if developers follow a standardized development flow so that the tool can collect and calculate metrics correctly.

### 1 - Calculation of metrics involving errors/failures in production

There must be a clear definition of what would constitute an issue related to an error/problem in the system (usually marked with a label indicating it's related to an error). The labels defining error issues as well as the branch containing the project's production code should have been configured in the project settings (Projects -> Projects Configurations -> Fields 'Issues Errors Labels' and 'Production Branch').

Justification:

*In GitLab, the failure rate is measured as the percentage of deploys that cause an incident in production in a given period. GitLab calculates this by the number of incidents divided by the number of deploys in a production environment.*
*This assumes:  1) GitLab incidents are tracked. 2) All incidents are related to a production environment. 3) Incidents and deploys have a strictly individual relationship. An incident is related to only one deploy in production, and any deploy in production is related to at most one incident.*
*To simplify, we will calculate the number of incidents in production as "error issues" opened for a project. Because we do not know if the entire team uses this concept of "incidents" and "production environment" existing in GitLab. Besides, it does not encompass other repositories with GitHub where these concepts do not exist.*

### 2 - Calculation of metrics involving deployment frequency

To calculate metrics involving deployment frequency, the tool considers project releases as the tags created in the repository. Whenever a new tag is created, the tool considers that a new release has been launched. 1 deploy = 1 tag created in the project.

Justification:

*In GitLab, the deployment frequency is measured by the average number of deploys per day in a given environment, based on the deploy end time (finished_at). GitLab calculates the deployment frequency as the number of deploys completed on a given day. Only successful deployments (Deployment.statuses = success) are counted. GitLab uses the concept of "Deployment". To simplify, we consider a tag as a deploy. Because we do not know if the entire team uses this concept of "deployment" existing in GitLab.*

### 3 - Calculation of CycleTime metrics (time between first commit and issue closure)

To calculate the CycleTime metric involving the time between the first commit and the completion of a task, there must be a relationship between commits and issues in GitLab. The tool uses commit comments to make this relationship, so for the correct calculation of this metric, commit comments must contain the issue number in the format #NNN - xxxxxxxx. Where NNN is the issue number. Example of commit comment:

            #98 - Unit test issue
            
            
 ## Author

Jadson Santos  -  jadson.santos@ufrn.br           
