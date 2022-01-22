package me.insanitydev.osuexplorer;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OsuExplorer {

    private static boolean recursive = false;
    private static final List<String> fileExtensions = new ArrayList<>();

    public static void main(String[] args) {
        //Potential options
        //-h, --help
        //-v, --version
        //-r, --recursive
        //-osz, --osz
        //-osk, --osk
        //-osb, --osb

        Options options = new Options();
        options.addOption("h", "help", false, "Prints this help message");
        options.addOption("v", "version", false, "Prints the version of this program");
        options.addOption("r", "recursive", false, "Recursively search for osu files");
        options.addOption("osz", "osz", false, "Search for osu! beatmap archive files");
        options.addOption("osu", "osu", false, "Search for osu! beatmap files");
        options.addOption("osk", "osk", false, "Search for osu! skin files");
        options.addOption("osb", "osb", false, "Search for osu! storyboard files");
        options.addOption("dir", "directory", true, "The directory to search in");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);


            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("OsuExplorer", options);
                System.exit(0);
            }

            if (cmd.hasOption("v")) {
                System.out.println("OsuExplorer v1.0");
                System.exit(0);
            }

            if (cmd.hasOption("r")) {
                System.out.println("Recursive search enabled");
                recursive = true;
            }

            if (cmd.hasOption("osz")) {
                System.out.println("Searching for osu! beatmap archive files");
                fileExtensions.add("osz");
            }

            if (cmd.hasOption("osu")) {
                System.out.println("Searching for osu! beatmap files");
                fileExtensions.add("osu");
            }

            if (cmd.hasOption("osk")) {
                System.out.println("Searching for osu! skin files");
                fileExtensions.add("osk");
            }

            if (cmd.hasOption("osb")) {
                System.out.println("Searching for osu! storyboard files");
                fileExtensions.add("osb");
            }

            if (cmd.hasOption("dir")) {
                System.out.println("Will be searching in directory: " + cmd.getOptionValue("dir"));
            } else {
                System.out.println("No directory specified, will be searching in current directory");
            }

            String filePath = (cmd.getOptionValue("dir") == null) ? System.getProperty("user.dir") : cmd.getOptionValue("dir");

            //Default osu exe path
            String osuPath = System.getenv("LOCALAPPDATA") + "\\osu!\\osu!.exe";

            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("The directory does not exist! Quitting...");
                System.exit(1);
            }

            if (fileExtensions.isEmpty()) {
                System.out.println("No file extensions specified! Only searching for osz files...");
                fileExtensions.add("osz");
            }

            File[] files = file.listFiles();
            List<File> fileList = new ArrayList<>();

            if (files == null) {
                System.out.println("The directory is empty! Quitting...");
                System.exit(1);
            }

            for (File f : files) {
                if (f.isFile()) {
                    if (fileExtensions.contains(f.getName().substring(f.getName().lastIndexOf(".") + 1))) {
                        fileList.add(f);
                    }
                } else if (f.isDirectory() && recursive) {
                    iterate(f, fileList);
                }
            }

            for (File osuFile : fileList) {
                System.out.println("Running file: " + osuFile.getName());
                try {
                    Runtime.getRuntime().exec( osuPath + " " + osuFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Could not run file: " + osuFile.getName());
                    System.out.println("Report this to the developer on GitHub!");
                }
            }

            System.out.println("Finished running all files!");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing command line arguments, report the error to the developer");
        }
    }

    private static void iterate(File file, List<File> fileList) {
        if (file.isFile()) {
            if (fileExtensions.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1))) {
                fileList.add(file);
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    iterate(f, fileList);
                }
            } else {
                System.out.println("Warning: Couldn't iterate over folder " + file.getName());
            }
        }
    }

    //https://www.baeldung.com/java-file-extension
    public static Optional<String> getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

}
