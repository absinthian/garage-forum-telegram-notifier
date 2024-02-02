package dev.alex_anghel;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@QuarkusMain
public class Main implements QuarkusApplication {
    final Logger LOG = LogManager.getLogger(UpdateDifferences.class);

    @Override
    public int run(String... args) throws Exception {
        LOG.info("App started...");
        Quarkus.waitForExit();
        return 0;
    }

}
