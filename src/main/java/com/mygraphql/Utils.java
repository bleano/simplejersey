package com.mygraphql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bobdo on 8/13/2017.
 */
public class Utils {
    public static String getTimeStamp(long epoch) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSSS");
        TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
        sdf.setTimeZone(tz);
        return sdf.format(new Date(epoch));
    }
    public static String getSchema(){
        BufferedReader br = null;
        FileReader fr = null;
        String filePathString = "C:\\CODE\\SimpleJersey\\src\\main\\resources\\Schema.graphql";
        File f = new File(filePathString);
        StringBuilder sb = new StringBuilder();
        if(f.exists() && !f.isDirectory()) {
            try {
                fr = new FileReader(filePathString);
                br = new BufferedReader(fr);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    sb.append(sCurrentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
