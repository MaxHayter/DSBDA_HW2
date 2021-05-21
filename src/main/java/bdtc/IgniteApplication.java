package bdtc;

import lombok.extern.log4j.Log4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Ignite application entry point class
 */
@Log4j
public class IgniteApplication {
    /**
     * main() method
     * @param args input CLI arguments. Requires 2 arguments:
     *             - ignite configFile
     *             - outputFile
     * @throws IOException from outputFile.write()
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            log.error("Usage: java -jar path/to/jar configFile outputFile");
        }

        Ignition.setClientMode(true);
        Ignite ignite = Ignition.start(args[0]);

        MyCompute compute = new MyCompute(ignite);

        log.info("start compute");

        Map<CustomKey, Long> result = compute.getResults();

        FileWriter wr;
        File output = new File(args[1]+"_output.txt");
        if (output.createNewFile()) {
            wr = new FileWriter(output);
            log.info("create output file");
        } else {
            wr = new FileWriter(output);
            wr.flush();
            log.info("open output file");
        }

        result.forEach((key, value) -> {
            try {
                wr.write("{ "+key.getNewsId()+", "+key.getTypeInteraction()+" } - "+value.toString()+"\n");
            } catch (IOException e) {
                log.error("unable to write result");
                e.printStackTrace();
            }
        });

        wr.close();
        ignite.close();
    }
}
