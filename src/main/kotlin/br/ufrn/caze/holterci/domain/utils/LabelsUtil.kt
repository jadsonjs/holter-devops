package br.ufrn.caze.holterci.domain.utils

import org.springframework.stereotype.Component
import java.util.*

@Component
class LabelsUtil {

    fun isErrorLabels(labelName: String?, errosWord : String?): Boolean {

        if (labelName == null) return false
        if (errosWord == null) return false

        val errorsWordList = Arrays.asList(*errosWord.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())


        var finalLabelName = labelName.trim { it <= ' ' }

        for (errorWord in errorsWordList) {

            // if contains the whole word, ok
            if (finalLabelName.equals(errorWord, ignoreCase = true)) return true

            if (finalLabelName.equals("good first issue", ignoreCase = true)) // good first issue is not an error labels
                return false

            val partsArray: Array<String> = finalLabelName.split(" ".toRegex()).toTypedArray() // many labels have more the one word to identify erros

            for ( part in partsArray) {
                var finalLabel: String = part
                if (part.contains("/"))
                    finalLabel = part.replace("/".toRegex(), ":")
                if (part.contains("="))
                    finalLabel = part.replace("=".toRegex(), ":")
                if (part.contains("-"))
                    finalLabel = part.replace("-".toRegex(), ":")

                if (finalLabel.contains(":")) {
                    val partSubArray = finalLabel.split(":".toRegex()).toTypedArray()
                    for (i in partSubArray.indices) {
                        if (partSubArray[i].equals(errorWord, ignoreCase = true)) {
                            return true
                        }
                    }
                } else {
                    if (finalLabel.equals(errorWord, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }

        return false

    }

}