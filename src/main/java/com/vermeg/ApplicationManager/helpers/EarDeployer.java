package com.vermeg.ApplicationManager.helpers;

import com.jcraft.jsch.*;
import com.vermeg.ApplicationManager.entities.*;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class EarDeployer implements AutoCloseable {

    private final Session session;
    private final Set<String> jarFiles = new HashSet<>();
    private final Set<String> warFiles = new HashSet<>();

    public EarDeployer(AppUpdaterConfig appUpdaterConfig) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(appUpdaterConfig.getDeployOn().getVirtualMachine().getUser(), appUpdaterConfig.getDeployOn().getVirtualMachine().getHost(), appUpdaterConfig.getDeployOn().getVirtualMachine().getPort());
        session.setPassword(appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

        // Avoid asking for key confirmation
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
    }

    private void executeCommand(String givenCmd, String... args) throws JSchException, IOException {
        String cmd = String.format(givenCmd, args);
        executeCommand(cmd);
    }

    private void executeCommand(String cmd ) throws JSchException, IOException {
        System.out.println("The command (" + cmd + ") is being executed");

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(cmd);

        InputStream input = channel.getInputStream();
        InputStream err = channel.getErrStream();

        channel.connect();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(err))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Info: " + line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }
        }
        channel.disconnect();
    }

    private void writeFile(ApplicationFile applicationFile, String targetFilePath) throws IOException, JSchException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");

        sftpChannel.connect();

        // Transfer the file
        try (InputStream inputStream = new ByteArrayInputStream(applicationFile.getNewValue())) {
            sftpChannel.put(inputStream, targetFilePath);
        }

        sftpChannel.exit();
    }

    private void executeCommands(List<Command> commands , String password) throws JSchException, IOException {
        String cmd ="";
        for (Command command : commands) {
            cmd = command.getRunAsRoot() ? String.format("echo '%s' | sudo -S sh -c '%s'",password,command.getCommand()): command.getCommand();
            executeCommand(cmd);
        }
    }


    private void updateFolder(ApplicationFile applicationFile,AppUpdaterConfig appUpdaterConfig,String earName ,Set<String> archiveFiles ,final String tempFolder ,final String extension, String newFileName) throws IOException, JSchException, SftpException {
        String archivePath = appUpdaterConfig.getDeployOn().getTempPath() + "/tempFolder/" +
                applicationFile.getPath().substring(applicationFile.getPath().indexOf(earName) + earName.length(),
                        applicationFile.getPath().indexOf(extension) + 4);
        String archiveName = archivePath.substring(archivePath.lastIndexOf("/") + 1);
        String archiveFolderName = archiveName.contains(".") ? archiveName.substring(0, archiveName.indexOf(".")) : earName.substring(0, earName.indexOf("."));
        String tempFolderArchive = extension.equals(".jar") ? "tempFolderJar" : "tempFolderWar";

        if (!archiveFiles.contains(archiveFolderName)) {
            executeCommand("mkdir %s/%s \n",appUpdaterConfig.getDeployOn().getTempPath(),tempFolder);
            executeCommand("unzip %s -d %s/%s/%s \n" ,archivePath, appUpdaterConfig.getDeployOn().getTempPath(),tempFolder, archiveFolderName);
            archiveFiles.add(archiveFolderName);
        }
        writeFile(applicationFile, appUpdaterConfig.getDeployOn().getTempPath() + "/" + tempFolderArchive + "/" + archiveFolderName + "/" + newFileName);
    }

    private void packageArchive(AppUpdaterConfig appUpdaterConfig,Set<String> archiveFiles , final String extension) throws JSchException, IOException {
        String tempFolderArchive = extension.equals(".jar") ? "tempFolderJar" : "tempFolderWar";
        for (String archive : archiveFiles) {
            executeCommand("cd %s/%s/%s && zip -r %s/tempFolder/%s * \n"
                    ,appUpdaterConfig.getDeployOn().getTempPath()
                    ,tempFolderArchive, archive, appUpdaterConfig.getDeployOn().getTempPath(), archive + extension);

            executeCommand("find %s/%s -mindepth 1 -exec rm -rf {} + \n"
                    ,appUpdaterConfig.getDeployOn().getTempPath(),tempFolderArchive);
        }
    }

    public void deploy(AppUpdaterConfig appUpdaterConfig) throws IOException, JSchException, SftpException {

        executeCommands(appUpdaterConfig.getBeforeUpdateCommands()
                ,appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

        String earName = appUpdaterConfig.getDeployOn().getFileName();

        // Backup ear
        executeCommand("mv %s %s/%s%s \n" ,appUpdaterConfig.getDeployOn().getEarPath(),
                appUpdaterConfig.getDeployOn().getBackupFolderPath(),
                earName.substring(0, earName.indexOf(".")),
                new java.text.SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss").format(new java.util.Date(System.currentTimeMillis())) +
                        earName.substring(earName.lastIndexOf('.')));

        executeCommand(appUpdaterConfig.getToBeDeployed().getEarCommand(appUpdaterConfig.getDeployOn().getEarPath()));

        // Extract the ear
        executeCommand("unzip %s -d %s/tempFolder \n"
                , appUpdaterConfig.getDeployOn().getEarPath()
                , appUpdaterConfig.getDeployOn().getTempPath());

        for (ApplicationFile applicationFile : appUpdaterConfig.getApplicationFiles()) {
            updateFile(applicationFile,earName);
        }

        packageArchive(appUpdaterConfig,jarFiles,".jar");

        packageArchive(appUpdaterConfig,warFiles,".war");

        // Zip the new ear
        executeCommand("cd %s/tempFolder && zip -r %s * \n"
                ,appUpdaterConfig.getDeployOn().getTempPath()
                , appUpdaterConfig.getDeployOn().getEarPath());

        // Clear the temp folder
        executeCommand("find %s/tempFolder -mindepth 1 -exec rm -rf {} + \n"
                ,appUpdaterConfig.getDeployOn().getTempPath());

        executeCommands(appUpdaterConfig.getAfterUpdateCommands()
                ,appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

    }

    private void updateFile(ApplicationFile applicationFile, String earName) throws IOException, JSchException, SftpException {
        AppUpdaterConfig appUpdaterConfig = applicationFile.getAppUpdaterConfig();
        String newFileName = applicationFile.getPath().substring(applicationFile.getPath().lastIndexOf("/") + 1);

        if (applicationFile.getPath().contains(".jar") && applicationFile.getPath().contains(".ear")) {
            updateFolder(applicationFile,appUpdaterConfig, earName,jarFiles, "tempFolderJar", ".jar", newFileName);
        } else if (applicationFile.getPath().contains(".war") && applicationFile.getPath().contains(".ear")) {
            updateFolder(applicationFile,appUpdaterConfig, earName,warFiles, "tempFolderWar", ".war", newFileName);
        } else {
            writeFile(applicationFile, appUpdaterConfig.getDeployOn().getTempPath() + "/tempFolder/" + newFileName );
        }
    }

    @Override
    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }
}



