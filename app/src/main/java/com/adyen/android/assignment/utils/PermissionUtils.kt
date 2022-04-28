package com.adyen.android.assignment.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import java.security.Permissions


class PermissionUtils(var activity: Activity, var permission: String) {
    @RequiresApi(Build.VERSION_CODES.M)
    fun neverAskAgainSelected(): Boolean {
        val prevShouldShowStatus = getRationaleDisplayStatus(activity, permission)
        val currShouldShowStatus: Boolean = activity.shouldShowRequestPermissionRationale(
            permission
        )
        return prevShouldShowStatus != currShouldShowStatus
    }

    fun setShouldShowStatus() {
        val genPrefs: SharedPreferences =
            activity.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        val editor = genPrefs.edit()
        editor.putBoolean(permission, true)
        editor.apply()
    }

    private fun getRationaleDisplayStatus(context: Context, permission: String?): Boolean {
        val genPrefs: SharedPreferences =
            context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        return genPrefs.getBoolean(permission, false)
    }

     fun displayNeverAskAgainDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setMessage(
            "Please permit the permission through Settings screen. Select Permissions -> Enable permission".trimIndent()
        )
        builder.setCancelable(false)
        builder.setPositiveButton("Permit Manually",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            })
        builder.show()
    }
}