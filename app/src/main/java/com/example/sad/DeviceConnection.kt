package com.example.sad

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectionThread(device: BluetoothDevice, context: Context, bluetoothAdapter: BluetoothAdapter, uuid: UUID) : Thread() {
    private val uuid = uuid
    private val context = context
    private val bluetoothAdapter = bluetoothAdapter

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(uuid)
    }
    public override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery()

        mmSocket?.connect()
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Toast.makeText(context, "Could not close the client socket", Toast.LENGTH_SHORT).show()
        }
    }
}