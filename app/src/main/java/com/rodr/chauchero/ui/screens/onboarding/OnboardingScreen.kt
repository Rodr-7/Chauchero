package com.rodr.chauchero.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rodr.chauchero.R
import com.rodr.chauchero.ui.theme.ChaucheroGreen
import com.rodr.chauchero.ui.theme.ChaucheroMintNavigation
import com.rodr.chauchero.ui.theme.ChaucheroTheme
import com.rodr.chauchero.ui.viewmodels.OnboardingUiState
import com.rodr.chauchero.ui.viewmodels.OnboardingViewModel

private data class IntroPage(
    val title: String,
    val body: String,
    @get:DrawableRes val image: Int,
)

private val introPages = listOf(
    IntroPage(
        title = "¡Bienvenido!",
        body = "Chauchero te ayudará a controlar tus gastos y saber cuánto dinero tienes para usar libremente sin salirte de tu presupuesto.",
        image = R.drawable.onboarding_screen_1,
    ),
    IntroPage(
        title = "Contrasta tus gastos y tu presupuesto",
        body = "Anota tus gastos mensuales, establece tu presupuesto mensual y Chauchero hará el resto. Podrás ver cuánto quedará disponible para usar libremente.",
        image = R.drawable.onboarding_image_2,
    ),
    IntroPage(
        title = "Lleva las cuentas del día a día",
        body = "Marca con un toque los gastos que ya pagaste y actualiza tu saldo para saber cuánto dinero te queda libre.",
        image = R.drawable.onboarding_image_3,
    ),
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToDashboard: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    OnboardingContent(
        uiState = uiState,
        onNombreChange = viewModel::actualizarNombre,
        onSalarioChange = viewModel::actualizarSalarioFijo,
        onComenzar = { viewModel.guardarPerfilInicial(onNavigateToDashboard) },
    )
}

@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    onNombreChange: (String) -> Unit,
    onSalarioChange: (String) -> Unit,
    onComenzar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var page by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ChaucheroMintNavigation)
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 28.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (page < introPages.size) {
            IntroPageContent(page = introPages[page])
            PrimaryAction(
                label = if (page == 0) "¡Vamos!" else "Siguiente",
                onClick = { page += 1 },
            )
        } else {
            ProfileForm(
                uiState = uiState,
                onNombreChange = onNombreChange,
                onSalarioChange = onSalarioChange,
                onComenzar = onComenzar,
            )
        }
    }
}

@Composable
private fun ColumnScope.IntroPageContent(page: IntroPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = true)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(page.image),
            contentDescription = null,
            modifier = Modifier.size(270.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        Text(
            text = page.body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ColumnScope.ProfileForm(
    uiState: OnboardingUiState,
    onNombreChange: (String) -> Unit,
    onSalarioChange: (String) -> Unit,
    onComenzar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = true)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Antes de empezar", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(48.dp))
        OutlinedTextField(
            value = uiState.nombrePerfil,
            onValueChange = onNombreChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("¿Cuál es tu nombre?") },
            placeholder = { Text("Nombre del perfil") },
            supportingText = if (uiState.nombrePerfil.isNotEmpty() && !uiState.nombreValido) {
                { Text("Ingresa un nombre válido") }
            } else null,
            isError = uiState.nombrePerfil.isNotEmpty() && !uiState.nombreValido,
            singleLine = true,
        )
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = uiState.salarioFijoStr,
            onValueChange = onSalarioChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Presupuesto mensual (opcional)") },
            placeholder = { Text("$ 0") },
            prefix = { Text("$ ") },
            supportingText = if (!uiState.salarioValido) {
                { Text("El monto debe ser menor o igual a $2.147.483.647") }
            } else {
                { Text("Puedes configurarlo o actualizarlo más tarde") }
            },
            isError = !uiState.salarioValido,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
        uiState.errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        }
    }
    PrimaryAction(
        label = "¡Comenzar!",
        onClick = onComenzar,
        enabled = uiState.puedeComenzar,
        loading = uiState.isLoading,
    )
}

@Composable
private fun PrimaryAction(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Spacer(Modifier.height(28.dp))
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ChaucheroGreen),
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
        } else {
            Text(label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun OnboardingPreview() {
    ChaucheroTheme {
        OnboardingContent(OnboardingUiState(), {}, {}, {})
    }
}
