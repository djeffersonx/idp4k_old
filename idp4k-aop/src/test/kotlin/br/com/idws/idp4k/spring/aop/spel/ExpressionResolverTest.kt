package br.com.idws.idp4k.spring.aop.spel

import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ExpressionResolverTest {

    @Nested
    @DisplayName("with template expression")
    inner class WithTemplateExpression {

        @Test
        fun `it evaluate a simple expression`() {
            val evaluateToString = ExpressionResolver.evaluateToString(
                "#{variable}", mapOf("variable" to "value")
            )
            evaluateToString shouldBeEqualTo "value"
        }

        @Test
        fun `it evaluate a expression with nested objects`() {
            val evaluateToString = ExpressionResolver.evaluateToString(
                "#{variable['nested']}", mapOf("variable" to mapOf("nested" to "value"))
            )
            evaluateToString shouldBeEqualTo "value"
        }

        @Test
        fun `it convert values to string`() {
            val evaluateToString = ExpressionResolver.evaluateToString(
                "#{variable}", mapOf("variable" to 1)
            )
            evaluateToString shouldBeEqualTo "1"
        }

    }

    @Nested
    @DisplayName("without template expression")
    inner class WithoutTemplateExpression {

        @Test
        fun `it return the passed 'expression'`() {
            val evaluateToString = ExpressionResolver.evaluateToString("isn't a expression template")
            evaluateToString shouldBeEqualTo "isn't a expression template"
        }

    }


}