# Criterios de Aceptación y Escenarios Límite

# Matriz de Casos Límite (*Edge Cases*)

## Módulo de Presupuesto y Cálculos Financieros (RF-04 al RF-09)

| **ID Prueba** | **Escenario Límite (Edge Case)** | **Condición / Disparador** | **Comportamiento Esperado del Sistema (Test Criteria)** |
| --- | --- | --- | --- |
| **TC-01** | **Salario Fijo No Configurado** | El usuario finaliza el Onboarding (RF-10) saltándose el salario fijo opcional (`salario_fijo = 0` o `null`). | El cálculo del **"Total libre mensual"** (`RF-06`)  debe operar sin errores de referencia nula, mostrando `$ 0` o un saldo negativo igual al total de gastos fijos, acompañado de una invitación visual para configurar el salario en la tarjeta. |
| **TC-02** | **Gastos Fijos superan el Salario** | El `Total de gastos fijos` (`RF-05`) es mayor al `salario_fijo`. | El sistema calcula el **"Total libre mensual"** dando un resultado matemático negativo. La interfaz (Jetpack Compose) debe renderizar el monto en un color de alerta (ej. rojo) sin romper el contenedor gráfico. |
| **TC-03** | **Deuda Pendiente supera el Saldo Bancario** | El total **"Por pagar"** (`RF-08`) supera el dinero ingresado en **`saldo_actual`** (`RF-07`). | El **"Libre mensual aproximado"** (`RF-09`)  debe arrojar un valor negativo, indicando que el usuario necesitará más ingresos o ahorros para cubrir sus deudas del ciclo. |

## Módulo de Cuentas y Estado de Pago (RF-01 al RF-03, RF-08)

| **ID Prueba** | **Escenario Límite (Edge Case)** | **Condición / Disparador** | **Comportamiento Esperado del Sistema (Test Criteria)** |
| --- | --- | --- | --- |
| **TC-04** | **Lista de Gastos Vacía (Eliminación total)** | El usuario elimina el último gasto de su lista (`borrarGasto`) o entra por primera vez sin transacciones registradas. | La consulta SQL agregada `SUM(valor)` en `GastoDao` debe estar protegida en Kotlin (usando `COALESCE(SUM(valor), 0)`) para que devuelva el entero `0` en lugar de `NULL`, evitando un `NullPointerException` al calcular `RF-05` y `RF-08`. |
| **TC-05** | **100% de los Gastos Marcados como Pagados** | Todos los registros de gasto tienen `estado_pagado = true` (`RF-02`). | La consulta condicional (`WHERE estado_pagado = false` o `0`) excluye todo del recálculo de deuda (`RF-03`). La métrica **"Por pagar"** (`RF-08`) debe arrojar exactamente `$ 0`, y el **"Libre mensual aproximado"** (`RF-09`)  debe ser igual a su `saldo_actual`. |
| **TC-06** | **Gasto con Valor Cero (`$ 0`)** | El usuario registra un gasto opcional o suscripción en periodo de prueba gratuito con `valor = 0` (`RF-01`). | El registro se guarda en Room y se muestra en la lista. Si se marca o desmarca su casilla, no altera numéricamente el saldo ni la deuda, pero se actualiza su estado visual tachado. |

## Onboarding y Validación de Entradas (RF-10 / CU-01 / CU-06)

| **ID Prueba** | **Escenario Límite (Edge Case)** | **Condición / Disparador** | **Comportamiento Esperado del Sistema (Test Criteria)** |
| --- | --- | --- | --- |
| **TC-07** | **Entrada de solo espacios en blanco** | El usuario ingresa únicamente barras espaciadoras en el campo `nombre_perfil` (Onboarding) o en `nombre_gasto` (Nuevo Gasto). | La validación reactiva de la pantalla debe limpiar el texto usando `.trim().isNotEmpty()`. Un nombre compuesto solo por espacios debe ser tratado como vacío, manteniendo **deshabilitado el botón principal**. |
| **TC-08** | **Desbordamiento Monetario (*Integer Overflow*)** | El usuario ingresa una cifra de salario o gasto extremadamente grande que supera el valor máximo del tipo `Int` en Kotlin (`2.147.483.647`). | Aunque en pesos chilenos es difícil superar el límite de un `Int`, como medida defensiva el campo de texto de Compose debe restringir la cantidad máxima de dígitos permitidos (ej. 9 o 10 dígitos) para evitar que SQLite o el `ViewModel` generen desbordamiento aritmético. |