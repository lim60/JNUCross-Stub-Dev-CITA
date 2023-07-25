package jnucross.stub.cita.util;

import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jnucross.stub.cita.constant.ContractType;
import jnucross.stub.cita.contract.ContractParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractUtil {

    private static final String LEFT_RIGHT_BRACKETS = "[]";

    private static final Map<String, Class> CLASS_MAP = Maps.newConcurrentMap();

    public static List<Type> convertInputParams(List<ContractParam> params) {
        if (CollectionUtils.isEmpty(params)) {
            return Lists.newArrayList();
        }
        List<Type> inputs = Lists.newArrayListWithCapacity(params.size());
        for (ContractParam param : params) {
            if (StringUtils.isBlank(param.getType()) || StringUtils.isBlank(param.getValue())) {
                continue;
            }
            Type type = convert(param);
            if (null == type) {
                throw new RuntimeException("");
            }
            inputs.add(type);
        }
        return inputs;
    }

    private static Type convert(ContractParam param) {
        String type = param.getType();
        String value = param.getValue();
        if (ContractType.BOOL.equalsIgnoreCase(type)) {
            return new Bool(Boolean.parseBoolean(value));
        } else if (type.contains(ContractType.UINT) && !type.contains(LEFT_RIGHT_BRACKETS)) {
            return reflectUintWithValue(param.getType(), param.getValue());
        } else if (type.contains(ContractType.BYTES) && !type.contains(LEFT_RIGHT_BRACKETS)) {
            return reflectBytesWithValue(param.getType(), param.getValue());
        } else if (ContractType.ADDRESS.equalsIgnoreCase(type)) {
            return new Address(value);
        } else if (ContractType.STRING.equalsIgnoreCase(type)) {
            return new Utf8String(value);
        }
        String[] array = value.split(",");
        if (type.contains(ContractType.UINT) && type.contains(LEFT_RIGHT_BRACKETS)) {
            List<Uint> list = Lists.newArrayListWithCapacity(array.length);
            for (String inner : array) {
                list.add(reflectUintWithValue(type.substring(0, type.length() - 2), inner));
            }
            return new DynamicArray<>(list);
        } else if (type.contains(ContractType.BYTES) && type.contains(LEFT_RIGHT_BRACKETS)) {
            List<Bytes> list = Lists.newArrayListWithCapacity(array.length);
            for (String inner : array) {
                list.add(reflectBytesWithValue(type.substring(0, type.length() - 2), inner));
            }
            return new DynamicArray<>(list);
        } else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(type)) {
            List<Address> list = Lists.newArrayListWithCapacity(array.length);
            for (String inner : array) {
                list.add(new Address(inner));
            }
            return new DynamicArray<>(list);
        }
        return null;
    }

    public static Function convertFunction(
            String methodName, List<ContractParam> params, List<String> out) {
        List<Type> inputs = convertInputParams(params);
        if (CollectionUtils.isEmpty(out)) {
            return new Function(methodName, inputs, Lists.newArrayList());
        }
        List<TypeReference<?>> output = Lists.newArrayListWithCapacity(out.size());
        for (String string : out) {
            if (string.contains(ContractType.UINT) && !string.contains(LEFT_RIGHT_BRACKETS)) {
                output.add(TypeReference.create(reflectUint(string)));
            } else if (ContractType.BOOL.equalsIgnoreCase(string)) {
                output.add(new TypeReference<Bool>() {});
            } else if (ContractType.ADDRESS.equalsIgnoreCase(string)) {
                output.add(new TypeReference<Address>() {});
            } else if (ContractType.STRING.equalsIgnoreCase(string)) {
                output.add(new TypeReference<Utf8String>() {});
            } else if (string.contains(ContractType.BYTES)
                    && !string.contains(LEFT_RIGHT_BRACKETS)) {
                output.add(TypeReference.create(reflectBytes(string)));
            } else if (string.contains(ContractType.UINT) && string.contains(LEFT_RIGHT_BRACKETS)) {
                output.add(ClassTransferUtil.transfer(string.substring(0, string.length() - 2)));
            } else if (string.contains(ContractType.BYTES)
                    && string.contains(LEFT_RIGHT_BRACKETS)) {
                output.add(ClassTransferUtil.transfer(string.substring(0, string.length() - 2)));
            } else if (ContractType.ADDRESS_ARRAY.equalsIgnoreCase(string)) {
                output.add(new TypeReference<DynamicArray<Address>>() {});
            }
        }
        return new Function(methodName, inputs, output);
    }

    private static Uint reflectUintWithValue(String name, String value) {
        for (Class<? extends Uint> clazz : reflectUintChildren()) {
            if (clazz.getSimpleName().equalsIgnoreCase(name)) {
                try {
                    CLASS_MAP.put(name, clazz);
                    Constructor<? extends Uint> constructor =
                            clazz.getConstructor(BigInteger.class);
                    return constructor.newInstance(new BigInteger(value));
                } catch (Exception e) {
                    throw new RuntimeException("get class constructor failed");
                }
            }
        }
        throw new RuntimeException("can't find class match");
    }

    private static Class reflectUint(String name) {
        Class cache = CLASS_MAP.get(name);
        if (null != cache) {
            return cache;
        }
        for (Class<? extends Uint> clazz : reflectUintChildren()) {
            if (clazz.getSimpleName().equalsIgnoreCase(name)) {
                try {
                    CLASS_MAP.put(name, clazz);
                    return clazz;
                } catch (Exception e) {
                    throw new RuntimeException("get class constructor failed");
                }
            }
        }
        throw new RuntimeException("reflect uint failed");
    }

    private static Class reflectBytes(String name) {
        Class cache = CLASS_MAP.get(name);
        if (null != cache) {
            return cache;
        }
        for (Class<? extends Bytes> clazz : reflectBytesChildren()) {
            if (clazz.getSimpleName().equalsIgnoreCase(name)) {
                try {
                    CLASS_MAP.put(name, clazz);
                    return clazz;
                } catch (Exception e) {
                    throw new RuntimeException("get class constructor failed");
                }
            }
        }
        throw new RuntimeException("reflect bytes failed");
    }

    private static Set<Class<? extends Uint>> reflectUintChildren() {
        String className = Uint.class.getName();
        Reflections reflections =
                new Reflections(
                        className.substring(0, className.indexOf(Uint.class.getSimpleName()) - 1));
        Set<Class<? extends Uint>> set = reflections.getSubTypesOf(Uint.class);
        set.add(Uint.class);
        return set;
    }

    private static Set<Class<? extends Bytes>> reflectBytesChildren() {
        String className = Bytes.class.getName();
        Reflections reflections =
                new Reflections(
                        className.substring(0, className.indexOf(Bytes.class.getSimpleName()) - 1));
        return reflections.getSubTypesOf(Bytes.class);
    }

    private static Bytes reflectBytesWithValue(String name, String value) {
        for (Class<? extends Bytes> clazz : reflectBytesChildren()) {
            if (clazz.getSimpleName().equalsIgnoreCase(name)) {
                try {
                    Constructor<? extends Bytes> constructor = clazz.getConstructor(byte[].class);
                    return constructor.newInstance(value.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException("");
                }
            }
        }
        return null;
    }
}
