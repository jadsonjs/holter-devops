/*
 * Federal University of Rio Grande do Norte
 * Department of Informatics and Applied Mathematics
 * Collaborative & Automated Software Engineering (CASE) Research Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package br.ufrn.caze.holterci.collectors.impl.gitlab.dora

import br.com.jadson.snooper.gitlab.data.tag.GitLabTagInfo
import br.com.jadson.snooper.gitlab.operations.GitLabTagQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

/**
 * In GitHub, Deployment frequency is measured by the average number of deployments per day.
 * Where a deployment is a release in GitHub
 *
 * In GitLab, Deployment frequency is measured by the average number of deployments per day to a given environment,
 * based on the deployment’s end time (its finished_at property). GitLab calculates the deployment frequency from
 * the number of finished deployments on the given day. Only successful deployments (Deployment.statuses = success) are counted.
 *
 * GitLab use ''deployments''. We will consider a tag as a deployment to simplify. Because I do not know if all team use this "deployment concept".
 * So, to our study, 1 deployment = 1 tag create at project.
 *
 * Deployment Frequency =  Mean ( qty tags / days  )
 *
 */
@Component
class DeploymentFrequencyByTagGitlabCollector
    : Collector(UUID.fromString("ca27a1f1-386b-44b3-aea7-2fb81eb7ffca"),  Metric.DEPLOYMENT_FREQUENCY, "Deployment Frequency by Tag at Gitlab", MetricRepository.GITLAB){



    /**
     * For the Gitlab, projects that do not create releases, we will consider the DeployFrequency the Frequency between tags of the project.
     * Using the published_at date
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabTagQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)

        /**
         * This will considerer a release a tag, bacause some places do not create release on gitlab
         */
        var allTags : MutableList<GitLabTagInfo> = executor.tags(project.organization + "/" + project.name)

        var tagsOfPeriod : MutableList<GitLabTagInfo> = arrayListOf()

        // for each release
        for(r in allTags){
            if( r.commit.created_at != null && dateUtil.isBetweenDates(dateUtil.toLocalDateTime( r.commit.created_at), period.init, period.end) ) {
                tagsOfPeriod.add(r)
            }
        }

        val qtdTotalDays = dateUtil.daysBetweenInclusiveWithOutWeekends(period.init, period.end)  // include the start and end.


        val releasesPerDayList = arrayOfNulls<Long>(qtdTotalDays)

        // init the array with zero
        for (i in releasesPerDayList.indices) {
            releasesPerDayList[i] = 0L
        }


        var currentTagDate: LocalDateTime = period.init

        for (index in 0 until qtdTotalDays) {
            for (tag in tagsOfPeriod) {

                    // check if there is a commit in current day (the date of tag is the commit of the tag date)
                    if (   currentTagDate.dayOfMonth == dateUtil.toLocalDateTime(tag.commit.created_at).getDayOfMonth() // same day
                        && currentTagDate.monthValue == dateUtil.toLocalDateTime(tag.commit.created_at).getMonthValue() // same month
                        && currentTagDate.year == dateUtil.toLocalDateTime(tag.commit.created_at).getYear()             // same year
                    ) {
                        releasesPerDayList[index] = releasesPerDayList[index]!! + 1
                    }

            }
            currentTagDate = currentTagDate.plusDays(1) // next day
        }


        var meanTagsInterval = mathUtil.meanOfLongValues(releasesPerDayList.toList() as List<Long>)
        return CollectResult( meanTagsInterval, generateMetricInfo(period, tagsOfPeriod, qtdTotalDays), null )
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period: Period, tagsOfPeriod : List<GitLabTagInfo>, qtdTotalDays : Int): String{

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
                "  - Tags: "+tagsOfPeriod.size+" <br>"+
                "  - Tags Name: "+tagsOfPeriod.joinToString("; "){ it.name }+" <br>"+
                "  - Tags Date: "+tagsOfPeriod.joinToString("; "){ dateUtil.format(dateUtil.toLocalDateTime(it.commit.created_at), "dd/MM/yyyy") }+" <br>"+
                "  - Total of Days: "+qtdTotalDays+" <br>"
    }

}