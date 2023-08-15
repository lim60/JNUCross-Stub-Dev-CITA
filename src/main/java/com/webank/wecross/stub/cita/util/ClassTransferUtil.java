package com.webank.wecross.stub.cita.util;

import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.Bytes;
import com.citahub.cita.abi.datatypes.DynamicArray;
import com.citahub.cita.abi.datatypes.Uint;
import com.citahub.cita.abi.datatypes.generated.*;

public class ClassTransferUtil {
    public static TypeReference<?> transfer(String type) {
        if (Uint.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint>>() {};
        } else if (Uint8.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint8>>() {};
        } else if (Uint16.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint16>>() {};
        } else if (Uint24.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint24>>() {};
        } else if (Uint32.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint32>>() {};
        } else if (Uint40.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint40>>() {};
        } else if (Uint48.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint48>>() {};
        } else if (Uint56.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint56>>() {};
        } else if (Uint64.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint64>>() {};
        } else if (Uint72.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint72>>() {};
        } else if (Uint80.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint80>>() {};
        } else if (Uint88.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint88>>() {};
        } else if (Uint96.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint96>>() {};
        } else if (Uint104.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint104>>() {};
        } else if (Uint112.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint112>>() {};
        } else if (Uint120.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint120>>() {};
        } else if (Uint128.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint128>>() {};
        } else if (Uint136.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint136>>() {};
        } else if (Uint144.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint144>>() {};
        } else if (Uint152.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint152>>() {};
        } else if (Uint160.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint160>>() {};
        } else if (Uint168.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint168>>() {};
        } else if (Uint176.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint176>>() {};
        } else if (Uint184.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint184>>() {};
        } else if (Uint192.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint192>>() {};
        } else if (Uint200.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint200>>() {};
        } else if (Uint208.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint208>>() {};
        } else if (Uint216.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint216>>() {};
        } else if (Uint224.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint224>>() {};
        } else if (Uint232.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint232>>() {};
        } else if (Uint240.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint240>>() {};
        } else if (Uint248.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint248>>() {};
        } else if (Uint256.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Uint256>>() {};
        } else if (Bytes.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes>>() {};
        } else if (Bytes1.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes1>>() {};
        } else if (Bytes2.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes2>>() {};
        } else if (Bytes3.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes3>>() {};
        } else if (Bytes4.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes4>>() {};
        } else if (Bytes5.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes5>>() {};
        } else if (Bytes6.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes6>>() {};
        } else if (Bytes7.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes7>>() {};
        } else if (Bytes8.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes8>>() {};
        } else if (Bytes9.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes9>>() {};
        } else if (Bytes10.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes10>>() {};
        } else if (Bytes11.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes11>>() {};
        } else if (Bytes12.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes12>>() {};
        } else if (Bytes13.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes13>>() {};
        } else if (Bytes14.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes14>>() {};
        } else if (Bytes15.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes15>>() {};
        } else if (Bytes16.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes16>>() {};
        } else if (Bytes17.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes17>>() {};
        } else if (Bytes18.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes18>>() {};
        } else if (Bytes19.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes19>>() {};
        } else if (Bytes20.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes20>>() {};
        } else if (Bytes21.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes21>>() {};
        } else if (Bytes22.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes22>>() {};
        } else if (Bytes23.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes23>>() {};
        } else if (Bytes24.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes24>>() {};
        } else if (Bytes25.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes25>>() {};
        } else if (Bytes26.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes26>>() {};
        } else if (Bytes27.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes27>>() {};
        } else if (Bytes28.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes28>>() {};
        } else if (Bytes29.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes29>>() {};
        } else if (Bytes30.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes30>>() {};
        } else if (Bytes31.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes31>>() {};
        } else if (Bytes32.class.getSimpleName().equalsIgnoreCase(type)) {
            return new TypeReference<DynamicArray<Bytes32>>() {};
        }
        return null;
    }
}
