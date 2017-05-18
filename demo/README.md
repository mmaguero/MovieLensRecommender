# Manual de usuario
El programa se ejecutará en línea de comandos. Para obtener las puntuaciones se lo puede hacer
introduciendo las valoraciones para cada película, o hacerlo aleatoriamente (0, manual, 1,
automática). Cabe recordar que las películas se presentan de forma aleatoria. También se puede
modificar para el sistema de recomendación sobre Mahout, la función a utilizar siendo los valores
posibles lo siguientes: PEARSON, COSINE, SPEARMAN, EUCLIDEAN, TANIMOTO,
LIKELIHOOD. Por ejemplo, se puede ingresar lo siguiente:

+ Para no solicitar valorar películas y calcular con Mahout con la función de similaridad de Coseno:
```
java -jar runnable.jar 1 COSINE
```
+ Para valorarlas y calcular con Mahout con la función de similaridad de Correlación de Pearson:
```
java -jar runnable.jar 0 PEARSON
```
Tan solo hay que ejecutar el .jar e introducir las valoraciones, el programa devolverá los resultados
para ambos sistemas de recomendación, de acuerdo a lo ingresado.
La estructura de programa es con el .jar y en la misma altura, es necesario tener los ficheros u.data y
u.item en el directorio ml-data para que el programa los pueda leer.
Si se desea cambiar cualquiera de los otros parámetros (número de valoraciones de usuario activo,
número de vecinos más cercanos, número de recomendaciones, puntuación base para recomendar,
activar estadísticas de Mahout) hay que cambiar necesariamente
el código y recompilar
