package com.vermeg.ApplicationManager.services.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import java.io.ByteArrayOutputStream;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.repositories.VirtualMachineRepository;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {
    @Autowired
    private VirtualMachineRepository virtualMachineRepository;

    @Autowired
    public VirtualMachineServiceImpl(VirtualMachineRepository virtualMachineRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @Override
    public VirtualMachine create(VirtualMachine virtualMachine) {
        return virtualMachineRepository.save(virtualMachine);
    }

    @Override
    public VirtualMachine getVirtualMachine(String name) {
        return virtualMachineRepository.getVirtualMachineByName(name);
    }

    @Override
    public List<VirtualMachine> getAllVirtualMachines() {
        return virtualMachineRepository.findAll();
    }

    @Override
    public void deleteVirtualMachine(String name) {
        virtualMachineRepository.delete(virtualMachineRepository.getVirtualMachineByName(name));
    }

    @Override
    public List<String> listActiveJavaProcesses(String name) {
        VirtualMachine vm = virtualMachineRepository.findByName(name);
        List<String> output = new ArrayList<>();

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(vm.getUser(), vm.getHost(), vm.getPort());
            session.setPassword(vm.getPassword());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("ps aux | grep java");
            channel.setErrStream(System.err);

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            output.add("Error: " + e.getMessage());
        }
        return output;
    }

    @Override
    public List<VirtualMachine> findAll() {
        return virtualMachineRepository.findAll();
    }

    @Override
    public String killProcess(String name, String pid) {
        Session session = null;
        ChannelExec channelExec = null;
        try {
            // Fetch VM details from API
            VirtualMachine vm = getVirtualMachine(name);

            if (vm == null) {
                return "Virtual Machine with name " + name + " not found.";
            }

            JSch jsch = new JSch();
            session = jsch.getSession(vm.getUser(), vm.getHost(), vm.getPort());
            session.setPassword(vm.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            String killCommand = "kill -9 " + pid;
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(killCommand);

            // Set input/output streams and error handling
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);

            // Connect and execute the command
            channelExec.connect();

            // Wait for the command to finish execution
            while (!channelExec.isClosed()) {
                Thread.sleep(1000);
            }

            // Retrieve exit status of the executed command
            int exitCode = channelExec.getExitStatus();

            // Disconnect channel and session
            channelExec.disconnect();
            session.disconnect();

            // Return result based on exit status
            if (exitCode == 0) {
                return "Process with VM name " + name + " (PID " + pid + ") killed successfully.";
            } else {
                return "Failed to kill process with VM name " + name + ". Exit code: " + exitCode;
            }

        } catch (JSchException | InterruptedException e) {
            e.printStackTrace();
            return "Failed to kill process with VM name " + name + " and PID " + pid + ": " + e.getMessage();
        } finally {
            // Ensure channels and session are properly disconnected
            if (channelExec != null) {
                channelExec.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public List<Command> getCommandsByVirtualMachineName(String name) {
        return null;
    }

    @Override
    public VirtualMachine findByName(String name) {
        return virtualMachineRepository.findByName(name);
    }

    @Override
    public void executeCommand(Command command) throws JSchException, IOException {
        VirtualMachine vm = command.getVirtualMachine();
        if (vm == null) {
            throw new IllegalArgumentException("Virtual Machine not found");
        }

        JSch jsch = new JSch();
        Session session = jsch.getSession(vm.getUser(), vm.getHost(), vm.getPort());
        session.setPassword(vm.getPassword());

        // Enable verbose JSch logging
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        session.setConfig(config);

        try {
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command.getCommand());
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);

            InputStream in = channelExec.getInputStream();
            channelExec.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    break;
                }
            }

            channelExec.disconnect();
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public String executeThreadDump(String name, String pid) throws InterruptedException, JSchException, IOException {
        VirtualMachine vm = findByName(name);
        if (vm == null) {
            throw new IllegalArgumentException("Virtual Machine not found");
        }

        JSch jsch = new JSch();
        Session session = jsch.getSession(vm.getUser(), vm.getHost(), vm.getPort());
        session.setPassword(vm.getPassword());

        // Enable verbose JSch logging
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        session.setConfig(config);

        try {
            session.connect();
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            String command = "jstack " + pid; // Command to generate thread dump
            channelExec.setCommand(command);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream in = channelExec.getInputStream();
            channelExec.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputStream.write(tmp, 0, i);
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    break;
                }

            }

            channelExec.disconnect();
            return outputStream.toString();
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

}