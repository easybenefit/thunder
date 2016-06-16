package thunder.network.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogUtil {

    private static final String LOG_FILE = "G:/IntelliJ/RpcClient/log/log.txt";

    public static void log(Object object) {

        if (object != null) {

            System.out.println("\n\n\n");
            boolean result = new File(LOG_FILE).getParentFile().mkdirs();
            if (!result) {

                return;
            }
            FileWriter fileWriter = null;
            try {

                fileWriter = new FileWriter(LOG_FILE, true);
                fileWriter.append(object.toString());
                fileWriter.append("\r\n");
                fileWriter.close();
            } catch (Exception exception) {

                exception.printStackTrace();
                try {

                    if (fileWriter != null) {

                        fileWriter.close();
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        }
    }
}
