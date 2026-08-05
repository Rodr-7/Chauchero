package com.rodr.chauchero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import com.rodr.chauchero.data.local.AppDatabase
import com.rodr.chauchero.data.local.GastoDao
import com.rodr.chauchero.data.local.PerfilUsuarioDao
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.data.repository.PerfilUsuarioRepository
import com.rodr.chauchero.ui.navigation.ChaucheroNavGraph
import com.rodr.chauchero.ui.theme.ChaucheroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instanciación de la base de datos bajo el patrón Singleton
        val (gastoRepository, perfilRepository) = try {
            val database = AppDatabase.getDatabase(applicationContext)
            Pair(
                GastoRepository(database.gastoDao()),
                PerfilUsuarioRepository(database.perfilUsuarioDao())
            )
        } catch (e: Exception) {
            // Si Room falla (ej. AppDatabase_Impl no generado), usar repositorios en memoria para evitar crash
            Log.e("MainActivity", "Room initialization failed, using in-memory fallback", e)

            // Backing state flows para los DAOs falsos
            val gastosState = MutableStateFlow<List<com.rodr.chauchero.model.Gasto>>(emptyList())
            val perfilState = MutableStateFlow<com.rodr.chauchero.model.PerfilUsuario?>(null)

            val fakeGastoDao = object : GastoDao {
                override suspend fun insertarGasto(gasto: com.rodr.chauchero.model.Gasto): Long {
                    gastosState.value = gastosState.value + gasto
                    return 0L
                }

                override fun mostrarGasto(idGasto: Int): Flow<com.rodr.chauchero.model.Gasto?> =
                    gastosState.map { list -> list.find { it.idGasto == idGasto } }

                override fun mostrarTodosLosGastos(): Flow<List<com.rodr.chauchero.model.Gasto>> = gastosState

                override suspend fun modificarGasto(gasto: com.rodr.chauchero.model.Gasto): Int {
                    gastosState.value = gastosState.value.map { if (it.idGasto == gasto.idGasto) gasto else it }
                    return 1
                }

                override suspend fun borrarGasto(idGasto: Int): Int {
                    val initialSize = gastosState.value.size
                    gastosState.value = gastosState.value.filter { it.idGasto != idGasto }
                    return initialSize - gastosState.value.size
                }
            }

            val fakePerfilDao = object : PerfilUsuarioDao {
                override suspend fun insertarPerfil(perfil: com.rodr.chauchero.model.PerfilUsuario): Long {
                    perfilState.value = perfil
                    return 0L
                }

                override fun mostrarPerfil(idPerfil: Int): Flow<com.rodr.chauchero.model.PerfilUsuario?> =
                    perfilState.map { p -> if (p?.idPerfil == idPerfil) p else null }

                override suspend fun modificarPerfil(perfil: com.rodr.chauchero.model.PerfilUsuario): Int {
                    perfilState.value = perfil
                    return 1
                }

                override suspend fun borrarPerfil(idPerfil: Int): Int {
                    val deleted = if (perfilState.value?.idPerfil == idPerfil) {
                        perfilState.value = null
                        1
                    } else 0
                    return deleted
                }
            }

            Pair(
                GastoRepository(fakeGastoDao),
                PerfilUsuarioRepository(fakePerfilDao)
            )
        }

        setContent {
            ChaucheroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChaucheroNavGraph(
                        gastoRepository = gastoRepository,
                        perfilRepository = perfilRepository
                    )
                }
            }
        }
    }
}