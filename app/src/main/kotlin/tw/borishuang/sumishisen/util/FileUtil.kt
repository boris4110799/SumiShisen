package tw.borishuang.sumishisen.util

import android.content.Context

/**
 * The utility of file.
 */
object FileUtil {
    /**
     * Read data from file.
     */
    fun readData(context: Context): List<String> {
        val dataList = mutableListOf<String>()

        //If file exist then load data, otherwise create the file
        if (context.getFileStreamPath("data.txt").exists()) {
            context.openFileInput("data.txt").bufferedReader().useLines {
                dataList.addAll(it)
            }
        }
        else {
            context.getFileStreamPath("data.txt").createNewFile()
            context.openFileOutput("data.txt", Context.MODE_PRIVATE).use {
                it.write("".toByteArray())
            }
        }

        return dataList
    }

    /**
     * Write data to file.
     */
    fun writeData(context: Context, dataSet: Set<String>) {
        context.openFileOutput("data.txt", Context.MODE_PRIVATE).use {
            it.write(dataSet.joinToString(separator = "\n").toByteArray())
        }
    }
}