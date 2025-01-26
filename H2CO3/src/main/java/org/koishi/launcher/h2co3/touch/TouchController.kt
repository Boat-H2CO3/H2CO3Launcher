package org.koishi.launcher.h2co3.touch

import android.content.Context
import android.os.Vibrator
import android.system.Os
import android.util.SparseIntArray
import android.view.MotionEvent
import org.koishi.launcher.h2co3core.util.Logging
import top.fifthlight.touchcontroller.proxy.client.LauncherProxyClient
import top.fifthlight.touchcontroller.proxy.client.android.SimpleVibrationHandler
import top.fifthlight.touchcontroller.proxy.client.android.transport.UnixSocketTransport
import top.fifthlight.touchcontroller.proxy.data.Offset
import java.util.logging.Level

class TouchController(context: Context, val width: Int, val height: Int) {
    private var client: LauncherProxyClient? = null
    private val socketName = "H2CO3Launcher"
    private val pointerIdMap = SparseIntArray()
    private var nextPointerId = 1

    init {
        createProxy(context)
    }

    private fun createProxy(context: Context) {
        try {
            val transport = UnixSocketTransport(socketName)
            Os.setenv("TOUCH_CONTROLLER_PROXY_SOCKET", socketName, true)
            client = LauncherProxyClient(transport)
            val vibrator = context.getSystemService<Vibrator>(Vibrator::class.java)
            val handler = SimpleVibrationHandler(vibrator)
            client?.vibrationHandler = handler
            client?.run()
        } catch (ex: Throwable) {
            Logging.LOG.log(
                Level.WARNING,
                "TouchController: TouchController proxy client create failed",
                ex
            )
        }
    }

    private fun MotionEvent.getOffset(index: Int) = Offset(
        getX(index) / width,
        getY(index) / height
    )

    fun handleTouchEvent(event: MotionEvent) {
        val client = client ?: return
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                handleActionDown(event, 0)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                handleActionDown(event, event.actionIndex)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val pointerId = pointerIdMap.get(event.getPointerId(i))
                    client.addPointer(pointerId, event.getOffset(i))
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                client.clearPointer()
                pointerIdMap.clear()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerId = pointerIdMap.get(event.getPointerId(event.actionIndex))
                if (pointerId != 0) {
                    pointerIdMap.delete(pointerId)
                    client.removePointer(pointerId)
                }
            }
            else -> {
                // Handle unexpected action if necessary
            }
        }
    }

    private fun handleActionDown(event: MotionEvent, index: Int) {
        val pointerId = nextPointerId++
        pointerIdMap.put(event.getPointerId(index), pointerId)
        client?.addPointer(pointerId, event.getOffset(index))
    }
}