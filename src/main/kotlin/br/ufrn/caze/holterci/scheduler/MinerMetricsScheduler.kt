package br.ufrn.caze.holterci.scheduler

import br.ufrn.caze.holterci.application.config.WebContextConfig
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.models.metric.Scheduler
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import br.ufrn.caze.holterci.domain.ports.repositories.crud.SchedulerRepository
import br.ufrn.caze.holterci.domain.services.alert.AlertService
import br.ufrn.caze.holterci.domain.services.miner.MinerMetricsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/***
 * This Scheduler execute the miner of metrics automatically.
 *
 * We can configure a scheduler to run automatically, this way the user do not need to run a miner manually.
 *
 * Jadson Santos - jadson.santos@ufrn.br
 */
@Component
class MinerMetricsScheduler (
    private val projectRepository : ProjectRepository,
    private val schedulerRepository : SchedulerRepository,
    private val service : MinerMetricsService,
    private val alertService: AlertService,
    private val webAppInfo: WebContextConfig.WebAppInfo,
) {

    @Value("\${send.email-alert}")
    val sendEmailAlert : Boolean = false


    @Value("\${app.url}")
    val appUrl : String = "http://localhost:8080/holter"


    /**
     * The cron expression consists of six fields (second, minute, hour, day of the month, month, day of the week).
     * Here's a breakdown of the cron expression "0 0 0 * * ?":
     *
     * 0 in the second field specifies that the task should run at the start of the minute.
     * 0 in the minute field specifies that the task should run at the 0th minute.
     * 1 in the hour field specifies that the task should run at 1 am.
     * * in the day of the month and month fields means the task runs every day of every month.
     * ? in the day of the week field is a wildcard that allows the task to run on any day of the
     *
     * The * in the day of the month and month fields means the task runs every day of every month.
     * The ? in the day of the week field is a wildcard that allows the task to run on any day of the week.
     * The 0/5 in the minute field specifies that the task should run every 5 minutes.
     */
    //@Scheduled(cron = "0 0/2 * * * ?") // This cron expression runs the task every 2 minutes (for tests)
    @Scheduled(cron = "0 0 23 * * ?") // This cron expression runs the task every day at 23 pm
    fun runTask() {
        println("---------------------------- starting automatic metric collector at: "+LocalDateTime.now()+"  ---------------------------------------")

        var projects : List<Project> = projectRepository.findAllActive()

        for (project in projects) {

            var scheduler: Scheduler? = schedulerRepository.findSchedulerByProject(project.id!!)

            if(scheduler == null)
                throw IllegalArgumentException("Scheduler not definied to the project: "+project.name)

            if(scheduler.automatic){

                println("project "+project.name+" is automatic, try to collect")

                var data = service.defineMinePeriod(project.id!!);

                //Project = data[0]
                //Scheduler = data[1]
                var periodExecution : Period = data[2] as Period

                var cycleCompleted =  ! periodExecution.init.equals(periodExecution.end) // if the new cycle of collection is complete.

                if( cycleCompleted ){ // can start a new miner
                    service.mine(periodExecution)

                    /**
                     * Alert the users by e-mail
                     */
                    if(sendEmailAlert)
                        alertService.sendAlert(project, periodExecution, appUrl)


                }else{
                    println("cycle not completed yet to "+project.name+ ". Waiting next execution.")
                }
            }else{
                println("project "+project.name+" is not automatic")
            }

        }

        println("------------------------------- ending automatic metric collector at: "+LocalDateTime.now()+"  -----------------------------------------")
    }

}