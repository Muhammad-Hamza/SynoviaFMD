package com.scanapp.util



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle


inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Activity.launchActivityFinish(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent, options)
    finish()
}

inline fun <reified T : Any> Activity.launchActivityForResult(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, 0, options)
}
inline fun <reified T : Any> Activity.launchActivityFromRightForResult(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, 0, options)
}

inline fun <reified T : Any> Activity.launchActivityTop(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    //    startActivity(intent, requestCode, options)
    startActivity(intent, options)
}

inline fun <reified T : Any> Activity.launchActivityTopForResult(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    //    startActivity(intent, requestCode, options)
    startActivityForResult(intent,0, options)
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    startActivity(intent, options)
    //    startActivity(intent)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)