
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch

#### Javier Esteban López
#### Miguel Ángel Fúquene

### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  
## Inicio
Para iniciar este laboratorio debemos clonar el laboratorio en nuestro repositorio local usando git con el siguiente comando
```bash
git clone https://github.com/ARSW-ECI-beta/PARALLELISM-JAVA_THREADS-INTRODUCTION_BLACKLISTSEARCH.git
```
Para luego abrir nuestro laboratorio desde la ruta correcta

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/3.PNG)

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
	- Se extendio Thread de la siguiente manera
	![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/1.png)
	
2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
		- Se realizo de la siguiente manera
		
		![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/2.PNG)
		
	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
		- Para ejecución se utilizo este comando de la ruta predeterminada
		```bash
		mvn exec:java -Dexec.mainClass="edu.eci.arsw.threads.CountThreadsMain"
		```
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
		- run(): Run ejecuta secuencialmente los threads, es decir, hace el primero y hasta no acabar no hace el siguiente. La salida cambia ejecutandolos en orden
		
		![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/run.PNG)
		
		- start(): Start ejecuta los thread a la vez, por lo que, no hay un orden a la hora de ver la salida. La salida cambia así
		
		![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/start.PNG)

**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

Como se puede ver en la siguiente imagen, se creo la clase ThreadHostBlackLists de tipo thread con el objetivo de hacer la refactorizacion del codigo y lograr asi que dicha clase tenga la funcion de hacer la busqueda de la ip en la blacklist teniendo como base dos rangos de la black list.
	
	
![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/ClaseThread.png)

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/HostBlack.png)


	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.

*Ip no: 202.24.34.55*

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/PrimerIP.png)
	

*Ip no: 212.24.24.55*

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/SegundaIp.png)


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.

**1 hilo**

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/1Hilo.png)


2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).

**8 hilos**

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/8Hilos.png)


3. Tantos hilos como el doble de núcleos de procesamiento.

**16 hilos**

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/16Hilos.png)


4. 50 hilos.


**50 Hilos**


![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/50Hilos.png)


5. 100 hilos.

**100 Hilos**

![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/100Hilos.png)



Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):



1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 
	
	-El procesador  puede tener varios hilos corriendo pero no puede tener demasiados, basicamente estos quedan en una cola o se pueden intercalar con otros, por lo cual, tener ejecutando 200 hilos puede ser mas eficiente que tener 500 hilos en ejecucion, debido al poder de computo puede no tener tanta capacidad para procesarlos a la vez.
	
	- Esto depende de P, esto depende de los hilos que puede paralelizar a la vez. Por lo tanto habrá un limite y cuando vayan mas hilos de los que se puede paralelizar los pondra en cola teniendo un peor desempeño, esto podría significar que entre 200 y 500 se encuentra este numero y al realizar 500 hilos esta dejando en cola
	- Un ejemplo usando nuestra implementación para entender esto, se enseña así. Primero hicimos una prueba con 200 hilos y vemos que el tiempo es un poco mas de dos segundos, luego teoricamente con mas hilos que pusimos 80000 debe ser mas rapido, pero por la razón ya explicada vemos que tarda mucho mas de lo que con menos hilos tarda
	
	200 Hilos - 2.078 segundos
	
	![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/200HilosTest.png)
	
	80000 Hilos - 11.592 segundos
	
	![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/80000HilosTest.png)
	
2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.


![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/VisualVm8.png)


![](https://github.com/MiguelFuquene1024/ARSW-Lab1/blob/main/img/readme/VisualVm16.png)


- Como se ve en las anteriores capturas, para el caso de 8 hilos el programa demoro entre 16 a 18 segundos para realizar el procesamiento de todo el programa, mientras que el de 16 hilos le tomo apenas entre 8 a 10 segundos, adicionalmente se puede evidenciar que el tiempo de vida de los hilos de la prueba de 8 es menor que en la de 16.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

- Como se mencionó anteriormente, se puede afirmar que no habria una mejora o un mayor desempeño al momento de ejecutar el programa, por el contrario se estarian consumiendo muchos mas recursos fisicos y no necesariamente esto seria mejor.

