package org.deblock.exercise.base

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

infix fun <T> T.eq(expected: T) = shouldBe(expected)
infix fun <T> T.notEq(expected: T) = shouldNotBe(expected)