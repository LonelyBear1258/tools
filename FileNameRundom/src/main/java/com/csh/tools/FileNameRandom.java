package com.csh.tools;

import com.csh.encode_decode_utils.AES;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileNameRandom {

    private static final String PASSWORD = "CSh1258284887==";

    public static void main(String[] args) throws Exception {
        String handleCode;
        if (args.length == 0){
            handleCode = "-help";
        }else {
           handleCode = args[0];
        }
        switch (handleCode) {
            case "-help":
            case "-h":
                System.out.println("-random 文件夹全路径不能有空格 结果文件路径不能有空格 :将文件名设置为随机名字");
                System.out.println("-bak 文件夹全路径不能有空格 结果文件路径不能有空格到文件名 :将文件名设置为随机名字");
                break;
            case "-random": {
                String targetPath = args[1];
                String resultPath = args[2];
                StringBuilder result = new StringBuilder();
                File resultFile = new File(resultPath + "\\result.txt");
                randomFileName(targetPath, result);
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile, true));
                bw.write(AES.parseByte2HexStr(AES.encrypt(result.toString(), PASSWORD)));
                bw.flush();
                bw.close();
                break;
            }
            case "-bak": {
                String targetPath = args[1];
                String resultPath = args[2];
                BufferedReader br = new BufferedReader(new FileReader(resultPath));
                Map<String, String> result = new HashMap<>();
                StringBuilder sb = new StringBuilder();
                String tmpStr;
                while ((tmpStr = br.readLine()) != null) {
                    sb.append(tmpStr);
                }
                String str = new String(AES.decrypt(AES.parseHexStr2Byte(sb.toString()), PASSWORD), StandardCharsets.UTF_8);
                System.out.println(str);
                String[] tmp = str.split("\\+");
                for (String s : tmp) {
                    String[] tmpPair = s.split("~");
                    if (tmpPair.length == 2) {
                        result.put(tmpPair[0], tmpPair[1]);
                    }
                }
                bakFileName(targetPath, result);
                break;
            }
            default:{
                System.out.println("-random 文件夹全路径不能有空格 结果文件路径不能有空格 :将文件名设置为随机名字");
                System.out.println("-bak 文件夹全路径不能有空格 结果文件路径不能有空格到文件名 :将文件名设置为随机名字");
            }
        }
    }

    private static void randomFileName(String targetPath,StringBuilder result) {
        File targetFile = new File(targetPath);
        File[] files = targetFile.listFiles();
        if (files == null){
            return;
        }
        for (File file : files) {
            if (file.isDirectory()){
                randomFileName(file.getPath(),result);
            }
            String fileName = file.getName();
            String randomName = UUID.randomUUID().toString();
            boolean flag = file.renameTo(new File(file.getParent() + "\\" + randomName));
            if (flag){
                result.append(randomName);
                result.append("~");
                result.append(fileName);
                result.append("+");
            }
        }

    }

    private static void bakFileName(String tarPath,Map<String,String> resultMap){
        File tarFile = new File(tarPath);
        File[] files = tarFile.listFiles();
        if (files == null){
            return;
        }
        for (File file : files) {
            if (file.isDirectory()){
                bakFileName(file.getPath(),resultMap);
            }
            String fileName = file.getName();
            String realName = resultMap.get(fileName);
            if (realName != null){
                boolean flag = file.renameTo(new File(file.getParent() + "\\" + realName));
                if (flag){
                    System.out.println(realName);
                }
            }
        }
    }
}
