package com.example.foikadrovskanfc.utils

import android.content.Context
import android.nfc.NfcAdapter

class NfcUtils(private val context: Context) {

    fun hasNfcCapability(): Boolean {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return adapter != null
    }

    fun isNfcEnabled(): Boolean {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return adapter != null && adapter.isEnabled
    }
}