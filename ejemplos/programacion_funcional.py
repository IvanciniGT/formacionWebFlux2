def saluda(nombre):
    print("Hola " + nombre)

saluda("Menchu")
saluda("Federico")      # Statement

    #  -> BIEN
    #  <- MAL
nombre = "Menchu"       # Statement -> Sentencia (Oración, frase)
    # Asignar la variable nombre al valor "Menchu"

    # El concepto de variable cambia de lenguaje de programación a lenguaje de programación. En algunos lenguajes, como Python, las variables son simplemente etiquetas que apuntan a objetos en memoria. En otros lenguajes, como C o Java, las variables pueden ser más estrictas y tener tipos de datos específicos.
    # En C, Fortran, ADA una variable es como una cajita que puede contener un valor específico, y ese valor puede cambiar a lo largo del tiempo. En Python, una variable es más como una etiqueta que apunta a un objeto en memoria, y ese objeto puede ser de cualquier tipo de datos.
    # En JAVA, JS, TS, PYTHON, una variable es otra cosa.. tiene que ver más con el concepto de puntero en C.
    # Una variable es una referenca a un dato en RAM (* NOTA en JAVA esto aplica a objetos no a primitivos)

    # "Menchu"     Crea un objeto de tipo String porque en Python (o en JAVA) los datos entre comillas se interpretan como String (Sintaxis del lenguaje).
    #              En RAM, con valor "Menchu". La RAM Es como un cuaderno de cuadrícula
    #              En algun sitio hemos escrito Menchu... NPI de donde.
    # nombre       Crea una variable llamada nombre.
    #              Una variable en PY(o Java) es como un postit.
    #              En el postit escribo "nombre"
    #               Python solo tiene postits de un color. Y los podemos pegar en cualquier sitio del cuaderno (RAM).
    #               En JAVA tenemos postits de colores (tipos de datos) y solo podemos pegar un postit al lado de un dato del mismo tipo.
    # =            Pega el posit "nombre" al lado del dato "Menchu" en el cuaderno (RAM).
    # La variable referencia/apunta al dato

nombre = "Felipe"       
    # Crear en RAM un nuevo dato con valor "Federico"
    # Dónde? En el mismo sitio que "Menchu" o en otro sitio? En otro sitio.
    # En este momento tengo en RAM 2 objetos de tipo String, uno con valor "Menchu" y otro con valor "Federico".
    # Luego muevo el postit (lo vario.. poruque es variable) para que apunte al nuevo dato "Federico"
    # Y el dato "Menchu" se queda sin postit, sin referencia, sin etiqueta...
    # Se queda huerfano de variable. En JAVA o Python ese dato es irrecuperable. 
    # En C si podría, ya que uedo conocer la dirección de memoria (en qué cuadrito de que página) del cuaderno
    # está ocupada por el dato "Menchu"
    # Se acaba de convertir en basura (GARBAGE) y quizás (o quizás no.. npi) entre el 
    # proceso de recolección de basura (GARBAGE COLLECTION) el dato "Menchu" sea eliminado de la RAM para liberar espacio.

# String nombre = "Menchu";
saluda(nombre)

variable = saluda # La función es un dato más que está en RAM
    # Ahora tengo una variable que apunta a una función.
variable("Margarita")
    # Ejecuto la función a través de la variable.


def generar_saludo_formal(nombre):
    return "Buenos días " + nombre

def generar_saludo_informal(nombre):
    return "Hola " + nombre

def imprimir_saludo(nombre, funcion_generadora_de_saludos): # En esta, la lógica de composición de saludos se entrega como parametro.
                                                            # Inyecto en tiempo de ejecución lógica a la función.
    saludo = funcion_generadora_de_saludos(nombre)
    print(saludo)

def saluda2(nombre): # En esta, la lógica de composición de saludos está dentro de la función. HARDCODEADA
    saludo = "Hola " + nombre
    print(saludo)

imprimir_saludo("Merlin", generar_saludo_formal)
imprimir_saludo("Arturo", generar_saludo_informal)
