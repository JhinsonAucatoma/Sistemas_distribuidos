1. Ante una interrupción de comunicación entre dos nodos, ¿qué propiedad del teorema CAP privilegia su implementación y por qué? Justifíquelo con el comportamiento observado en su prototipo.
RESPUESTA: Ante una interrupción entre dos nodos, la implementación privilegia la disponibilidad (A) sobre la consistencia (C). Esto se puede ver  en el ServidorTCP: si un nodo se desconecta, los demás no se detienen esperando que vuelva, sino que siguen procesando operaciones normalmente. El sistema prefiere continuar respondiendo aunque en ese momento no todos los nodos tengan exactamente la misma información.
   
2. ¿Qué falacias de la computación distribuida tuvo que considerar al delimitar los mensajes y al definir los tiempos de espera de los latidos?
RESPUESTA: La primera, la red no es confiable. Por eso cada operación de lectura y escritura está envuelta en un bloque try-catch de IOException, y nunca se asume que el mensaje siempre llega. La segunda: la latencia no es cero. Por eso el timeout del heartbeat es un valor  definido, en lugar de esperar indefinidamente a que un nodo responda.

3. ¿Qué tipos de transparencia (ubicación, acceso, fallos, replicación) ofrece o no ofrece su solución? Argumente cada caso.
RESPUESTA: El de acceso	ya que el cliente usa siempre el mismo socket TCP sin importar a qué nodo se conecte.

4. Proponga un acuerdo de nivel de servicio (SLA) de disponibilidad para este sistema y calcule el tiempo de inactividad anual admisible que implicaría.


5. Si reemplazara el algoritmo Bully por un consenso tipo Raft, ¿qué ganaría y qué costo introduciría?
RESPUESTA: Reemplazar el algoritmo Bully por Raft permitiría tener un mecanismo de consenso distribuido más completo, ya que, a diferencia de Bully, que únicamente se encarga de elegir un líder, Raft se asegura de que los nodos alcancen un acuerdo sobre el estado del sistema antes de confirmar cada operación. Sin embargo, estas ventajas implican un incremento en la complejidad de implementación, ya que este tendrá que gestionar registros replicados, mensajes AppendEntries y procesos de votación por quórum. Además, la latencia de las operaciones aumenta, ya que cada cambio debe ser validado por la mayoría de los nodos antes de ser confirmado.
