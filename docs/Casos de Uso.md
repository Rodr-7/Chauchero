# Casos de Uso

<aside>
📔

**Versión del Documento:** 1.0.0

**Proyecto:** Chauchero: App de Presupuesto Personal 

**Categoría:** Ahorro y seguimiento de gastos 

</aside>

[**Documento de Requisitos de Software (DRS)**](Documento%20de%20Requisitos%20de%20Software%20(DRS)%203a3677c36f9d8008b569f4fe1d81cf9c.md)

# **Historial de Versiones**

| **Versión** | **Fecha** | **Autor** | **Cambios Realizados** |
| --- | --- | --- | --- |
| **1.0.0** | 07/2026 | Rodrigo A. | Creación del documento base (Módulos 1 y 2). |

> **Nota de Ingeniería de Software:** Aunque el DRS detalla explícitamente el *registro* y el *estado* del gasto, a nivel de arquitectura y bases de datos siempre debemos contemplar las operaciones completas de mantenimiento (conocidas como CRUD: Crear, Leer, Actualizar, Eliminar). Esto significa que, indirectamente, el sistema también necesitará Casos de Uso más sencillos para **"Editar un gasto existente"** y **"Eliminar un gasto"**.
> 

# Casos de Uso: Módulo 1 (Gestión de Cuentas)

## **CU-01: Registrar un nuevo gasto fijo**

**Requisitos asociados:** RF-01.

**Actor principal:** Usuario.

**Flujo Principal:**

1. El usuario selecciona la acción para añadir un nuevo gasto en la interfaz.
2. El sistema despliega un formulario en pantalla.
3. El usuario ingresa los atributos obligatorios: nombre, categoría, prioridad y valor monetario en pesos.
4. El usuario confirma el guardado.
5. El sistema almacena el gasto, le asigna por defecto el estado "pendiente" y establece el valor dinámico igual al valor original.
    
    **Flujo Alternativo (Campos incompletos):**
    
6. El usuario intenta guardar el formulario dejando un campo obligatorio en blanco.
7. El sistema bloquea la acción de guardado.
8. El sistema muestra un mensaje de error solicitando que se complete la información faltante.

## **CU-02: Alternar estado de pago de un gasto**

**Requisitos asociados:** RF-02 , RF-03.

**Actor principal:** Usuario.

**Flujo Principal (Marcar como pagado):**

1. El usuario visualiza su lista de gastos mensuales.
2. El usuario toca la casilla de verificación de un gasto que se encuentra en estado "pendiente".
3. El sistema actualiza el estado visual del gasto a "pagado".
4. En segundo plano, el sistema vuelve a recalcular el total de deuda ****pendiente excluyendo de los recálculos el valor de este gasto.
**Flujo Alternativo (Desmarcar pago):**
5. El usuario toca la casilla de verificación de un gasto que ya estaba "pagado".
6. El sistema actualiza el estado visual del gasto nuevamente a "pendiente".
7. En segundo plano, el sistema vuelve a recalcular el total de deuda ****pendiente considerando el valor de este gasto para los recálculos.

# Casos de Uso: Módulo 2 (Gestión de Presupuesto)

## **CU-03: Configurar Saldo Líquido Mensual (Salario)**

**Requisitos asociados:** RF-04 , RF-06.

**Actor principal:** Usuario.

**Flujo Principal:**

1. El usuario navega a la sección de presupuesto de la aplicación.
2. Selecciona la opción para ingresar o editar su "saldo líquido mensual".
3. El sistema despliega un campo de entrada numérica.
4. El usuario ingresa el monto que representa su salario fijo y guarda los cambios.
5. El sistema almacena este nuevo valor y, en segundo plano, recalcula automáticamente la métrica de "total libre mensual" (tu proyección ideal).

## **CU-04: Actualizar Saldo Bancario Actual**

**Requisitos asociados:** RF-07 , RF-09.

**Actor principal:** Usuario.

**Flujo Principal:**

1. El usuario accede a la pantalla principal o panel de control de *Chauchero*.
2. Toca la interfaz designada para actualizar su saldo en cuenta.
3. El usuario ingresa manualmente el dinero exacto y real que tiene en el banco en ese momento y confirma.
4. El sistema guarda la actualización y recalcula de inmediato el "Libre mensual aproximado" (tu flujo de caja real).

## **CU-05: Visualización de Resumen Financiero (Dashboard)**

**Requisitos asociados:** RF-05 , RF-06 , RF-08 , RF-09.

**Actor principal:** Sistema (ejecución automática para el usuario).

**Flujo Principal:**

1. El usuario abre la pestaña de resumen de la aplicación.
2. El sistema suma de forma automática todos los gastos fijos y muestra el "Total de gastos fijos".
3. El sistema calcula la proyección ideal restando el total de gastos al saldo líquido mensual.
4. El sistema realiza la suma exclusiva de los valores dinámicos que se encuentran en estado "pendiente" y muestra el total "Por pagar".
5. El sistema resta el monto "Por pagar" al saldo actual de la cuenta bancaria y muestra el resultado en la métrica "Libre mensual aproximado".
6. El usuario visualiza una radiografía completa y actualizada de sus finanzas.

## **CU-06: Configuración inicial de la cuenta (Onboarding)**

- **Actor principal:** Usuario (Nuevo)
- **Flujo Principal:**
    1. El usuario abre la aplicación por primera vez tras la instalación.
    2. El sistema muestra pantallas con el resumen de las funciones principales.
    3. El usuario avanza por las pantallas (botón "Siguiente").
    4. En la última pantalla, el sistema despliega un formulario solicitando el `nombre_perfil` y, opcionalmente, el `salario_fijo`.
    5. El usuario ingresa su nombre, su salario (opcional) y presiona "Comenzar".
    6. El sistema crea el registro en la base de datos y redirige al usuario al Dashboard (CU-05).
- **Flujo Alternativo:**
    - Si el usuario deja el campo de nombre en blanco, el sistema bloquea el botón "Comenzar" y pide que ingrese al menos un nombre para identificar su cuenta.