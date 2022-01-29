package com.codebase.caller

import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.CallResult
import io.crossbar.autobahn.wamp.types.ExitInfo
import sun.rmi.runtime.Log
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class AutobahnCalling {
    fun main(args: Array<String>) {
        connect()
    }

    private fun connect(): Int {
        val wampSession = Session(Executors.newFixedThreadPool(1))
        val start = System.currentTimeMillis().toDouble()
        wampSession.addOnJoinListener { session, details ->
            println("Detail ")
            println("called")
            val aList: MutableList<CompletableFuture<CallResult>> = ArrayList()

            for (i in 0..100000) {
                aList.add(session.call("simplethings", "arg1"))
//                println(session.call("simplethings", "arg1").get())
            }
            val array: Array<CompletableFuture<CallResult>> = aList.toTypedArray()
            CompletableFuture.allOf(*array)
                .whenComplete { unused: Void?, throwable: Throwable? ->
                    val finish = System.currentTimeMillis().toDouble()
                    val timeElapsed = finish - start
                    println(timeElapsed / 1000)
                }
        }
        val client = io.crossbar.autobahn.wamp.Client(wampSession, "ws://localhost:8080/ws", "realm1")
        val exitFuture: CompletableFuture<ExitInfo> = client.connect()
        return try {
            val exitInfo: ExitInfo = exitFuture.get()
            exitInfo.code
        } catch (e: Exception) {
            1
        }
    }
}


fun main(args: Array<String>) {
    AutobahnCalling().main(args)
}

