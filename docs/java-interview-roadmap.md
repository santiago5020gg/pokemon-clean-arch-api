# Ruta de aprendizaje — Entrevistas Java (Core → Spring Boot)

> Guía de estudio progresiva para preparar entrevistas técnicas de Java, desde
> los fundamentos del lenguaje hasta Spring Boot. Pensada para recorrerse de
> arriba hacia abajo: cada nivel asume que dominas el anterior.
>
> **Cómo usarla:** marca cada casilla `[ ]` a medida que puedas *explicar el tema
> en voz alta y escribir un ejemplo de memoria*. No basta con "me suena": el
> entrevistador va a repreguntar el *por qué*.

---

## Cómo estudiar (método)

1. **Lee el concepto** → **escribe un ejemplo mínimo** → **explícalo en voz alta** como si tuvieras al entrevistador enfrente.
2. Por cada tema pregúntate siempre: *¿por qué existe?*, *¿qué problema resuelve?*, *¿cuál es la alternativa y su trade-off?*.
3. Prioriza los temas marcados con ⭐ (los que **casi siempre** caen).
4. Cierra cada bloque resolviendo 2–3 preguntas de la sección "Preguntas trampa".

**Ritmo sugerido (4 semanas):**

| Semana | Foco |
|--------|------|
| 1 | Nivel 0 y 1 — Java Core, POO, colecciones, excepciones |
| 2 | Nivel 2 — Java 8+, concurrencia, JVM/memoria, genéricos |
| 3 | Nivel 3 — Spring Core, Spring Boot, REST, JPA |
| 4 | Nivel 4 — Testing, diseño/SOLID, BD + Nivel 5 (Performance) + repaso de "los 10 clásicos" |

---

## Nivel 0 — Fundamentos del lenguaje

### Tipos y variables
- [ ] Primitivos vs. wrappers (`int` vs `Integer`), autoboxing/unboxing
- [ ] ⭐ Caché de `Integer` (-128 a 127) y por qué `==` falla con objetos
- [ ] ⭐ `String` inmutable, *String pool*, `intern()`, `StringBuilder` vs `StringBuffer`
- [ ] ⭐ Java **siempre** es paso por valor (incluso con referencias)

### POO (base de casi todo)
- [ ] 4 pilares: encapsulación, herencia, polimorfismo, abstracción
- [ ] Sobrecarga (overload) vs. sobreescritura (override)
- [ ] ⭐ `abstract class` vs `interface` — cuándo usar cada una
- [ ] `interface` con `default` / `static` / `private` methods (Java 8+)
- [ ] `final` en clases, métodos y variables

### `equals()` y `hashCode()`
- [ ] ⭐ Contrato entre ambos (si sobrescribes uno, sobrescribes el otro)
- [ ] Impacto en `HashMap` / `HashSet`
- [ ] Propiedades de `equals()`: reflexivo, simétrico, transitivo, consistente

### Modificadores y palabras clave
- [ ] `public` / `private` / `protected` / *default* (visibilidad)
- [ ] `static` (variables, métodos, bloques, nested classes)
- [ ] `this` vs `super`
- [ ] `transient`, `volatile`, `synchronized`

**Preguntas trampa**
- ¿`new Integer(127) == new Integer(127)`? ¿Y `Integer a = 127; Integer b = 127; a == b`?
- ¿Puede una interface tener estado? ¿Por qué (no)?
- Si cambio un objeto dentro de un método, ¿se refleja fuera? ¿Y si lo reasigno?

---

## Nivel 1 — Colecciones y excepciones

### Collections Framework
- [ ] Jerarquía: `List`, `Set`, `Map`, `Queue`, `Deque`
- [ ] `Collection` (interface) vs `Collections` (utility class)
- [ ] ⭐ `ArrayList` vs `LinkedList` (acceso vs inserción/borrado)
- [ ] ⭐ `HashMap` internamente: buckets, colisiones, *treeify* (Java 8), factor de carga
- [ ] `HashMap` vs `LinkedHashMap` vs `TreeMap`
- [ ] `HashSet` vs `TreeSet` vs `LinkedHashSet`
- [ ] `Hashtable` vs `HashMap` vs `ConcurrentHashMap` (thread-safety)
- [ ] `Comparable` vs `Comparator`
- [ ] *fail-fast* vs *fail-safe*, `ConcurrentModificationException`
- [ ] `Iterator` vs `ListIterator`

