package moe.emi.steamp

@kotlin.jvm.JvmName("avgFromInt")
fun List<Int>.avg() = avgBy { it }

fun List<Double>.avg() = avgBy { it }

@kotlin.jvm.JvmName("avgByInt")
@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <T> List<T>.avgBy(f: (T) -> Int): Double = sumOf(f).toDouble() / size

@kotlin.jvm.JvmName("avgByDouble")
@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
inline fun <T> List<T>.avgBy(f: (T) -> Double): Double = sumOf(f) / size

inline fun <T> List<T>.percentage(f: (T) -> Boolean): Double = count(f) / size.toDouble() * 100
