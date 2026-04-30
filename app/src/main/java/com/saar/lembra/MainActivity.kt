package com.saar.lembra

import android.R.attr.onClick
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.saar.lembra.ui.theme.LembraTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Se a palavra LembraTheme ficar vermelha, use Option + Return para importar
            com.saar.lembra.ui.theme.LembraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LembraApp() // É aqui que chamamos toda a tela que construímos!
                }
            }
        }
    }
}
    @OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
    @Composable
    fun LembraApp() {
        var mostrarBottomSheet by remember { mutableStateOf(false) }
        var abaSelecionada by remember { mutableIntStateOf(0) }
        var diaSelecionado by remember { mutableIntStateOf(0) }
        val abasDias = remember { gerarAbasDias(10) }
        var fabExpandido by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Lembra", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) },
                        navigationIcon = {
                            IconButton(onClick = { /* Menu */ }) { Icon(Icons.Default.Menu, "Menu") }
                        },
                        actions = {
                            IconButton(onClick = { /* Perfil */ }, modifier = Modifier.padding(end = 8.dp)) {
                                Icon(Icons.Default.AccountCircle, "Perfil", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(selected = abaSelecionada == 0, onClick = { abaSelecionada = 0 }, icon = { Icon(Icons.Default.Checklist, null) }, label = { Text("Início") })
                        NavigationBarItem(selected = abaSelecionada == 1, onClick = { abaSelecionada = 1 }, icon = { Icon(Icons.Default.Group, null) }, label = { Text("Pessoas") })
                        NavigationBarItem(selected = abaSelecionada == 2, onClick = { abaSelecionada = 2 }, icon = { Icon(Icons.Default.QrCode, null) }, label = { Text("Meu QR") })
                    }
                }
            ) { paddingValues ->
                val modifierPadrao = Modifier.padding(paddingValues)

                when (abaSelecionada) {
                    0 -> {
                        Column(modifier = modifierPadrao.fillMaxSize()) {
                            ScrollableTabRow(
                                selectedTabIndex = diaSelecionado,
                                edgePadding = 16.dp,
                                containerColor = MaterialTheme.colorScheme.background,
                                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp) },
                                indicator = { tabPositions ->
                                    if (diaSelecionado < tabPositions.size) {
                                        TabRowDefaults.SecondaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[diaSelecionado]).padding(horizontal = 16.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            height = 4.dp
                                        )
                                    }
                                }
                            ) {
                                abasDias.forEachIndexed { index, titulo ->
                                    Tab(selected = diaSelecionado == index, onClick = { diaSelecionado = index }, text = { Text(text = titulo, fontWeight = if (diaSelecionado == index) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal, color = if (diaSelecionado == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) })
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (diaSelecionado == 0) {
                                LembreteCard("Tomar vitamina", "08:00", "Mim mesmo", androidx.compose.foundation.shape.RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                LembreteCard("Dar remédio da vovó", "10:00", "Marusa", androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                            } else if (diaSelecionado == 1) {
                                LembreteCard("Comprar pão", "18:30", "Rique", androidx.compose.foundation.shape.RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                            }
                        }
                    }
                    1 -> TelaPessoas(modifier = modifierPadrao)
                    2 -> TelaQrCode(modifier = modifierPadrao)
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = fabExpandido,
                enter = androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.60f))
                        .clickable(onClick = { fabExpandido = false })
                )
            }

            if (abaSelecionada != 2) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 16.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 96.dp
                        ),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = fabExpandido,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically(targetOffsetY = { 50 })
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ExtendedFloatingActionButton(
                                onClick = { fabExpandido = false; mostrarBottomSheet = true },
                                icon = { Icon(Icons.Default.PersonAdd, null) },
                                text = { Text("Lembrar Alguém") },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            ExtendedFloatingActionButton(
                                onClick = { fabExpandido = false; mostrarBottomSheet = true },
                                icon = { Icon(Icons.Default.NotificationsActive, null) },
                                text = { Text("Me Lembrar") },
                                shape = androidx.compose.foundation.shape.CircleShape,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { fabExpandido = !fabExpandido },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        shape = if (fabExpandido) androidx.compose.foundation.shape.CircleShape else MaterialTheme.shapes.large
                    ) {
                        Icon(
                            imageVector = if (fabExpandido) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "Adicionar Lembrete"
                        )
                    }
                }
            }
        }

        if (mostrarBottomSheet) {
            NovoLembreteBottomSheet(onDismiss = { mostrarBottomSheet = false })
        }
    }

    @Composable
    fun LembreteCard(
        titulo: String,
        hora: String,
        destinatario: String,
        shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium,
        ativoInicialmente: Boolean = true
    ) {
        var isAtivo by remember { mutableStateOf(ativoInicialmente) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "$hora • Para: $destinatario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(checked = isAtivo, onCheckedChange = { isAtivo = it })
            }
        }
    }

    @Composable
    fun TelaPessoas(modifier: Modifier = Modifier) {
        var pessoaSelecionada by remember { mutableIntStateOf(0) }
        val listaPessoas = listOf("Todos", "Rique", "Marusa", "Rolinka")

        Column(modifier = modifier.fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = pessoaSelecionada,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp) },
                indicator = { tabPositions ->
                    if (pessoaSelecionada < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pessoaSelecionada]).padding(horizontal = 16.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            height = 4.dp
                        )
                    }
                }
            ) {
                listaPessoas.forEachIndexed { index, nome ->
                    Tab(selected = pessoaSelecionada == index, onClick = { pessoaSelecionada = index }, text = { Text(text = nome, fontWeight = if (pessoaSelecionada == index) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal, color = if (pessoaSelecionada == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) })
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val bordaArredondada = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
            val bordaReta = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)

            when (pessoaSelecionada) {
                0 -> {
                    LembreteCard(titulo = "Comprar pão", hora = "18:30", destinatario = "Rique", shape = bordaArredondada)
                    LembreteCard(titulo = "Dar remédio", hora = "10:00", destinatario = "Marusa", shape = bordaReta)
                    LembreteCard(titulo = "Confirmar voo", hora = "15:00", destinatario = "Rolinka", shape = bordaReta)
                }
                1 -> LembreteCard(titulo = "Comprar pão", hora = "18:30", destinatario = "Rique", shape = bordaArredondada)
                2 -> LembreteCard(titulo = "Dar remédio", hora = "10:00", destinatario = "Marusa", shape = bordaArredondada)
                3 -> LembreteCard(titulo = "Confirmar voo", hora = "15:00", destinatario = "Rolinka", shape = bordaArredondada)
            }
        }
    }

    @Composable
    fun TelaQrCode(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.QrCode, contentDescription = "Meu QR Code", modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mostre este código para receber lembretes", style = MaterialTheme.typography.bodyLarge)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NovoLembreteBottomSheet(onDismiss: () -> Unit) {
        val sheetState = rememberModalBottomSheetState()
        var titulo by remember { mutableStateOf("") }
        var detalhes by remember { mutableStateOf("") }

        ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Foto", modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Novo lembrete") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = detalhes, onValueChange = { detalhes = it }, label = { Text("Adicionar detalhes") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextButton(onClick = { /* Calendário */ }) { Icon(Icons.Default.DateRange, null); Spacer(Modifier.width(4.dp)); Text("Def. data") }
                    TextButton(onClick = { /* Relógio */ }) { Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(4.dp)); Text("Def. hora") }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { /* Salvar */ }) { Text("Concluído") }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    fun gerarAbasDias(quantidadeDeDias: Int = 7): List<String> {
        val abas = mutableListOf<String>()
        val hoje = java.time.LocalDate.now()
        val localeBr = java.util.Locale.forLanguageTag("pt-BR")
        val formatador = java.time.format.DateTimeFormatter.ofPattern("E dd", localeBr)

        for (i in 0 until quantidadeDeDias) {
            when (i) {
                0 -> abas.add("Hoje")
                1 -> abas.add("Amanhã")
                else -> {
                    val dataFutura = hoje.plusDays(i.toLong())
                    val textoData = dataFutura.format(formatador).replace(".", "").replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeBr) else it.toString() }
                    abas.add(textoData)
                }
            }
        }
        return abas
    }


