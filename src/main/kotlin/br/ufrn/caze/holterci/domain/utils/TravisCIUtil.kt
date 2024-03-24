package br.ufrn.caze.holterci.domain.utils

import br.com.jadson.snooper.travisci.data.builds.TravisBuildsInfo
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class TravisCIUtil {


     fun getBuildsOfPeriod(builds: List<TravisBuildsInfo>, startPeriod: LocalDateTime, endPeriod: LocalDateTime): List<TravisBuildsInfo> {
        val buildsOfRelease: MutableList<TravisBuildsInfo> = ArrayList()
        for (build in builds) {
            if (build.started_at != null) {
                val startBuild = LocalDateTime.parse(build.started_at, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH))
                if ( startBuild.isAfter(startPeriod) && startBuild.isBefore(endPeriod) ) { // this build if of this release
                    buildsOfRelease.add(build)
                }
            }
        }
        return buildsOfRelease
    }
}