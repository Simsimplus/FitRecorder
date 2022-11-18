@file:Suppress("LocalVariableName", "PropertyName","ClassName")
package io.simsim.fit.recorder.utils

abstract class SingletonHolder<INPUT,OUTPUT>(
    private val initializer:(INPUT)->OUTPUT
) {
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    fun getInstance(input: INPUT):OUTPUT{
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            return _v1 as OUTPUT
        }

        return synchronized(this) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST") (_v2 as OUTPUT)
            } else {
                val typedValue = initializer(input)
                _value = typedValue
                typedValue
            }
        }
    }
}
private object UNINITIALIZED_VALUE