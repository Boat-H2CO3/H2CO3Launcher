package org.koishi.launcher.h2co3.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.control.view.KeycodeView
import org.koishi.launcher.h2co3.control.view.KeycodeView.OnKeycodeChangeListener
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout
import java.util.Objects
import java.util.function.Consumer

class SelectKeycodeDialog(
    context: Context,
    private val list: ObservableList<Int>,
    private val singleSelection: Boolean,
    mouse: Boolean
) : H2CO3LauncherDialog(context) {
    private val selectionProperty = SimpleIntegerProperty(this, "selection", -1)

    private val positive: H2CO3LauncherButton

    private val container: H2CO3LauncherLinearLayout

    private var onConfirm: (SelectKeycodeDialog)->Unit = {}

    fun selectionProperty(): SimpleIntegerProperty {
        return selectionProperty
    }

    init {
        setCancelable(false)
        setContentView(R.layout.dialog_select_keycode)

        if (singleSelection) {
            selectionProperty.set(list[0])
        }

        container = findViewById(R.id.parent_layout)!!
        initializeAllButtons(container)
        checkSelection(container)

        val mouseLayout = checkNotNull(findViewById<H2CO3LauncherLinearLayout>(R.id.mouse))
        mouseLayout.visibility = if (mouse) View.VISIBLE else View.GONE

        positive = findViewById(R.id.positive)!!
        positive.setOnClickListener{
            onConfirm(this)
            dismiss()
        }
    }

    constructor(
        context: Context,
        list: ObservableList<Int>,
        singleSelection: Boolean,
        mouse: Boolean,
        onConfirm: (SelectKeycodeDialog)->Unit
    ) : this(context, list, singleSelection, mouse) {
        this.onConfirm = onConfirm
    }

    private fun checkSelection(container: ViewGroup) {
        for (i in 0 until container.childCount) {
            if (container.getChildAt(i) is KeycodeView) {
                val l = ArrayList<Int>()
                if (singleSelection) {
                    l.add(selectionProperty.get())
                } else {
                    l.addAll(list)
                }
                (container.getChildAt(i) as KeycodeView).checkSelection(l)
            } else if (container.getChildAt(i) is ViewGroup) {
                checkSelection(container.getChildAt(i) as ViewGroup)
            }
        }
    }

    private fun initializeAllButtons(container: ViewGroup) {
        for (i in 0 until container.childCount) {
            if (container.getChildAt(i) is KeycodeView) {
                (container.getChildAt(i) as KeycodeView).setOnKeycodeChangeListener(object :
                    OnKeycodeChangeListener {
                    override fun onKeycodeAdd(view: KeycodeView, keycode: Int) {
                        if (singleSelection) {
                            selectionProperty.set(keycode)
                            checkSelection(this@SelectKeycodeDialog.container)
                        } else {
                            list.add(keycode)
                        }
                    }

                    override fun onKeycodeRemove(view: KeycodeView, keycode: Int) {
                        if (singleSelection) {
                            view.setSelectedWithoutCallback(true)
                        } else {
                            for (j in list.indices) {
                                if (list[j] == keycode) {
                                    list.removeAt(j)
                                    break
                                }
                            }
                        }
                    }
                })
            } else if (container.getChildAt(i) is ViewGroup) {
                initializeAllButtons(container.getChildAt(i) as ViewGroup)
            }
        }
    }
}
