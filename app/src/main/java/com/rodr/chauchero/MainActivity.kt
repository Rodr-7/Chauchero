package com.rodr.chauchero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rodr.chauchero.data.local.AppDatabase
import com.rodr.chauchero.data.repository.GastoRepository
import com.rodr.chauchero.data.repository.PerfilUsuarioRepository
import com.rodr.chauchero.ui.navigation.ChaucheroNavGraph
import com.rodr.chauchero.ui.theme.ChaucheroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciación de la base de datos bajo el patrón Singleton
        val database = AppDatabase.getDatabase(applicationContext)
        val gastoRepository = GastoRepository(database.gastoDao())
        val perfilRepository = PerfilUsuarioRepository(database.perfilUsuarioDao())

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