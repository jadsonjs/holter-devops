package br.ufrn.caze.holterci.domain.utils

import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class LineChartUtil {

    /**
     * Return values in this format
     * [
     *  ['headerTitle', 'columnHeaders(0)', 'columnHeaders(0)', 'columnHeaders(N)'],
     *  ['columnValues(0)',  value, value, value],
     *  ['columnValues(1)',  value, value, value],
     *  ['columnValues(2)',  value, value, value]
     * ]
     */
    fun generateLinerData(headerTitle: String, columnHeaders: List<String>, columnValues: ArrayList<String>, values: MutableList<BigDecimal>) : String{

        val lineChartData = StringBuilder("[")

        generateHeader(headerTitle, lineChartData, columnHeaders)

        generateBody(columnValues, columnHeaders, values, lineChartData)

        lineChartData.append("]")

        return lineChartData.toString()

    }

    private fun generateHeader(headerTitle : String, lineChartData: StringBuilder, columnHeaders: List<String>) {
        var measuresTags = mutableListOf<String>()
        lineChartData.append("[")
        measuresTags.add("\""+headerTitle+"\"")
        for (m in columnHeaders) {
            measuresTags.add("\"" + m + "\"")
        }
        var index = 0
        for (tag in measuresTags) {
            lineChartData.append(tag)
            if (index < measuresTags.size - 1)
                lineChartData.append(",")
            index++
        }
        lineChartData.append("],")
        lineChartData.append("\n")
    }

    private fun generateBody(columnValues: ArrayList<String>, columnHeaders: List<String>, values: MutableList<BigDecimal>, lineChartData: StringBuilder) {

        val QTY_VALUES = (columnValues.size/columnHeaders.size) // qty values by metric

        var periodIndex = 1
        for (p in 1..QTY_VALUES) { // for each line

            var valuesTags = mutableListOf<String>()

            //valuesTags.add("\"" + p  + "\"")  // X axis labels
            valuesTags.add("\"" + columnValues.get(p-1)  + "\"")

            var valueIndex: Int
            // for each metric, get 1 value of metric in the list
            // In the list we have: all values of metric1, after all values of metric 2, etc...
            // metric1v1, metric1v2, metric1v3, metric2v1, metric2v2, metric2v3, metric3v1, metric3v2, metric3v3,
            // values index
            //    0                                3                             6
            //    1                                4                             7
            //    2                                5                             8
            for (j in columnHeaders.indices) {
                valueIndex = (periodIndex-1) + (QTY_VALUES * j)
                valuesTags.add("" + values.get(valueIndex) + "")
            }

            // generate the json format
            lineChartData.append("[")

            var index = 0
            for (tag in valuesTags) {
                lineChartData.append(tag)
                if (index < valuesTags.size - 1)
                    lineChartData.append(",")
                index++
            }

            lineChartData.append("]")
            if (periodIndex < QTY_VALUES)
                lineChartData.append(",")

            lineChartData.append("\n")

            periodIndex++

        }
    }




}