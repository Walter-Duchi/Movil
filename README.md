# Preescolar Digital

Aplicación Android educativa diseñada para niños en etapa preescolar. Ofrece actividades interactivas de aprendizaje con audio, imágenes y retroalimentación visual para las áreas de colores, vocales, números, animales y rutinas diarias, con sistema de registro y autenticación de usuarios respaldado por SQLite.

---

## Tabla de contenidos

- [Descripción general](#descripción-general)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Requisitos previos](#requisitos-previos)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Módulos de la aplicación](#módulos-de-la-aplicación)
- [Arquitectura y estructura del proyecto](#arquitectura-y-estructura-del-proyecto)
- [Pruebas](#pruebas)
- [Permisos requeridos](#permisos-requeridos)
- [Preguntas frecuentes](#preguntas-frecuentes)
- [Autor](#autor)

---

## Descripción general

Preescolar Digital es una aplicación móvil nativa para Android que acompaña el aprendizaje de los primeros conceptos educativos de niños entre 3 y 6 años. Cada módulo está diseñado para ser autónomo, con instrucciones de audio, imágenes coloridas y respuesta inmediata a la interacción del niño, lo que convierte el aprendizaje en una experiencia sensorial y lúdica.

La aplicación incluye un sistema completo de registro e inicio de sesión con base de datos local SQLite, y soporte para modo oscuro y diseño responsivo adaptado a distintos tamaños de pantalla y orientaciones.

---

## Tecnologías utilizadas

### Android y SDK
| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 11 | Lenguaje principal de desarrollo |
| Android SDK | API 35 (Android 15) | Target SDK de la aplicación |
| Android minSdk | API 26 (Android 8.0) | Versión mínima soportada |
| Gradle | 8.11.1 | Sistema de construcción |
| Android Gradle Plugin | 8.9.1 | Plugin de build para Android |

### Librerías AndroidX
| Librería | Versión | Propósito |
|---|---|---|
| AppCompat | 1.7.0 | Compatibilidad hacia atrás de componentes UI |
| Material Design | 1.12.0 | Componentes visuales Material 3 |
| ConstraintLayout | 2.2.1 | Layout flexible y eficiente para UI compleja |
| Navigation Fragment | 2.8.9 | Navegación entre fragments con NavGraph |
| Navigation UI | 2.8.9 | Integración de navegación con ActionBar |
| Activity | 1.10.1 | Gestión moderna del ciclo de vida de actividades |

### Funcionalidades nativas de Android
| API | Propósito |
|---|---|
| `MediaPlayer` | Reproducción de sonidos para cada elemento educativo |
| `SQLiteOpenHelper` | Base de datos local para registro y autenticación de usuarios |
| `View Binding` | Acceso typesafe a las vistas sin `findViewById` |
| `Canvas` / `View` personalizado | Trazado de vocales con el dedo (`VocalTraceView`) |
| `Handler` / `Looper` | Temporización de eventos (reproducción secuencial, retroalimentación) |
| `ScaleAnimation` | Animaciones de pulsación en tarjetas interactivas |
| Edge-to-edge UI | Pantalla completa con soporte de insets del sistema |

### Testing
| Librería | Versión | Propósito |
|---|---|---|
| JUnit | 4.13.2 | Pruebas unitarias locales |
| Espresso | 3.6.1 | Pruebas de instrumentación en dispositivo |
| AndroidX Test JUnit | 1.2.1 | Runner de pruebas instrumentadas |

---

## Requisitos previos

- [Android Studio](https://developer.android.com/studio) Hedgehog (2023.1.1) o superior
- JDK 11 (incluido con Android Studio)
- Android SDK con API Level 26 o superior instalado
- Dispositivo Android físico con Android 8.0+ o emulador configurado (API 26+)

---

## Configuración y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/Walter-Duchi/Preescolar-Digital.git
```

### 2. Abrir el proyecto en Android Studio

Seleccionar `File → Open` y navegar hasta la carpeta `Preescolar-Digital`. Android Studio detectará automáticamente la estructura Gradle del proyecto.

### 3. Sincronizar dependencias

Android Studio mostrará una notificación para sincronizar Gradle. Hacer clic en **Sync Now**. Alternativamente, desde la terminal:

```bash
./gradlew build
```

En Windows:

```bash
gradlew.bat build
```

### 4. Ejecutar la aplicación

Conectar un dispositivo Android con depuración USB activada o iniciar un emulador desde el AVD Manager, luego hacer clic en el botón **Run** (▶) o usar el atajo `Shift + F10`.

La aplicación se instalará automáticamente y abrirá la pantalla de inicio de sesión.

### Compilar el APK manualmente

```bash
./gradlew assembleDebug
```

El APK generado estará en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Módulos de la aplicación

### Autenticación
Pantalla de inicio de sesión (`MainActivity`) y registro (`Registrar`) con validación de campos y persistencia local mediante SQLite. El `DatabaseHelper` gestiona la creación de la base de datos, el versionado del esquema y las operaciones CRUD sobre la tabla de usuarios.

### Pantalla principal (Home)
Dashboard central desde donde el niño (o su tutor) accede a todos los módulos de aprendizaje disponibles. Utiliza un ActionBar personalizado con menú de opciones.

### Colores
Doce tarjetas interactivas, una por color, con sonido al tocarlas, imagen representativa superpuesta como overlay y animación de escala. Incluye un botón "Reproducir Todos" que recorre todos los colores secuencialmente con pausas entre cada uno.

### Vocales
Actividad de reconocimiento y trazado de vocales. Incluye `VocalTraceView`, una vista personalizada que extiende `View` y permite al niño trazar la vocal con el dedo sobre el canvas, proporcionando práctica de escritura táctil.

### Números
Módulo de aprendizaje numérico con audio y representación visual para reforzar la asociación número-cantidad.

### Animales
Juego de reconocimiento de sonidos de animales: la aplicación reproduce el sonido de un animal y el niño debe identificar cuál es entre tres opciones de imagen. Cada ronda se baraja aleatoriamente con `Collections.shuffle()`. La retroalimentación positiva o negativa se muestra inmediatamente con color diferenciado y pasa automáticamente a la siguiente ronda tras un acierto.

### Rutinas
Módulo de rutinas diarias con visualización de actividades cotidianas para reforzar hábitos en niños preescolares. Incluye `Routine`, `Rutinas` y `UserSelectedRoutine` para gestión de rutinas personalizadas por usuario.

---

## Arquitectura y estructura del proyecto

```
Preescolar-Digital/
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/logintarea/
│       │   │   ├── MainActivity.java          # Login — actividad de entrada
│       │   │   ├── Registrar.java             # Registro de nuevos usuarios
│       │   │   ├── DatabaseHelper.java        # SQLiteOpenHelper — CRUD de usuarios
│       │   │   ├── Home.java                  # Dashboard principal
│       │   │   ├── Colores.java               # Módulo de colores con MediaPlayer y animaciones
│       │   │   ├── Vocales.java               # Módulo de vocales
│       │   │   ├── VocalTraceView.java        # Vista personalizada de trazado en Canvas
│       │   │   ├── Numeros.java               # Módulo de números
│       │   │   ├── Animales.java              # Juego de reconocimiento de sonidos
│       │   │   ├── Rutinas.java               # Lista de rutinas diarias
│       │   │   ├── Routine.java               # Modelo de datos de una rutina
│       │   │   └── UserSelectedRoutine.java   # Rutinas seleccionadas por usuario
│       │   ├── res/
│       │   │   ├── layout/                    # XML de cada actividad
│       │   │   ├── drawable/                  # Imágenes de animales, colores y fondos
│       │   │   ├── raw/                       # Archivos de audio (.mp3) por elemento
│       │   │   ├── navigation/nav_graph.xml   # Grafo de navegación
│       │   │   ├── values/                    # Strings, colores, dimensiones, estilos
│       │   │   ├── values-night/              # Tema modo oscuro
│       │   │   ├── values-land/               # Dimensiones para orientación horizontal
│       │   │   └── values-w600dp/             # Dimensiones para tablets
│       │   └── AndroidManifest.xml
│       ├── test/                              # Pruebas unitarias locales (JUnit)
│       └── androidTest/                       # Pruebas instrumentadas (Espresso)
├── gradle/
│   ├── libs.versions.toml                     # Catálogo de versiones centralizado
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts                           # Build script raíz
├── app/build.gradle.kts                       # Build script del módulo app
└── settings.gradle.kts                        # Configuración del proyecto
```

El proyecto usa **Version Catalog** (`libs.versions.toml`) para centralizar todas las versiones de dependencias en un único lugar, siguiendo las prácticas recomendadas de Gradle moderno.

---

## Pruebas

### Pruebas unitarias locales

```bash
./gradlew test
```

Ubicadas en `app/src/test/`. Se ejecutan en la JVM del equipo de desarrollo, sin necesidad de dispositivo.

### Pruebas instrumentadas (Espresso)

```bash
./gradlew connectedAndroidTest
```

Requieren un dispositivo o emulador conectado. Ubicadas en `app/src/androidTest/`. Verifican el comportamiento de la aplicación en el contexto real de Android, incluyendo la validación del `applicationId` del paquete instalado.

---

## Permisos requeridos

| Permiso | Motivo |
|---|---|
| `INTERNET` | Disponible para futuras integraciones con contenido en línea |
| `READ_EXTERNAL_STORAGE` | Lectura de recursos multimedia externos si se requiere |
| `WRITE_EXTERNAL_STORAGE` | Escritura de datos del usuario en almacenamiento externo |

---

## Preguntas frecuentes

**¿En qué versión de Android funciona la aplicación?**
La aplicación es compatible con Android 8.0 (API 26) en adelante. El target SDK es Android 15 (API 35), por lo que aprovecha las últimas optimizaciones del sistema sin sacrificar compatibilidad con dispositivos más antiguos.

**¿Los datos de usuario se guardan localmente o en la nube?**
Los datos se guardan en una base de datos SQLite local en el dispositivo mediante `DatabaseHelper`. No se requiere conexión a internet para registrarse ni para iniciar sesión. Si el usuario desinstala la app, los datos se eliminan junto con ella.

**¿Cómo funciona la vista de trazado de vocales (`VocalTraceView`)?**
`VocalTraceView` es una clase que extiende `View` y sobreescribe `onDraw()` para dibujar en un `Canvas`. Captura los eventos táctiles (`onTouchEvent`) y dibuja el trazo del dedo en tiempo real, permitiendo que el niño practique la forma de cada vocal de manera interactiva.

**¿Cómo se maneja el audio para evitar fugas de memoria?**
Cada actividad que usa `MediaPlayer` libera la instancia en `onStop()` y `onDestroy()`, y cancela cualquier `Runnable` pendiente en el `Handler` con `removeCallbacksAndMessages(null)`. Esto previene fugas de memoria y comportamiento inesperado al navegar entre pantallas.

**¿La app soporta modo oscuro?**
Sí. El proyecto incluye recursos diferenciados en `res/values-night/themes.xml` con una paleta adaptada para modo oscuro usando `Theme.Material3.DayNight.NoActionBar` como tema base.

**¿Funciona en tablets?**
Sí. El proyecto incluye recursos de dimensiones específicos para pantallas de 600dp (`values-w600dp`) y 1240dp (`values-w1240dp`) que ajustan márgenes y espaciados para ofrecer una experiencia visual adecuada en pantallas más grandes.

**¿Por qué se usa View Binding en lugar de `findViewById`?**
View Binding, habilitado en `build.gradle.kts` con `buildFeatures { viewBinding = true }`, genera una clase de binding typesafe por cada layout XML. Esto elimina los errores de casting en tiempo de ejecución y hace el código más legible y mantenible.

---

## Autor

**Walter Alejandro Duchi Rivera**

Desarrollador Full Stack con experiencia en React, .NET y arquitecturas orientadas a eventos con WebSockets.

- GitHub: [@WalterDuchi](https://github.com/Walter-Duchi)
- LinkedIn: [linkedin.com/in/walter-duchi](https://www.linkedin.com/in/walter-duchi/)
- Portafolio Profesional: [Walter Duchi](https://portafolio-theta-ten-87.vercel.app/)
- Correo: [waltduchi@gmail.com](mailto:waltduchi@gmail.com)
- WhatsApp: [+593 993 516 268](https://wa.me/593993516268)

---

*Proyecto desarrollado como parte del portafolio profesional.*
