package br.ufrn.caze.holterci.collectors.impl.gitlab.repository

import br.com.jadson.snooper.gitlab.data.commit.GitLabCommitInfo
import br.com.jadson.snooper.gitlab.operations.GitLabCommitQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.Metric
import br.ufrn.caze.holterci.domain.models.metric.MetricRepository
import br.ufrn.caze.holterci.domain.models.metric.MetricSegment
import br.ufrn.caze.holterci.domain.models.metric.Period
import br.ufrn.caze.holterci.domain.models.metric.Project
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.collections.iterator

@Component
class CommitPerDeveloperGitLabCollector :
    Collector(UUID.fromString("99998213-d487-4103-bce6-bece8c533f85"), Metric.COMMITS_PER_DEVELOPER, "Commit per developer", MetricRepository.GITLAB)
{

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {
        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = GitLabCommitQueryExecutor()
        executor.setGitlabURL(projectConfiguration.mainRepository.url)
        executor.setGitlabToken(projectConfiguration.mainRepository.token)
        executor.setDisableSslVerification(disableSslVerification)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))
        executor.setPageSize(100)

        var commitsOfPeriod: List<GitLabCommitInfo>

        commitsOfPeriod = executor.getCommits(project.organization + "/" +project.name)

        val commitsPerDeveloper = mutableMapOf<String, Int>()

        for(commit in commitsOfPeriod){
            if(commit.author_name != null){
            commitsPerDeveloper[commit.author_name] = commitsPerDeveloper.getOrDefault(commit.author_name, 0) + 1

            }

        }

        val qntOfDevelopers = BigDecimal(commitsPerDeveloper.size.toString());


        val resultMetric = mutableListOf<CollectResult>()

        for((author, qttOfCommits) in commitsPerDeveloper){

            val metricSegment = MetricSegment()
            metricSegment.type = "DEV"
            metricSegment.identifier = author

            resultMetric.add(
                CollectResult(
                    BigDecimal(qttOfCommits),
                    generateMetricInfo(period, qntOfDevelopers, commitsPerDeveloper, commitsOfPeriod),
                    metricSegment
                )
            )
        }

        return CollectResult(resultMetric)

    }

    override fun cleanCache() {

    }


    fun generateMetricInfo(period : Period, qntOfDevelopers: BigDecimal, commitsPerDeveloper : Map<String, Int>, commitsOfPeriod : List<GitLabCommitInfo>): String{
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var returnPhrase = StringBuilder()

        returnPhrase.append("<strong> ["+ dtf.format(period.init) + " to "+ dtf.format(period.end) + "] </strong> <br>"+
                "  - Commits: " + commitsOfPeriod.size+"<br>" +
                "  - Total of developers: "+qntOfDevelopers+" <br>")

        val sortedMap = commitsPerDeveloper.toList().sortedByDescending { it.second }.toMap()


        for(author in sortedMap.keys){
            returnPhrase.append(" - " + author + ": " + commitsPerDeveloper[author] + " commits <br>")
        }

        return returnPhrase.toString()


    }



}