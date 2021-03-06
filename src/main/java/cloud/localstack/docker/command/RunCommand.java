package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RunCommand extends Command {

    private static final int PULL_AND_RUN_TIMEOUT_MINUTES = 7;

    private final String imageName;

    private final String imageTag;

    public RunCommand(String imageName) {
        this(imageName, null);
    }

    public RunCommand(String imageName, String imageTag) {
        this.imageName = imageName;
        this.imageTag = imageTag;
    }

    public String execute() {
        List<String> args = new ArrayList<>();
        args.add("run");
        args.add("-d");
        args.add("--rm");
        args.addAll(options);
        args.add(imageTag == null ? imageName : String.format("%s:%s", imageName, imageTag));

        // See details here: https://docs.docker.com/engine/reference/run/#exit-status
        List<Integer> errorCodes = Arrays.asList(125, 126, 127);
        return dockerExe.execute(args, PULL_AND_RUN_TIMEOUT_MINUTES, errorCodes);
    }

    public RunCommand withExposedPorts(String portsToExpose, boolean randomize) {
        String portsOption = String.format("%s:%s", randomize ? "" : portsToExpose, portsToExpose );
        addOptions("-p", portsOption);
        return this;
    }

    public RunCommand withEnvironmentVariable(String name, String value) {
        addEnvOption(name, value);
        return this;
    }

    public RunCommand withEnvironmentVariables(Map<String, String> environmentVariables) {
        environmentVariables.forEach((name, value) -> addEnvOption(name, value));
        return this;
    }

    private void addEnvOption(String name, String value) {
        addOptions("-e", String.format("%s=%s", name, value));
    }

}
