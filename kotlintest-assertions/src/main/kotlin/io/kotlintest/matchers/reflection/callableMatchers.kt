package io.kotlintest.matchers.reflection

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters

infix fun KCallable<*>.shouldHaveVisibility(visibility: KVisibility) = this should haveCallableVisibility(visibility)
infix fun KCallable<*>.shouldNotHaveVisibility(visibility: KVisibility) = this shouldNot haveCallableVisibility(visibility)
fun haveCallableVisibility(expected: KVisibility) = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.visibility == expected,
      "Member $value should have visibility ${expected.humanName()}",
      "Member $value should not have visibility ${expected.humanName()}"
  )
}

fun KCallable<*>.shouldBeFinal() = this should beFinal()
fun KCallable<*>.shouldNotBeFinal() = this shouldNot beFinal()
fun beFinal() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isFinal,
      "Member $value should be final",
      "Member $value should not be final"
  )
}

fun KCallable<*>.shouldBeOpen() = this should beOpen()
fun KCallable<*>.shouldNotBeOpen() = this shouldNot beOpen()
fun beOpen() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isOpen,
      "Member $value should be open",
      "Member $value should not be open"
  )
}

fun KCallable<*>.shouldBeAbstract() = this should beAbstract()
fun KCallable<*>.shouldNotBeAbstract() = this shouldNot beAbstract()
fun beAbstract() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isAbstract,
      "Member $value should be abstract",
      "Member $value should not be abstract"
  )
}

fun KCallable<*>.shouldBeSuspendable() = this should beSuspendable()
fun KCallable<*>.shouldNotBeSuspendable() = this shouldNot beSuspendable()
fun beSuspendable() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isSuspend,
      "Member $value should be suspendable",
      "Member $value should not be suspendable"
  )
}

fun KCallable<*>.shouldAcceptParameters(parameters: List<KClass<*>>, block: (List<KParameter>) -> Unit) {
  this should acceptParametersOfType(parameters)
  block(this.parameters)
}
infix fun KCallable<*>.shouldAcceptParameters(parameters: List<KClass<*>>) = this should acceptParametersOfType(parameters)
infix fun KCallable<*>.shouldNotAcceptParameters(parameters: List<KClass<*>>) = this shouldNot acceptParametersOfType(parameters)
fun acceptParametersOfType(parameters: List<KClass<*>>) = object : Matcher<KCallable<*>> {
  private fun validate(index: Int, acc: Boolean, parameter: KParameter): Boolean {
    return index == 0 || acc && parameter.type.isSupertypeOf(parameters[index - 1].starProjectedType)
  }

  override fun test(value: KCallable<*>) = Result(
      parameters.size + 1 == value.parameters.size && value.parameters.foldIndexed(true, ::validate),
      "Member $value should accept these parameters: ${parameters.joinToString(", ")}",
      "Member $value should not accept these parameters: ${parameters.joinToString(", ")}"
  )
}

fun KCallable<*>.shouldHaveParametersWithName(parameters: List<String>, block: (List<KParameter>) -> Unit) {
  this should haveParametersWithName(parameters)
  block(this.parameters)
}
infix fun KCallable<*>.shouldHaveParametersWithName(parameters: List<String>) = this should haveParametersWithName(parameters)
infix fun KCallable<*>.shouldNotHaveParametersWithName(parameters: List<String>) = this shouldNot haveParametersWithName(parameters)
fun haveParametersWithName(parameters: List<String>) = object : Matcher<KCallable<*>> {
  private fun validate(index: Int, acc: Boolean, parameter: KParameter): Boolean {
    return index == 0 || acc && parameter.name == parameters[index - 1]
  }

  override fun test(value: KCallable<*>) = Result(
      parameters.size + 1 == value.parameters.size && value.parameters.foldIndexed(true, ::validate),
      "Member $value should have these parameters name: ${parameters.joinToString(", ")}",
      "Member $value should not have these parameters name: ${parameters.joinToString(", ")}"
  )
}