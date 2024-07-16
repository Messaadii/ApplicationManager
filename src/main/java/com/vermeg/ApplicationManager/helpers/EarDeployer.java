package com.vermeg.ApplicationManager.helpers;

import com.jcraft.jsch.*;
import com.vermeg.ApplicationManager.entities.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EarDeployer implements AutoCloseable {

    private Session session;
    private final Set<String> archives = new HashSet<>();
    private final UpdateResult updateResult = new UpdateResult(new Date(), UpdateStatus.UPDATED);
    private AppUpdaterConfig appUpdaterConfig;

    public EarDeployer(AppUpdaterConfig appUpdaterConfig){
        this.appUpdaterConfig = appUpdaterConfig;
        updateResult.setAppUpdaterConfig(appUpdaterConfig);
    }

    public void init() throws JSchException {
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

    private void executeCommand(String givenCmd,boolean runAsRoot ,String password,String... args) throws JSchException, IOException, InterruptedException {
        String cmd = String.format(givenCmd, args);
        executeCommand(cmd,runAsRoot,password);
    }

    private void executeCommand(String cmd,boolean runAsRoot, String password) throws JSchException, IOException, InterruptedException {
        updateResult.appendLog( runAsRoot ? "[sudo] " + cmd : cmd , "CMD");

        if (runAsRoot) cmd = String.format("echo '%s' | sudo -S sh -c '%s'",password,cmd);

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(cmd);

        channel.connect();

        Thread inputStream = readLog(channel.getInputStream());
        Thread errorStream = readLog(channel.getErrStream());

        inputStream.join();
        errorStream.join();

        updateResult.breakLine();
        channel.disconnect();
    }

    private Thread readLog(InputStream input) {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    updateResult.appendLog("\t" + line.trim());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }

    private void writeFile(ApplicationFile applicationFile) throws IOException, JSchException, SftpException {
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");

        sftpChannel.connect();

        String targetFile = appUpdaterConfig.getDeployOn().getTempPath() + "/ear/"
                + applicationFile.getPath();
        // Transfer the file
        try (InputStream inputStream = new ByteArrayInputStream(applicationFile.getNewValue())) {
            updateResult.appendLog("Updating " + targetFile, "INFO");
            sftpChannel.put(inputStream, targetFile);
        }

        sftpChannel.exit();
    }

    private void executeCommands(List<Command> commands , String password) throws JSchException, IOException, InterruptedException {
        if (commands.isEmpty()) return;
        updateResult.appendLog("Executing user commands", "INFO");
        for (Command command : commands) {
            executeCommand(command.getCommand(),command.getRunAsRoot(),password);
        }
    }

    private void updateFile(ApplicationFile applicationFile) throws IOException, JSchException, SftpException, InterruptedException {
        if(!archives.contains(applicationFile.getPath())) {
            extractArchive(applicationFile);
        }

        writeFile(applicationFile);
    }

    private void extractArchive(ApplicationFile applicationFile) throws JSchException, IOException, InterruptedException {
        String tempFolder = appUpdaterConfig.getDeployOn().getTempPath();
        String path = applicationFile.getPath();
        String archive;
        if(path.contains(".jar")){
            archive = tempFolder + "/ear/" + path.substring(0, path.indexOf(".jar")) + ".jar";
        } else if (path.contains(".war")){
            archive = tempFolder + "/ear/" +  path.substring(0, path.indexOf(".war")) + ".war";
        } else {
            return;
        }

        String fileName = archive.substring(archive.lastIndexOf("/") + 1);

        archives.add(archive);

        String tempArchive = tempFolder + "/" + fileName;
        executeCommand("unzip %s -d %s",false,null
                ,archive, tempArchive);
        executeCommand("rm -fr %s",false,null, archive);
        executeCommand("mv %s %s",false,null, tempArchive, archive);
    }


    public void deploy() throws IOException, JSchException, SftpException, InterruptedException {
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
        updateResult.appendLog("Clearing the temp folder", "INFO");
        executeCommand("rm -rf %s/*",false,null
                ,appUpdaterConfig.getDeployOn().getTempPath());
        updateResult.appendLog("Extracting the " + earName.substring(earName.lastIndexOf(".") + 1 ) + " file", "INFO");
        executeCommand("unzip %s -d %s",false,null
                , appUpdaterConfig.getDeployOn().getEarPath()
                , appUpdaterConfig.getDeployOn().getTempPath() + "/ear/");
        executeCommand("rm -rf %s",false,null
                ,appUpdaterConfig.getDeployOn().getEarPath());


        for (ApplicationFile applicationFile : appUpdaterConfig.getApplicationFiles()) {
            applicationFile.setPath(applicationFile.getPath().replace("\\","/"));
            updateFile(applicationFile);
        }

        packageArchives();

        // Zip the new ear
        updateResult.appendLog("Zipping the new " + earName.substring(earName.lastIndexOf(".")), "INFO");
        executeCommand("cd %s/ear && zip -r %s *",false,null,
                appUpdaterConfig.getDeployOn().getTempPath(),
                appUpdaterConfig.getDeployOn().getEarPath());

        // Clear the temp folder
        updateResult.appendLog("Clearing the temp folder", "INFO");
        executeCommand("rm -rf %s/*",false,null
                ,appUpdaterConfig.getDeployOn().getTempPath());

        executeCommands(appUpdaterConfig.getAfterUpdateCommands()
                ,appUpdaterConfig.getDeployOn().getVirtualMachine().getPassword());

    }

    private void packageArchives() throws JSchException, IOException, InterruptedException {
        for(String archive : archives){
            String folderName = archive.substring(archive.lastIndexOf("/"));
            String tempArchive = appUpdaterConfig.getDeployOn().getTempPath() + folderName;
            executeCommand("cd %s && zip -r %s *",false,null
                    ,archive, tempArchive);
            executeCommand("rm -fr %s",false,null, archive);
            executeCommand("mv %s %s",false,null, tempArchive, archive);
        }
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



