package se.jku.at.inout;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class InOutHelper {

    public static String runMainAndCaptureOutput(String fullyQualifiedClassName, String simulatedInput)
            throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "java",
                "-Dfile.encoding=UTF-8",
                "-cp",
                "target/classes",
                fullyQualifiedClassName
        );

        pb.redirectErrorStream(true);

        System.out.println("Executing command: " + pb.command());

        Process process = pb.start();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write(simulatedInput);
            writer.flush();
            writer.close();
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("Process exited with code: " + exitCode);
            System.out.println("Captured Output: " + output);
            throw new RuntimeException("Subprocess exited with code: " + exitCode);
        }

        return output.toString();
    }
}
