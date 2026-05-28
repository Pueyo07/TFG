package com.example.tfgaplicacion.model

data class InfoAdiccion(
    val nombre: String,
    val tituloBeneficios: String,
    val beneficios: List<String>,
    val tituloEstrategias: String,
    val estrategias: List<String>,
    val consejosDiarios: List<String>
)

object InfoAdicciones {
    fun getInfo(nombre: String): InfoAdiccion {
        val nombreNormalizado = nombre.lowercase().replace("sociales", "").trim()
        return when (nombreNormalizado) {
            "nicotina" -> InfoAdiccion(
                nombre = "Nicotina",
                tituloBeneficios = "Beneficios de dejar de fumar",
                beneficios = listOf(
                    "• 20 min: Presión arterial y frecuencia cardíaca bajan",
                    "• 8 horas: Nivel de nicotina baja un 50%",
                    "• 24 horas: Monóxido de carbono sale del cuerpo",
                    "• 2 semanas: Circulación mejora significativamente",
                    "• 1 mes: Función pulmonar comienza a mejorar",
                    "• 1 año: Riesgo de enfermedad cardíaca baja 50%"
                ),
                tituloEstrategias = "Tips para manejar el craving",
                estrategias = listOf(
                    "• Bebe agua lentamente",
                    "• Mastica chicle sin azúcar",
                    "• Haz ejercicios de respiración",
                    "• Camina o haz alguna actividad física",
                    "• Evita situaciones que teíen ganas de fumar"
                ),
                consejosDiarios = listOf(
                    "Hoy es un buen día para empezar",
                    "Cada día sin fumar es una victoria",
                    "Tu cuerpo te lo agradecerá"
                )
            )
            "alcohol" -> InfoAdiccion(
                nombre = "Alcohol",
                tituloBeneficios = "Tu cuerpo se recupera",
                beneficios = listOf(
                    "• 1 día: Presión arterial baja",
                    "• 3 días: El cuerpo elimina toxinas",
                    "• 1 semana: Duermes mejor, más energía",
                    "• 2 semanas: Piel más saludable",
                    "• 1 mes: Función hepática mejora",
                    "• 3 meses: Energía y motivación aumentan"
                ),
                tituloEstrategias = "Estrategias para resistir",
                estrategias = listOf(
                    "• Evita lugares donde bebas",
                    "• Busca actividades alternativas",
                    "• Practica decir 'no' con confianza",
                    "• Contacta a tu grupo de apoyo",
                    "• Mantén las manos ocupadas"
                ),
                consejosDiarios = listOf(
                    "Un día a la vez, tú puedes",
                    "Hoy elegimos otra forma de vivir",
                    "Tu salud es lo más importante"
                )
            )
            "azúcar" -> InfoAdiccion(
                nombre = "Azúcar",
                tituloBeneficios = "Efectos positivos en tu cuerpo",
                beneficios = listOf(
                    "• 1 semana: Más energía estable",
                    "• 2 semanas: Piel más limpia y luminosa",
                    "• 1 mes: Peso más estable",
                    "• 2 meses: Dormirás mejor",
                    "• 3 meses: Sistema inmune más fuerte",
                    "• 6 meses: Salud dental mejora"
                ),
                tituloEstrategias = "Alternativas saludables",
                estrategias = listOf(
                    "• Come fruta fresca cuando quieras dulce",
                    "• Bebe agua con limón",
                    "• Come frutos secos",
                    "• Planea tus comidas",
                    "• Lee las etiquetas de los alimentos"
                ),
                consejosDiarios = listOf(
                    "Lo dulce puede esperar",
                    "Tu cuerpo merece lo mejor",
                    "Hoy elige naturales sabores"
                )
            )
            "videojuegos" -> InfoAdiccion(
                nombre = "Videojuegos",
                tituloBeneficios = "Recupera tu tiempo",
                beneficios = listOf(
                    "• Ganas 2-4 horas diarias",
                    "• Mejor concentración en trabajo/estudios",
                    "• Más tiempo para ejercicio físico",
                    "• Mejora el sueño",
                    "• Relaciones sociales más fuertes",
                    "• Más tiempo para hobbies productivos"
                ),
                tituloEstrategias = "Alternativas saludables",
                estrategias = listOf(
                    "• Lee un libro o manga",
                    "• Haz ejercicio o deporte",
                    "• Aprende una habilidad nueva",
                    "• Salir con amigos",
                    "• Practica un instrumento"
                ),
                consejosDiarios = listOf(
                    "El mundo real te espera",
                    "Hay vida más allá de la pantalla",
                    "Crea algo hoy"
                )
            )
            "redes sociales", "redes" -> InfoAdiccion(
                nombre = "Redes Sociales",
                tituloBeneficios = "Recupera tu mente",
                beneficios = listOf(
                    "• Menos ansiedad y estrés",
                    "• Más tiempo para relaciones reales",
                    "• Mejora la concentración",
                    "• Duermes mejor sin pantallas",
                    "• Mayor productividad",
                    "• Más tiempo para ti mismo"
                ),
                tituloEstrategias = "Cómo desconectarte",
                estrategias = listOf(
                    "• Configura límites de tiempo en apps",
                    "• Elimina redes de la pantalla de inicio",
                    "• Apaga notificaciones",
                    "• Establece horarios sin móvil",
                    "• Busca hobbies offline"
                ),
                consejosDiarios = listOf(
                    "Desconecta para reconectar",
                    "Lo que ves no es la realidad",
                    "Vive el momento"
                )
            )
            "internet" -> InfoAdiccion(
                nombre = "Internet",
                tituloBeneficios = "Libérate de la screens",
                beneficios = listOf(
                    "• Más tiempo productivo",
                    "• Mejor saludvisual",
                    "• Sueño de mejor calidad",
                    "• Relaciones más profundas",
                    "• Menos distracciones",
                    "• Mayor enfoque"
                ),
                tituloEstrategias = "Hábitos digitales saludables",
                estrategias = listOf(
                    "• Establece horarios de uso",
                    "• Crea zonas sin tecnología",
                    "• Usa apps de control de tiempo",
                    "• Practica el método Pomodoro",
                    "• Desconecta 1 hora antes de dormir"
                ),
                consejosDiarios = listOf(
                    "La red puede esperar",
                    "Tu mente necesita descanso digital",
                    "Hoy priorízate a ti"
                )
            )
            "compras" -> InfoAdiccion(
                nombre = "Compras",
                tituloBeneficios = "Recupera tu estabilidad",
                beneficios = listOf(
                    "• Ahorro significativo de dinero",
                    "• Menos estrés financiero",
                    "• Más espacio en casa",
                    "• Decisiones más racionales",
                    "• Enfoque en lo esencial",
                    "• Mejor salud mental"
                ),
                tituloEstrategias = "Estrategias de control",
                estrategias = listOf(
                    "• Haz una lista y síguela",
                    "• Espera 24h antes de comprar",
                    "• Evita comprar cuando emociones",
                    "•Limita el acceso a tarjetas",
                    "• Busca actividades gratuitas"
                ),
                consejosDiarios = listOf(
                    "No necesitas más cosas",
                    "Lo que tienes es suficiente",
                    "Ahorra para lo que importa"
                )
            )
            "trabajo" -> InfoAdiccion(
                nombre = "Trabajo",
                tituloBeneficios = "Equilibra tu vida",
                beneficios = listOf(
                    "• Más tiempo para ti y familia",
                    "• Menor riesgo de burnout",
                    "• Mejor salud física y mental",
                    "• Relaciones más fuertes",
                    "• Qualidade de vida improves",
                    "• Creativity increases"
                ),
                tituloEstrategias = "Establecer límites",
                estrategias = listOf(
                    "• Define horarios claros de trabajo",
                    "• Aprende a decir 'no'",
                    "• Toma descansos regulares",
                    "• Delega tareas cuando sea posible",
                    "• Separa trabajo de vida personal"
                ),
                consejosDiarios = listOf(
                    "Trabaja para vivir, no al revés",
                    "Tu descanso es productivo",
                    "Prioriza tu bienestar"
                )
            )
            else -> InfoAdiccion(
                nombre = nombre,
                tituloBeneficios = "Beneficios de superar esta adicción",
                beneficios = listOf(
                    "• Más energía y motivación",
                    "• Mejor salud física",
                    "• Mejor salud mental",
                    "• Relaciones más fuertes",
                    "• Más tiempo libre",
                    "• Mayor autoconfianza"
                ),
                tituloEstrategias = "Consejos para continuar",
                estrategias = listOf(
                    "• Mantente ocupado",
                    "• Busca apoyo en otros",
                    "• Celebra cada pequeño logro",
                    "• Aprende de las recaídas",
                    "• No te rindas"
                ),
                consejosDiarios = listOf(
                    "Hoy es un buen día",
                    "Tú puedes con esto",
                    "Un día a la vez"
                )
            )
        }
    }
}