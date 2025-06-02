package br.ufrn.caze.holterci.domain.utils

import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Utility for executing shell commands and capturing their output.
 */
@Component
class CommandExecutorUtil {

    /**
     * Executes a command and returns its standard output as a string.
     *
     * @param command The command and arguments to execute.
     * @return The output of the command.
     * @throws RuntimeException if the command fails or times out.
     */
    fun runCommand(vararg command: String): String {
        val process = ProcessBuilder(*command).start()

        val stdOut = StringBuilder()
        val stdErr = StringBuilder()

        val stdoutThread = Thread {
            process.inputStream.bufferedReader().useLines { lines -> lines.forEach { stdOut.append(it).append("\n") } }
        }
        val stderrThread = Thread {
            process.errorStream.bufferedReader().useLines { lines -> lines.forEach { stdErr.append(it).append("\n") } }
        }

        stdoutThread.start()
        stderrThread.start()

        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroy()
            throw RuntimeException("Command timed out: ${command.joinToString(" ")}")
        }

        stdoutThread.join()
        stderrThread.join()

        if (process.exitValue() != 0) {
            throw RuntimeException("Command failed with exit code: ${process.exitValue()}\nError: $stdErr")
        }

        println("[OK] Scan completed, exit code: ${process.exitValue()}")

        return stdOut.toString()
    }
}