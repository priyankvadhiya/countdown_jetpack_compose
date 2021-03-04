package pl.bartoszostrowski.countdowntimer

import android.os.CountDownTimer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun TimerScreen() {
    val seconds = remember(calculation = { mutableStateOf(0) })
    val minutes = remember(calculation = { mutableStateOf(10) })
    val userCreatedMinutes = remember(calculation = { mutableStateOf(10) })

    val timer: MutableState<CountDownTimer?> = remember(calculation = { mutableStateOf(null) })

    val btnImage: MutableState<ImageVector> =
        remember(calculation = { mutableStateOf(Icons.Filled.PlayArrow) })

    val progress = remember(calculation = { mutableStateOf(0.0f) })
    val animatedProgress = animateFloatAsState(
        targetValue = progress.value,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    ).value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Set Minutes",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.size(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (userCreatedMinutes.value > 1) {
                            userCreatedMinutes.value = userCreatedMinutes.value - 1
                        }
                    },
                    Modifier.background(color = Color.Blue, shape = CircleShape,)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Remove",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = userCreatedMinutes.value.toString(),
                    style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.size(20.dp))
                IconButton(
                    onClick = {
                        userCreatedMinutes.value = userCreatedMinutes.value + 1
                    },
                    Modifier.background(color = Color.Blue, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.size(40.dp))
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(5.dp, Color.LightGray),
                    modifier = Modifier.size(250.dp)
                ) {}
                CircularProgressIndicator(
                    progress = animatedProgress, Modifier.size(250.dp),
                    color = Color.Blue,
                    strokeWidth = 5.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "00:${String.format("%02d", minutes.value)}:${
                            String.format(
                                "%02d",
                                seconds.value
                            )
                        }",
                        style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                    FloatingActionButton(onClick = {
                        if (timer.value == null) {
                            btnImage.value = Icons.Filled.Stop
                            timer.value = getCountDownTimer(
                                userCreatedMinutes,
                                seconds,
                                minutes,
                                progress
                            ).start()
                        } else {
                            timer.value?.cancel()
                            btnImage.value = Icons.Filled.PlayArrow
                            timer.value = null
                            progress.value = 0f
                            seconds.value = 0
                            minutes.value = 10
                            userCreatedMinutes.value = 10
                        }
                    }) {
                        Icon(imageVector = btnImage.value, contentDescription = "Play")
                    }
                }
            }
        }

    }
}

fun getCountDownTimer(
    userCreatedMinutes: MutableState<Int>,
    seconds: MutableState<Int>,
    minutes: MutableState<Int>,
    progress: MutableState<Float>
): CountDownTimer {
    val totalTimeSeconds = userCreatedMinutes.value * 60 // minute * 60 + second
    return object : CountDownTimer(totalTimeSeconds.toLong() * 1000 + 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val totalSeconds = (millisUntilFinished / 1000).toInt()
            progress.value =
                ((totalTimeSeconds - totalSeconds).toDouble() / totalTimeSeconds).toFloat()
            seconds.value = totalSeconds % 60
            minutes.value = totalSeconds / 60
        }

        override fun onFinish() {

        }
    }
}