### Manejo de excepciones
- [ ] Jerarquía: `Throwable` → `Error` / `Exception`
- [ ] ⭐ Checked vs unchecked exceptions
- [ ] `try-catch-finally` — orden de ejecución, `finally` con `return`
- [ ] `try-with-resources` y `AutoCloseable`
- [ ] `throw` vs `throws`
- [ ] Excepciones personalizadas y multi-catch

**Preguntas trampa**
- ¿Qué pasa si `finally` tiene un `return` y el `try` también?
- ¿Por qué `HashMap` no es thread-safe? ¿Qué falla exactamente en concurrencia?
- ¿Cuándo elegirías una checked exception sobre una unchecked?

---

## Nivel 2 — Java moderno, concurrencia y JVM

### Java 8+ (funcional y streams) ⭐
- [ ] Interfaces funcionales: `Function`, `Consumer`, `Supplier`, `Predicate`, `BiFunction`
- [ ] `@FunctionalInterface` y method references (`Clase::metodo`)
- [ ] Streams: operaciones intermedias vs terminales (*lazy evaluation*)
- [ ] `map`, `filter`, `reduce`, `collect`, `flatMap`
- [ ] `Collectors` (`groupingBy`, `toMap`, `joining`, `partitioningBy`)
- [ ] `Optional`: `orElse` vs `orElseGet`, antipatrones
- [ ] `java.time` (`LocalDate`, `LocalDateTime`, `Duration`)

### Concurrencia y multithreading
- [ ] `Thread` vs `Runnable` vs `Callable`
- [ ] Ciclo de vida de un hilo
- [ ] `synchronized` (método vs bloque), `wait()` / `notify()` / `notifyAll()`
- [ ] ⭐ `volatile` y visibilidad de memoria (Java Memory Model)
- [ ] `ExecutorService`, thread pools, `Future`, `CompletableFuture`
- [ ] `ConcurrentHashMap`, `CountDownLatch`, `Semaphore`, `AtomicInteger`
- [ ] Deadlock, race condition, starvation
- [ ] `ThreadLocal`

### JVM y gestión de memoria
- [ ] Áreas de memoria: Heap, Stack, Metaspace, Program Counter
- [ ] Garbage Collection: generaciones (young/old), GC roots
- [ ] Tipos de GC (Serial, Parallel, G1, ZGC)
- [ ] `StackOverflowError` vs `OutOfMemoryError`
- [ ] Referencias: strong, weak, soft, phantom
- [ ] JDK vs JRE vs JVM, class loading

### Genéricos
- [ ] Type erasure
- [ ] Wildcards: `? extends` vs `? super` (regla **PECS**)
- [ ] Bounded type parameters

**Preguntas trampa**
- ¿`orElse` vs `orElseGet`? ¿Cuándo importa la diferencia?
- ¿Por qué `volatile` no basta para `i++`?
- ¿Qué es *type erasure* y qué limitación causa en runtime?

---

## Nivel 3 — Spring y Spring Boot ⭐

### Spring Core — IoC y DI
- [ ] ⭐ Inversión de control: qué problema resuelve
- [ ] ⭐ Tipos de inyección (constructor vs setter vs field) y por qué **constructor** es la recomendada
- [ ] `@Component`, `@Service`, `@Repository`, `@Controller`
- [ ] `@Autowired`, `@Qualifier`, `@Primary`
- [ ] `ApplicationContext` vs `BeanFactory`

### Beans
- [ ] ⭐ Scopes: `singleton`, `prototype`, `request`, `session`
- [ ] Ciclo de vida (`@PostConstruct`, `@PreDestroy`)
- [ ] `@Bean` vs `@Component`
- [ ] `@Configuration` y proxy CGLIB
- [ ] Dependencias circulares

### AOP
- [ ] Conceptos: aspect, advice, pointcut, join point
- [ ] `@Before`, `@After`, `@Around`
- [ ] Casos de uso: logging, transacciones, seguridad

