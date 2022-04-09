package net.raccoon.tpt.template

object TemplateProcessor {

    private val templateRegex = Regex("\\$\\{(.+)}")

    fun processTemplate(template: String, variables: Map<String, Any>): String {
        val builder = StringBuilder(template)
        val variablesList = mutableListOf<Any>()
        for (matchResult in templateRegex.findAll(template)) {
            val key = matchResult.groupValues[1]
            val formatKey = "\${$key}"
            val index = builder.indexOf(formatKey)
            if (index != -1) {
                builder.replace(index, index + formatKey.length, "%s")
                variablesList += variables[key]
                    ?: throw IllegalArgumentException("Template variable with key $key does not provided")
            }
        }
        return String.format(builder.toString(), *variablesList.toTypedArray())
    }

}
