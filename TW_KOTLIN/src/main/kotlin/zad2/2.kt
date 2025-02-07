package zad2

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun produce(broker: Channel<Int>, N: Int) {
    for (i in 0..<N) {
        delay(2000)
        val rand = (0..<10).random()
        broker.send(rand)
        print("($rand) P -> ")
    }
    broker.close()
}

suspend fun consume(broker: Channel<Int>) {
    var i = 0
    for(recv in broker){
        delay(100)
        println("C ($recv)")
        println("i = ${++i}")
        System.out.flush()
    }
}

suspend fun broker(from: Channel<Int>, to: Channel<Int>, I: Int) {
    for(recv in from){
        delay(100)
        to.send(recv)
        print("$I -> ")
        System.out.flush()
    }
    to.close()
}

fun main() {
    val channels = ArrayList<Channel<Int>>()

    val N = 10
    for (i in 0..<N + 1) {
        channels.add(Channel())
    }

    runBlocking {
        for (i in 0..<N) {
            launch {
                broker(channels[i], channels[i + 1], i)
            }
        }
        launch {
            produce(channels[0], 5)
        }
        launch {
            consume(channels[N])
        }
    }
}