### Spring Boot — fundamentos
- [ ] ⭐ Qué aporta sobre Spring (autoconfiguración, starters, servidor embebido)
- [ ] `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`
- [ ] Cómo funciona la autoconfiguración (`@Conditional`, `spring.factories`)
- [ ] `application.properties` vs `.yml`, perfiles (`@Profile`)
- [ ] `@Value` vs `@ConfigurationProperties`

### Spring Web / REST
- [ ] `@RestController` vs `@Controller`
- [ ] `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`
- [ ] `@PathVariable`, `@RequestParam`, `@RequestBody`
- [ ] `ResponseEntity` y códigos HTTP
- [ ] ⭐ Manejo global de errores: `@ControllerAdvice`, `@ExceptionHandler`
- [ ] Validación: `@Valid`, Bean Validation (`@NotNull`, `@Size`)

### Spring Data JPA
- [ ] `JpaRepository`, `CrudRepository`, query methods derivados
- [ ] `@Query` (JPQL y nativas)
- [ ] Relaciones: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- [ ] ⭐ Fetch LAZY vs EAGER, problema **N+1** y soluciones
- [ ] ⭐ `@Transactional` (propagación, aislamiento, `readOnly`, rollback)
- [ ] Persistence context: `persist`, `merge`, estado *detached*
- [ ] Optimistic vs pessimistic locking (`@Version`)

### Spring Security (si aplica al puesto)
- [ ] Autenticación vs autorización
- [ ] `SecurityFilterChain`, cadena de filtros
- [ ] JWT: flujo, validación, *stateless*
- [ ] `BCrypt`

**Preguntas trampa**
- ¿Por qué inyección por constructor y no por field?
- ¿Qué hace realmente `@Transactional`? ¿Funciona si llamas al método desde la misma clase?
- ¿Cómo se produce el N+1 y cómo lo resuelves (fetch join, `@EntityGraph`)?

> 💡 Este repo es un buen banco de prácticas: revisa cómo se aplican estos temas
> en `backend/` (hexagonal, aggregate services, JPA, Flyway, JWT). Ver `CLAUDE.md`
> y `docs/api_doc.md`.

---

## Nivel 4 — Testing, diseño y base de datos

### Testing
- [ ] JUnit 5 (`@Test`, `@BeforeEach`, ciclo de vida)
- [ ] Mockito (`@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`)
- [ ] `@SpringBootTest` vs `@WebMvcTest` vs `@DataJpaTest`
- [ ] `MockMvc`
- [ ] Testcontainers (integración con BD real)
- [ ] TDD (Red → Green → Refactor)

### Diseño y arquitectura
- [ ] ⭐ Principios **SOLID** (uno por uno, con ejemplo)
- [ ] Patrones: Singleton, Factory, Builder, Strategy, Observer, Proxy
- [ ] Arquitectura hexagonal / por capas / clean architecture
- [ ] REST: idempotencia, verbos HTTP, códigos de estado, versionado
- [ ] Inmutabilidad y `record` (Java 14+)

### Base de datos
- [ ] SQL: JOINs, índices, `GROUP BY`, subconsultas
- [ ] Transacciones ACID
- [ ] Niveles de aislamiento (read committed, repeatable read, serializable)
- [ ] Connection pooling (HikariCP)

**Preguntas trampa**
- Explica el principio de **inversión de dependencias** con un ejemplo de este repo.
- ¿Diferencia entre `@Mock` y `@Spy`?
- ¿Qué problema de concurrencia previene *repeatable read* que *read committed* no?

---

## Nivel 5 — Performance, profiling y observabilidad

> Tema transversal muy preguntado en roles semi-senior/senior. Responde a:
> *"la app está lenta o consume mucha memoria, ¿cómo lo diagnosticas y lo
> resuelves?"*. La clave no es memorizar herramientas, sino demostrar un
> **método**: medir antes de tocar código.

### Performance de la JVM
- [ ] Garbage Collection: pausas de GC, *GC logs*, elección de colector (G1, ZGC)
- [ ] Tamaño de heap (`-Xmx`, `-Xms`) y su impacto
- [ ] ⭐ **Memory leaks** en Java: cómo se producen (`static` que crece, referencias no liberadas, listeners no removidos)
- [ ] Herramientas: `jstack`, `jmap` / heap dump, `jstat`, **VisualVM**, **async-profiler**, **Java Flight Recorder (JFR)**
- [ ] Clasificar el problema: ¿CPU-bound, memory-bound o I/O-bound?

