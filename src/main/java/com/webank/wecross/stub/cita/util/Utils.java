package com.webank.wecross.stub.cita.util;

import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.webank.wecross.stub.Block;
import com.webank.wecross.stub.BlockHeader;
import com.webank.wecross.stub.cita.contract.Abi;
import com.webank.wecross.stub.cita.contract.AbiFunctionType;
import com.webank.wecross.stub.cita.contract.ContractParam;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Utils {

    public static String getResourceName(String path) {
        String[] sp = path.split("\\.");

        return sp[sp.length - 1];
    }

    public static Function convertFunction(String abiString, String name, String[] args) {
        List<Abi> abis = parseAbi(abiString);
        for (Abi abi : abis) {
            if (!abi.getName().equalsIgnoreCase(name)) {
                continue;
            }
            List<String> outs = Lists.newArrayListWithCapacity(abi.getOutputTypes().size());
            abi.getOutputTypes().forEach(type -> outs.add(type.getType()));
            List<AbiFunctionType> inputs = abi.getInputTypes();
            if (null == args || args.length == 0) {
                return ContractUtil.convertFunction(name, null, outs);
            }
            if (args.length != inputs.size()) {
                throw new RuntimeException("input args number not equals to abi");
            }
            List<ContractParam> params = Lists.newArrayListWithCapacity(inputs.size());
            for (int i = 0; i < inputs.size(); i++) {
                ContractParam param = new ContractParam();
                param.setType(inputs.get(i).getType());
                param.setValue(args[i]);
                params.add(param);
            }
            return ContractUtil.convertFunction(name, params, outs);
        }
        throw new RuntimeException("method name can't be found in abi");
    }

    public static List<Abi> parseAbi(String abi) {
        List<Abi> abis = Lists.newArrayList();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode trees = objectMapper.readTree(abi);
            for (JsonNode tree : trees) {
                String type = tree.get("type").asText();
                if (!"function".equalsIgnoreCase(type)) {
                    continue;
                }
                Abi inner = new Abi();
                inner.setName(tree.get("name").asText());
                inner.setInputTypes(makeType(tree.get("inputs")));
                inner.setOutputTypes(makeType(tree.get("outputs")));
                abis.add(inner);
            }
            return abis;
        } catch (IOException e) {
            throw new RuntimeException("parse abi failed");
        }
    }

    public static List<AbiFunctionType> makeType(JsonNode node) {
        final List<AbiFunctionType> result = Lists.newArrayListWithCapacity(node.size());
        node.forEach(
                input -> {
                    AbiFunctionType type = new AbiFunctionType();
                    type.setType(input.get("type").asText());
                    type.setName(input.get("name").asText());
                    result.add(type);
                });
        return result;
    }

    public static String hexRemove0x(String hex) {
        if (hex.contains("0x")) {
            return hex.substring(2);
        }
        return hex;
    }

    public static String hexStr2Str(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, StandardCharsets.UTF_8); // UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    public static Block convertBlock(AppBlock appBlock) {
        Block block = new Block();
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setNumber(appBlock.getBlock().getHeader().getNumberDec().longValue());
        blockHeader.setHash(appBlock.getBlock().getHash());
        // todo 完善字段

        block.setBlockHeader(blockHeader);
        block.setRawBytes(new byte[] {}); // raw data
        return block;
    }

    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    public static Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}
