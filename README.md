# Random Fitness Challenge 🏋️‍♀️

Una aplicación JavaFX que te ayuda a mantenerte activo con retos de fitness aleatorios y personalizados.

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![SQLite](https://img.shields.io/badge/SQLite-3.42.0-green)
![Maven](https://img.shields.io/badge/Maven-Build-red)

## 📋 Descripción

Random Fitness Challenge es una aplicación de escritorio diseñada para motivarte a realizar ejercicios de forma regular mediante retos aleatorios. La aplicación presenta diferentes tipos de ejercicios organizados por categorías y niveles de dificultad, con un sistema de seguimiento de progreso y estadísticas personales.

## ✨ Características Principales

- **🎲 Retos Aleatorios**: Sistema inteligente que presenta retos sin repetición hasta completar todos los disponibles
- **⏱️ Temporizador Integrado**: Cronómetro para cada reto con opción de completar antes de tiempo
- **📊 Estadísticas Detalladas**: Seguimiento de progreso, retos completados, tiempo total de ejercicio
- **🎯 Categorías Variadas**: Ejercicios de estiramiento, cardio y fuerza
- **📈 Niveles de Dificultad**: Tres niveles (1-3) para adaptarse a diferentes capacidades
- **✏️ Retos Personalizados**: Crea y gestiona tus propios retos de ejercicio
- **🔔 Sistema de Notificaciones**: Recordatorios configurables para mantener la constancia
- **💾 Persistencia de Datos**: Base de datos SQLite para guardar progreso y configuraciones

## 🛠️ Tecnologías Utilizadas

- **Java 21**: Lenguaje de programación principal
- **JavaFX 21**: Framework para la interfaz gráfica de usuario
- **SQLite 3.42.0**: Base de datos embebida para persistencia
- **Maven**: Gestión de dependencias y construcción del proyecto
- **JUnit 5**: Framework de testing

## 📋 Requisitos del Sistema

- **Java Development Kit (JDK) 21** o superior
- **Maven 3.6+** para construcción del proyecto
- **Sistema Operativo**: Windows, macOS, o Linux con soporte para JavaFX

## 🚀 Instalación y Ejecución

### Opción 1: Ejecutar desde código fuente

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/fmmdevs/Random-Fitness-Challenge.git
   cd Random-Fitness-Challenge
   ```

2. **Compilar y ejecutar con Maven**:
   ```bash
   mvn clean javafx:run
   ```

### Opción 2: Ejecutar JAR precompilado

1. **Descargar el JAR** desde las releases del repositorio
2. **Ejecutar la aplicación**:
   ```bash
   java -jar random-fitness-challenge.jar
   ```

### Opción 3: Construcción manual

1. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

2. **Generar el JAR**:
   ```bash
   mvn package
   ```

3. **Ejecutar**:
   ```bash
   java -jar target/RFC_01-1.0-SNAPSHOT.jar
   ```

## 🎮 Uso de la Aplicación

### Pantalla Principal
- **Retos**: Visualiza y completa retos de fitness aleatorios
- **Estadísticas**: Revisa tu progreso y logros
- **Crear Reto**: Añade nuevos retos personalizados
- **Gestionar Retos**: Edita o elimina retos existentes
- **Configuración**: Ajusta notificaciones y preferencias

### Completar un Reto
1. La aplicación muestra un reto aleatorio con descripción e imagen
2. Inicia el temporizador cuando comiences el ejercicio
3. Puedes completar el reto antes de tiempo o al finalizar el temporizador
4. Opción de saltar al siguiente reto si es necesario

### Crear Retos Personalizados
1. Ve a la sección "Crear Reto"
2. Completa los campos: nombre, descripción, categoría, dificultad y duración
3. Opcionalmente añade una imagen
4. Guarda el reto para incluirlo en la rotación aleatoria

## 📁 Estructura del Proyecto

```
src/main/java/devs/fmm/rfc_01/
├── controller/          # Controladores JavaFX
├── dao/                # Data Access Objects
├── db/                 # Gestión de base de datos
├── model/              # Modelos de datos
├── service/            # Lógica de negocio
├── util/               # Utilidades
├── Main.java           # Punto de entrada principal
└── RandomFitnessChallengeApp.java  # Aplicación JavaFX

src/main/resources/
├── devs/fmm/rfc_01/
│   ├── view/           # Archivos FXML
│   ├── images/         # Recursos gráficos
│   └── css/            # Estilos CSS
└── META-INF/           # Metadatos del módulo
```

## 🧪 Testing

Ejecutar las pruebas unitarias:

```bash
mvn test
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Autores

- **Desarrollador Principal** - [fmmdevs](https://github.com/fmmdevs)

## 🙏 Agradecimientos

- Comunidad JavaFX por la documentación y ejemplos
- Iconos y recursos gráficos utilizados en la aplicación
- Contribuidores y testers del proyecto

---

⭐ ¡Si te gusta este proyecto, no olvides darle una estrella en GitHub!
