package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Name private constructor(val value: String) {
    companion object {
        // Solo letras (sin espacios) 2-50
        private val REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,50}$")
        fun create(input: String): Result<Name> {
            val v = input.trim()
            return if (REGEX.matches(v)) Result.success(Name(v))
            else Result.failure(IllegalArgumentException("El nombre debe tener solo letras (2-50) sin espacios"))
        }
    }
}

@JvmInline
value class LastName private constructor(val value: String) {
    companion object {
        // Apellidos compuestos separados por espacios, cada parte >=2 letras, total <=60
        private val REGEX = Regex("^([A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,})( [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]{2,})*$")
        fun create(input: String): Result<LastName> {
            val v = input.trim()
            return if (v.length <= 60 && REGEX.matches(v)) Result.success(LastName(v))
            else Result.failure(IllegalArgumentException("Apellido inválido (palabras de ≥2 letras, total máximo 60)"))
        }
    }
}