### Concurrencia como causa de lentitud
- [ ] **Deadlocks** y *thread contention* (hilos bloqueados esperando locks)
- [ ] Thread pools mal dimensionados (se agotan → peticiones encoladas)
- [ ] ⭐ Cómo leer un **thread dump** para ver dónde se atascan los hilos

### Base de datos (la causa #1 real en apps web) ⭐
- [ ] ⭐ Problema **N+1** (ver también Nivel 3)
- [ ] Queries lentas: `EXPLAIN` / plan de ejecución, **índices** faltantes
- [ ] **Connection pool** agotado (HikariCP): síntomas y tuning
- [ ] Paginación vs traer todo; proyecciones/DTOs en vez de entidades completas
- [ ] Fetch LAZY vs EAGER mal usado

### A nivel de aplicación / Spring
- [ ] **Caching** (Caffeine, Redis) para reducir llamadas repetidas
- [ ] Latencia de llamadas externas: timeouts, reintentos, circuit breakers
- [ ] Serialización costosa, DTOs vs entidades

### Observabilidad — cómo lo detectas en producción
- [ ] **Métricas**: Micrometer + **Prometheus** + **Grafana**, Spring Boot **Actuator**
- [ ] Logs estructurados y niveles
- [ ] **Tracing distribuido** (Zipkin / OpenTelemetry): dónde se va el tiempo en una petición
- [ ] **APM**: New Relic, Datadog, Dynatrace
- [ ] ⭐ Percentiles de latencia (p95, p99), no solo el promedio

### Metodología de diagnóstico (lo que buscan que digas) ⭐
1. **Medir primero, no adivinar** — reproducir y observar métricas
2. Localizar el cuello de botella (¿DB? ¿CPU? ¿red? ¿GC?)
3. Aislar con profiling / logs / tracing
4. Corregir **una** cosa y volver a medir
5. Evitar la **optimización prematura**

**Preguntas trampa**
- La app va lenta en producción, ¿cuál es tu primer paso? (respuesta: **medir**, no tocar código)
- ¿Cómo detectas un memory leak sin reiniciar el servidor?
- ¿Cuál suele ser el cuello de botella más común en una app web con Spring + JPA? ¿Por qué?
- ¿Por qué mirar el p99 y no solo el promedio de latencia?

> 💡 Frase que suelen querer escuchar: *"no optimizo a ciegas; primero mido con
> herramientas (Actuator, profiler, `EXPLAIN` de la query, thread dump) para
> encontrar el cuello de botella real, y en una app web lo más común es la capa
> de base de datos — N+1, índices o el connection pool."*

> Este repo ya aplica varias de estas ideas: la **caché Caffeine** sobre PokeAPI
> (justificada por el patrón N+1) y **Actuator** en `/actuator/health`. Ver `CLAUDE.md`.

---

## Los 10 clásicos que casi siempre caen ⭐

Si te queda poco tiempo, asegura estos:

1. Diferencia entre `==` y `equals()`
2. Contrato `equals()` / `hashCode()`
3. `ArrayList` vs `LinkedList` / cómo funciona `HashMap` internamente
4. Checked vs unchecked exceptions
5. `interface` vs `abstract class`
6. Cómo funciona `String` (inmutabilidad y pool)
7. Tipos de inyección de dependencias y cuál preferir
8. Scopes de beans y ciclo de vida
9. Problema N+1 en JPA y cómo resolverlo
10. `@Transactional`: cómo funciona y sus atributos

---

## Recursos de práctica

- **Este repositorio** — código real de Spring Boot + hexagonal + JPA + JWT + tests.
- Practica escribiendo streams y colecciones en un scratch file y ejecutándolos.
- Simula la entrevista en voz alta: explicar > memorizar.

---

_Ruta viva: ve marcando las casillas y añade tus propias notas / preguntas que te
hayan hecho en entrevistas reales debajo de cada bloque._
