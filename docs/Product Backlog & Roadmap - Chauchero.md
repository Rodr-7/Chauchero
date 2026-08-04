# Product Backlog & Roadmap - Chauchero

Este documento establece la visión a futuro y las fases de desarrollo planificadas para **Chauchero**, posteriores a la implementación de la Versión 1.0.0. Su propósito principal es estructurar la escalabilidad del sistema.

## **Fase 1: Versión 1.1 - Mejoras de Interfaz y Experiencia de Usuario (UI/UX)**

Enfoque en la pulcritud visual y la retención del usuario, añadiendo detalles que mejoran la interacción diaria sin alterar la lógica de negocio base.

| ID | Característica | Descripción Técnica / Valor |
| --- | --- | --- |
| PB-1.1.1 | Modo Oscuro (Dark Mode) | Implementación de un tema nocturno nativo en Android (Material Design) para reducir la fatiga visual del usuario. |
| PB-1.1.2 | Notificaciones Locales | Recordatorios programables usando WorkManager para alertar al usuario sobre el registro o pago de gastos a fin de mes. |
| PB-1.1.3 | Animaciones de Transición | Suavizar los cambios de pantalla entre el gestor de Cuentas y el Dashboard de Presupuesto. |
| PB-1.1.4 | Creación de categorías personalizadas | Implementación de opción para crear nuevas categorías con nombre y color personalizados |

## **Fase 2: Versión 1.5 - Reinicio Mensual y Gastos temporales**

Aprovechando la relación 1:N y la clave foránea (id_perfil) definida en el Modelo Entidad-Relación, esta fase permitirá gestionar múltiples contextos financieros en un mismo dispositivo.

| ID | Característica | Descripción Técnica / Valor |
| --- | --- | --- |
| PB-1.0.5 | Boton de reinicio | Botón para restablecer todos los gastos en estado “Pendiente” |
| PB-1.0.6 | Gastos Imprevistos | Habilita la opción de añadir un gasto temporal que se borra al usar el botón de reinicio de periodo. Este nuevo tipo de gasto debe tener una referencia a su fecha de creación y opcionalmente un nombre. |
| PB-1.0.7 | Acceso directo a gastos imprevistos | Habilita un acceso directo fuera de la app que permita añadir un gasto imprevisto de manera rapida  |

## **Fase 3: Versión 2.0 - Arquitectura Multiperfil y Gastos temporales**

Aprovechando la relación 1:N y la clave foránea (id_perfil) definida en el Modelo Entidad-Relación, esta fase permitirá gestionar múltiples contextos financieros en un mismo dispositivo.

| ID | Característica | Descripción Técnica / Valor |
| --- | --- | --- |
| PB-2.0.1 | Gestión de Perfiles | Pantalla (CRUD) para crear, editar y eliminar perfiles (ej. "Personal", "Negocio"), utilizando la entidad física PerfilUsuario. |
| PB-2.0.2 | Aislamiento de Datos | Filtros a nivel de DAO en Room para asegurar que la vista de Gastos y Presupuesto consulte y muestre solo los datos del perfil activo. |
| PB-2.0.3 | Eliminación en Cascada (UI) | Flujo de advertencia gráfica antes de eliminar un perfil, informando que sus gastos asociados también se borrarán por la regla CASCADE. |

## **Fase 4: Versión 3.0 - Análisis Avanzado y Visualización de Datos**

Integración de librerías de terceros para representar visualmente el comportamiento financiero del usuario y ayudar a la toma de decisiones.

| ID | Característica | Descripción Técnica / Valor |
| --- | --- | --- |
| PB-3.0.1 | Gráfico de Distribución | Visualización porcentual (Pie Chart) de los gastos agrupados por el atributo categoria (ej. Vivienda, Alimentación, Ocio). |
| PB-3.0.2 | Indicadores de Prioridad | Filtros rápidos o gráficos de barras que separen los gastos según su atributo de prioridad (Alta, Media, Baja). |
| PB-3.0.3 | Histórico Mensual | Registro de cierres de mes para permitir al sistema comparar la evolución de gastos e ingresos con meses anteriores. |

## **Fase 5: Versión 4.0 - Exportación y Respaldo**

Dotar al usuario de herramientas para sacar su información del dispositivo, aumentando la seguridad y utilidad del sistema.

| ID | Característica | Descripción Técnica / Valor |
| --- | --- | --- |
| PB-4.0.1 | Exportación a PDF | Generación de un reporte formal del resumen financiero mensual (Dashboard) para compartir o imprimir. |
| PB-4.0.2 | Exportación a CSV / Excel | Permitir la descarga de todos los registros de la tabla Gasto en formato de hoja de cálculo estándar. |
| PB-4.0.3 | Respaldo Local (Backup) | Mecanismo para exportar e importar la base de datos de SQLite/Room en un archivo físico para evitar pérdida de datos al cambiar de teléfono. |