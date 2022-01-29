package com.codebase.caller

import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.ExitInfo
import io.crossbar.autobahn.wamp.types.Registration
import io.crossbar.autobahn.wamp.types.SessionDetails
import java.util.concurrent.CompletableFuture


class Callee {
    fun main(args: Array<String>) {
        connect()
    }
    private fun connect(): Int {
        val wampSession = Session()
        wampSession.addOnJoinListener { session: Session, details: SessionDetails? ->
            println(details)
            session.register("simplethings") { o: Any? -> "ABU" }
                .whenComplete { registration: Registration?, throwable: Throwable? ->
                    println("Registered,,,,,")
                }
        }
        val client = Client(wampSession, "ws://localhost:8080/ws", "realm1")
        val exitFuture: CompletableFuture<ExitInfo> = client.connect()
        return try {
            val exitInfo = exitFuture.get()
            exitInfo.code
        } catch (e: Exception) {
            1
        }
    }
}

fun main(args: Array<String>) {
    Callee().main(args)
}