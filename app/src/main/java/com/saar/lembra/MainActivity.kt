package com.saar.lembra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saar.lembra.ui.theme.LembraTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aqui estamos chamando o tema padrão do seu app e, dentro dele, a nossa tela principal
            LembraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LembraApp()
                }
            }
        }
    }
}

// --- DAQUI PARA BAIXO SÃO AS NOSSAS TELAS (PEÇAS DE LEGO) ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun LembraApp() {
    var mostrarBottomSheet by remember { mutableStateOf(false) }
    // Variável para a barra inferior (Início, Pessoas, QR)
    var abaSelecionada by remember { mutableIntStateOf(0) }

    // NOVA Variável: controla qual dia está selecionado na parte superior
    var diaSelecionado by remember { mutableIntStateOf(0) }
    // Lista com os dias (baseado na data de hoje: Terça, 28 de abril)
    val abasDias = remember { gerarAbasDias(10) } // Aqui você escolhe quantos dias quer prever na barra

    Scaffold(
        topBar = {
            // Barra superior mais limpa, igual ao print do Tarefas
            TopAppBar(
                title = { Text("Lembra", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Perfil */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = abaSelecionada == 0,
                    onClick = { abaSelecionada = 0 },
                    icon = { Icon(Icons.Default.Checklist, null) },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = abaSelecionada == 1,
                    onClick = { abaSelecionada = 1 },
                    icon = { Icon(Icons.Default.Group, null) },
                    label = { Text("Pessoas") }
                )
                NavigationBarItem(
                    selected = abaSelecionada == 2,
                    onClick = { abaSelecionada = 2 },
                    icon = { Icon(Icons.Default.QrCode, null) },
                    label = { Text("Meu QR") }
                )
            }
        },
        floatingActionButton = {
            if (abaSelecionada != 2) {
                FloatingActionButton(onClick = { mostrarBottomSheet = true }) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    ) { paddingValues ->
        val modifierPadrao = Modifier.padding(paddingValues)

        when (abaSelecionada) {
            0 -> {
                // Tela Inicial com as Abas de Dias
                Column(modifier = modifierPadrao.fillMaxSize()) {

                    // --- NOVA BARRA DE ABAS ROLÁVEL ---
                    ScrollableTabRow(
                        selectedTabIndex = diaSelecionado,
                        edgePadding = 16.dp, // Espaçamento na borda da tela
                        containerColor = MaterialTheme.colorScheme.background,
                        divider = {}, // Remove a linha fina de baixo para ficar mais limpo
                        indicator = { tabPositions ->
                            if (diaSelecionado < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[diaSelecionado]),
                                    color = MaterialTheme.colorScheme.primary, // Cor do sublinhado
                                    height = 3.dp // Espessura do sublinhado
                                )
                            }
                        }
                    ) {
                        abasDias.forEachIndexed { index, titulo ->
                            Tab(
                                selected = diaSelecionado == index,
                                onClick = { diaSelecionado = index },
                                text = {
                                    Text(
                                        text = titulo,
                                        // Deixa o texto em negrito se estiver selecionado
                                        fontWeight = if (diaSelecionado == index) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                                        color = if (diaSelecionado == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp)) // Espacinho entre a aba e os lembretes

                    // Se a aba "Hoje" estiver selecionada, mostra os cards de hoje
                    if (diaSelecionado == 0) {
                        LembreteCard(
                            titulo = "Tomar vitamina",
                            hora = "08:00",
                            destinatario = "Mim mesmo",
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp
                            )
                        )

                        LembreteCard(
                            titulo = "Dar remédio da vovó",
                            hora = "10:00",
                            destinatario = "Marusa",
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                        )
                    } else if (diaSelecionado == 1) {
                        // Se "Amanhã" estiver selecionada, mostra outra lista
                        LembreteCard(
                            titulo = "Comprar pão",
                            hora = "18:30",
                            destinatario = "Rique",
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                topStart = 28.dp,
                                topEnd = 28.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 4.dp
                            )
                        )
                    }
                }
            }
            1 -> TelaPessoas(modifier = modifierPadrao)
            2 -> TelaQrCode(modifier = modifierPadrao)
        }

        if (mostrarBottomSheet) {
            NovoLembreteBottomSheet(onDismiss = { mostrarBottomSheet = false })
        }
    }
}

@Composable
fun LembreteCard(
    titulo: String,
    hora: String,
    destinatario: String,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium, // O segredo está aqui!
    ativoInicialmente: Boolean = true
) {
    var isAtivo by remember { mutableStateOf(ativoInicialmente) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
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

            Switch(
                checked = isAtivo,
                onCheckedChange = { isAtivo = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoLembreteBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var titulo by remember { mutableStateOf("") }
    var detalhes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Foto",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Novo lembrete") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = detalhes,
                onValueChange = { detalhes = it },
                label = { Text("Adicionar detalhes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { /* Abrir calendário */ }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Def. data")
                }
                TextButton(onClick = { /* Abrir relógio */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Def. hora")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* Salvar lembrete */ }) {
                    Text("Concluído")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TelaPessoas(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pessoas que estou lembrando", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Dados de exemplo para visualizarmos
        LembreteCard(titulo = "Ligar para o médico", hora = "14:00", destinatario = "Marusa")
        LembreteCard(titulo = "Comprar pão", hora = "18:00", destinatario = "Rique")
    }
}

@Composable
fun TelaQrCode(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = "Meu QR Code",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Mostre este código para receber lembretes", style = MaterialTheme.typography.bodyLarge)
    }
}

fun gerarAbasDias(quantidadeDeDias: Int = 7): List<String> {
    val abas = mutableListOf<String>()
    val hoje = LocalDate.now()
    // Define o idioma para português do Brasil para os dias da semana
    val localeBr = Locale("pt", "BR")
    val formatador = DateTimeFormatter.ofPattern("E dd", localeBr)

    for (i in 0 until quantidadeDeDias) {
        when (i) {
            0 -> abas.add("Hoje")
            1 -> abas.add("Amanhã")
            else -> {
                val dataFutura = hoje.plusDays(i.toLong())
                // Formata (ex: "qui. 30"), tira o ponto e deixa a primeira letra maiúscula ("Qui 30")
                val textoData = dataFutura.format(formatador)
                    .replace(".", "")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeBr) else it.toString() }
                abas.add(textoData)
            }
        }
    }
    return abas
}