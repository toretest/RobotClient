package di.optimize.robotclient

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun RobotControlScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Videovindu (Placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Kamerastrøm (sanntid)", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Instrumentpanel (4 verdier)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InstrumentValue("Fart", 10)
            InstrumentValue("Temp", 45)
            InstrumentValue("Volt", 12)
            InstrumentValue("Signal", 88)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Gass og Styring
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gass (Vertikal slider/joystick)
            ThrottleControl()

            // Styring (Knapper i starten, kan utvides til ratt)
            SteeringControl()
        }
    }
}

@Composable
fun InstrumentValue(label: String, initialValue: Int) {
    var value by remember { mutableStateOf(initialValue) }
    // Enkel animasjon av tall (placeholder for ekte data)
    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(2000)
            value += (-2..2).random()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 20.sp, style = MaterialTheme.typography.bodyLarge)
        Text(text = label, fontSize = 12.sp, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ThrottleControl() {
    var offsetY by remember { mutableStateOf(0f) }
    val maxOffset = 300f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Gass")
        Box(
            modifier = Modifier
                .height(200.dp)
                .width(60.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val nextOffset = (offsetY + dragAmount.y).coerceIn(-maxOffset/2, maxOffset/2)
                                offsetY = nextOffset
                                val power = -(offsetY / (maxOffset/2)) * 100
                                Log.d("RobotControl", "Gass: ${power.roundToInt()}%")
                            },
                            onDragEnd = {
                                offsetY = 0f
                                Log.d("RobotControl", "Gass: 0% (Sluppet)")
                            }
                        )
                    }
            )
        }
    }
}

@Composable
fun SteeringControl() {
    var steerX by remember { mutableStateOf(0f) }
    val maxSteer = 200f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Styring")
        Box(
            modifier = Modifier
                .height(60.dp)
                .width(200.dp)
                .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(steerX.roundToInt(), 0) }
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val nextOffset = (steerX + dragAmount.x).coerceIn(-maxSteer/2, maxSteer/2)
                                steerX = nextOffset
                                val turn = (steerX / (maxSteer/2)) * 100
                                Log.d("RobotControl", "Sving: ${turn.roundToInt()}%")
                            },
                            onDragEnd = {
                                steerX = 0f
                                Log.d("RobotControl", "Sving: 0% (Sluppet)")
                            }
                        )
                    }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { Log.d("RobotControl", "Knapp: Venstre") }) {
                Text("V")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { Log.d("RobotControl", "Knapp: Rett frem") }) {
                Text("R")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { Log.d("RobotControl", "Knapp: Høyre") }) {
                Text("H")
            }
        }
    }
}
