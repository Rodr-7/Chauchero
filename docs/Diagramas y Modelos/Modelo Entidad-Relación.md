# Modelo Entidad-Relación

<aside>
📔

**Versión del Documento:** 1.0.0

**Proyecto:** Chauchero: App de Presupuesto Personal 

**Categoría:** Ahorro y seguimiento de gastos 

</aside>

[**Documento de Requisitos de Software (DRS)**](../Documento%20de%20Requisitos%20de%20Software%20(DRS)%203a3677c36f9d8008b569f4fe1d81cf9c.md)

# **Historial de Versiones**

| **Versión** | **Fecha** | **Autor** | **Cambios Realizados** |
| --- | --- | --- | --- |
| **1.0.0** | 07/2026 | Rodrigo A. | Creación del documento base (Entidades Gasto y PerfilUsuario). |

# **Diseño de Entidades**

## **Entidad Gasto**

| **Columna** | **Tipo de Dato** | **Descripción** |
| --- | --- | --- |
| `id_gasto` | Entero (Clave Primaria) | Identificador único del registro |
| `id_perfil` | Entero (Clave Foránea) | Identificador del perfil propietario |
| `nombre_gasto` | Cadena | Nombre distintivo del registro |
| `categoria` | Cadena | Categoría asignada al registro |
| `prioridad` | Enumeracion | Prioridad asignada al registro |
| `valor` | Entero | Valor numérico en pesos asignado al registro  |
| `estado_pagado` | Booleano | Estado actual del registro |

## **Entidad PerfilUsuario**

| **Columna** | **Tipo de Dato** | **Descripción** |
| --- | --- | --- |
| `id_perfil` | Entero (Clave Primaria) | Identificador único del registro |
| `nombre_perfil` | Cadena | Nombre descriptivo del perfil |
| `salario_fijo` | Entero  | Saldo fijo mensual preestablecido por el usuario |
| `saldo_actual` | Entero  | Saldo actual del usuario |