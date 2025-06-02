package br.ufrn.caze.holterci.collectors.impl.github.ci

import br.com.jadson.snooper.github.data.issue.GitHubIssueInfo
import br.com.jadson.snooper.github.operations.IssueQueryExecutor
import br.ufrn.caze.holterci.collectors.Collector
import br.ufrn.caze.holterci.collectors.dtos.CollectResult
import br.ufrn.caze.holterci.domain.models.metric.*
import br.ufrn.caze.holterci.domain.utils.GitHubUtil
import br.ufrn.caze.holterci.domain.utils.LabelsUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class NClosedIssuesErrorGitHubCollector
    : Collector(UUID.fromString("f7d1965a-de32-4df3-ae99-34a80d912d6a"), Metric.NUMBER_OF_BUGS, "Number of Issues closed associated with errors at GitHub", MetricRepository.GITHUB){

    @Autowired
    lateinit var gitHubUtils: GitHubUtil

    /** cache all issues of a project, because is very slow this query*/
    var issuesCache = mutableListOf<GitHubIssueInfo>()

    /**
     * GET ALL ISSUES CLOSED ASSOCIATED WITH ERROR in period
     */
    override fun calcMetricValue(period: Period, globalPeriod: Period, project: Project): CollectResult {

        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        val executor = IssueQueryExecutor(projectConfiguration.mainRepository.token)
        executor.setQueryParameters(arrayOf("state=closed"))
        executor.setPageSize(100)

        // bring all issues first time to memory
        if(issuesCache.isEmpty()){
            var allIssues = executor.issuesClosedInPeriod(project.organization+"/"+project.name, globalPeriod.init, globalPeriod.end)
            issuesCache = allIssues
        }
        var issuesOfPeriod = gitHubUtils.getIssueClosedInPeriod(issuesCache, period.init, period.end);
        var errorIssuesOfPeriod = mutableListOf<GitHubIssueInfo>()
        for (issue in issuesOfPeriod){
            if(isErrorIssue(issue, projectConfiguration.mainRepository.issuesErrosLabels))
                errorIssuesOfPeriod.add(issue)
        }

        return CollectResult( BigDecimal(errorIssuesOfPeriod.size), generateMetricInfo(period, errorIssuesOfPeriod ), null)

    }

    @Autowired
    lateinit var labelsUtil : LabelsUtil

    private fun isErrorIssue(issue: GitHubIssueInfo, errorsWord : String?): Boolean {
        if (issue.labels != null && issue.labels.size > 0) {
            for (label in issue.labels) {
                if (labelsUtil.isErrorLabels(label.name, errorsWord)) {
                    return true
                }
            }
        }
        return false
    }


    override fun cleanCache() {
        issuesCache.clear()
    }

    fun generateMetricInfo(period: Period, errorIssuesOfPeriod : List<GitHubIssueInfo> ): String{

        val statusCountMap = mutableMapOf<String, Int>()
        var issueUniqueNumbers = mutableSetOf<Int>()

        for (issue in errorIssuesOfPeriod) {
            // Count occurrences of each status
            statusCountMap[issue.state] = statusCountMap.getOrDefault(issue.state, 0) + 1
            issueUniqueNumbers.add(issue.number)
        }

        return  "<strong> [ "+ dateUtil.format(period.init, "dd/MM/yyyy" ) +" to "+ dateUtil.format(period.end, "dd/MM/yyyy") + "] </strong> <br> "+
            "  - Error Issues: "+errorIssuesOfPeriod.size+" <br>"+
            "  - Error Issues Status: "+statusCountMap+" <br>"+
            "  - Error Issues Numbers: "+issueUniqueNumbers+" <br>"+
            "  - Error Issues Labels: "+errorIssuesOfPeriod.joinToString("; "){ it.labels.toString() }+" <br>"
    }


}