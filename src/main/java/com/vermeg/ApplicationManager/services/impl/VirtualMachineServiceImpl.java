package com.vermeg.ApplicationManager.services.impl;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.vermeg.ApplicationManager.repositories.VirtualMachineRepository;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {

    VirtualMachineRepository virtualMachineRepository;

    @Autowired
    public VirtualMachineServiceImpl(VirtualMachineRepository virtualMachineRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @Override
    public VirtualMachine create(VirtualMachine virtualMachine) {
        return virtualMachineRepository.save(virtualMachine);
    }
    @Override
    public VirtualMachine getVirtualMachine( String name) {
        return virtualMachineRepository.getVirtualMachineByName(name);
    }
    @Override
    public List<String> listActiveJavaProcesses(String name){
        VirtualMachine vm = virtualMachineRepository.findByName(name);
        StringBuilder output = new StringBuilder();

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(vm.getUser(), vm.getHost(), vm.getPort());
            session.setPassword(vm.getPassword());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("ps aux |grep java");
            channel.setErrStream(System.err);

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            channel.disconnect();
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            output.append("Error: ").append(e.getMessage());
        }

        return Collections.singletonList(output.toString());
    }

    @Override
    public List<VirtualMachine> findAll() {
        return virtualMachineRepository.findAll();
    }
}

