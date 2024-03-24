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
 * publicano
 * br.ufrn.caze.publicano
 * Metric
 * 07/05/21
 */
package br.ufrn.caze.holterci.domain.models.metric

import br.ufrn.caze.holterci.domain.dtos.crud.MetricDto
import br.ufrn.caze.holterci.domain.models.division.MetricCategory
import br.ufrn.caze.holterci.domain.models.division.MetricStage
import br.ufrn.caze.holterci.domain.models.division.MetricTeam

/**
 * Metrics Supported by our tool
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 */
enum class Metric(val id: Int, val denomination: String, var metricTeam : MetricTeam, var metricStage : MetricStage, var metricCategory: MetricCategory, var direct :Boolean, var percentage :Boolean, var unit: String, var description: String, var formula: String){

    // CONTROL (DEPRECATED) //
    //N_DEVELOPERS      (1,  "N Developers", MetricTeam.NEUTRAL, MetricStage.NEUTRAL, MetricCategory.NEUTRAL,   true, false, " ","Number of Developers of a project", ""),
    //PROJECT_SIZE      (2,  "Project Size", MetricTeam.NEUTRAL, MetricStage.NEUTRAL, MetricCategory.NEUTRAL,   true, false, " ","Size of a project in line of code", ""),
    //PROJECT_COMPLEXITY(3,  "Project Complexity", MetricTeam.NEUTRAL, MetricStage.NEUTRAL, MetricCategory.NEUTRAL,   true, false, " ","It measures the number of linearly-independent paths through a program module", ""),
    //TECHNICAL_DEBT    (4,  "Technical Debt", MetricTeam.NEUTRAL, MetricStage.NEUTRAL, MetricCategory.NEUTRAL,   true, false, " ","The implied cost incurred when businesses do not fix problems that will affect them in the future", ""),

    // CI METRICS //
    BUILD_DURATION             (11, "Build Duration"            , MetricTeam.DEV, MetricStage.CI, MetricCategory.PRODUCTIVITY,    false, false, "minutes","It is a measure used to describe the time spend to execute a build. To practice good CI, the builds should run fast to have fast feedbacks.",                                                        " MEAN ( build.finished_at - build.started_at ) "),
    BUILD_ACTIVITY             (12, "Build Activity"            , MetricTeam.DEV, MetricStage.CI, MetricCategory.PRODUCTIVITY,    true,  true,  "0..1",       "It is a measure used to describe the percente of *** working days *** with builds. To practice good CI, the project should make constant builds.",                                               " COUNT days_with_builds  / COUNT days  "),
    // COMMIT_ACTIVITY         (13, Category.CI,      true,  true,  "",       "It is a measure used to describe the percente of day with commits"),
    BUILD_HEALTH               (14, "Build Health"              , MetricTeam.DEV, MetricStage.CI, MetricCategory.RESILIENCE,      true,  true,  "0..1",    "It measure the percentage of successful builds out of total project builds. To practice good CI, builds should fail less.",                                                                         " ( COUNT builds - COUNT broken_builds  ) /  COUNT builds  "),
    TIME_TO_FIX_BROKEN_BUILD   (15, "Time to Fix a Broken Build", MetricTeam.DEV, MetricStage.CI, MetricCategory.PRODUCTIVITY,    false, false, "hours",  "It is a measure used to describe the time to fix a build that was broken. To practice good CI, failed builds should be fixed quickly.",                                                              " MEAN ( build.fixed_at - build.failed_at )"),
    COMMIT_PER_DAY             (16, "Commit per Day"            , MetricTeam.DEV, MetricStage.CI, MetricCategory.PRODUCTIVITY,    true,  true,  " ",       "Mean of number of commits per week *** working days ***. To practice good CI, the developer should make constant commits.",                                                                         " MEAN (   ( COUNT commits  )  GROUP BY working days  )  "),
    COVERAGE                   (17, "Coverage"                  , MetricTeam.DEV, MetricStage.CI, MetricCategory.RESILIENCE,      true,  true,  "%",      "It is a measure used to describe the degree to which the source code of a program is executed when a particular test suite runs. To practice good CI, the project should have good testing quality", " % lines_of_code with test "),
    COMMENTS_PER_CHANGE        (18, "Comments Per Change"       , MetricTeam.DEV, MetricStage.CI, MetricCategory.RESILIENCE,      true,  false,  " ",      "Mean of the number of comments grouped by changes ( Pull Request or Merge Request). To practice good CI, the developer should have a good communication.",                                          " MEAN (   ( COUNT comments  ) GROUP BY changes  ) "),

    // BASIC METRICS //
    NUMBER_OF_CHANGES_DELIVERED     (21, "Number of Changes Delivered", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.PRODUCTIVITY, true, false, " ", "Number of changes develiry in the period of analysis (Pull Requests or Merge Requests)",              " COUNT  merge_requests "),
    CYCLE_TIME                      (22, "Cycle Time", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.PRODUCTIVITY, false, false, "days", "Mean time from first commit (in the period of analysis) to issue closed",                                       " MEAN ( issue.closed_at - fisrt_commit.created_at )  "),
    LEAD_TIME                       (23, "Lead Time", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.PRODUCTIVITY, false, false, "days", "Mean time from when the issue (in the period of analysis) was created to when it was closed",                   " MEAN ( issue.closed_at - issue.created_at )  "),
    NUMBER_OF_CLOSED_ISSUES         (24, "Number of Closed Issues", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.PRODUCTIVITY, true, false, " ", "Number of ISSUES closed in the period of analysis",                                                   " COUNT closed_issues "),
    NUMBER_OF_BUGS                  (25, "Number of Bugs", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.RESILIENCE,   false, false, " ", "Number of Bug-related Issues closed in the period of analysis",                                      " COUNT bug_closed_issues "),
    BUGS_RATE                       (26, "Bugs Rate", MetricTeam.DEV, MetricStage.BASIC, MetricCategory.RESILIENCE,   false, true, "0..1", "Number of Bug-related Issues closed divided by the total of ISSUES closed (in the period of analysis)", " COUNT bug_closed_issues / COUNT closed_issues "), // Error rates



