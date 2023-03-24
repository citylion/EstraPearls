package net.estra.EstraPearls.damage;

import org.bukkit.Bukkit;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DamageFunctionDS {
    //Data saver
    //poor mans config.. or a permanently duck taped together solution

    static File pluginFolder;
    static String filename = "PearlFunctionConfig.txt";

    static String subfoldername = "EstraPearls";



    /*
    static {
        URL url = DataSaver.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            pluginFolder = new File(url.toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }*/

    static {
        URL url = DamageFunctionDS.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            pluginFolder = new File(url.toURI().getPath()).getParentFile();
            File scarcityFolder = new File(pluginFolder, subfoldername);
            if (!scarcityFolder.exists()) {
                scarcityFolder.mkdir();
            }
            pluginFolder = scarcityFolder;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void createFiles(){
        File f = new File(pluginFolder, filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveGenericData(String identifier, String dataPoint) {
        //saves data to a txt file
        //if the identifier is already in the file, then replace that line immediately
        //if not, create a new line with the identifier datapoint pair

        //first part of the line is the line number
        //second part is the actual string identifier
        //third part of the line is the data associated to that identifier
        //data separated by pipe symbol

        // LINE# | STRING ID | STRING DATA

        //rebuilds the entire file, with the desired line changed per request

        createFiles();

        File f = new File(pluginFolder,filename);


        String toWrite = identifier + "|" + dataPoint;//this is the full line we want to write to the file


        List<String> lines = getAllLines();

        boolean givenDataIsNotInFile = true;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            //Bukkit.getLogger().info("Creating " + filename +  " file writer.");
            for (String line : lines) {
                //Bukkit.getLogger().info("From our file reading earlier: this line is " + line);


                String[] arr = line.split("\\|");

                //Bukkit.getLogger().info("This line's identifier is: " + arr[0] + ".");
                //Bukkit.getLogger().info("The identifier we are looking for is " + identifier + ".");

                if(arr[0].contains(identifier)){//if the identifier was present in the txt file at the current line
                    //Bukkit.getLogger().info("They match!");
                    writer.write(toWrite);//then instead of pasting the old line, write the new line from given data "toWrite"
                    givenDataIsNotInFile=false;//the data WAS in the file smile
                    //Bukkit.getLogger().info("Just wrote " + toWrite);
                }
                else{
                    //Bukkit.getLogger().info("They do not match!");
                    writer.write(line);//otherwise paste the old line
                    //Bukkit.getLogger().info("Just wrote " + line);
                }

                writer.newLine();//get ready to write a new line
            }

            if(givenDataIsNotInFile){
                //Bukkit.getLogger().info("The given data was NOT found in the data file, so a new datapoint is being saved to the file.");
                writer.write(toWrite);//if the identifier was not in the file, write the data
                //Bukkit.getLogger().info("Just wrote " + toWrite);
            }

            writer.flush();

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static List<String> getAllLines(){


        File f = new File(pluginFolder,filename);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            //Bukkit.getLogger().info("Before we write anything, we need to read first.");

            String line;
            while ((line = reader.readLine()) != null) {

                lines.add(line);
                //Bukkit.getLogger().info("Adding a line to the file reader. " + line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        return lines;

    }

    public static void nullifyData(){
        //sets all the data to "NULL"

        File f = new File(pluginFolder,filename);
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            //Bukkit.getLogger().info("Before we write anything, we need to read first.");

            String line;
            while ((line = reader.readLine()) != null) {

                lines.add(null);
                //Bukkit.getLogger().info("Adding a line to the file reader. " + line);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

    }

    public static String getDatapointFromIdentifier(String id){
        File f = new File(pluginFolder,filename);

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            //Bukkit.getLogger().info("Before we write anything, we need to read first.");

            String line;
            while ((line = reader.readLine()) != null) {

                lines.add(line);
                //Bukkit.getLogger().info("Adding a line to the file reader. " + line);


                String[] arr = line.split("\\|");

                //Bukkit.getLogger().info("This line's identifier is: " + arr[0] + ".");
                //Bukkit.getLogger().info("The identifier we are looking for is " + id + ".");


                if(arr[0].contains(id)){//if the identifier was present in the txt file at the current line
                    //Bukkit.getLogger().info("They match! Going to return: " + arr[1]);
                    return arr[1];
                }

            }

            return "NULL";
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        Bukkit.getLogger().severe("Critical failure in access to " + filename);
        return "NULL";//meaning no datapoint exists // other big problem

    }

}
