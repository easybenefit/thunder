package thunder.compiler;

import thunder.network.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;


final class RpcAnnotatedFieldInfo {

    public final String className;
    public final String packageName;
    public final String targetClassName;
    private final Map<String, RpcFieldBind> rpcFieldBindMap = new LinkedHashMap<>();

    RpcAnnotatedFieldInfo(String className, String packageName, String targetClassName) {

        if (StringUtil.isEmpty(className) || StringUtil.isEmpty(packageName) || StringUtil.isEmpty(targetClassName)) {

            throw new IllegalArgumentException(String.format("class name %s: , package name: %s, target class name: %s, cannot be null or empty", className, packageName, targetClassName));
        }
        this.className = className;
        this.packageName = packageName;
        this.targetClassName = targetClassName + Constants.BINDING_CLASS_SUFFIX;
    }

    public RpcFieldBind getRpcFieldBinding(String variableName) {

        return rpcFieldBindMap.get(variableName);
    }

    public Map<String, RpcFieldBind> getRpcFieldBindMap() {

        return rpcFieldBindMap;
    }


    public boolean addRpcFieldBind(RpcFieldBind rpcFieldBind) {

        if (rpcFieldBind == null) {

            return false;
        }
        RpcFieldBind fieldBind = getRpcFieldBinding(rpcFieldBind.name);
        if (fieldBind != null) {

            throw new RuntimeException("Rpc thunder " + rpcFieldBind.name + " already exists, cannot duplicate it.\n");
        } else {

            rpcFieldBindMap.put(rpcFieldBind.name, rpcFieldBind);
        }

        return true;
    }
}
