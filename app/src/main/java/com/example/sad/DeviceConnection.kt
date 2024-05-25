package com.example.sad

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothSocketException
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectionThread(device: BluetoothDevice, context: Context, bluetoothAdapter: BluetoothAdapter) : Thread() {
    private val uuid = device.uuids[0].uuid
    private val device = device
    private val context = context
    private val bluetoothAdapter = bluetoothAdapter

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(uuid)
    }
    public override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery()

        try {
            mmSocket?.connect()
        } catch (e: Exception){
            Log.e("CONNECTION", "Error while connecting")
//            Toast.makeText(context, "ERROR while connecting", Toast.LENGTH_LONG).show()
        }
        Log.i("CONNECTION", "Connected to ${this.device.name}")
//        Toast.makeText(context, "Connected to ${this.device.name}", Toast.LENGTH_LONG).show()
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e("CONNECTION", "Could not close the client socket")
//            Toast.makeText(context, "Could not close the client socket", Toast.LENGTH_SHORT).show()
        }
    }
}