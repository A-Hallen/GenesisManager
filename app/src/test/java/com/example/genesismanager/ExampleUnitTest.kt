package com.example.genesismanager

import org.junit.Test
import java.time.LocalDate
import java.util.Date

data class Person(
    val bornDate: Date? = null,
    val genre: Boolean,
    val edad: Int
)

open class Empleado(
    val id: Long = 0,
    val nombre: String = "",
    val cargo: String = "",
    val salario: Long = 0,
    val fecha: Date? = null,
    val edad: Int = 0,
    val sexo: String = ""
)

class DepartamentoComercial(venta: Long, ciudad: String): Empleado() {

}

class Gerencia(pais: String): Empleado() {

}


class ExampleUnitTest {

    @Test
    fun technicalTest(){
        val id = 97091918500
        val date = getBornDate(id)!!
        val edad = obtenerEdad(date)
        val sexo = if (isManOrNot(id)) "Masculino" else "Femenino"

        println("La fecha es: $date")
        println("La edad es: $edad")
        println("El sexo es: $sexo")
    }

    @Test
    fun getEmpleadoTest(){
        val empleado = getEmpleado()
        println("Empleado: $empleado")
    }

    fun getEmpleado(): Empleado {
        val id = 97091918500
        val date = getBornDate(id)!!
        val edad = obtenerEdad(date)
        val sexo = if (isManOrNot(id)) "Masculino" else "Femenino"

        return Empleado(
            id = 34547624768,
            nombre = "Susan",
            cargo = "Jefa de Ventas",
            salario = 3000,
            fecha = date,
            edad = edad,
            sexo = sexo
        )
    }

    fun getTotalSalary(salario: Long, bonos: Int = 0): Long {
        return salario + bonos
    }

    @Test
    fun littleTest(){
        val nacimiento = java.sql.Date(1997, 9, 19)
        val edad = obtenerEdad(nacimiento)
        println("edad: $edad")
    }

    private fun obtenerEdad(date: Date): Int{
        return LocalDate.now().year - date.year
    }

    private fun isManOrNot(id: Long): Boolean {
        val digitTen = id.toString()[10].digitToInt()
        return  digitTen % 2 == 1
    }

    private fun getBornDate(id: Long): Date? {
        if (id.toString().length != 11) return dateErrorMessage()

        val carnet = id.toString()

        val year = carnet.slice(0..1).toInt()
        val month = carnet.slice(2..3).toInt()
        val day = carnet.slice(4..5).toInt()

        if (month > 12 || month < 0) return dateErrorMessage()
        if (day < 0 || day > 31) return dateErrorMessage()

        return java.sql.Date(year, month, day)
    }

    private fun dateErrorMessage(): Date? {
        println("Error: Número de identidad incorrecto")
        return null
    }


}