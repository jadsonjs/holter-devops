# Holter Devops

holter-devops is a tool that collect and analyze DevOps metrics. To assess the maturity of DevOps adoption.

![Dashboard do projeto](holter_devops.png)


This tool is part of a doctoral thesis and is still under development Therefore, there are no guarantees about its perfect functioning; the methodology contained in it is under evaluation, still without any scientific proof of its efficiency.

#### Supported DevOps practices

##### BASIC

- **NUMBER OF CHANGES DELIVERED**: Number of changes develiry in the period of analysis (Pull Requests or Merge Requests). ``` COUNT ( merge_requests OR pull_requests ) ```

- **CYCLE TIME**: Mean time from first commit (in the period of analysis) to issue closed.  ``` MEAN ( issue.closed_at - fisrt_commit.created_at ) ```

- **LEAD TIME**:Mean time from when the issue (in the period of analysis) was created to when it was closed".  ``` MEAN ( issue.closed_at - issue.created_at ) ```

- **NUMBER OF CLOSED ISSUES**: Number of ISSUES closed in the period of analysis. ``` COUNT closed_issues  ```

- **NUMBER OF BUGS**: Number of Bug-related Issues closed ( in the period of analysis ). ``` COUNT bug_closed_issues ```

- **BUGS RATE**: Number of Bug-related Issues closed divided by the total of ISSUES closed (in the period of analysis). ```( COUNT bug_closed_issues ) / ( COUNT closed_issues ) ```

##### CI

- **BUILD DURATION**: It is a measure used to describe the time spend to execute a build. To practice good CI, the builds should run fast to have fast feedbacks. ``` MEAN ( build.finished_at - build.started_at ) ```

- **BUILD ACTIVITY**: It is a measure used to describe the percente of **working days** with builds. To practice good CI, the project should make constant builds. ``` COUNT days_with_builds  / COUNT days ```

- **BUILD HEALTH**: It measure the percentage of successful builds out of total project builds. To practice good CI, builds should fail less. ``` ( COUNT builds - COUNT broken_builds  ) /  COUNT builds```

- **TIME TO FIX BROKEN BUILD**:It is a measure used to describe the time to fix a build that was broken. To practice good CI, failed builds should be fixed quickly. ``` MEAN ( build.fixed_at - build.failed_at ) ```

- **COMMIT PER DAY**: Mean of number of commits per week **working days**. To practice good CI, the developer should make constant commits. ``` MEAN (   ( COUNT commits  )  GROUP BY working days ```

- **COMMENTS PER CHANGE**: Mean of the number of comments grouped by changes ( Pull Request or Merge Request). To practice good CI, the developer should have a good communication. ``` MEAN (   ( COUNT comments  ) GROUP BY changes  ) ```

- **COVERAGE**:  It is a measure used to describe the degree to which the source code of a program is executed when a particular test suite runs. To practice good CI, the project should have good testing quality. ``` ( COUNT ( LOC with test )  / COUNT ( LOC ) ) * 100```


##### DORA

- **DEPLOYMENT FREQUENCY**: Refers to the frequency of successful software releases to production.  ``` Mean (  COUNT deploys  / COUNT days  ) ```

- **LEAD TIME FOR CHANGES**: Captures the time between a code change commit and its deployable state. ``` Mean ( merge_request <in_prodution_branch>.merged_at - commit.created_at  ) ```

- **MEAN TIME TO RECOVERY**: Measures the time between a system failure and full recovery. ```  Mean ( bug_issue_closing_date - bug_issues_opening_date)   / COUNT deploys ```

- **CHANGE FAILURE RATE**: This metric captures the percentage of changes that were made to a code that then resulted in incidents, rollbacks, or any type of production failure. ```( ( COUNT bug_issues / COUNT issues ) / COUNT deploys ) * 100 ```


##### OPERATION

- **NUMBER OF VULNERABILITIES**: Total of security vulnerabilities of the software.  ``` COUNT ( vulnerabilities ) ```

- **INFRASTRUCTURE COSTS**: The estimate to maintain the DevOps process measured from the time spent on each build.  ``` SUM (  build.finished_at - build.started_at ) ```


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



#### License
Copyright (c) 2023 Jadson Santos - jadson.santos@ufrn.br

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


#### Project status
This tool is part of a doctoral thesis and is still under development.

Therefore, there are no guarantees about its perfect functioning; the methodology contained in it is under evaluation, still without any scientific proof of its efficiency.


## Project Structure


