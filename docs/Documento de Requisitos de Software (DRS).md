# Documento de Requisitos de Software (DRS)

<aside>
📔

**Versión del Documento:** 1.0.0

**Proyecto:** Chauchero: App de Presupuesto Personal 

**Categoría:** Ahorro y seguimiento de gastos 

</aside>

# **Historial de Versiones**

| **Versión** | **Fecha** | **Autor** | **Cambios Realizados** |
| --- | --- | --- | --- |
| **1.0.0** | 07/2026 | Rodrigo A. | Creación del documento base (RNFs 01-04, RFs 01-09 y sus respectivos Módulos 1 y 2). |

# 1. Requisitos No Funcionales (RNF)

Estos requisitos definen los atributos de calidad, restricciones tecnológicas y metodológicas del sistema.

| **ID** | **Categoría** | **Descripción** |
| --- | --- | --- |
| **RNF-01** | Plataforma | La aplicación debe ser desarrollada de forma nativa exclusivamente para el sistema operativo Android. |
| **RNF-02** | Lenguaje | El desarrollo del código fuente se realizará utilizando el lenguaje de programación Kotlin. |
| **RNF-03** | Mantenibilidad | El diseño del software debe aplicar buenas prácticas y patrones de diseño que garanticen que el código sea fácil de mantener, escalar y bifurcar por otros desarrolladores. |
| **RNF-04** | Documentación | El proceso de desarrollo exige la creación y validación de diagramas de software y modelos de datos de forma estricta antes de iniciar la escritura de código. |

## Restricciones y Decisiones

- *"**RNF-X (Base de Datos):** Las entidades de Room deben mantener integridad referencial utilizando `onDelete = CASCADE` entre PerfilUsuario y Gasto. Además, se implementará un `TypeConverter` para gestionar la persistencia del Enum de Prioridad (ALTO, MEDIO, BAJO)."*
- **En tu Diagrama de Clases (txt/UML):**
Puedes añadir una pequeña nota o estereotipo junto a la clase `Gasto` (ej. `<<TypeConverter>>`) y especificar la regla de cascada en la relación entre el perfil y los gastos.

# 2. Requisitos Funcionales (RF)

Estos requisitos describen el comportamiento del sistema, sus módulos y las funciones específicas que debe realizar.

## **Módulo 1: Gestión de Cuentas (Gastos Fijos)**

| **ID** | **Requisito** | **Descripción** |
| --- | --- | --- |
| **RF-01** | Registro de gastos | El sistema debe permitir al usuario registrar gastos mensuales fijos, almacenando obligatoriamente los atributos: nombre, categoría, prioridad y valor monetario en pesos. |
| **RF-02** | Estado de pago | El sistema debe incluir un elemento interactivo (casilla de verificación) que permita al usuario alternar el estado de cada gasto entre "pagado"(casilla marcada) y "pendiente"(casilla desmarcada). |
| **RF-03** | Lógica de valor dinámico | El sistema debe procesar en segundo plano de forma reactiva el cambio de estado de los Gastos de pendiente a pagado y viceversa, determinando a partir de esto si el gasto se excluye o incluye respectivamente de los recálculos de deuda ****pendiente. |

## **Módulo 2: Gestión de Presupuesto (Cálculos y Resúmenes)**

| **ID** | **Requisito** | **Descripción** |
| --- | --- | --- |
| **RF-04** | Ingreso de salario | El sistema debe permitir al usuario definir e ingresar manualmente un "saldo líquido mensual" que represente su salario fijo. |
| **RF-05** | Total de gastos fijos | El sistema debe sumar, calcular y mostrar en pantalla el total absoluto de los gastos mensuales fijos registrados. |
| **RF-06** | Cálculo de proyección ideal | El sistema debe calcular la métrica "total libre mensual", obtenida al restar el total de gastos mensuales fijos al "saldo líquido mensual". |
| **RF-07** | Ingreso de saldo bancario actual | El sistema debe proveer una interfaz para que el usuario ingrese y actualice manualmente el saldo actual real de su cuenta bancaria. |
| **RF-08** | Cálculo de deuda pendiente | El sistema debe calcular la métrica *"Por pagar"*, realizando la suma exclusiva del atributo valor de todos los gastos que se encuentren en estado *"pendiente"*. |
| **RF-09** | Cálculo de flujo de caja real | El sistema debe calcular y mostrar la métrica "Libre mensual aproximado", restando el valor total de la métrica "Por pagar" al saldo actual de la cuenta bancaria. |
| **RF-10** | Onboarding y Creación de Perfil | El sistema debe detectar el primer inicio de la aplicación y presentar una serie de pantallas introductorias. Al finalizar, debe solicitar obligatoriamente un nombre para el perfil y, de forma opcional, el ingreso del saldo líquido mensual. |