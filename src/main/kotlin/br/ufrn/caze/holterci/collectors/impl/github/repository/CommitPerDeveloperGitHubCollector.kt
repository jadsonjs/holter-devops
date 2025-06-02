package br.ufrn.caze.holterci.collectors.impl.github.repository

import br.com.jadson.snooper.github.data.commit.GitHubCommitInfo
import br.com.jadson.snooper.github.operations.CommitQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CommitPerDeveloperGitHubCollector
    : Collector(UUID.fromString("06cd5244-888c-4ec6-9c67-6fad80612484"), Metric.COMMITS_PER_DEVELOPER, "Commit per developer", MetricRepository.GITHUB) {

    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {
        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)
        // as exclamações garantem que project.id não é nulo, e se for, pode lançar um erro3

        val executor = CommitQueryExecutor();
        executor.setGithubToken(projectConfiguration.mainRepository.token)
        executor.setQueryParameters(arrayOf("since=" + dateUtil.toIso8601(period.init), "until=" + dateUtil.toIso8601(period.end)))
        executor.setPageSize(100)
        println("Query: since=" + dateUtil.toIso8601(period.init) + " until=" + dateUtil.toIso8601(period.end))


        var commitsOfPeriod : List<GitHubCommitInfo>


        commitsOfPeriod = executor.getCommits(project.organization + "/" + project.name)

        val commitsPerDeveloper = mutableMapOf<String, Int>()

        for(commit in commitsOfPeriod){
            if(commit.author != null && commit.author.login != null){
                commitsPerDeveloper[commit.author.login] = commitsPerDeveloper.getOrDefault(commit.author.login, 0) + 1
            }
        }


        val qntOfDevelopers = BigDecimal(commitsPerDeveloper.size.toString());


        val resultMetric = mutableListOf<CollectResult>()

        for((author, qttOfCommits) in commitsPerDeveloper){

            val metricSegment = MetricSegment()
            metricSegment.type = "DEV"
            metricSegment.identifier = author

            resultMetric.add(CollectResult(BigDecimal(qttOfCommits), generateMetricInfo(period, qntOfDevelopers, commitsPerDeveloper, commitsOfPeriod), metricSegment))
        }

        return CollectResult(resultMetric)
    }

    override fun cleanCache() {

    }

    fun generateMetricInfo(period : Period, qntOfDevelopers: BigDecimal, commitsPerDeveloper : Map<String, Int>, commitsOfPeriod : List<GitHubCommitInfo>): String{
        val dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        var returnPhrase = StringBuilder()

        returnPhrase.append("<strong> ["+ dtf.format(period.init) + " to "+ dtf.format(period.end) + "] </strong> <br>"+
                "  - Commits: " + commitsOfPeriod.size+"<br>" +
                "  - Total of developers: "+qntOfDevelopers+" <br>")


        val sortedMap = commitsPerDeveloper.toList().sortedByDescending { it.second }.toMap()
        // Vai formar uma lista de pairs
        // sortedBy { it.second } -> "Ordene essa lista com base no segundo elemento de cada item".


        for(author in sortedMap.keys){
            returnPhrase.append(" - " + author + ": " + commitsPerDeveloper[author] + " commits <br>")
        }

        return returnPhrase.toString()


    }




}