📦holter-devops \
┃ ┣ 📂**libs** (bibliotecas externas da aplicação) \
┃ ┣ 📂**src** \
┃ ┃ ┣ 📂**main**  \
┃ ┃ ┃ ┣ 📂kotlin  (os controladores do spring boot) \
┃ ┃ ┃ ┣ 📂resources  \
┃ ┃ ┣ 📂**test** (classes de testes) \
┃ ┃ ┣ 📂**frontend** (as páginas internas da sua aplicação em VUE) \
┃ ┃ ┃ ┣ 📂src \
┃ ┃ ┃ ┃ ┣ 📂assets (imagens e css da aplicação) \
┃ ┃ ┃ ┃ ┣ 📂components (componentes Vue reutilizáveis por outros componentes da aplicação) \
┃ ┃ ┃ ┃ ┃ ┣ 📂base (componentes da arquitetura da sua aplicação (**NAO ALTERAR**) ) \
┃ ┃ ┃ ┃ ┃ ┗ 📂**use-case** (componentes da apilcacao) \
┃ ┃ ┃ ┃ ┣ 📂layout (componentes do layout da arquitetura  (**NAO ALTERAR**) ) \
┃ ┃ ┃ ┃ ┣ 📂router (arquivo com as rotas da aplicação) \
┃ ┃ ┃ ┃ ┣ 📂stores (armazenamento do lado do frontend) \
┃ ┃ ┃ ┃ ┣ 📂utils (funções utilitárias globais da aplicação) \
┃ ┃ ┃ ┃ ┣ 📂views (componentes Vue que representam as páginas da aplicação) \
┃ ┃ ┃ ┃ ┃ ┣ 📂base (páginas da arquitetura da sua aplicação (**NAO ALTERAR**) \
┃ ┃ ┃ ┃ ┃ ┗ 📂**use-case** (páginas da sua aplicação) \
┣ 😺 .gitlab-ci.yml \
┣ 🚢 DockerFile \
┗ 🚀 build.gradle.kts \



## Getting started

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




### Running localhost

#### frontend

open the src/frontend

        pnpm install
        pnpm run dev

to start the vue app in http://localhost:3000/holter/

#### backend

Execute the **HolterCIApplication.kt** after configure the [variáveis de ambiente](#environment-variables).






### Configuring the Initial User

To perform the first login to the tool you need to register 1 initial user via SQL command, for this access the H2 database console:

        https://server.xxxx.br:8080/holter/h2-console/

In the "Password" field, we enter the database password defined in the docker run command (--env DATABASE_PASSWORD=sa123456)

In the JDBC URL field, enter the database name (holter_db) along with the path defined in the Docker run command using the parameter --env DATABASE_PATH=/data. For this example, the JDBC URL will be jdbc:h2:/data/holter_db.

Now insert the initial ADMIN user with the SQL commands below:

        INSERT INTO HOLTER.USER_ (email, password) VALUES ('xxxxx.xxxx@br', BCryptPasswordEncoder('plain text password')); INSERT INTO HOLTER.PERMISSION (role, user_id) VALUES ('ADMIN', (SELECT id FROM HOLTER.USER_ WHERE email = 'xxxxx.xxxx@br'));

**PS.: The password field is a ```BCryptPasswordEncoder('plain text password')``` that can be generated here:  https://bcrypt-generator.com**



## Environment Variables

| NOME               | VALOR                             | Descrição                                                                                                                          |
|--------------------|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| APPLICATION_URL    | http://localhost:3000             | URl do frontend da aplicação (projeto VUE)                                                                                         |
| DATABASE_PASSWORD  | saxxxxx                           | The password for the database built into the tool                                                                                  |
| DATABASE_PATH      | /data/holter                      | The server directory where the tool database will be located (created on the first run)                                            |
| HIBERNATE_DLL_AUTO | update/none                       | If the hiberante will create the database after each application redeploy                                                          |
| PROJECTS_PATH      | /tmp/projects                     | directory to clone in projects using in vulnerability report                                                                       |
| TOKEN_SECRET       | aaaaaaaaaaaaaaaaaaaaaaaaaaaaa ... | Random sha512 hash (128 characters) that can be generated at this address: https://emn178.github.io/online-tools/sha512.html       |
| MAIL_ACCOUNT       | xxxx@xxxx.br                      | The google email account to send alerts                                                                                            |
| MAIL_PASSWORD      | 'xxxx xxxx xxxx xxxx'             | The app password to send emails (replaces the Google account password). https://support.google.com/accounts/answer/185833?hl=pt-BR |





## Project setup on Docker

To build the tool just type the docker command below inside the main directory of the tool, where the Dockerfile is located:

          docker build . -t <institution>/holter-devops:vX --build-arg ( FRONTEND_BUILD=build:dev or FRONTEND_BUILD=build:test or FRONTEND_BUILD=build:prod)

Where:
* < institution >: replace it with the name of your institution
* vX: The version of the image



### Running
To run the tool type the command below (in a single line):

       docker container run -d -p 8280:8080 --env DATABASE_PATH=/data --env DATABASE_PASSWORD=sa123456 --env MAIL_ACCOUNT=xxxx@com.br --env MAIL_PASSWORD='xxxx xxxx xxxx xxxx' --env TOKEN_SECRET=aaaaaaaa00000 --env APPLICATION_URL=https://xxxx.xxx.xxx.br/holter -v /data/holter:/data --name holter-devops <institution>/holter-devops:vX

Where:
* 8200: The port where the tool will run.
* sa123456: The password of the database embedded in the tool.
* xxxx@br: The google email account to send alerts
* 'xxxx xxxx xxxx xxxx': The app password to send emails (replaces the password of the google account). https://support.google.com/accounts/answer/185833?hl=en
* aaaaaaaa00000: Random sha512 hash (128 characters) that can be generated at this address: https://emn178.github.io/online-tools/sha512.html
* /data/holter: The server directory where the tool database will be located (created on the first run).
* <institution>/holter-devops:vX: The name of the docker image generated in the previous step.



## Dependencies on other projects or services

| Serviço                                      | Descrição                                      |
|----------------------------------------------|------------------------------------------------|
|  |  |


## Tools Dependencies

* Using node v22.11.0+
* Java 21


## Authors

* **Jadson Santos** - [jadson.santos@ufrn.br](mailto:jadson.santos@ufrn.br)
