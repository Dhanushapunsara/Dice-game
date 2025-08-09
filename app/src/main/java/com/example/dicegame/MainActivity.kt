package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame.ui.theme.DiceGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    // Use rememberSaveable to preserve state across configuration changes
    var currentScreen by rememberSaveable { mutableStateOf("screen1") }

    when (currentScreen) {
        "screen1" -> screen1 { currentScreen = "screen2" }
        "screen2" -> screen2 { currentScreen = "screen1" }
    }
}

@Composable
fun screen1(onNavigate: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Content on top of background
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //New game button
            Button(
                onClick = onNavigate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212529)
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .offset(y = (-2).dp), // Slight upward offset for 3D effect
                shape = RoundedCornerShape(4.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    hoveredElevation = 8.dp
                )
            ) {
                Text(
                    text = "New Game",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            //About button
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212529)
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .offset(y = (-2).dp), // Slight upward offset for 3D effect
                shape = RoundedCornerShape(4.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp,
                    hoveredElevation = 8.dp
                )
            ) {
                Text(
                    text = "About",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            //Pop up window
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Dice challenge")},
                    text = { Text("This game, you can play with computer player who follows his own stratergy to win." +
                            " If you play better than him you can easly win.\n "+
                            "Play more win more")},
                    confirmButton = {
                        Button(onClick = { showDialog = false},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF212529)),
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun screen2(onNavigate: () -> Unit) {
    BackHandler {
        onNavigate()
    }

    // Dice values
    var humanDice by rememberSaveable { mutableStateOf(listOf(1, 2, 3, 4, 5)) }
    var computerDice by rememberSaveable { mutableStateOf(listOf(1, 2, 3, 4, 5)) }

    // Current round scores
    var humanCurrentRoundScore by rememberSaveable { mutableStateOf(0) }
    var computerCurrentRoundScore by rememberSaveable { mutableStateOf(0) }

    // Current round totals (cumulative for the round)
    var humanCurrentRoundTotal by rememberSaveable { mutableStateOf(0) }
    var computerCurrentRoundTotal by rememberSaveable { mutableStateOf(0) }

    // Round count
    var roundCount by rememberSaveable { mutableStateOf(0) }

    // Target score with default value of 100
    var targetScore by rememberSaveable { mutableStateOf(100) }
    var showTargetDialog by rememberSaveable { mutableStateOf(false) }
    var tempTargetScore by remember { mutableStateOf("100") }

    // Total game scores
    var humanGameScore by rememberSaveable { mutableStateOf(0) }
    var computerGameScore by rememberSaveable { mutableStateOf(0) }

    // Game state flags
    var hasThrown by rememberSaveable { mutableStateOf(false) }  // Whether throw has been made
    var rerollCount by rememberSaveable { mutableStateOf(0) }   // Number of rerolls made (0, 1, or 2)
    var selectedDiceIndices by rememberSaveable { mutableStateOf(setOf<Int>()) }

    // Win condition variables
    var gameOver by rememberSaveable { mutableStateOf(false) }
    var isTieBreaker by rememberSaveable { mutableStateOf(false) }
    var showWinDialog by rememberSaveable { mutableStateOf(false) }
    var humanWins by rememberSaveable { mutableStateOf(false) }

    // Computer strategy variables
    var computerTurn by rememberSaveable { mutableStateOf(false) }
    var initialThrowSum by rememberSaveable { mutableStateOf(0) }
    var afterFirstRerollSum by rememberSaveable { mutableStateOf(0) }
    var computerRerollCount by rememberSaveable { mutableStateOf(0) }
    var diceKeptByComputer by rememberSaveable { mutableStateOf(listOf<Int>()) }

    // Function to check for a winner using dynamic target score
    fun checkForWinner() {
        when {
            // Both players reach target score in the same round
            humanGameScore >= targetScore && computerGameScore >= targetScore -> {
                if (humanGameScore > computerGameScore) {
                    // Human wins the tiebreaker
                    gameOver = true
                    humanWins = true
                    showWinDialog = true
                } else if (computerGameScore > humanGameScore) {
                    // Computer wins the tiebreaker
                    gameOver = true
                    humanWins = false
                    showWinDialog = true
                } else {
                    // Exact tie, go to tiebreaker
                    isTieBreaker = true
                    // Reset scores for the tiebreaker round
                    humanCurrentRoundScore = 0
                    computerCurrentRoundScore = 0
                    // Reset round totals for tiebreaker
                    humanCurrentRoundTotal = 0
                    computerCurrentRoundTotal = 0
                }
            }
            // Human reaches target score first
            humanGameScore >= targetScore -> {
                gameOver = true
                humanWins = true
                showWinDialog = true
            }
            // Computer reaches target score first
            computerGameScore >= targetScore -> {
                gameOver = true
                humanWins = false
                showWinDialog = true
            }
        }
    }

    /**
     * Computer Strategy Explanation:
     *
     * This strategy follows a simple but effective approach:
     *
     * 1. First Reroll Decision:
     *    - The computer keeps all dice with values > 2 (3, 4, 5, 6)
     *    - It rerolls all dice with values 1 or 2
     *    - If ALL dice are 1 or 2, it keeps only the last die and rerolls the others
     *    - This maximizes the chance of improving the score by keeping higher values
     *
     * 2. Second Reroll Decision:
     *    - The computer compares the sum after the first reroll with the initial throw sum
     *    - If the sum after first reroll is <= the initial sum, it does a second reroll
     *    - If the sum improved, it keeps the current dice and doesn't reroll again
     *    - This avoids risking a good result after the first reroll
     *
     * 3. Timing:
     *    - The computer only takes its turn after the human player has either:
     *      a) Clicked the Score button, or
     *      b) Completed their second reroll
     *    - This ensures the computer's turn happens at the appropriate time
     *
     */

    // Function to handle computer's reroll strategy
    fun computerRerollStrategy(computerDice: List<Int>): List<Int> {
        // Indices of dice to keep (not reroll)
        val diceToKeep = mutableListOf<Int>()

        // Check if all dice are 1 or 2
        val allLowValues = computerDice.all { it <= 2 }

        if (allLowValues) {
            // If all values are 1 or 2, keep only the last die
            diceToKeep.add(4) // Index of the last die (0-based indexing)
        } else {
            // Keep dice with values > 2
            computerDice.forEachIndexed { index, value ->
                if (value > 2) {
                    diceToKeep.add(index)
                }
            }
        }

        return diceToKeep
    }

    // Function to decide if computer should do second reroll
    fun shouldDoSecondReroll(initialSum: Int, afterFirstRerollSum: Int): Boolean {
        // Do second reroll if sum after first reroll is <= initial sum
        return afterFirstRerollSum <= initialSum
    }

    // Function to handle computer's turn with round total tracking
    fun handleComputerTurn() {
        if (!computerTurn) return

        // If this is the start of computer's turn
        if (computerRerollCount == 0) {
            // For tiebreaker, no rerolls
            if (isTieBreaker) {
                // Just add the current dice sum to the score
                computerGameScore += computerDice.sum()
                computerTurn = false

                // Check for winner after tiebreaker
                checkForWinner()
                return
            }

            // Store the initial throw sum
            initialThrowSum = computerDice.sum()

            // Decide which dice to keep
            diceKeptByComputer = computerRerollStrategy(computerDice)

            // First reroll - reroll dice not in diceKeptByComputer
            val newDice = computerDice.toMutableList()
            for (i in computerDice.indices) {
                if (!diceKeptByComputer.contains(i)) {
                    newDice[i] = (1..6).random()
                }
            }
            computerDice = newDice

            // Update score after first reroll
            afterFirstRerollSum = computerDice.sum()
            computerCurrentRoundScore += afterFirstRerollSum
            // Update computer's round total
            computerCurrentRoundTotal += afterFirstRerollSum
            computerRerollCount++

            // Decide whether to do second reroll
            if (shouldDoSecondReroll(initialThrowSum, afterFirstRerollSum)) {
                // Second reroll - reroll the same dice again
                val newDice2 = computerDice.toMutableList()
                for (i in computerDice.indices) {
                    if (!diceKeptByComputer.contains(i)) {
                        newDice2[i] = (1..6).random()
                    }
                }
                computerDice = newDice2

                // Update score after second reroll
                val secondRerollSum = computerDice.sum()
                computerCurrentRoundScore += secondRerollSum
                // Update computer's round total
                computerCurrentRoundTotal += secondRerollSum
                computerRerollCount++
            }

            // Add to total score
            computerGameScore += computerCurrentRoundScore

            // Reset for next round including round total
            computerTurn = false
            computerRerollCount = 0
            diceKeptByComputer = emptyList()

            // Check for winner
            checkForWinner()
        }
    }

    // Call handleComputerTurn when computerTurn is true
    LaunchedEffect(computerTurn) {
        if (computerTurn) {
            handleComputerTurn()
        }
    }

    // Win dialog
    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing, user must press Back button */ },
            title = { Text(text = if (humanWins) "You Win!" else "You Lose",
                color = if (humanWins) Color.Green else Color.Red) },
            text = { Text("Game Over! Press the Back button to return to the main menu.") },
            confirmButton = {
                Button(onClick = { showWinDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF212529)),
                    ) {
                    Text("OK")
                }
            }
        )
    }

    // Target Score Change Dialog
    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = {
                showTargetDialog = false
                tempTargetScore = targetScore.toString() // Reset temp value
            },
            title = {
                Text(
                    text = "Change Target Score",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter new target score (minimum 50):",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = tempTargetScore,
                        onValueChange = { newValue ->
                            // Only allow numeric input
                            if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                                tempTargetScore = newValue
                            }
                        },
                        label = { Text("Target Score") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newTarget = tempTargetScore.toIntOrNull()
                        if (newTarget != null && newTarget >= 50) {
                            targetScore = newTarget
                            showTargetDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF212529)
                    )
                ) {
                    Text("Set", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showTargetDialog = false
                        tempTargetScore = targetScore.toString() // Reset temp value
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    // UI Layout with top spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Free space at the top
        Spacer(modifier = Modifier.height(48.dp))

        // Top Header Row with Target and Round info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Target Score Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6C757D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target : $targetScore",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            tempTargetScore = targetScore.toString()
                            showTargetDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF212529)
                        ),
                        modifier = Modifier
                            .height(32.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Set target",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Round Counter
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6C757D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Round : $roundCount",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Player Scores Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Computer Player Score Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6C757D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Computer player: ${String.format("%02d", computerGameScore)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Human Player Score Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6C757D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Human player: ${String.format("%02d", humanGameScore)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Computer Player Section
        Text(
            text = "Computer Player",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Computer Dice Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            computerDice.forEachIndexed { index, value ->
                val isKeptByComputer = diceKeptByComputer.contains(index) && computerTurn
                DiceImage(
                    value = value,
                    isSelected = isKeptByComputer,
                    onClick = {} // Computer dice are not clickable by the player
                )
            }
        }

        // Computer Current Round Total
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF9E9E9E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "Current round total : ${String.format("%02d", computerCurrentRoundTotal)}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Human Dice Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            humanDice.forEachIndexed { index, value ->
                val isSelected = selectedDiceIndices.contains(index)
                // Only allow selection after throw and before first reroll
                val canSelect = hasThrown && rerollCount == 0 && !gameOver && !isTieBreaker

                DiceImage(
                    value = value,
                    isSelected = isSelected,
                    onClick = {
                        if (canSelect) {
                            selectedDiceIndices = if (isSelected) {
                                // Deselect if already selected
                                selectedDiceIndices - index
                            } else {
                                // Only allow selection if we haven't reached 4 selected dice
                                if (selectedDiceIndices.size < 4) {
                                    selectedDiceIndices + index
                                } else {
                                    selectedDiceIndices
                                }
                            }
                        }
                    }
                )
            }
        }

        // Human Current Round Total
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF9E9E9E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "Current round total : ${String.format("%02d", humanCurrentRoundTotal)}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }

        // Human Player Label
        Text(
            text = "You",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Selection Instruction
        Text(
            text = "*Select by clicking on the dice to keep*",
            fontSize = 14.sp,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        if (isTieBreaker) {
            Text(
                text = "TIEBREAKER!",
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Throw button with round count and round total logic
            Button(
                onClick = {
                    // Increment round count when throw is clicked
                    roundCount++

                    // Reset for new throw
                    humanDice = List(5) { (1..6).random() }
                    computerDice = List(5) { (1..6).random() }

                    // Reset and initialize round scores and totals
                    humanCurrentRoundScore = humanDice.sum()
                    computerCurrentRoundScore = computerDice.sum()

                    // Initialize round totals with first throw
                    humanCurrentRoundTotal = humanDice.sum()
                    computerCurrentRoundTotal = computerDice.sum()

                    // Reset game state
                    hasThrown = true
                    rerollCount = 0

                    // Reset selected dice
                    selectedDiceIndices = emptySet()

                    // For tiebreaker, immediately add scores and check winner
                    if (isTieBreaker) {
                        humanGameScore += humanDice.sum()
                        computerTurn = true
                    }
                },
                enabled = !hasThrown && !gameOver, // Only enabled before throw and if game not over
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212529)
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Throw",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Reroll button with round total tracking
            Button(
                onClick = {
                    if (rerollCount == 0) {
                        // First reroll: only reroll unselected dice
                        // Make sure at least one die is selected
                        if (selectedDiceIndices.isNotEmpty()) {
                            val newDice = humanDice.toMutableList()

                            // Only reroll unselected dice
                            for (i in humanDice.indices) {
                                if (!selectedDiceIndices.contains(i)) {
                                    newDice[i] = (1..6).random()
                                }
                            }

                            humanDice = newDice
                            val rerollSum = humanDice.sum()
                            humanCurrentRoundScore += rerollSum
                            // Add to round total
                            humanCurrentRoundTotal += rerollSum
                            rerollCount++
                        }
                    } else if (rerollCount == 1) {
                        // Second reroll: only reroll the dice that were selected in the first reroll
                        val newDice = humanDice.toMutableList()

                        // Only reroll the dice that were selected in the first reroll
                        for (i in selectedDiceIndices) {
                            newDice[i] = (1..6).random()
                        }

                        humanDice = newDice
                        val rerollSum = humanDice.sum()
                        humanCurrentRoundScore += rerollSum
                        // Add to round total
                        humanCurrentRoundTotal += rerollSum
                        rerollCount++

                        // Automatically add score after second reroll
                        humanGameScore += humanCurrentRoundScore

                        // Reset round state including round total
                        hasThrown = false
                        selectedDiceIndices = emptySet() // Reset selections
                        humanCurrentRoundTotal = 0 // Reset round total for next round

                        // Trigger computer's turn after human's second reroll
                        computerTurn = true
                    }
                },
                enabled = hasThrown && rerollCount < 2 &&
                        (rerollCount == 1 || selectedDiceIndices.isNotEmpty()) &&
                        !gameOver && !isTieBreaker,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212529)
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Reroll",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Score button with round total reset
            Button(
                onClick = {
                    // Add current round scores to total scores
                    humanGameScore += humanCurrentRoundScore

                    // Reset round state including round total
                    hasThrown = false
                    selectedDiceIndices = emptySet()
                    humanCurrentRoundTotal = 0 // Reset round total for next round

                    // Trigger computer's turn after human scores
                    computerTurn = true
                },
                enabled = hasThrown && !gameOver && !isTieBreaker, // Only enabled after a throw and if game not over
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF212529)
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Score",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DiceImage(
    value: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Image(
        painter = painterResource(id = getDiceImage(value)),
        contentDescription = "Dice",
        modifier = Modifier
            .size(64.dp)
            // Add a border or background for selected dice
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.Red, RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            )
            // Make the dice clickable
            .clickable(onClick = onClick)
    )
}

fun getDiceImage(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice1
        2 -> R.drawable.dice2
        3 -> R.drawable.dice3
        4 -> R.drawable.dice4
        5 -> R.drawable.dice5
        else -> R.drawable.dice6
    }
}