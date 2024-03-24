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
 *
 * holter-devops
 * br.ufrn.caze.publicano.domain.collectors
 * CollectorFactory
 * 01/07/21
 */
package br.ufrn.caze.holterci.collectors;

import br.ufrn.caze.holterci.collectors.impl.codecov.CoverageCodeCovCollector
import br.ufrn.caze.holterci.collectors.impl.github.*
import br.ufrn.caze.holterci.collectors.impl.gitlab.ci.*
import br.ufrn.caze.holterci.collectors.impl.gitlab.dora.ChangeFailureRateByIssueGitlabCollector
import br.ufrn.caze.holterci.collectors.impl.gitlab.dora.DeploymentFrequencyByTagGitlabCollector
import br.ufrn.caze.holterci.collectors.impl.gitlab.dora.LeadTimeForChangesGitlabCollector
import br.ufrn.caze.holterci.collectors.impl.gitlab.dora.MeanTimetoRecoverByIssueGitlabCollector
import br.ufrn.caze.holterci.collectors.impl.gitlab.operation.CPUUsegeBuildGitlabCollector
import br.ufrn.caze.holterci.collectors.impl.gitlab.performance.*
import br.ufrn.caze.holterci.collectors.impl.sonar.CoverageSonarCollector
import br.ufrn.caze.holterci.collectors.impl.sonar.NumberOfVulnerabilitiesSonarCollector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


/**
 * List of collector of the system supports
 *
 * This list is static, because each collect has a specific implementation
 *
 * Jadson Santos - jadsonjs@gmail.com
 */
@Component
class CollectorFactory {

    /*******************  GITHUB  **************/
    // CI
    @Autowired
    lateinit var commitPerDayGitHubCollector : CommitPerDayGitHubCollector
    // Productivity and Quality
    @Autowired
    lateinit var nClosedPullRequestsGitHubCollector : NClosedPullRequestsGitHubCollector
    @Autowired
    lateinit var nClosedIssuesErrorGitHubCollector : NClosedIssuesErrorGitHubCollector
    // DORA Metrics
    @Autowired
    lateinit var deploymentFrequencyGitHubCollector : DeploymentFrequencyGitHubCollector
    @Autowired
    lateinit var leadTimeForChangesGitHubCollector : LeadTimeForChangesGitHubCollector


    /******************* GITLAB *******************/
    // CI
    @Autowired
    lateinit var commitPerDayGitLabCollector : CommitPerDayGitlabCollector
    @Autowired
    lateinit var buildDurationGitlabCollector : BuildDurationGitlabCollector
    @Autowired
    lateinit var buildActivityGitlabCollector : BuildActivityGitlabCollector
    @Autowired
    lateinit var buildHealthGitlabCollector : BuildHealthGitlabCollector
    @Autowired
    lateinit var timeToFixBrokenBuildGitlabCollector : TimeToFixBrokenBuildGitlabCollector
    @Autowired
    lateinit var commentByMergeRequestGitlabCollector : CommentByMergeRequestGitlabCollector
    @Autowired
    lateinit var commentByIssuesGitlabCollector : CommentByIssuesGitlabCollector

    // Performance Metrics
    @Autowired
    lateinit var nClosedMergeRequestsGitabCollector : NClosedMergeRequestsGitabCollector
    @Autowired
    lateinit var issuesLeadTimeGitlabCollector : IssuesLeadTimeGitlabCollector
    @Autowired
    lateinit var issuesCycleTimeGitlabCollector : IssuesCycleTimeGitlabCollector
    @Autowired
    lateinit var nClosedIssuesErrorGitLabCollector : NClosedIssuesErrorGitlabCollector
    @Autowired
    lateinit var nClosedIssuesGitlabCollector : NClosedIssuesGitlabCollector
    @Autowired
    lateinit var nClosedIssuesErrorRateGitlabCollector : NClosedIssuesErrorRateGitlabCollector

    // operation
    @Autowired
    lateinit var cpuUsegeBuildGitlabCollector : CPUUsegeBuildGitlabCollector



    // DORA Metrics
    @Autowired
    lateinit var deploymentFrequencyByTagGitlabCollector : DeploymentFrequencyByTagGitlabCollector
    @Autowired
    lateinit var leadTimeForChangesGitlabCollector : LeadTimeForChangesGitlabCollector
    @Autowired
    lateinit var meanTimetoRecoverByIssueGitlabCollector : MeanTimetoRecoverByIssueGitlabCollector
    @Autowired
    lateinit var changeFailureRateByIssueGitlabCollector : ChangeFailureRateByIssueGitlabCollector


    /******************* CODECOV *******************/
    @Autowired
    lateinit var coverageCodeCovCollector : CoverageCodeCovCollector

    /******************* SONAR *******************/

    // CI
    @Autowired
    lateinit var coverageSonarCollector : CoverageSonarCollector

    // operation
    @Autowired
    lateinit var numberOfVulnerabitiesSonarCollector : NumberOfVulnerabilitiesSonarCollector


    /******************* JENKINS *******************/






    /**
     * Static List if collectors supported by the system.
     * When create a new collector add here
     */
    val collectors = mutableListOf<Collector>()

    /**
     * return all collectors available
     */
    fun getAll( ) : List<Collector> {
        if(collectors.size == 0){
            /*******************  GITHUB  **************/
            // CI
            collectors.add(commitPerDayGitHubCollector)

            // productivity and quality
            collectors.add(nClosedPullRequestsGitHubCollector)
            collectors.add(nClosedIssuesErrorGitHubCollector)

            // dora
            collectors.add(deploymentFrequencyGitHubCollector)
            collectors.add(leadTimeForChangesGitHubCollector)


            /******************* GITLAB *******************/

            // ci
            collectors.add(commitPerDayGitLabCollector)
            collectors.add(buildDurationGitlabCollector)
            collectors.add(buildActivityGitlabCollector)
            collectors.add(buildHealthGitlabCollector)
            collectors.add(timeToFixBrokenBuildGitlabCollector)
            collectors.add(commentByMergeRequestGitlabCollector)
            collectors.add(commentByIssuesGitlabCollector)


            // performance
            collectors.add(nClosedMergeRequestsGitabCollector)
            collectors.add(issuesLeadTimeGitlabCollector)
            collectors.add(issuesCycleTimeGitlabCollector)

            collectors.add(nClosedIssuesErrorGitLabCollector)
            collectors.add(nClosedIssuesGitlabCollector)
            collectors.add(nClosedIssuesErrorRateGitlabCollector)


            // dora
            collectors.add(deploymentFrequencyByTagGitlabCollector)
            collectors.add(leadTimeForChangesGitlabCollector)
            collectors.add(meanTimetoRecoverByIssueGitlabCollector)
            collectors.add(changeFailureRateByIssueGitlabCollector)


            // operation
            collectors.add(cpuUsegeBuildGitlabCollector)




            /******************* CODECOV *******************/

            collectors.add(coverageCodeCovCollector)



            /******************* SONAR *******************/

            collectors.add(coverageSonarCollector)
            collectors.add(numberOfVulnerabitiesSonarCollector)



            /******************* JENKINS *******************/



        }
        return collectors
    }

    /**
     * return collectors from analysis
     */
    fun get(collectorsId : List<UUID> ) : List<Collector> {

        val collectorsList : MutableList<Collector> = mutableListOf();

        val all = getAll();

        for(collector in all){
            for (id in collectorsId){
                if(collector.id == id){
                    collectorsList.add(collector)
                }
            }
        }


        return collectorsList;
    }
}