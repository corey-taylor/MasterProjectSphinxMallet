


import java.io.*;

public class ConcatenateFiles {

        static public void main(String arg[]) throws java.io.IOException {
                PrintWriter pw = new PrintWriter(new FileOutputStream("resources/concat.txt"));
                File file = new File("resources/txt/");
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {

                        System.out.println("Processing " + files[i].getPath() + "... ");
                        BufferedReader br = new BufferedReader(new FileReader(files[i]
                                        .getPath()));
                        String line = br.readLine();
                        while (line != null) {
                                pw.println(i+"\t"+"a\t"+line);
                                line = br.readLine();
                        }
                        br.close();
                }
                pw.close();
                System.out.println("All files have been concatenated into concat.txt");
        }
}