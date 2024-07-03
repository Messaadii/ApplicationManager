package com.vermeg.ApplicationManager.helpers;

import com.jcraft.jsch.*;
import com.vermeg.ApplicationManager.entities.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EarDeployer implements AutoCloseable {

    private final Session session;
    private final Set<String> jarFiles = new HashSet<>();
    private final Set<String> warFiles = new HashSet<>();
    private final UpdateResult updateResult = new UpdateResult(new Date(), UpdateStatus.UPDATED);
    private AppUpdaterConfig appUpdaterConfig;

    public EarDeployer(AppUpdaterConfig appUpdaterConfig) throws JSchException {
        this.appUpdaterConfig = appUpdaterConfig;
        updateResult.setAppUpdaterConfig(appUpdaterConfig);

        JSch jsch = new JSch();
        session = jsch.getSession(appUpdaterConfig.getDeployOn().getVirtualMachine().getUser()
                , appUpdaterConfig.getDeployOn().getVirtualMachine().getHost()
                , appUpdaterConfig.getDeployOn().getVirtualMachine().getPort());

        session.setPassword(appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

        // Avoid asking for key confirmation
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
    }

    private void executeCommand(String givenCmd,boolean runAsRoot ,String password,String... args) throws JSchException, IOException {
        String cmd = String.format(givenCmd, args);
        executeCommand(cmd,runAsRoot,password);
    }

    private void executeCommand(String cmd,boolean runAsRoot ,String password) throws JSchException, IOException {
        updateResult.appendLog( runAsRoot ? "[sudo] " + cmd : cmd , "CMD");

        if (runAsRoot) cmd = String.format("echo '%s' | sudo -S sh -c '%s'",password,cmd);

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(cmd);

        InputStream input = channel.getInputStream();
        InputStream err = channel.getErrStream();

        channel.connect();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             BufferedReader errorReader = new BufferedReader(new InputStreamReader(err))) {
            String line;
            while ((line = reader.readLine()) != null) {
                updateResult.appendLog("\t" + line.trim());
            }
            while ((line = errorReader.readLine()) != null) {
                updateResult.appendLog("\t" + line.trim());
            }
        }
        updateResult.breakLine();
        channel.disconnect();
    }

    private void writeFile(ApplicationFile applicationFile, String targetFilePath) throws IOException, JSchException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");

        sftpChannel.connect();

        // Transfer the file
        try (InputStream inputStream = new ByteArrayInputStream(applicationFile.getNewValue())) {
            updateResult.appendLog("Updating " + targetFilePath, "INFO");
            sftpChannel.put(inputStream, targetFilePath);
        }

        sftpChannel.exit();
    }

    private void executeCommands(List<Command> commands , String password) throws JSchException, IOException {
        if (commands.isEmpty()) return;
        updateResult.appendLog("Executing user commands", "INFO");
        for (Command command : commands) {
            executeCommand(command.getCommand(),command.getRunAsRoot(),password);
        }
    }


    private void updateFolder(ApplicationFile applicationFile,AppUpdaterConfig appUpdaterConfig,String earName ,Set<String> archiveFiles ,final String tempFolder ,final String extension, String newFileName) throws IOException, JSchException, SftpException {
        String archivePath = appUpdaterConfig.getDeployOn().getTempPath() + "/tempFolder/" +
                applicationFile.getPath().substring(0,
                        applicationFile.getPath().indexOf(extension) + 4);
        String archiveName = archivePath.substring(archivePath.lastIndexOf("/") + 1);
        String archiveFolderName = archiveName.contains(".") ? archiveName.substring(0, archiveName.indexOf(".")) : earName.substring(0, earName.indexOf("."));
        String tempFolderArchive = extension.equals(".jar") ? "tempFolderJar" : "tempFolderWar";

        if (!archiveFiles.contains(archiveFolderName)) {
            updateResult.appendLog("Extracting the " + archiveFolderName + extension, "INFO");
            executeCommand("mkdir %s/%s",false,null,appUpdaterConfig.getDeployOn().getTempPath(),tempFolder);

            updateResult.appendLog("Copying the " + archiveFolderName + extension + " to the temp folder", "INFO");
            executeCommand("unzip %s -d %s/%s/%s",false,null ,archivePath, appUpdaterConfig.getDeployOn().getTempPath(),tempFolder, archiveFolderName);
            archiveFiles.add(archiveFolderName);
        }
        writeFile(applicationFile, appUpdaterConfig.getDeployOn().getTempPath() + "/" + tempFolderArchive + "/" + archiveFolderName + "/" + newFileName);
    }

    private void packageArchive(AppUpdaterConfig appUpdaterConfig,Set<String> archiveFiles , final String extension) throws JSchException, IOException {
        String tempFolderArchive = extension.equals(".jar") ? "tempFolderJar" : "tempFolderWar";
        for (String archive : archiveFiles) {
            updateResult.appendLog("Zipping the " + archive + extension, "INFO");
            executeCommand("cd %s/%s/%s && zip -r %s/tempFolder/%s *",false,null
                    ,appUpdaterConfig.getDeployOn().getTempPath()
                    ,tempFolderArchive, archive, appUpdaterConfig.getDeployOn().getTempPath(), archive + extension);

            updateResult.appendLog("Clearing the " + archive + extension + " temp folder", "INFO");
            executeCommand("find %s/%s -mindepth 1 -exec rm -rf {} +",false,null
                    ,appUpdaterConfig.getDeployOn().getTempPath(),tempFolderArchive);
        }
    }

    private void updateFile(ApplicationFile applicationFile, String earName) throws IOException, JSchException, SftpException {
        AppUpdaterConfig appUpdaterConfig = applicationFile.getAppUpdaterConfig();
        String newFileName = applicationFile.getPath().contains("/") ?
                applicationFile.getPath().substring(applicationFile.getPath().lastIndexOf("/") + 1)
                : applicationFile.getPath();

        if (applicationFile.getPath().contains(".jar")) {
            updateFolder(applicationFile,appUpdaterConfig, earName,jarFiles, "tempFolderJar", ".jar", newFileName);
        } else if (applicationFile.getPath().contains(".war")) {
            updateFolder(applicationFile,appUpdaterConfig, earName,warFiles, "tempFolderWar", ".war", newFileName);
        } else {
            writeFile(applicationFile, appUpdaterConfig.getDeployOn().getTempPath() + "/tempFolder/" + newFileName );
        }
    }


    public void deploy() throws IOException, JSchException, SftpException {
        executeCommands(appUpdaterConfig.getBeforeUpdateCommands()
                ,appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

        String earName = appUpdaterConfig.getDeployOn().getFileName();

        // Backup ear
        updateResult.appendLog("Backing up the ear", "INFO");
        executeCommand("mv %s %s/%s%s",false,null
                ,appUpdaterConfig.getDeployOn().getEarPath(),
                appUpdaterConfig.getDeployOn().getBackupFolderPath(),
                earName.substring(0, earName.indexOf(".")),
                new java.text.SimpleDateFormat("_yyyy-MM-dd_HH-mm-ss").format(new java.util.Date(System.currentTimeMillis())) +
                        earName.substring(earName.lastIndexOf('.')));

        updateResult.appendLog("Getting the new ear", "INFO");
        executeCommand(appUpdaterConfig.getToBeDeployed().getEarCommand(appUpdaterConfig.getDeployOn().getEarPath()),false,null);

        // Extract the ear
        updateResult.appendLog("Extracting the " + earName.substring(earName.lastIndexOf(".") + 1 ) + " file", "INFO");
        executeCommand("unzip %s -d %s/tempFolder",false,null
                , appUpdaterConfig.getDeployOn().getEarPath()
                , appUpdaterConfig.getDeployOn().getTempPath());

        for (ApplicationFile applicationFile : appUpdaterConfig.getApplicationFiles()) {
            updateFile(applicationFile,earName);
        }

        packageArchive(appUpdaterConfig,jarFiles,".jar");

        packageArchive(appUpdaterConfig,warFiles,".war");

        // Zip the new ear
        updateResult.appendLog("Zipping the new " + earName.substring(earName.lastIndexOf(".")), "INFO");
        executeCommand("cd %s/tempFolder && zip -r %s *",false,null
                ,appUpdaterConfig.getDeployOn().getTempPath()
                , appUpdaterConfig.getDeployOn().getEarPath());

        // Clear the temp folder
        updateResult.appendLog("Clearing the temp folder", "INFO");
        executeCommand("find %s/tempFolder -mindepth 1 -exec rm -rf {} +",false,null
                ,appUpdaterConfig.getDeployOn().getTempPath());

        executeCommands(appUpdaterConfig.getAfterUpdateCommands()
                ,appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

    }


    @Override
    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }

    public UpdateResult getUpdateResult() {
        return updateResult;
    }

}



