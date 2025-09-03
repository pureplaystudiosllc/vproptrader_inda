package com.futureharvest.vproptrader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.google.firebase.analytics.FirebaseAnalytics
import com.facebook.appevents.AppEventsLogger


class CommonActionsActivity : ComponentActivity() {

    private lateinit var logger: AppEventsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = AppEventsLogger.newLogger(this)
        FirebaseAnalytics.getInstance(this).logEvent("enter_common_actions", null)

        val actionType = intent.getStringExtra("action_type")

        when (actionType) {
            "shareApp" -> shareApp()
            "openMore" -> openMore()
            "openPrivacy" -> openPrivacy()
            "openWebsiteWithToken" -> openWebsiteWithToken()
            "openWithdrawWithToken" -> openWithdrawWithToken()
            "openDepositWithToken" -> openDepositWithToken()
            else -> finish()
        }
    }


    private fun shareApp() {
        val packageName = applicationContext.packageName
        val shareText = "Check out this Amazing app: https://play.google.com/store/apps/details?id=$packageName"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            Intent.setType = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        try {
            startActivity(Intent.createChooser(shareIntent, "Share App"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finish()
        }
    }


    private fun openMore() {
        val packageName = applicationContext.packageName
        val uri = "https://play.google.com/store/apps/details?id=$packageName".toUri()
        val playIntent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(playIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finish()
        }
    }


    private fun openPrivacy() {
        val privacyUrl = getString(R.string.privacy_policy_url)
        val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finish()
        }
    }

    private fun openWebsiteWithToken() {
        try {
            logBattleTheMonsterEvent()
        } catch (e: Exception) {
           Log.e("FacebookLogger", "Error logging BattleTheMonster event: $e")
        }
        val token = intent.getStringExtra("token")
        println("token: $token")
        if (token.isNullOrEmpty()) {
            openWebsite("https://vproptrader.com")
            return
        }

        val websiteUrl = "https://vproptrader.com/#/account/add-challenge?token=$token"
        openWebsite(websiteUrl)
    }

    private fun openWithdrawWithToken() {
        try {
            logBattleTheMonsterEvent()
        } catch (e: Exception) {
            Log.e("FacebookLogger", "Error logging BattleTheMonster event: $e")
        }
        val token = intent.getStringExtra("token")
        println("token: $token")
        if (token.isNullOrEmpty()) {
            openWebsite("https://vproptrader.com")
            return
        }

        val websiteUrl = "https://vproptrader.com/#/me/personal-center/withdraw-funds?token=$token"
        openWebsite(websiteUrl)
    }

    private fun openDepositWithToken() {
        try {
            logBattleTheMonsterEvent()
        } catch (e: Exception) {
            Log.e("FacebookLogger", "Error logging BattleTheMonster event: $e")
        }
        val token = intent.getStringExtra("token")
        println("token: $token")
        if (token.isNullOrEmpty()) {
            openWebsite("https://vproptrader.com")
            return
        }

        val websiteUrl = "https://vproptrader.com/#/me/personal-center/deposit-funds?token=$token"
        openWebsite(websiteUrl)
    }



    private fun logBattleTheMonsterEvent() {
        logger.logEvent("openWebsite")
        FirebaseAnalytics.getInstance(this).logEvent("openWebsite", null)
    }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            finish()
        }
    }

}
