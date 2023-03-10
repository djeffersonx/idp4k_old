package br.com.idws.idp4k.spring.aop.spel

import org.springframework.expression.ParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

object ExpressionResolver {

    fun evaluateToString(expression: String, vars: Map<String, Any> = mapOf()): String {

        val expression = expression.replaceFirst("#{", "#{#")

        val context = StandardEvaluationContext().apply {
            vars.forEach { setVariable(it.key, it.value) }
        }

        val value = eval(expression, context) ?: throw IllegalArgumentException("Expression result without value")

        return value.toString()
    }

    private fun eval(
        expression: String,
        context: StandardEvaluationContext
    ) = SpelExpressionParser().parseExpression(expression, ParserContext.TEMPLATE_EXPRESSION).getValue(context)

}

