package zad1

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select

suspend fun produce(brokers: ArrayList<Channel<Int>>, N: Int) {
    for (i in 0..<N) {
        delay(1000)
        select {
            val rand = (0..<10).random()
            brokers.random().onSend(rand) {
                println("sent $rand");
            }
        }
    }
}

suspend fun consume(brokers: ArrayList<Channel<Int>>, N: Int) {
    for (num in 0..<N) {
        select {
            brokers.forEach {
                ch -> ch.onReceive {
                    value -> println("received $value")
                }
            }
        }
    }
}

suspend fun broker(producerCh: Channel<Int>, consumerCh: Channel<Int>, I: Int) {
    while (true) {
        val value = producerCh.receive()
        println("$I brokers $value")
        consumerCh.send(value)
    }
}

fun main() {
    val producersChannels = ArrayList<Channel<Int>>()
    val consumersChannels = ArrayList<Channel<Int>>()

    val N = 10
    for(i in 0..<N) {
        producersChannels.add(Channel<Int>())
        consumersChannels.add(Channel<Int>())
    }
    runBlocking {
        for (i in 0..<N) {
            launch {
                broker(producersChannels[i], consumersChannels[i], i)
            }
        }

        launch {
            produce(producersChannels, 10)
        }
        launch {
            consume(consumersChannels, 10)
        }
    }
}