    // DORA METRICS //
    DEPLOYMENT_FREQUENCY (31,   "Deployment Frequency", MetricTeam.DEV, MetricStage.CD, MetricCategory.PRODUCTIVITY,   true, false, "days","Refers to the frequency of successful software releases to production",    " Mean (  COUNT deploys  / COUNT days  )"),
    LEAD_TIME_FOR_CHANGES(32,   "Lead Time for Changes", MetricTeam.DEV, MetricStage.CD, MetricCategory.PRODUCTIVITY,   false, false, "days","Captures the time between a code change commit and its deployable state", " Mean ( merge_request <in_prodution_branch>.merged_at - commit.created_at  ) "),
    MEAN_TIME_TO_RECOVERY(33,   "Mean Time to Recovery", MetricTeam.DEV, MetricStage.CD, MetricCategory.RESILIENCE,     false, false, "days","Measures the time between a system failure and full recovery.",           " Mean ( bug_issue_closing_date - bug_issues_opening_date)   / COUNT deploys   "),
    CHANGE_FAILURE_RATE  (34,   "Change Failure Rate", MetricTeam.DEV, MetricStage.CD, MetricCategory.RESILIENCE,     false, true,  "%","This metric captures the percentage of changes that were made to a code that then resulted in incidents, rollbacks, or any type of production failure.", " ( ( COUNT bug_issues / COUNT issues ) / COUNT deploys ) * 100  "),




    // Operation Metrics
    NUMBER_OF_VULNERABILITIES (41, "Number of Vulnerabilities", MetricTeam.OPS, MetricStage.CS, MetricCategory.RESILIENCE,      false, false,  " ","Total of security vulnerabilities of the software", " COUNT vulnerabilities  "),
    INFRASTRUCTURE_COSTS    (42,   "Infrastructure Costs"     , MetricTeam.OPS, MetricStage.CM, MetricCategory.NEUTRAL,         false, false,  "hours","The estimate to maintain the DevOps process measured from the time spent on each build", " SUM (  build.finished_at - build.started_at )"),

    ;

    // Kotlin allows us to create objects that are common to all instances of a class
    companion object {

        fun toDto(metric: Metric) : MetricDto {
            return MetricDto(metric.id, metric.denomination, metric.unit, metric.description, metric.formula,
            metric.metricCategory.name, metric.metricStage.name, metric.metricTeam.name,
            metric.metricCategory.highlightColor, metric.metricStage.highlightColor, metric.metricTeam.highlightColor)
        }

        fun getById(vararg ids: Int): List<Metric> {

            var list: ArrayList<Metric> = ArrayList()

            var metrics: Array<Metric> = Metric.values()
            for (m in metrics) {
                for (id in ids) {
                    if (m.id == id) {
                        list.add(m);
                    }
                }
            }
            return list
        }

        /**
         * Return metric of a specific category
         */
        fun getByCategory(vararg categories : MetricCategory): List<Metric> {

            var list: ArrayList<Metric> = ArrayList()

            var metrics: Array<Metric> = Metric.values()
            for (m in metrics) {
                for (c in categories) {
                    if (m.metricCategory == c) {
                        list.add(m);
                    }
                }
            }
            return list
        }

        fun getByStage(vararg stage : MetricStage): List<Metric> {

            var list: ArrayList<Metric> = ArrayList()

            var metrics: Array<Metric> = Metric.values()
            for (m in metrics) {
                for (c in stage) {
                    if (m.metricStage == c) {
                        list.add(m);
                    }
                }
            }
            return list
        }


        fun getByTeam(vararg team : MetricTeam): List<Metric> {

            var list: ArrayList<Metric> = ArrayList()

            var metrics: Array<Metric> = Metric.values()
            for (m in metrics) {
                for (c in team) {
                    if (m.metricTeam == c) {
                        list.add(m);
                    }
                }
            }
            return list
        }

        /**
         * Return all metrics
         */
        fun getAll(): List<Metric> {

            var list: ArrayList<Metric> = ArrayList()

            // var metrics: Array<Metric> = Metric.values()
            var metrics: List<Metric> = listOf(COVERAGE, BUILD_DURATION, BUILD_ACTIVITY, TIME_TO_FIX_BROKEN_BUILD, COMMIT_PER_DAY, BUILD_HEALTH, COMMENTS_PER_CHANGE,
                                            NUMBER_OF_CHANGES_DELIVERED, NUMBER_OF_CLOSED_ISSUES, CYCLE_TIME, LEAD_TIME, NUMBER_OF_BUGS, BUGS_RATE,
                                            DEPLOYMENT_FREQUENCY, LEAD_TIME_FOR_CHANGES, MEAN_TIME_TO_RECOVERY, CHANGE_FAILURE_RATE,
                                            NUMBER_OF_VULNERABILITIES, INFRASTRUCTURE_COSTS )
            for (m in metrics) {
                list.add(m);
            }
            return list
        }
    }

}