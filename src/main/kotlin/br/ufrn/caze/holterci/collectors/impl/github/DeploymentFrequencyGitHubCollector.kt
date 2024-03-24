package br.ufrn.caze.holterci.collectors.impl.github

import br.com.jadson.snooper.github.data.release.GitHubReleaseInfo
import br.com.jadson.snooper.github.operations.ReleaseQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * In GitHub, Deployment frequency is measured by the average number of deployments per day.
 * Where a deployment is a release in GitHub
 */
@Component
class DeploymentFrequencyGitHubCollector
    : Collector(UUID.fromString("16216ef3-1139-44e7-ac67-743de061a2b6"), Metric.DEPLOYMENT_FREQUENCY, "Deployment Frequency at Github",  MetricRepository.GITHUB){




    /**
     * For the GitHub we will consider the DeployFrequency the Frequency between releases of the project.
     * Using the published_at date
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): Pair<BigDecimal, String>  {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = ReleaseQueryExecutor()
        executor.setGithubToken(projectConfiguration.mainRepositoryToken)
        var allReleases : MutableList<GitHubReleaseInfo> = executor.releases(project.organization + "/" + project.name)

        var releasesOfPeriod : MutableList<GitHubReleaseInfo> = arrayListOf()

        // for each release
        for(r in allReleases){
            if( r.published_at != null && dateUtil.isBetweenDates(dateUtil.toLocalDateTime(r.published_at), period.init, period.end) ) {
                releasesOfPeriod.add(r)
            }
        }

        val qtdTotalDays = dateUtil.daysBetweenInclusiveWithOutWeekends(period.init, period.end)  // include the start and end.

        val releasesPerDayList = arrayOfNulls<Long>(qtdTotalDays)

        // init the array with zero
        for (i in releasesPerDayList.indices) {
            releasesPerDayList[i] = 0L
        }


        var currentReleaseDate: LocalDateTime = period.init

        for (index in 0 until qtdTotalDays) {
            for (release in releasesOfPeriod) {

                    // check if there is a commit in current day
                    if (   currentReleaseDate.dayOfMonth == dateUtil.toLocalDateTime(release.published_at).getDayOfMonth() // same day
                        && currentReleaseDate.monthValue == dateUtil.toLocalDateTime(release.published_at).getMonthValue() // same month
                        && currentReleaseDate.year == dateUtil.toLocalDateTime(release.published_at).getYear()             // same year
                    ) {
                        releasesPerDayList[index] = releasesPerDayList[index]!! + 1
                    }

            }
            currentReleaseDate = currentReleaseDate.plusDays(1) // next day
        }


        var meanTagsInterval = mathUtil.meanOfLongValues(releasesPerDayList.toList() as List<Long>)
        return Pair(meanTagsInterval, generateMetricInfo(period, releasesOfPeriod, qtdTotalDays))
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, releasesOfPeriod : MutableList<GitHubReleaseInfo>, qtdTotalDays : Int): String{

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Releases: "+releasesOfPeriod.size+" <br>"+
                "  - Releases Name: "+releasesOfPeriod.joinToString("; "){ it.name }+" <br>"+
                "  - Releases Date: "+releasesOfPeriod.joinToString("; "){ dateUtil.format(dateUtil.toLocalDateTime(it.created_at), "dd/MM/yyyy") }+" <br>"+
                "  - Total of Days: "+qtdTotalDays+" <br>"
    }
}