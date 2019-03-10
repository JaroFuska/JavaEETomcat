package docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DockerManager {
    private static DockerClient dockerClient;

    static {
        try {
            dockerClient = DefaultDockerClient.fromEnv().connectionPoolSize(100).build();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public static String buildImage(Path pathToDockerfile, String imageName) {
        try {
            return dockerClient.build(pathToDockerfile.getParent(), imageName);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String buildImage(String pathToDockerfileDir, String imageName) {
        try {
            return dockerClient.build(Paths.get(pathToDockerfileDir), imageName);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String createContainer(String imageId) {
        ContainerConfig containerConfig = ContainerConfig.builder().image(imageId).attachStdout(true).cmd("sh", "-c", "while :; do sleep 1; done").build();
        ContainerCreation containerCreation = null;
        try {
            containerCreation = dockerClient.createContainer(containerConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return containerCreation.id();
    }

    public static String execStart(String containerId, String runCommand) {
        LogStream output = null;
        try {
            dockerClient.startContainer(containerId);
            String[] command = {"sh", "-c", runCommand};
            ExecCreation execCreation = dockerClient.execCreate(
                    containerId, command, DockerClient.ExecCreateParam.attachStdout(),
                    DockerClient.ExecCreateParam.attachStderr(),
                    DockerClient.ExecCreateParam.tty());
//            System.out.println(execCreation.id());
            output = dockerClient.execStart(execCreation.id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.readFully();
    }

    public static void cleanUp(String containerId) {
        if (containerId == null) {
            return;
        }
        try {
            dockerClient.killContainer(containerId);
            dockerClient.removeContainer(containerId);
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void cleanUp() {
//        for (String containerID : dockerClient.listContainers()) {
//
//        }
//    }

    public static String runFile(String file) {
        String imageId = DockerManager.buildImage(file.substring(0, file.lastIndexOf("/")), "testingimage:latest");
        String containerId = DockerManager.createContainer(imageId);
        String ret = DockerManager.execStart(containerId, "python " + file);
        return ret;
    }

    public List<Container> getContainers() {
        List<Container> ret = null;
        try {
            ret = dockerClient.listContainers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<Image> getImages() {
        List<Image> ret = null;
        try {
            ret = dockerClient.listImages();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        try {
//            DockerClient dockerClient = DefaultDockerClient.fromEnv().connectionPoolSize(100).build();
//            TODO - version for HTTPS support
//            final DockerClient docker = DefaultDockerClient.builder()
//                    .uri(URI.create("https://boot2docker:2376"))
//                    .dockerCertificates(new DockerCertificates(Paths.get("/Users/rohan/.docker/boot2docker-vm/")))
//                    .build();

            Path docFile = Paths.get("C:\\DP\\python_docker_test3\\Dockerfile");
            String imageId = buildImage(docFile, "testingimage:latest");
            String containerId = createContainer(imageId);
            String execOutput = execStart(containerId, "python -m unittest discover");

            System.out.println(execOutput);

            dockerClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
