package org.deblock.exercise.base

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

public open class Specification {
}

// Just labels for BDD style scenarios with nicer yellow highlighting of extension methods by IDEA
public fun Specification.Given(s: String = "") = Unit
public fun Specification.When(s: String = "") = Unit
public fun Specification.Then(s: String = "") = Unit
public fun Specification.And(s: String = "") = Unit
public fun Specification.Expect(s: String = "") = Unit

public open class Where(val block: WhereArguments.() -> Unit) : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<Arguments> {
        val arguments = mutableListOf<Arguments>()
        block(WhereArguments(arguments))
        return arguments.stream()
    }
}

public class WhereArguments(
    private val arguments: MutableList<Arguments>
) {
    fun of(vararg arguments: Any?) {
        this.arguments.add(Arguments.of(*arguments));
    }
}