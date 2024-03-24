package br.ufrn.caze.holterci.domain.utils

import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ScatterChartUtil {

    /**
     *
     * [
     *   ["Diameter", "Age"],
     *   [8, 37], [4, 19.5], [11, 52] ...
     * ]
     */
    fun generateScatterData(metric1: String, valuesMetric1: MutableList<BigDecimal>, metric2: String, valuesMetric2: MutableList<BigDecimal>) : String{

        val lineChartData = StringBuilder("[")

        generateHeader(metric1, metric2, lineChartData)

        generateBody(valuesMetric1, valuesMetric2, lineChartData)

        lineChartData.append("]")

        return lineChartData.toString()

    }

     fun generateScatterChartOption(metricName : String, qualityName : String) = "{ " +
            "            \"title\": \" " + metricName + " vs " + qualityName + "\"," +
            "            \"hAxis\": { \"title\": \"" + metricName + "\"}," +
            "            \"vAxis\": { \"title\": \"" + qualityName + "\"}," +
            "            \"legend\": \"none\"," +
            "            \"trendlines\": { \"0\" : {} }, " +
            "            \"height\": 500" +
            "        }"


    private fun generateHeader(metric1: String, metric2: String, lineChartData: StringBuilder) {

        lineChartData.append("[")
            lineChartData.append("\""+metric1+"\",")
            lineChartData.append("\""+metric2+"\"")
        lineChartData.append("],")
        lineChartData.append("\n")
    }

    private fun generateBody(valuesMetric1: MutableList<BigDecimal>, valuesMetric2: MutableList<BigDecimal>, lineChartData: StringBuilder) {

        var size = if( valuesMetric1.size < valuesMetric2.size) valuesMetric1.size else valuesMetric2.size

        for (index in 0..size-1){
            lineChartData.append("[")
            lineChartData.append( valuesMetric1.get(index).toString()+", "+valuesMetric2.get(index).toString())
            lineChartData.append("]")
            if(index < valuesMetric1.size-1)
                lineChartData.append(",")
        }

    }
}