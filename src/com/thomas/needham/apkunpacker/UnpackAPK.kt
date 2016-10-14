/*
    The MIT License (MIT)

    apkUnpacker Copyright (c) 2016 thoma

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
*/
package com.thomas.needham.apkunpacker

import java.io.File
import kotlin.system.exitProcess

/**
 * Created by thoma on 11/10/2016.
 */
object UnpackAPK {
    @JvmStatic var file : File? = null

    @JvmStatic val CheckAPKFile = { path : String ->
        println("Checking Integrity Of APK File")
        file = File(path)
        if(!file?.exists()!! || !file?.extension?.equals("apk",true)!!){
            println("Invalid APK File: " + path)
            exitProcess(1)
        }
        println("File Is Valid APK")
    }
    @JvmStatic val ConvertAPKToZip = { path : String ->
        println("Converting APK To ZIP")
        val dest = file?.copyTo(File(path.substring(0..path.length - 4) + "zip"),true) // Convert APK To .ZIP
        if(!dest?.exists()!!){
            println("Could Not Convert APK To ZIP")
        }
        println("APK File Successfully Converted To ZIP")
    }
    @JvmStatic val UnzipAPK = { path: String ->
        val zip = File(path.substring(0..path.length - 4) + "zip")
        println("Unzipping APK")
        val zipProcBuilder = ProcessBuilder(mutableListOf("cmd", "/C", "7z", "e", "-y", "${zip.path}", "-o${zip.nameWithoutExtension}"))
        zipProcBuilder.inheritIO()
        val zipProc = zipProcBuilder.start()
        while(zipProc.isAlive) {}
        println("APK Successfully Unzipped")
    }

    @JvmStatic val UnpackResources = { path : String ->
        println("Unpacking Resources...")
        val apkToolProcBuilder = ProcessBuilder(mutableListOf("cmd", "/C", "java", "-jar", "lib/apktool.jar", "d", "${path}", "-o", "${file?.nameWithoutExtension + "-res"}"))
        apkToolProcBuilder.inheritIO()
        val apkToolProc = apkToolProcBuilder.start()
        while(apkToolProc.isAlive) {}
        println("Resources Successfully Unpacked")
    }
    @JvmStatic val ConvertDexToJar = { path : String ->
        val abs = file?.absolutePath!!
        val dir = File("${abs.substring(0..abs.length - file?.name?.length!! - 2)}" + "/" + file?.nameWithoutExtension)
        println("Converting DEX Files To JAR...")
        if(dir.isDirectory){
            for(f: File in dir.listFiles()){
                if(f.extension.equals("dex")){
                    val jar = f.path.substring(0..f.path.length - 4 ) + "jar"
                    val dexProcBuilder = ProcessBuilder(mutableListOf("cmd", "/C" ,"lib\\d2j-dex2jar.bat", "${f.path}", "-o", "${jar}"))
                    dexProcBuilder.inheritIO()
                    val dexProc = dexProcBuilder.start()
                    while(dexProc.isAlive) {}
                    println("DEX File: ${f.nameWithoutExtension} Converted Successfully")
                }
            }
            println("All DEX Files Converted Successfully")
        }
        else{
            println("Invalid App Directory: ${dir}")
            exitProcess(2)
        }
    }
    @JvmStatic val DecompileJars = { path: String ->
        val abs = file?.absolutePath!!
        val dir = File("${abs.substring(0..abs.length - file?.name?.length!! - 2)}" + "/" + file?.nameWithoutExtension)
        println("Decompiling JAR Files")
        if(dir.isDirectory){
            for(f: File in dir.listFiles()){
                if(f.extension.equals("jar")){
                    val decProcBuilder = ProcessBuilder(mutableListOf("cmd", "/C" ,"java", "-jar", "lib/fernflower.jar",
                            "${f.path}", "${dir.path + "/src" }"))
                    decProcBuilder.inheritIO()
                    val decProc = decProcBuilder.start()
                    while(decProc.isAlive) {}
                    println("JAR File: ${f.nameWithoutExtension} Decompiled Successfully")
                }
            }
            println("All JAR Files Decompiled Successfully")
        }
        else{
            println("Invalid App Directory: ${dir}")
            exitProcess(2)
        }
    }
}